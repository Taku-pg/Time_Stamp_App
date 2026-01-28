package ServiceTest;

import org.example.timestampapp.Model.DTO.DepartmentStatisticsDTO;
import org.example.timestampapp.Model.Entity.Department;
import org.example.timestampapp.Repository.DepartmentRepository;
import org.example.timestampapp.Service.DepartmentService;
import org.example.timestampapp.Service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {
    @Mock
    DepartmentRepository departmentRepository;
    @Mock
    StatisticsService statisticsService;

    @InjectMocks
    DepartmentService departmentService;

    @Test
    void getDepartmentByName_Find(){
        Department mockDepartment = new Department();
        mockDepartment.setId(1L);
        mockDepartment.setName("Manager");
        when(departmentRepository.findDepartmentByName("Manager")).thenReturn(Optional.of(mockDepartment));

        Department department = departmentService.getDepartment("Manager");

        assertEquals(1L, department.getId());
        verify(departmentRepository).findDepartmentByName("Manager");
    }

    @Test
    void getDepartmentByName_Not_Find(){
        when(departmentRepository.findDepartmentByName("Mock")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, ()-> departmentService.getDepartment("Mock") );
        verify(departmentRepository).findDepartmentByName("Mock");
    }

    @Test
    void getAllDepartmentNameTest(){
        Department mockManagerDepartment = new Department();
        mockManagerDepartment.setId(1L);
        mockManagerDepartment.setName("Manager");

        Department mockKitchenDepartment = new Department();
        mockKitchenDepartment.setId(2L);
        mockKitchenDepartment.setName("Kitchen");

        when(departmentRepository.findAll()).thenReturn(List.of(mockManagerDepartment,mockKitchenDepartment));

        List<String> departments = departmentService.getAllDepartmentName();

        assertEquals(2, departments.size());
        verify(departmentRepository).findAll();
    }

    @Test
    void getDeptNameTest_Find(){
        Department mockManagerDepartment = new Department();
        mockManagerDepartment.setId(1L);
        mockManagerDepartment.setName("Manager");

        when(departmentRepository.findAll()).thenReturn(List.of(mockManagerDepartment));
        String departmentName = departmentService.getDeptName();
        assertEquals("Manager",departmentName);
        verify(departmentRepository).findAll();
    }

    @Test
    void getDeptNameTest_Not_Find(){
        when(departmentRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(NoSuchElementException.class, ()-> departmentService.getDeptName());
        verify(departmentRepository).findAll();
    }

    @Test
    void getDeptStatisticsTest(){
        DepartmentStatisticsDTO mockData=new DepartmentStatisticsDTO();
        mockData.setName("Manager");
        mockData.setRegular(480.0);
        mockData.setYear(2025);
        mockData.setMonth(1);
        when(statisticsService.getDeptStatistics("Manager",2025,1)).thenReturn(mockData);

        DepartmentStatisticsDTO departmentStatisticsDTO = departmentService.getDeptStatistics("Manager",2025,1);
        assertEquals("Manager",departmentStatisticsDTO.getName());
        verify(statisticsService).getDeptStatistics("Manager",2025,1);
    }

}
