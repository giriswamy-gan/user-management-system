package com.apica.user_service.service;

import com.apica.user_service.entity.Users;
import com.apica.user_service.utils.CustomApiException;
import com.apica.user_service.repository.UserRepository;
import com.apica.user_service.repository.RolesRepository;
import com.apica.user_service.dto.GetUserResponseDto;
import com.apica.user_service.dto.UserRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock RolesRepository rolesRepository;
    @Mock PasswordEncoder pwEncoder;
    @Mock KafkaService kafkaService;

    @InjectMocks UserService userService;

    private Users existingUser;
    private UserRequestDto updateDto;

    @BeforeEach
    void setUp() {
        existingUser = new Users();
        existingUser.setId("user-1");
        existingUser.setUsername("oldName");
        existingUser.setFullName("Old Full");
        existingUser.setPassword("oldPassHash");
        existingUser.setRoles(new HashSet<>(List.of()));  // empty roles for delete test

        updateDto = new UserRequestDto();
        updateDto.setUsername("newName");
        updateDto.setFullName("New Full");
        updateDto.setPassword("newPassword");
    }

    @Test
    void getAllUsers_returnsMappedList() {
        Users u1 = new Users(); u1.setId("id1"); u1.setUsername("u1"); u1.setFullName("User One");
        Users u2 = new Users(); u2.setId("id2"); u2.setUsername("u2"); u2.setFullName("User Two");
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<GetUserResponseDto> dtos = userService.getAllUsers();

        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting("userId").containsExactly("id1","id2");
        assertThat(dtos).extracting("userName").containsExactly("u1","u2");
        assertThat(dtos).extracting("fullName").containsExactly("User One","User Two");
        verify(userRepository).findAll();
    }

    @Test
    void getUser_existing_returnsDto() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(existingUser));

        GetUserResponseDto dto = userService.getUser("user-1");

        assertThat(dto.getUserId()).isEqualTo("user-1");
        assertThat(dto.getUserName()).isEqualTo("oldName");
        assertThat(dto.getFullName()).isEqualTo("Old Full");
        verify(userRepository).findById("user-1");
    }

    @Test
    void getUser_notFound_throws() {
        when(userRepository.findById("user-2")).thenReturn(Optional.empty());

        CustomApiException ex = assertThrows(CustomApiException.class, () -> userService.getUser("user-2"));
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getCustomCode()).isEqualTo("USER_NOT_FOUND");
        verify(userRepository).findById("user-2");
    }

    @Test
    void updateUser_existing_savesAndPublishes() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(existingUser));
        when(pwEncoder.encode("newPassword")).thenReturn("newPassHash");

        userService.updateUser("user-1", updateDto);

        // verify the user fields were updated
        assertThat(existingUser.getUsername()).isEqualTo("newName");
        assertThat(existingUser.getFullName()).isEqualTo("New Full");
        assertThat(existingUser.getPassword()).isEqualTo("newPassHash");

        // verify save and kafka publish
        verify(userRepository).save(existingUser);
        verify(kafkaService).sendUserEvent(existingUser, "UPDATED", updateDto);
    }

    @Test
    void updateUser_notFound_throws() {
        when(userRepository.findById("user-99")).thenReturn(Optional.empty());

        assertThrows(CustomApiException.class, () -> userService.updateUser("user-99", updateDto));
        verify(userRepository).findById("user-99");
        verifyNoMoreInteractions(userRepository, kafkaService);
    }

    @Test
    void deleteUser_existing_clearsRolesAndPublishes() {
        // give the user some roles to clear
        existingUser.setRoles(new HashSet<>(List.of(mock(com.apica.user_service.entity.Roles.class))));
        when(userRepository.findById("user-1")).thenReturn(Optional.of(existingUser));

        userService.deleteUser("user-1");

        // roles should be cleared before delete
        assertThat(existingUser.getRoles()).isEmpty();
        verify(userRepository).delete(existingUser);
        verify(kafkaService).sendUserEvent(existingUser, "DELETED", null);
    }

    @Test
    void deleteUser_notFound_throws() {
        when(userRepository.findById("no-user")).thenReturn(Optional.empty());

        assertThrows(CustomApiException.class, () -> userService.deleteUser("no-user"));
        verify(userRepository).findById("no-user");
        verifyNoMoreInteractions(userRepository, kafkaService);
    }
}
