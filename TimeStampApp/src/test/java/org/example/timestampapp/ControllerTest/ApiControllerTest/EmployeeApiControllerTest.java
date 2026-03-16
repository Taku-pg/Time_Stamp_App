package org.example.timestampapp.ControllerTest.ApiControllerTest;

import org.example.timestampapp.Controller.ApiController.EmployeeApiController;
import org.example.timestampapp.Model.DTO.EmployeeDTO;
import org.example.timestampapp.Model.DTO.EmployeeHistoryDTO;
import org.example.timestampapp.Model.DTO.EmployeeStatisticsDTO;
import org.example.timestampapp.Service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest( controllers = EmployeeApiController.class)
public class EmployeeApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private EmployeeService employeeService;

    @WithMockUser( roles = "ADMIN")
    @Test
    public void getEmployeeByIdTest_Found() throws Exception {
        EmployeeDTO mockEmployeeDTO = new EmployeeDTO();
        mockEmployeeDTO.setFirstName("John");
        when(employeeService.getEmployeeById(1L)).thenReturn(mockEmployeeDTO);

        mockMvc.perform(get("/api/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @WithMockUser( roles = "ADMIN")
    @Test
    public void getEmployeeByIdTest_NotFound() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenThrow(new NoSuchElementException());

        mockMvc.perform(get("/api/employee/1"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser( roles = "ADMIN")
    @Test
    public void getAllEmployeeWorkingStatisticsTest_Found() throws Exception {
        EmployeeStatisticsDTO mockEmployeeStatisticsDTO = new EmployeeStatisticsDTO();
        mockEmployeeStatisticsDTO.setEmployeeId(1L);
        mockEmployeeStatisticsDTO.setYear(2020);
        mockEmployeeStatisticsDTO.setMonth(12);

        when(employeeService.getEmployeeWorkingStatistics(1L,2020,12))
                .thenReturn(mockEmployeeStatisticsDTO);

        mockMvc.perform(get("/api/employee/1/statistics/2020/12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(1));
    }

    @WithMockUser( roles = "ADMIN")
    @Test
    public void getAllEmployeeWorkingStatisticsTest_NotFound() throws Exception {
        when(employeeService.getEmployeeWorkingStatistics(1L,2020,12))
                .thenReturn(null);

        mockMvc.perform(get("/api/employee/1/statistics/2020/12"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser( roles = "ADMIN")
    @Test
    public void getAllEmployeeHistoryTest_Found() throws Exception {
        EmployeeHistoryDTO mockEmployeeHistoryDTO = new EmployeeHistoryDTO();
        when(employeeService.getEmployeeHistory(1L,2020,12))
                .thenReturn(List.of(mockEmployeeHistoryDTO));

        mockMvc.perform(get("/api/employee/1/history/2020/12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2020));
    }

    @WithMockUser( roles = "ADMIN")
    @Test
    public void getAllEmployeeHistoryTest_NotFound() throws Exception {
        when(employeeService.getEmployeeHistory(1L,2020,12))
                .thenReturn(null);

        mockMvc.perform(get("/api/employee/1/history/2020/12"))
                .andExpect(status().isNotFound());
    }

}
