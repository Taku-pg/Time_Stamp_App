package org.example.timestampapp.Service;

import org.example.timestampapp.Model.DTO.EmployeeDTO;
import org.example.timestampapp.Model.DTO.EmployeeHistoryDTO;
import org.example.timestampapp.Model.DTO.EmployeeStatisticsDTO;
import org.example.timestampapp.Model.DTO.EmployeeStatusDTO;
import org.example.timestampapp.Model.Entity.*;
import org.example.timestampapp.Repository.EmployeeRepository;
import org.example.timestampapp.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;
    private final StatisticsService statisticsService;
    private final StatusService statusService;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final WorkingHourService workingHourService;

    public EmployeeService(EmployeeRepository employeeRepository,
                           UserRepository userRepository,
                           EmployeeMapper employeeMapper,
                           StatisticsService statisticsService,
                           StatusService statusService,
                           PasswordEncoder bCryptPasswordEncoder,
                           WorkingHourService workingHourService) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.employeeMapper = employeeMapper;
        this.statisticsService = statisticsService;
        this.statusService = statusService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.workingHourService = workingHourService;
    }

    public EmployeeDTO getEmployeeById(long id) {
        Employee employee = employeeRepository
                .getEmployeeById(id)
                .orElseThrow(()->new NoSuchElementException("Employee with id " + id + " not found"));
        return employeeMapper.map(employee);
    }

    public EmployeeStatusDTO getEmployeeStatusByEmail(String email) {
        Employee employee=employeeRepository
                .findEmployeeByEmail(email)
                .orElseThrow(()->new NoSuchElementException("Employee with email " + email + " not found"));
        return employeeMapper.mapEmployeeStatusDTO(employee);
    }

    public List<EmployeeDTO> getAllEmployees() {
        Iterable<Employee> employees=employeeRepository.findAll();
        List<EmployeeDTO> employeeDTOS=new ArrayList<>();

        for (Employee employee : employees) {
            employeeDTOS.add(employeeMapper.map(employee));
        }
        return employeeDTOS;
    }

    public EmployeeStatisticsDTO getEmployeeWorkingStatistics(Long employeeId, int year, int month) {
        return statisticsService.getWorkingHourStatistics(employeeId,year,month);
    }

    public List<EmployeeHistoryDTO> getEmployeeHistory(Long employeeId, int year, int month) {
        return workingHourService.getEmployeeHistory(employeeId,year,month);
    }

    @Transactional
    public void registerEmployee(EmployeeDTO employeeDTO, Department department, Status status) {
        String rawPassword = "Password";
        String hashedPassword = bCryptPasswordEncoder.encode(rawPassword);
        User user =new User(employeeDTO.getEmail(),hashedPassword);
        user=userRepository.save(user);

        Employee employee = new Employee(employeeDTO.getFirstName(),
                                         employeeDTO.getLastName(),
                                         employeeDTO.getEmail(),
                                         employeeDTO.getSalary(),
                                         department,
                                         user,
                                         status);
        user.setEmployee(employee);
        employeeRepository.save(employee);
    }

    @Transactional
    public void updateEmployee(EmployeeDTO employeeDTO) {
        employeeRepository.save(employeeMapper.map(employeeDTO));
    }

    @Transactional
    public void deleteEmployee(Long employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    @Transactional
    public void workTimeStamp(Long employeeId) {
        Employee employee = employeeRepository
                .getEmployeeById(employeeId)
                .orElseThrow(()->new NoSuchElementException("Employee with id " + employeeId + " not found"));
        workingHourService.workTimeStamp(employee);
        timeStamp(employee,"Work");
    }

    @Transactional
    public void leaveTimeStamp(Long employeeId) {
        Employee employee = employeeRepository
                .getEmployeeById(employeeId)
                .orElseThrow(()->new NoSuchElementException("Employee with id " + employeeId + " not found"));
        workingHourService.leaveTimeStamp(employeeId);
        timeStamp(employee,"Leave");
    }

    @Transactional
    public void breakTimeStamp(Long employeeId) {
        Employee employee = employeeRepository
                .getEmployeeById(employeeId)
                .orElseThrow(()->new NoSuchElementException("Employee with id " + employeeId + " not found"));
        workingHourService.breakTimeStamp(employeeId);
        timeStamp(employee,"Break");
    }

    @Transactional
    public void backTimeStamp(Long employeeId) {
        Employee employee = employeeRepository
                .getEmployeeById(employeeId)
                .orElseThrow(()->new NoSuchElementException("Employee with id " + employeeId + " not found"));
        workingHourService.backTimeStamp(employeeId);
        timeStamp(employee,"Work");
    }


    private void timeStamp(Employee employee,String type){
        Status status=statusService.getStatus(type);
        employee.setStatus(status);
        employee.setLastUpdate(LocalDateTime.now());
        employeeRepository.save(employee);
    }
}

