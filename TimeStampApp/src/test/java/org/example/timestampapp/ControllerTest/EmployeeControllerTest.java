package org.example.timestampapp.ControllerTest;

import org.example.timestampapp.Controller.WebController.EmployeeController;
import org.example.timestampapp.Model.DTO.EmployeeDTO;
import org.example.timestampapp.Model.DTO.EmployeeHistoryDTO;
import org.example.timestampapp.Model.DTO.EmployeeStatisticsDTO;
import org.example.timestampapp.Model.DTO.EmployeeStatusDTO;
import org.example.timestampapp.Service.EmployeeService;
import org.example.timestampapp.Service.UserService;
import org.example.timestampapp.TestConfig.TestConfig;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = EmployeeController.class,
        excludeAutoConfiguration = ThymeleafAutoConfiguration.class)
@Import({TestConfig.class})
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private EmployeeService employeeService;
    @MockitoBean
    private UserService userService;

    @WithMockUser(roles = "Employee", username = "testUser")
    @Test
    public void employeeMainTest() throws Exception {
        EmployeeStatusDTO mockEmployee = new EmployeeStatusDTO();
        mockEmployee.setEmployeeId(1L);

        when(employeeService.getEmployeeStatusByEmail("testUser")).thenReturn(mockEmployee);

        mockMvc.perform(get("/employee/main"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("employee"))
                .andExpect(view().name("employee"))
                .andExpect(request().sessionAttribute("employeeId", 1L));
    }

    @WithMockUser(roles = "Employee")
    @Test
    public void employeeStatisticsTest() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1L);
        EmployeeStatisticsDTO mockEmployeeStatistics = new EmployeeStatisticsDTO();
        LocalDate mockDate = LocalDate.of(2026,1,1);

        when(employeeService.getEmployeeWorkingStatistics(1L,2026,1))
                .thenReturn(mockEmployeeStatistics);

        try(MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(mockDate);

            mockMvc.perform(get("/employee/employee-statistics").session(session))
                    .andExpect(status().isOk())
                    .andExpect(view().name("employee_statistics"))
                    .andExpect(model().attributeExists("statistics"));
        }
    }

    @WithMockUser(roles = "Employee")
    @Test
    public void employeeHistoryTest() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1L);
        EmployeeHistoryDTO mockEmployeeHistory = new EmployeeHistoryDTO();
        LocalDate mockDate = LocalDate.of(2026,1,1);

        when(employeeService.getEmployeeHistory(1L,2026,1))
                .thenReturn(List.of(mockEmployeeHistory));

        try(MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(mockDate);

            mockMvc.perform(get("/employee/monthly-history").session(session))
                    .andExpect(status().isOk())
                    .andExpect(view().name("monthly_history"))
                    .andExpect(model().attributeExists("monthlyHistory"));
        }
    }

    @WithMockUser(roles = "Employee")
    @Test
    public void employeePersonalInfoTest() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1L);
        EmployeeDTO mockEmployee = new EmployeeDTO();

        when(employeeService.getEmployeeById(1L))
                .thenReturn(mockEmployee);

        mockMvc.perform(get("/employee/personal-information").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("personal_information"))
                .andExpect(model().attributeExists("employee"));
    }

    @WithMockUser(roles = "Employee")
    @Test
    public void employeePasswordChangeTest() throws Exception {
        mockMvc.perform(get("/employee/password-change"))
                .andExpect(status().isOk())
                .andExpect(view().name("password_change"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithMockUser( roles = "Employee", username = "testUser")
    @Test
    public void changePasswordTest_Success() throws Exception {
        mockMvc.perform(post("/employee/password-change")
                        .with(csrf())
                        .param("currentPassword", "testPassword")
                        .param("newPassword", "test-NewPassword")
                        .param("confirmPassword", "test-NewPassword"))
                .andExpect(status().is3xxRedirection());
        verify(userService).changePassword("testUser","testPassword","test-NewPassword");
    }

    @WithMockUser(roles = "Employee", username = "testUser")
    @Test
    public void changePasswordTest_Fail_InvalidInput() throws Exception {
        mockMvc.perform(post("/employee/password-change")
                        .with(csrf())
                        .param("currentPassword", "testPassword")
                        .param("newPassword", "testNewPassword")
                        .param("confirmPassword", "testNewPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("password_change"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithMockUser(roles = "Employee", username = "testUser")
    @Test
    public void changePasswordTest_Fail_PasswordMismatch() throws Exception {
        mockMvc.perform(post("/employee/password-change")
                        .with(csrf())
                        .param("currentPassword", "testPassword")
                        .param("newPassword", "test-NewPassword")
                        .param("confirmPassword", "test-DifferentPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("password_change"))
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @WithMockUser(roles = "Employee", username = "testUser")
    @Test
    public void changePasswordTest_Fail_WrongCurrentPassword() throws Exception {
        doThrow(new InputMismatchException("Current password mismatch"))
                .when(userService)
                .changePassword("testUser","testPassword","test-NewPassword");

        mockMvc.perform(post("/employee/password-change")
                        .with(csrf())
                        .param("currentPassword", "testPassword")
                        .param("newPassword", "test-NewPassword")
                        .param("confirmPassword", "test-NewPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("password_change"))
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("errorMessage"));

    }

    @WithMockUser(roles = "Employee")
    @Test
    public void timestampTest() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1L);

        mockMvc.perform(post("/employee/time-stamp")
                        .with(csrf())
                        .param("type","work")
                        .session(session))
                .andExpect(status().is3xxRedirection());

        verify(employeeService).workTimeStamp(1L);
    }
}
