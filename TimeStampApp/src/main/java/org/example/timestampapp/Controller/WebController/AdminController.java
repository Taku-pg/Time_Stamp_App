package org.example.timestampapp.Controller.WebController;

import jakarta.validation.Valid;
import org.example.timestampapp.Model.DTO.DepartmentStatisticsDTO;
import org.example.timestampapp.Model.DTO.EmployeeDTO;
import org.example.timestampapp.Model.DTO.EmployeeStatisticsDTO;
import org.example.timestampapp.Model.DTO.FixRecordDTO;
import org.example.timestampapp.Model.Entity.Department;
import org.example.timestampapp.Model.Entity.Status;
import org.example.timestampapp.Service.DepartmentService;
import org.example.timestampapp.Service.EmployeeService;
import org.example.timestampapp.Service.StatusService;
import org.example.timestampapp.Service.WorkingHourService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final WorkingHourService workingHourService;
    private final StatusService statusService;

    public AdminController(EmployeeService employeeService,
                           DepartmentService departmentService,
                           WorkingHourService workingHourService,
                           StatusService statusService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.workingHourService = workingHourService;
        this.statusService = statusService;
    }

    @GetMapping("/main")
    public String adminMain() {
        return "admin";
    }

    @GetMapping("/register")
    public String register(Model model) {
        List<String> departments=departmentService.getAllDepartmentName();
        model.addAttribute("departments", departments);
        model.addAttribute("employee", new EmployeeDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute(name = "employee") EmployeeDTO employeeDTO,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<String> departments=departmentService.getAllDepartmentName();
            model.addAttribute("departments", departments);
            model.addAttribute("employee", employeeDTO);
            return "register";
        }
        Department department=departmentService.getDepartment(employeeDTO.getDepartment());
        Status status=statusService.getStatus("Leave");
        if(status==null || department==null) {
            return "redirect:/admin/register";
        }
        employeeService.registerEmployee(employeeDTO,department,status);
        return "admin";
    }

    @GetMapping("/employee-list")
    public String getEmployeeList(Model model) {
        List<EmployeeDTO> employees=employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "all_employee";
    }

    @GetMapping("/employee-statistics")
    public String getEmployeeStatistic(Model model, @RequestParam(name="employeeId") Long employeeId) {
        int year= LocalDate.now().getYear();
        int month= LocalDate.now().getMonthValue();
        EmployeeStatisticsDTO statistics=employeeService.getEmployeeWorkingStatistics(employeeId,year,month);
        model.addAttribute("statistics", statistics);
        return "employee_statistics";
    }

    @GetMapping("/department-statistics")
    public String getDepartmentStatistic(Model model) {
        int year= LocalDate.now().getYear();
        int month= LocalDate.now().getMonthValue();
        String dName=departmentService.getDeptName();
        DepartmentStatisticsDTO statistics=departmentService.getDeptStatistics(dName,year,month);
        model.addAttribute("statistics", statistics);
        List<String> departments=departmentService.getAllDepartmentName();
        model.addAttribute("departments", departments);
        return "department_statistics";
    }

    @GetMapping("/modify-employee")
    public String modifyEmployee(Model model, @ModelAttribute EmployeeDTO employee) {
        model.addAttribute("employee", employee);
        List<String> departments=departmentService.getAllDepartmentName();
        model.addAttribute("departments", departments);
        return "modify_employee";
    }

    @PostMapping("/modify-employee")
    public String modifyEmployee(@Valid @ModelAttribute EmployeeDTO employee,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            List<String> departments=departmentService.getAllDepartmentName();
            model.addAttribute("departments", departments);
            model.addAttribute("employee", employee);
            return "register";
        }
        employeeService.updateEmployee(employee);
        return "admin";
    }

    @GetMapping("/modify-record")
    public String modifyRecord(Model model) {
        List<FixRecordDTO> records=workingHourService.getFixRecord();
        if(!records.isEmpty())
            model.addAttribute("records", records);
        return "modify_record";
    }

    @PostMapping("/modify-record")
    public String modifyRecord(Model model,
                               @RequestParam(name = "workingHourId") Long workingHourId,
                               @RequestParam(name = "startTime") LocalDateTime startTime,
                               @RequestParam(name = "endTime") LocalDateTime endTime) {
        workingHourService.updateWorkingHour(workingHourId,startTime,endTime);
        List<FixRecordDTO> records=workingHourService.getFixRecord();
        if(!records.isEmpty())
            model.addAttribute("records", records);
        return "modify_record";
    }


    @PostMapping("/delete-employee")
    public String deleteEmployee(@RequestParam(name = "employeeId") Long employeeId) {
        employeeService.deleteEmployee(employeeId);
        return "admin";
    }
}
