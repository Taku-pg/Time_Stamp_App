package org.example.timestampapp.Service;

import org.example.timestampapp.Model.DTO.*;
import org.example.timestampapp.Model.Entity.*;
import org.example.timestampapp.Repository.SegmentTypeRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class WorkingHourMapper {

    private final SegmentTypeRepository segmentTypeRepository;

    public WorkingHourMapper(SegmentTypeRepository segmentTypeRepository) {
        this.segmentTypeRepository = segmentTypeRepository;
    }

    public EmployeeStatisticsDTO mapEmpStatistics(List<WorkingHour> monthlyRecord, int year, int month, Long employeeId) {
        EmployeeStatisticsDTO recordDTO=new EmployeeStatisticsDTO();

        Map<String,Double> hours=calculateHours(monthlyRecord);
        recordDTO.setEmployeeId(employeeId);
        recordDTO.setRegular(hours.get("regular"));
        recordDTO.setOverTime(hours.get("overtime"));
        recordDTO.setNightShift(hours.get("night"));
        recordDTO.setOverNight(hours.get("overNight"));
        recordDTO.setBreakTime(hours.get("breakHours"));
        recordDTO.setTotal(hours.get("totalHours"));
        recordDTO.setYear(year);
        recordDTO.setMonth(month);

        return recordDTO;
    }

    public DepartmentStatisticsDTO mapDeptStatistics(List<WorkingHour> monthlyRecord, int year, int month, String dName) {
        DepartmentStatisticsDTO recordDTO=new DepartmentStatisticsDTO();
        Map<String,Double>hours=calculateHours(monthlyRecord);

        recordDTO.setName(dName);
        recordDTO.setRegular(hours.get("regular"));
        recordDTO.setOverTime(hours.get("overtime"));
        recordDTO.setNightShift(hours.get("night"));
        recordDTO.setOverNight(hours.get("overNight"));
        recordDTO.setBreakTime(hours.get("breakHours"));
        recordDTO.setYear(year);
        recordDTO.setMonth(month);

        return recordDTO;
    }

    private Map<String,Double> calculateHours(List<WorkingHour> monthlyRecord) {
        double regular=0;
        double overTime=0;
        double night=0;
        double overNight=0;
        double breakHours=0;
        for(WorkingHour workingHour : monthlyRecord) {
            //calculate working hours
            List<WorkingHourSegment> segments=workingHour.getSegments();
            for(WorkingHourSegment segment : segments) {
                Optional<SegmentType> type=segmentTypeRepository.findById(segment.getSegmentType().getId());
                if(type.isPresent()) {
                    String typeName=type.get().getName();
                    switch(typeName) {
                        case "regular":
                            regular += segment.getDuration();
                            break;
                        case "overtime":
                            overTime += segment.getDuration();
                            break;
                        case "night":
                            night += segment.getDuration();
                            break;
                        case "over-night":
                            overNight += segment.getDuration();
                            break;
                    }
                }
            }

            //calculate break time
            List<Break> breaks=workingHour.getBreaks();
            for(Break break_ : breaks) {
                Duration duration=Duration.between(break_.getStartTime(),break_.getEndTime());
                breakHours+=duration.toMinutes();
            }
        }
        regular=(double)Math.round((100*regular)/60)/100;
        overTime=(double)Math.round((100*overTime)/60)/100;
        night=(double)Math.round((100*night)/60)/100;
        overNight=(double)Math.round((100*overNight)/60)/100;
        breakHours=(double)Math.round((100*breakHours)/60)/100;

        double totalHours=regular+overTime+night+overNight-breakHours;
        totalHours=(double)Math.round((100*totalHours))/100;

        Map<String,Double> hours= new HashMap<>();

        hours.put("regular",regular);
        hours.put("overtime",overTime);
        hours.put("night",night);
        hours.put("overNight",overNight);
        hours.put("breakHours",breakHours);
        hours.put("totalHours",totalHours);
        return hours;
    }

    public List<FixRecordDTO> mapFixRecord(List<WorkingHour> autoLeaveRecords) {
        List<FixRecordDTO> records=new ArrayList<>();
        for(WorkingHour workingHour : autoLeaveRecords) {
            FixRecordDTO fixRecordDTO=new FixRecordDTO();
            fixRecordDTO.setEmployeeId(workingHour.getEmployee().getId());
            fixRecordDTO.setWorkingHourId(workingHour.getId());
            fixRecordDTO.setDate(workingHour.getStartTime().toLocalDate());
            fixRecordDTO.setStartTime(workingHour.getStartTime());
            fixRecordDTO.setEndTime(workingHour.getEndTime());
            records.add(fixRecordDTO);
        }
        return records;
    }

    public EmployeeHistoryDTO mapEmployeeHistoryDTO(WorkingHour workingHour,
                                                    List<WorkingHourSegment> segments, int baseSalary) {
        EmployeeHistoryDTO history= new EmployeeHistoryDTO();
        history.setDate(workingHour.getStartTime().toLocalDate());
        history.setStartTime(workingHour.getStartTime().toLocalTime());
        if(workingHour.getEndTime()!=null)
            history.setEndTime(workingHour.getEndTime().toLocalTime());
        double sal=0;
        for(WorkingHourSegment segment : segments) {
            Optional<SegmentType> type=segmentTypeRepository.findById(segment.getSegmentType().getId());
            if(type.isPresent()) {
                double magnification=type.get().getMagnification();
                double hour = (double)Math.round((100*segment.getDuration())/60)/100;
                sal+=magnification*hour*baseSalary;
            }
        }
        history.setCalculatedSalary(sal);

        return history;
    }
}
