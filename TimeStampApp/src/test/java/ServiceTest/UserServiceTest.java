package ServiceTest;

import org.example.timestampapp.Model.Entity.User;
import org.example.timestampapp.Repository.UserRepository;
import org.example.timestampapp.Service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void changePasswordTest_Success(){
        User mockUser = new User();
        mockUser.setUsername("test");
        mockUser.setPassword("currentPassword");

        when(userRepository.findUserByUsername("test")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("currentPassword","currentPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i-> i.getArgument(0));

        userService.changePassword("test","currentPassword","newPassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("newPassword",savedUser.getPassword());
    }
    @Test
    public void changePasswordTest_WrongPassword(){
        User mockUser = new User();
        mockUser.setUsername("test");
        mockUser.setPassword("currentPassword");

        when(userRepository.findUserByUsername("test")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongPassword","currentPassword")).thenReturn(false);

        assertThrows(InputMismatchException.class,
                () ->  userService.changePassword("test","wrongPassword","newPassword"));

    }
    @Test
    public void changePasswordTest_User_NotFound(){
        when(userRepository.findUserByUsername("test")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                ()-> userService.changePassword("test","currentPassword","newPassword"));
    }
}
