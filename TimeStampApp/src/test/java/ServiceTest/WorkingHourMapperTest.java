package ServiceTest;

import org.example.timestampapp.Model.DTO.EmployeeHistoryDTO;
import org.example.timestampapp.Model.DTO.EmployeeStatisticsDTO;
import org.example.timestampapp.Model.Entity.SegmentType;
import org.example.timestampapp.Model.Entity.WorkingHour;
import org.example.timestampapp.Model.Entity.WorkingHourSegment;
import org.example.timestampapp.Repository.SegmentTypeRepository;
import org.example.timestampapp.Service.WorkingHourMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkingHourMapperTest {
    @Mock
    private SegmentTypeRepository segmentTypeRepository;

    @InjectMocks
    private WorkingHourMapper workingHourMapper;

    @Test
    public void calculateHoursTest(){
        SegmentType mockRegularType = new SegmentType();
        mockRegularType.setId(1L);
        mockRegularType.setName("regular");

        SegmentType mockNightType = new SegmentType();
        mockNightType.setId(2L);
        mockNightType.setName("night");

        when(segmentTypeRepository.findById(1L)).thenReturn(Optional.of(mockRegularType));
        when(segmentTypeRepository.findById(2L)).thenReturn(Optional.of(mockNightType));

        WorkingHourSegment mockRegularWorkingHourSegment = new WorkingHourSegment();
        mockRegularWorkingHourSegment.setId(1L);
        mockRegularWorkingHourSegment.setSegmentType(mockRegularType);
        mockRegularWorkingHourSegment.setDuration(360);

        WorkingHourSegment mockNightWorkingHourSegment = new WorkingHourSegment();
        mockNightWorkingHourSegment.setId(2L);
        mockNightWorkingHourSegment.setSegmentType(mockNightType);
        mockNightWorkingHourSegment.setDuration(100);

        WorkingHour mockWorkingHour1 = new WorkingHour();
        mockWorkingHour1.setId(1L);
        mockWorkingHour1.setSegments(List.of(mockRegularWorkingHourSegment,mockNightWorkingHourSegment));
        mockWorkingHour1.setBreaks(Collections.emptyList());

        WorkingHour mockWorkingHour2 = new WorkingHour();
        mockWorkingHour2.setId(1L);
        mockWorkingHour2.setSegments(List.of(mockRegularWorkingHourSegment,mockNightWorkingHourSegment));
        mockWorkingHour2.setBreaks(Collections.emptyList());

        EmployeeStatisticsDTO result =
                workingHourMapper.mapEmpStatistics(
                        List.of(mockWorkingHour1,mockWorkingHour2),2025,1,1L);

        assertEquals(12,result.getRegular());
        assertEquals(3.33,result.getNightShift());
    }

    @Test
    public void mapEmployeeHistoryDTOTest(){
        SegmentType mockRegularType = new SegmentType();
        mockRegularType.setId(1L);
        mockRegularType.setMagnification(1);

        SegmentType mockNightType = new SegmentType();
        mockNightType.setId(2L);
        mockNightType.setMagnification(1.25);

        SegmentType mockNullType = new SegmentType();
        mockNullType.setId(3L);

        when(segmentTypeRepository.findById(1L)).thenReturn(Optional.of(mockRegularType));
        when(segmentTypeRepository.findById(2L)).thenReturn(Optional.of(mockNightType));
        when(segmentTypeRepository.findById(3L)).thenReturn(Optional.empty());

        WorkingHourSegment mockRegularWorkingHourSegment = new WorkingHourSegment();
        mockRegularWorkingHourSegment.setId(1L);
        mockRegularWorkingHourSegment.setSegmentType(mockRegularType);
        mockRegularWorkingHourSegment.setDuration(360);

        WorkingHourSegment mockNightWorkingHourSegment = new WorkingHourSegment();
        mockNightWorkingHourSegment.setId(2L);
        mockNightWorkingHourSegment.setSegmentType(mockNightType);
        mockNightWorkingHourSegment.setDuration(60);

        WorkingHourSegment mockNullWorkingHourSegment = new WorkingHourSegment();
        mockNullWorkingHourSegment.setSegmentType(mockNullType);

        WorkingHour mockWorkingHour = new WorkingHour();
        mockWorkingHour.setStartTime(LocalDateTime.of(2025,1,1,16,0));
        mockWorkingHour.setEndTime(LocalDateTime.of(2025,1,1,23,0));

        EmployeeHistoryDTO mockEmployeeHistoryDTO =
                workingHourMapper.mapEmployeeHistoryDTO(mockWorkingHour,
                        List.of(mockRegularWorkingHourSegment,
                                mockNightWorkingHourSegment,
                                mockNullWorkingHourSegment),
                        100);

        assertEquals(LocalDate.of(2025,1,1),mockEmployeeHistoryDTO.getDate());
        assertEquals(LocalTime.of(16,0),mockEmployeeHistoryDTO.getStartTime());
        assertEquals(LocalTime.of(23,0),mockEmployeeHistoryDTO.getEndTime());
        assertEquals(725,mockEmployeeHistoryDTO.getCalculatedSalary());

    }
}
