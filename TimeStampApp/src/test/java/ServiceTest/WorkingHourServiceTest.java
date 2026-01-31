package ServiceTest;

import org.example.timestampapp.Model.Entity.*;
import org.example.timestampapp.Repository.WorkingHourRepository;
import org.example.timestampapp.Service.WorkingHourSegmentService;
import org.example.timestampapp.Service.WorkingHourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class WorkingHourServiceTest {
    @Mock
    private WorkingHourRepository workingHourRepository;
    @Mock
    private WorkingHourSegmentService workingHourSegmentService;

    @InjectMocks
    private WorkingHourService workingHourService;

    private final WorkingHour mockWorkingHour = new WorkingHour();

    @BeforeEach
    public void setup() {
        mockWorkingHour.setId(1L);
        mockWorkingHour.setStartTime(LocalDateTime.of(2025,1,1,8,0));
        mockWorkingHour.setEndTime(LocalDateTime.of(2025,1,1,17,0));
        mockWorkingHour.setBreaks(Collections.emptyList());
        mockWorkingHour.setSegments(Collections.emptyList());

        when(workingHourRepository.findById(1L)).thenReturn(Optional.of(mockWorkingHour));
        when(workingHourRepository.findById(2L)).thenReturn(Optional.empty());

        when(workingHourRepository.findCurrentWorkingHourByEmployeeId(1L)).thenReturn(Optional.of(mockWorkingHour));
        when(workingHourRepository.findCurrentWorkingHourByEmployeeId(2L)).thenReturn(Optional.empty());
        when(workingHourRepository.save(any(WorkingHour.class))).thenAnswer(i->i.getArgument(0));
    }

    @Test
    public void updateWorkingHourTest(){
        //set break
        Break mockBreak = new Break();
        mockBreak.setEndTime(LocalDateTime.of(2025,1,3,0,0));
        mockWorkingHour.setBreaks(new ArrayList<>(List.of(mockBreak)));

        //set segment
        WorkingHourSegment mockWorkingHourSegment = new WorkingHourSegment();
        mockWorkingHour.setSegments(new ArrayList<>(List.of(mockWorkingHourSegment)));

        LocalDateTime newStart =  LocalDateTime.of(2025,1,2,9,0);
        LocalDateTime newEnd =  LocalDateTime.of(2025,1,2,14,0);

        assertThrows(NoSuchElementException.class,
                () -> workingHourService.updateWorkingHour(2L,newStart,newEnd));

        workingHourService.updateWorkingHour(1L,newStart,newEnd);

        ArgumentCaptor<WorkingHour> captor = ArgumentCaptor.forClass(WorkingHour.class);
        verify(workingHourRepository).save(captor.capture());
        WorkingHour updatedWorkingHour = captor.getValue();

        assertEquals(newStart,updatedWorkingHour.getStartTime());
        assertEquals(newEnd,updatedWorkingHour.getEndTime());
        assertEquals(0,updatedWorkingHour.getBreaks().size());
    }

    @Test
    public void leaveTimeStampTest(){
        assertThrows(NoSuchElementException.class,
                ()->workingHourService.leaveTimeStamp(2L));

        workingHourService.leaveTimeStamp(1L);
        ArgumentCaptor<WorkingHour> captor = ArgumentCaptor.forClass(WorkingHour.class);
        verify(workingHourRepository).save(captor.capture());
        WorkingHour updatedWorkingHour = captor.getValue();

        assertFalse(updatedWorkingHour.isAutoLeave());
        assertNotNull(updatedWorkingHour.getEndTime());
    }

    @Test
    public void break_BackTimeStampTest(){
        assertThrows(NoSuchElementException.class,
                ()->workingHourService.breakTimeStamp(2L));
        assertThrows(NoSuchElementException.class,
                ()->workingHourService.backTimeStamp(2L));
    }

    @Test
    public void autoCheckOutTest(){
        Employee emp = new Employee();
        mockWorkingHour.setEndTime(null);
        mockWorkingHour.setEmployee(emp);

        when(workingHourRepository.findAllByEndTimeIsNull()).thenReturn(List.of(mockWorkingHour));

        Status leaveStatus = new Status();
        leaveStatus.setType("Leave");

        workingHourService.autoCheckOut(leaveStatus);
        ArgumentCaptor<WorkingHour> captor = ArgumentCaptor.forClass(WorkingHour.class);
        verify(workingHourRepository).save(captor.capture());
        WorkingHour autoCheckOutWorkingHour = captor.getValue();

        assertTrue(autoCheckOutWorkingHour.isAutoLeave());
        assertNotNull(autoCheckOutWorkingHour.getEndTime());
        assertEquals("Leave",emp.getStatus().getType());
        assertNotNull(emp.getLastUpdate());
    }
}
