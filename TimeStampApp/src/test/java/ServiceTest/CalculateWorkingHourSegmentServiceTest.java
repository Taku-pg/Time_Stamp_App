package ServiceTest;

import org.example.timestampapp.Model.Entity.Break;
import org.example.timestampapp.Model.Entity.SegmentType;
import org.example.timestampapp.Model.Entity.WorkingHour;
import org.example.timestampapp.Repository.SegmentTypeRepository;
import org.example.timestampapp.Service.CalculateWorkingHourSegmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CalculateWorkingHourSegmentServiceTest {
    @Mock
    SegmentTypeRepository segmentTypeRepository;

    @InjectMocks
    private CalculateWorkingHourSegmentService calculateWorkingHourSegmentService;

    @Test
    void calculateWorkingHourSegment_RegularWithoutBreak() {
        WorkingHour mockWorkingHour = new WorkingHour();
        mockWorkingHour.setStartTime(LocalDateTime.of(2025,1,1,9,0));
        mockWorkingHour.setEndTime(LocalDateTime.of(2025,1,1,17,0));
        mockWorkingHour.setSegments(new ArrayList<>());
        mockWorkingHour.setBreaks(List.of());

        SegmentType mockRegularType = new SegmentType();
        mockRegularType.setName("regular");
        when(segmentTypeRepository.findSegmentTypeByName("regular")).thenReturn(Optional.of(mockRegularType));


        calculateWorkingHourSegmentService.calculateWorkingHourSegment(mockWorkingHour, mockWorkingHour.getStartTime(), mockWorkingHour.getEndTime());

        assertEquals(1, mockWorkingHour.getSegments().size());
        assertEquals(480, mockWorkingHour.getSegments().getFirst().getDuration());
        assertEquals("regular", mockWorkingHour.getSegments().getFirst().getSegmentType().getName());
    }

    @Test
    void calculateWorkingHourSegment_RegularWithBreak() {
        WorkingHour mockWorkingHour = new WorkingHour();
        mockWorkingHour.setStartTime(LocalDateTime.of(2025,1,1,9,0));
        mockWorkingHour.setEndTime(LocalDateTime.of(2025,1,1,18,0));
        mockWorkingHour.setSegments(new ArrayList<>());

        Break mockBreak = new Break();
        mockBreak.setStartTime(LocalDateTime.of(2025,1,1,11,0));
        mockBreak.setEndTime(LocalDateTime.of(2025,1,1,12,0));
        mockWorkingHour.setBreaks(List.of(mockBreak));

        SegmentType mockRegularType = new SegmentType();
        mockRegularType.setName("regular");
        when(segmentTypeRepository.findSegmentTypeByName("regular")).thenReturn(Optional.of(mockRegularType));
        /*SegmentType mockOverType = new SegmentType();
        mockRegularType.setName("overtime");
        when(segmentTypeRepository.findSegmentTypeByName("overtime")).thenReturn(Optional.of(mockOverType));*/

        calculateWorkingHourSegmentService.calculateWorkingHourSegment(mockWorkingHour, mockWorkingHour.getStartTime(), mockWorkingHour.getEndTime());

        assertEquals(1, mockWorkingHour.getSegments().size());
        assertEquals(480, mockWorkingHour.getSegments().getFirst().getDuration());
        //assertEquals(180, mockWorkingHour.getSegments().get(1).getDuration());
        assertEquals("regular", mockWorkingHour.getSegments().getFirst().getSegmentType().getName());
        //assertEquals("overtime", mockWorkingHour.getSegments().get(1).getSegmentType().getName());
    }

    @Test
    void calculateWorkingHourSegment_NightWithoutBreak() {
        WorkingHour mockWorkingHour = new WorkingHour();
        mockWorkingHour.setStartTime(LocalDateTime.of(2025,1,1,22,0));
        mockWorkingHour.setEndTime(LocalDateTime.of(2025,1,2,5,0));
        mockWorkingHour.setSegments(new ArrayList<>());
        mockWorkingHour.setBreaks(List.of());

        SegmentType mockNightType = new SegmentType();
        mockNightType.setName("night");
        when(segmentTypeRepository.findSegmentTypeByName("night")).thenReturn(Optional.of(mockNightType));


        calculateWorkingHourSegmentService.calculateWorkingHourSegment(mockWorkingHour, mockWorkingHour.getStartTime(), mockWorkingHour.getEndTime());

        assertEquals(1, mockWorkingHour.getSegments().size());
        assertEquals(420, mockWorkingHour.getSegments().getFirst().getDuration());
        assertEquals("night", mockWorkingHour.getSegments().getFirst().getSegmentType().getName());
    }

    @Test
    void calculateWorkingHourSegment_NightWithBreak() {
        WorkingHour mockWorkingHour = new WorkingHour();
        mockWorkingHour.setStartTime(LocalDateTime.of(2025,1,1,22,0));
        mockWorkingHour.setEndTime(LocalDateTime.of(2025,1,2,5,0));
        mockWorkingHour.setSegments(new ArrayList<>());

        Break mockBreak = new Break();
        mockBreak.setStartTime(LocalDateTime.of(2025,1,1,23,0));
        mockBreak.setEndTime(LocalDateTime.of(2025,1,2,0,0));
        mockWorkingHour.setBreaks(List.of(mockBreak));

        SegmentType mockNightType = new SegmentType();
        mockNightType.setName("night");
        when(segmentTypeRepository.findSegmentTypeByName("night")).thenReturn(Optional.of(mockNightType));

        calculateWorkingHourSegmentService.calculateWorkingHourSegment(mockWorkingHour, mockWorkingHour.getStartTime(), mockWorkingHour.getEndTime());

        assertEquals(1, mockWorkingHour.getSegments().size());
        assertEquals(360, mockWorkingHour.getSegments().getFirst().getDuration());
        assertEquals("night", mockWorkingHour.getSegments().getFirst().getSegmentType().getName());
    }

    @Test
    void calculateWorkingHourSegment_RegularAndOvertimeWithoutBreak() {
        WorkingHour mockWorkingHour = new WorkingHour();
        mockWorkingHour.setStartTime(LocalDateTime.of(2025,1,1,8,0));
        mockWorkingHour.setEndTime(LocalDateTime.of(2025,1,1,20,0));
        mockWorkingHour.setSegments(new ArrayList<>());
        mockWorkingHour.setBreaks(List.of());

        SegmentType mockRegularType = new SegmentType();
        mockRegularType.setName("regular");
        when(segmentTypeRepository.findSegmentTypeByName("regular")).thenReturn(Optional.of(mockRegularType));
        SegmentType mockOverType = new SegmentType();
        mockOverType.setName("overtime");
        when(segmentTypeRepository.findSegmentTypeByName("overtime")).thenReturn(Optional.of(mockOverType));

        calculateWorkingHourSegmentService.calculateWorkingHourSegment(mockWorkingHour, mockWorkingHour.getStartTime(), mockWorkingHour.getEndTime());

        assertEquals(2, mockWorkingHour.getSegments().size());
        assertEquals(480, mockWorkingHour.getSegments().get(0).getDuration());
        assertEquals(240, mockWorkingHour.getSegments().get(1).getDuration());
        assertEquals("regular", mockWorkingHour.getSegments().get(0).getSegmentType().getName());
        assertEquals("overtime", mockWorkingHour.getSegments().get(1).getSegmentType().getName());
    }

    @Test
    void calculateWorkingHourSegment_RegularAndOvertimeWithBreak() {
        WorkingHour mockWorkingHour = new WorkingHour();
        mockWorkingHour.setStartTime(LocalDateTime.of(2025,1,1,8,0));
        mockWorkingHour.setEndTime(LocalDateTime.of(2025,1,1,20,0));
        mockWorkingHour.setSegments(new ArrayList<>());

        Break mockBreak = new Break();
        mockBreak.setStartTime(LocalDateTime.of(2025,1,1,10,0));
        mockBreak.setEndTime(LocalDateTime.of(2025,1,1,11,0));
        mockWorkingHour.setBreaks(List.of(mockBreak));

        SegmentType mockRegularType = new SegmentType();
        mockRegularType.setName("regular");
        when(segmentTypeRepository.findSegmentTypeByName("regular")).thenReturn(Optional.of(mockRegularType));
        SegmentType mockOverType = new SegmentType();
        mockOverType.setName("overtime");
        when(segmentTypeRepository.findSegmentTypeByName("overtime")).thenReturn(Optional.of(mockOverType));

        calculateWorkingHourSegmentService.calculateWorkingHourSegment(mockWorkingHour, mockWorkingHour.getStartTime(), mockWorkingHour.getEndTime());

        assertEquals(2, mockWorkingHour.getSegments().size());
        assertEquals(480, mockWorkingHour.getSegments().get(0).getDuration());
        assertEquals(180, mockWorkingHour.getSegments().get(1).getDuration());
        assertEquals("regular", mockWorkingHour.getSegments().get(0).getSegmentType().getName());
        assertEquals("overtime", mockWorkingHour.getSegments().get(1).getSegmentType().getName());
    }

    @Test
    void calculateWorkingHourSegment_Regular_Night_Overtime() {
        WorkingHour mockWorkingHour = new WorkingHour();
        mockWorkingHour.setStartTime(LocalDateTime.of(2025,1,1,8,0));
        mockWorkingHour.setEndTime(LocalDateTime.of(2025,1,2, 0,0));
        mockWorkingHour.setSegments(new ArrayList<>());

        WorkingHour mockWorkingHourWithBreak = new WorkingHour();
        mockWorkingHourWithBreak.setStartTime(LocalDateTime.of(2025,1,1,8,0));
        mockWorkingHourWithBreak.setEndTime(LocalDateTime.of(2025,1,2, 0,0));
        mockWorkingHourWithBreak.setSegments(new ArrayList<>());
        mockWorkingHour.setBreaks(List.of());

        Break mockBreak = new Break();
        mockBreak.setStartTime(LocalDateTime.of(2025,1,1,10,0));
        mockBreak.setEndTime(LocalDateTime.of(2025,1,1,11,0));
        mockWorkingHourWithBreak.setBreaks(List.of(mockBreak));

        SegmentType mockRegularType = new SegmentType();
        mockRegularType.setName("regular");
        when(segmentTypeRepository.findSegmentTypeByName("regular")).thenReturn(Optional.of(mockRegularType));

        SegmentType mockOverType = new SegmentType();
        mockOverType.setName("overtime");
        when(segmentTypeRepository.findSegmentTypeByName("overtime")).thenReturn(Optional.of(mockOverType));

        SegmentType mockOverNightType = new SegmentType();
        mockOverNightType.setName("over-night");
        when(segmentTypeRepository.findSegmentTypeByName("over-night")).thenReturn(Optional.of(mockOverNightType));

        calculateWorkingHourSegmentService.calculateWorkingHourSegment(mockWorkingHour,
                                                                        mockWorkingHour.getStartTime(),
                                                                        mockWorkingHour.getEndTime());

        calculateWorkingHourSegmentService.calculateWorkingHourSegment(mockWorkingHourWithBreak,
                                                                        mockWorkingHourWithBreak.getStartTime(),
                                                                        mockWorkingHourWithBreak.getEndTime());

        assertEquals(3, mockWorkingHour.getSegments().size());
        assertEquals(480, mockWorkingHour.getSegments().get(0).getDuration());
        assertEquals(360, mockWorkingHour.getSegments().get(1).getDuration());
        assertEquals(120, mockWorkingHour.getSegments().get(2).getDuration());

        assertEquals("regular", mockWorkingHour.getSegments().get(0).getSegmentType().getName());
        assertEquals("overtime", mockWorkingHour.getSegments().get(1).getSegmentType().getName());
        assertEquals("over-night", mockWorkingHour.getSegments().get(2).getSegmentType().getName());

        assertEquals(3, mockWorkingHourWithBreak.getSegments().size());
        assertEquals(480, mockWorkingHourWithBreak.getSegments().get(0).getDuration());
        assertEquals(300, mockWorkingHourWithBreak.getSegments().get(1).getDuration());
        assertEquals(120, mockWorkingHourWithBreak.getSegments().get(2).getDuration());

        assertEquals("regular", mockWorkingHourWithBreak.getSegments().get(0).getSegmentType().getName());
        assertEquals("overtime", mockWorkingHourWithBreak.getSegments().get(1).getSegmentType().getName());
        assertEquals("over-night", mockWorkingHourWithBreak.getSegments().get(2).getSegmentType().getName());
    }


}
