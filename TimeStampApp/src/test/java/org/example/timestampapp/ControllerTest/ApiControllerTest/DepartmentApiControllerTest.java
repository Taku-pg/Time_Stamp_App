package org.example.timestampapp.ControllerTest.ApiControllerTest;

import org.example.timestampapp.Controller.ApiController.DepartmentApiController;
import org.example.timestampapp.Model.DTO.DepartmentStatisticsDTO;
import org.example.timestampapp.Service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DepartmentApiController.class)
public class DepartmentApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DepartmentService departmentService;

    @WithMockUser( roles = "ADMIN")
    @Test
    public void getDepartmentNameTest() throws Exception {
        when(departmentService.getAllDepartmentName()).thenReturn(List.of("mockDept"));
        mockMvc.perform(get("/api/department/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("mockDept"));
    }

    @WithMockUser( roles = "ADMIN")
    @Test
    public void getDepartmentStatisticsTest_Found() throws Exception {
        DepartmentStatisticsDTO mockDepartmentStatisticsDTO = new DepartmentStatisticsDTO();
        mockDepartmentStatisticsDTO.setName("mockDept");
        mockDepartmentStatisticsDTO.setYear(2020);
        mockDepartmentStatisticsDTO.setMonth(12);

        when(departmentService.getDeptStatistics("mockDept",2020,12))
                .thenReturn(mockDepartmentStatisticsDTO);

        mockMvc.perform(get("/api/department/mockDept/statistics/2020/12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("mockDept"));
    }

    @WithMockUser( roles = "ADMIN")
    @Test
    public void getDepartmentStatisticsTest_NotFound() throws Exception {
        when(departmentService.getDeptStatistics("mockDept",2020,12))
                .thenReturn(null);

        mockMvc.perform(get("/api/department/mockDept/statistics/2020/12"))
                .andExpect(status().isNotFound());
    }

}
