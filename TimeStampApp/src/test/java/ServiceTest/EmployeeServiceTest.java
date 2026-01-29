package ServiceTest;

import org.example.timestampapp.Model.DTO.EmployeeDTO;
import org.example.timestampapp.Model.Entity.Department;
import org.example.timestampapp.Model.Entity.Employee;
import org.example.timestampapp.Model.Entity.Status;
import org.example.timestampapp.Model.Entity.User;
import org.example.timestampapp.Repository.EmployeeRepository;
import org.example.timestampapp.Repository.UserRepository;
import org.example.timestampapp.Service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private StatisticsService statisticsService;
    @Mock
    private StatusService statusService;
    @Mock
    private WorkingHourService workingHourService;
    @Mock
    private PasswordEncoder bcryptPasswordEncoder;



    @InjectMocks
    private EmployeeService employeeService;

    @Test
    public void getEmployeeByIdTest() {
        Employee mockEmployee = new Employee();
        mockEmployee.setId(1L);
        mockEmployee.setFirstName("John");
        mockEmployee.setLastName("Doe");
        EmployeeDTO mockEmployeeDTO = new EmployeeDTO();
        mockEmployeeDTO.setEmployeeId(1L);
        mockEmployeeDTO.setFirstName("John");
        mockEmployeeDTO.setLastName("Doe");

        when(employeeRepository.getEmployeeById(1L)).thenReturn(Optional.of(mockEmployee));
        when(employeeMapper.map(mockEmployee)).thenReturn(mockEmployeeDTO);

        EmployeeDTO employeeDTO = employeeService.getEmployeeById(1L);

        assertEquals("John", employeeDTO.getFirstName());
        verify(employeeRepository).getEmployeeById(1L);

    }

    @Test
    public void getEmployeeByEmployeeIdTest_Not_Find() {
        when(employeeRepository.getEmployeeById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, ()->employeeService.getEmployeeById(1L));
        verify(employeeRepository).getEmployeeById(1L);
    }

    @Test
    public void getAllEmployeeTest() {
        Employee mockEmployee = new Employee();
        mockEmployee.setId(1L);
        when(employeeRepository.findAll()).thenReturn(List.of(mockEmployee));
        EmployeeDTO mockEmployeeDTO = new EmployeeDTO();
        mockEmployeeDTO.setEmployeeId(1L);
        when(employeeMapper.map(mockEmployee)).thenReturn(mockEmployeeDTO);

        List<EmployeeDTO> employeeDTOS = employeeService.getAllEmployees();

        assertEquals(employeeDTOS,List.of(mockEmployeeDTO));
        verify(employeeRepository).findAll();
    }

    @Test
    public void registerEmployeeTest() {
        EmployeeDTO mockEmployeeDTO = new EmployeeDTO();
        mockEmployeeDTO.setEmployeeId(1L);
        mockEmployeeDTO.setFirstName("John");
        mockEmployeeDTO.setLastName("Doe");
        mockEmployeeDTO.setEmail("mockEmail");
        mockEmployeeDTO.setSalary(1000);

        Department mockDepartment = new Department();
        mockDepartment.setName("mockDepartment");

        Status mockStatus = new Status();

        when(bcryptPasswordEncoder.encode("Password")).thenReturn("Password");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        //when
        employeeService.registerEmployee(mockEmployeeDTO,mockDepartment,mockStatus);

        //then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser =  userCaptor.getValue();
        assertEquals("mockEmail",savedUser.getUsername());

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeCaptor.capture());
        Employee savedEmployee = employeeCaptor.getValue();
        assertEquals("John",savedEmployee.getFirstName());

        assertEquals(savedEmployee,savedUser.getEmployee());
        assertEquals(savedUser,savedEmployee.getUser());
    }

    @Test
    public void updateEmployeeTest() {
        EmployeeDTO mockEmployeeDTO = new EmployeeDTO();

        Employee mockEmployee = new Employee();
        mockEmployee.setId(1L);
        mockEmployee.setFirstName("John");
        mockEmployee.setLastName("Doe");
        mockEmployee.setEmail("mockEmail");
        mockEmployee.setSalary(1000);

        when(employeeMapper.map(mockEmployeeDTO)).thenReturn(mockEmployee);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        employeeService.updateEmployee(mockEmployeeDTO);

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeCaptor.capture());
        Employee updatedEmployee = employeeCaptor.getValue();
        assertEquals("John",updatedEmployee.getFirstName());
    }

    @Test
    public void timestampTest(){
        //mock employee
        Employee  mockEmployee = new Employee();
        mockEmployee.setId(1L);
        mockEmployee.setFirstName("John");
        mockEmployee.setLastName("Doe");
        mockEmployee.setEmail("mockEmail");
        mockEmployee.setSalary(1000);

        //mock status
        Status mockStatus = new Status();
        mockStatus.setId(1L);
        mockStatus.setType("Work");
        mockEmployee.setStatus(mockStatus);

        when(employeeRepository.getEmployeeById(1L)).thenReturn(Optional.of(mockEmployee));
        when(statusService.getStatus("Work")).thenReturn(mockStatus);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        employeeService.workTimeStamp(1L);

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeCaptor.capture());
        Employee savedEmployee = employeeCaptor.getValue();

        assertEquals(mockStatus,savedEmployee.getStatus());
        assertNotNull(savedEmployee.getLastUpdate());
    }
}
