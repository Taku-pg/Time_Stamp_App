package org.example.timestampapp.ServiceTest;

import org.example.timestampapp.Model.Entity.Break;
import org.example.timestampapp.Model.Entity.WorkingHour;
import org.example.timestampapp.Repository.BreakRepository;
import org.example.timestampapp.Service.BreakService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BreakServiceTest {
    @Mock
    private BreakRepository breakRepository;
    @InjectMocks
    private BreakService breakService;

    @Test
    void breakTimeStampTest() {
        WorkingHour mockWorkingHour = new WorkingHour();
        breakService.breakTimeStamp(mockWorkingHour);

        ArgumentCaptor<Break> breakCaptor = ArgumentCaptor.forClass(Break.class);
        verify(breakRepository).save(breakCaptor.capture());

        Break capture = breakCaptor.getValue();
        assertEquals(mockWorkingHour, capture.getWorkingHour());
        assertNotNull(capture.getStartTime());
    }

    @Test
    void backTimeStampTest_Success() {
        WorkingHour mockWorkingHour = new WorkingHour();
        mockWorkingHour.setId(1L);
        Break mockBreak = new Break();
        mockBreak.setWorkingHour(mockWorkingHour);
        mockWorkingHour.setBreaks(List.of(mockBreak));

        when(breakRepository.findCurrentBreakByWorkingHourId(1L)).thenReturn(Optional.of(mockBreak));

        breakService.backTimeStamp(1L);

        ArgumentCaptor<Break> breakCaptor = ArgumentCaptor.forClass(Break.class);
        verify(breakRepository).save(breakCaptor.capture());

        Break capture = breakCaptor.getValue();
        assertEquals(mockBreak, capture);
        assertEquals(mockWorkingHour, capture.getWorkingHour());
        assertNotNull(capture.getEndTime());
    }

    @Test
    void backTimeStampTest_Failure() {
        when(breakRepository.findCurrentBreakByWorkingHourId(1000L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> breakService.backTimeStamp(1000L));
        verify(breakRepository).findCurrentBreakByWorkingHourId(1000L);

    }


}
