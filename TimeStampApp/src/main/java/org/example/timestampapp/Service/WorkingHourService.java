package org.example.timestampapp.Service;

import org.example.timestampapp.Model.DTO.EmployeeHistoryDTO;
import org.example.timestampapp.Model.DTO.FixRecordDTO;
import org.example.timestampapp.Model.Entity.*;
import org.example.timestampapp.Repository.WorkingHourRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WorkingHourService {
    private final WorkingHourRepository workingHourRepository;

    private final WorkingHourMapper workingHourMapper;
    private final WorkingHourSegmentService workingHourSegmentService;
    private final BreakService breakService;

    public WorkingHourService(WorkingHourRepository workingHourRepository,
                              WorkingHourMapper workingHourMapper,
                              WorkingHourSegmentService workingHourSegmentService,
                              BreakService breakService) {
        this.workingHourRepository = workingHourRepository;
        this.workingHourMapper = workingHourMapper;
        this.workingHourSegmentService = workingHourSegmentService;
        this.breakService = breakService;
    }


    public List<FixRecordDTO> getFixRecord() {
        return workingHourMapper.mapFixRecord(workingHourRepository.findWorkingHourWithAutoLeave());
    }

    public List<EmployeeHistoryDTO> getEmployeeHistory(Long employeeId, int year, int month, int baseSalary) {
        List<EmployeeHistoryDTO> history = new ArrayList<>();
        List<WorkingHour> monthlyRecord=workingHourRepository.findDetailWorkingHourByEmployeeId(employeeId,year,month);
        for (WorkingHour workingHour : monthlyRecord) {
            List<WorkingHourSegment> segments=workingHourSegmentService.getWorkingHourSegment(workingHour.getId());
            history.add(workingHourMapper.mapEmployeeHistoryDTO(workingHour, segments, baseSalary));
        }
        return history;
    }

    @Transactional
    public void updateWorkingHour(Long workingHourId,LocalDateTime startTime,LocalDateTime endTime) {
        WorkingHour target=workingHourRepository
                .findById(workingHourId)
                .orElseThrow(()-> new NoSuchElementException("No working record found"));

        target.getBreaks().removeIf(break_ -> break_.getEndTime().isAfter(endTime));
        target.setStartTime(startTime);
        target.setEndTime(endTime);
        target.setAutoLeave(false);
        target.getSegments().clear();
        workingHourSegmentService.updateWorkingHourSegment(target,startTime,endTime);
        workingHourRepository.save(target);
    }

    @Transactional
    public void workTimeStamp(Employee employee){
        WorkingHour workingHour=new WorkingHour(LocalDateTime.now(),employee);
        workingHourRepository.save(workingHour);
    }

    @Transactional
    public void leaveTimeStamp(Long employeeId){
        WorkingHour workingHour=workingHourRepository
                .findCurrentWorkingHourByEmployeeId(employeeId)
                .orElseThrow(()->new NoSuchElementException("Employee with id " + employeeId + " doesn't work now"));

        workingHour.setAutoLeave(false);
        workingHour.setEndTime(LocalDateTime.now());
        workingHourSegmentService
                .updateWorkingHourSegment(
                workingHour,
                workingHour.getStartTime(),
                workingHour.getEndTime());
        workingHourRepository.save(workingHour);
    }

    @Transactional
    public void breakTimeStamp(Long employeeId){
        WorkingHour workingHour=workingHourRepository.
                findCurrentWorkingHourByEmployeeId(employeeId).
                orElseThrow(()->new NoSuchElementException("Employee with id " + employeeId + " doesn't work now"));
        breakService.breakTimeStamp(workingHour);
    }

    @Transactional
    public void backTimeStamp(Long employeeId){
        WorkingHour workingHour=workingHourRepository.
                findCurrentWorkingHourByEmployeeId(employeeId).
                orElseThrow(()->new NoSuchElementException("Employee with id " + employeeId + " doesn't work now"));
        breakService.backTimeStamp(workingHour.getId());
    }

    @Transactional
    public void autoCheckOut(Status leaveStatus){
        List<WorkingHour> withoutLeave=workingHourRepository.findAllByEndTimeIsNull();
        for (WorkingHour workingHour : withoutLeave) {
            workingHour.setAutoLeave(true);
            workingHour.setEndTime(LocalDateTime.now());
            Employee employee=workingHour.getEmployee();
            employee.setStatus(leaveStatus);
            employee.setLastUpdate(LocalDateTime.now());
            workingHourSegmentService
                    .updateWorkingHourSegment(
                            workingHour,
                            workingHour.getStartTime(),
                            workingHour.getEndTime());
            workingHourRepository.save(workingHour);
        }
    }

}
