package ServiceTest;

import org.example.timestampapp.Model.Entity.Status;
import org.example.timestampapp.Repository.StatusRepository;
import org.example.timestampapp.Service.StatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatusServiceTest {
    @Mock
    StatusRepository statusRepository;

    @InjectMocks
    StatusService statusService;

    @Test
    public void getStatusTest_Found(){
        Status mockStatus = new Status();
        mockStatus.setType("Work");

        when(statusRepository.findStatusByType("Work")).thenReturn(Optional.of(mockStatus));
        Status foundStatus = statusService.getStatus("Work");

        assertEquals(foundStatus.getType(), mockStatus.getType());
    }

    @Test
    public void getStatusTest_NotFound(){
        when(statusRepository.findStatusByType("Work")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, ()-> statusService.getStatus("Work"));
    }
}
