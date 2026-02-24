package org.example.timestampapp.ControllerTest;

import org.example.timestampapp.Controller.WebController.AdminController;
import org.example.timestampapp.Model.DTO.DepartmentStatisticsDTO;
import org.example.timestampapp.Model.DTO.EmployeeStatisticsDTO;
import org.example.timestampapp.Model.Entity.Department;
import org.example.timestampapp.Model.Entity.Status;
import org.example.timestampapp.Repository.EmployeeRepository;
import org.example.timestampapp.Service.DepartmentService;
import org.example.timestampapp.Service.EmployeeService;
import org.example.timestampapp.Service.StatusService;
import org.example.timestampapp.Service.WorkingHourService;
import org.example.timestampapp.TestConfig.TestConfig;
import org.example.timestampapp.Validation.UniqueEmailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class,
            excludeAutoConfiguration = ThymeleafAutoConfiguration.class)
@Import({UniqueEmailValidator.class, TestConfig.class})
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private EmployeeService employeeService;
    @MockitoBean
    private DepartmentService departmentService;
    @MockitoBean
    private WorkingHourService workingHourService;
    @MockitoBean
    private StatusService statusService;
    @MockitoBean
    private EmployeeRepository  employeeRepository;

    @BeforeEach
    public void setup(){
        when(employeeRepository.findEmployeeByEmail("test@mail.com")).thenReturn(Optional.empty());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void registerGetTest() throws Exception{
        when(departmentService.getAllDepartmentName()).thenReturn(List.of("mock","test"));

        mockMvc.perform(get("/admin/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("employee"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void registerPostTest() throws Exception{
        Department mockDepartment = new Department();
        Status mockStatus = new Status();
        when(departmentService.getDepartment("mockDept")).thenReturn(mockDepartment);
        when(statusService.getStatus("Leave")).thenReturn(mockStatus);
        when(employeeRepository.findEmployeeByEmail("test@mail.com")).thenReturn(Optional.empty());


        mockMvc.perform(post("/admin/register")
                        .with(csrf())
                        .param("firstName","mock")
                        .param("lastName", "test")
                        .param("email", "test@mail.com")
                        .param("salary", "30")
                        .param("department", "mockDept"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));

        verify(employeeService).registerEmployee(any(),any(),any());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void registerPostTest_Failure_InvalidInput() throws Exception{
        mockMvc.perform(post("/admin/register")
                        .with(csrf())
                        .param("firstName","")
                        .param("lastName", "test")
                        .param("email", "test@mail.com")
                        .param("salary", "30")
                        .param("department", "mockDept"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("employee","firstName"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("employee"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void registerPostTest_Failure_NullDeptAndStatus() throws Exception{
        when(departmentService.getDepartment("mockDept")).thenReturn(null);
        when(statusService.getStatus("Leave")).thenReturn(null);

        mockMvc.perform(post("/admin/register")
                        .with(csrf())
                        .param("firstName","mock")
                        .param("lastName", "test")
                        .param("email", "test@mail.com")
                        .param("salary", "30")
                        .param("department", "mockDept"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/register"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void getEmployeeListTest() throws Exception{
        when(employeeService.getAllEmployees()).thenReturn(List.of());

        mockMvc.perform(get("/admin/employee-list"))
                .andExpect(status().isOk())
                .andExpect(view().name("all_employee"))
                .andExpect(model().attributeExists("employees"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void getEmployeeStatisticTest() throws Exception{
        EmployeeStatisticsDTO mockStatisticDTO = new EmployeeStatisticsDTO();
        int mockYear = LocalDate.now().getYear();
        int mockMonth = LocalDate.now().getMonthValue();
        when(employeeService
                .getEmployeeWorkingStatistics(1L,mockYear,mockMonth))
                .thenReturn(mockStatisticDTO);

        mockMvc.perform(get("/admin/employee-statistics").
                        param("employeeId","1"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee_statistics"))
                .andExpect(model().attributeExists("statistics"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void getDepartmentStatisticTest() throws Exception{
        int mockYear = LocalDate.now().getYear();
        int mockMonth = LocalDate.now().getMonthValue();

        String mockDName="mock";
        DepartmentStatisticsDTO mockDepartmentStatisticsDTO = new DepartmentStatisticsDTO();

        when(departmentService.getDeptName()).thenReturn(mockDName);
        when(departmentService
                .getDeptStatistics(mockDName,mockYear,mockMonth))
                .thenReturn(mockDepartmentStatisticsDTO);
        when(departmentService.getAllDepartmentName())
                .thenReturn(List.of());


        mockMvc.perform(get("/admin/department-statistics"))
                .andExpect(status().isOk())
                .andExpect(view().name("department_statistics"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("departments"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void modifyEmployeeGetTest() throws Exception{
        when(departmentService.getAllDepartmentName()).thenReturn(List.of());

        mockMvc.perform(get("/admin/modify-employee")
                        .param("employeeId","1")
                        .param("firstName","mock")
                        .param("lastName", "test")
                        .param("email", "test@mail.com")
                        .param("salary", "30")
                        .param("department", "mockDept"))
                .andExpect(status().isOk())
                .andExpect(view().name("modify_employee"))
                .andExpect(model().attributeExists("employee"))
                .andExpect(model().attributeExists("departments"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void modifyEmployeePostTest() throws Exception{
        mockMvc.perform(post("/admin/modify-employee")
                        .with(csrf())
                        .param("employeeId","1")
                        .param("firstName","mock")
                        .param("lastName", "test")
                        .param("email", "test@mail.com")
                        .param("salary", "30")
                        .param("department", "mockDept"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));
        verify(employeeService).updateEmployee(any());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void modifyEmployeePostTest_Failure_InvalidInput() throws Exception{
        mockMvc.perform(post("/admin/modify-employee")
                        .with(csrf())
                        .param("employeeId","1")
                        .param("firstName","")
                        .param("lastName", "test")
                        .param("email", "test@mail.com")
                        .param("salary", "30")
                        .param("department", "mockDept"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("employeeDTO", "firstName"))
                .andExpect(view().name("register"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void deleteEmployeeTest() throws Exception{
        mockMvc.perform(post("/admin/delete-employee")
                    .with(csrf())
                    .param("employeeId","1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));

        verify(employeeService).deleteEmployee(any());
    }
}
