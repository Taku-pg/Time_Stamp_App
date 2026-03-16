package org.example.timestampapp.Controller.ApiController;

import org.example.timestampapp.Model.DTO.EmployeeDTO;
import org.example.timestampapp.Model.DTO.EmployeeHistoryDTO;
import org.example.timestampapp.Model.DTO.EmployeeMonthlyHistoryDTO;
import org.example.timestampapp.Model.DTO.EmployeeStatisticsDTO;
import org.example.timestampapp.Service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/employee")
public class EmployeeApiController {
    private final EmployeeService employeeService;
    public EmployeeApiController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable long id) {
        try{
            EmployeeDTO employee = employeeService.getEmployeeById(id);
            return ResponseEntity.ok(employee);
        }catch (NoSuchElementException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/statistics/{year}/{month}")
    public ResponseEntity<EmployeeStatisticsDTO> getEmployeeWorkingStatistics(@PathVariable long id,
                                                                              @PathVariable int year,
                                                                              @PathVariable int month) {
        EmployeeStatisticsDTO statistics=employeeService.getEmployeeWorkingStatistics(id,year,month);
        if(statistics==null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/{id}/history/{year}/{month}")
    public ResponseEntity<EmployeeMonthlyHistoryDTO> getEmployeeWorkingHistory(@PathVariable long id,
                                                                              @PathVariable int year,
                                                                              @PathVariable int month) {
        List<EmployeeHistoryDTO> history=employeeService.getEmployeeHistory(id,year,month);
        if(history==null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        EmployeeMonthlyHistoryDTO employeeMonthlyHistoryDTO=new EmployeeMonthlyHistoryDTO(year,month,history);
        return ResponseEntity.ok(employeeMonthlyHistoryDTO);
    }
}

