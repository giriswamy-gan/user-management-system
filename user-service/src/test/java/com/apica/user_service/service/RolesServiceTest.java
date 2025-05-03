package com.apica.user_service.service;

import com.apica.user_service.dto.GetRoleResponseDto;
import com.apica.user_service.dto.RoleRequestDto;
import com.apica.user_service.entity.Roles;
import com.apica.user_service.utils.CustomApiException;
import com.apica.user_service.repository.RolesRepository;
import com.apica.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RolesServiceTest {

    @Mock
    RolesRepository rolesRepo;

    @Mock
    UserRepository userRepo;        // not used in these methods, but required by constructor

    @InjectMocks
    RolesService rolesService;

    private RoleRequestDto validReq;
    private Roles existingRole;

    @BeforeEach
    void setUp() {
        validReq = new RoleRequestDto();
        validReq.setName("ROLE_USER");

        existingRole = new Roles();
        existingRole.setId("role-1");
        existingRole.setName("ROLE_EXISTING");
    }

    // createRoles

    @Test
    void createRoles_nullName_throws() {
        RoleRequestDto req = new RoleRequestDto();
        req.setName(null);

        CustomApiException ex = assertThrows(CustomApiException.class, () ->
                rolesService.createRoles(req)
        );
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getCustomCode()).isEqualTo("NAME_NOT_FOUND");

        verifyNoInteractions(rolesRepo);
    }

    @Test
    void createRoles_nameAlreadyExists_throws() {
        when(rolesRepo.findByName("ROLE_USER"))
                .thenReturn(Optional.of(existingRole));

        CustomApiException ex = assertThrows(CustomApiException.class, () ->
                rolesService.createRoles(validReq)
        );
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getCustomCode()).isEqualTo("ROLE_NAME_EXISTS");

        verify(rolesRepo).findByName("ROLE_USER");
        verify(rolesRepo, never()).save(any());
    }

    @Test
    void createRoles_happyPath_savesRole() {
        when(rolesRepo.findByName("ROLE_USER")).thenReturn(Optional.empty());
        // simulate save returning an entity with id
        Roles saved = new Roles();
        saved.setId("new-role");
        saved.setName("ROLE_USER");
        when(rolesRepo.save(any(Roles.class))).thenReturn(saved);

        rolesService.createRoles(validReq);

        // capture the role passed to save
        ArgumentCaptor<Roles> captor = ArgumentCaptor.forClass(Roles.class);
        verify(rolesRepo).findByName("ROLE_USER");
        verify(rolesRepo).save(captor.capture());
        Roles toSave = captor.getValue();
        assertThat(toSave.getName()).isEqualTo("ROLE_USER");
    }

    // getAllRoles

    @Test
    void getAllRoles_returnsMappedList() {
        Roles r1 = new Roles(); r1.setId("r1"); r1.setName("A");
        Roles r2 = new Roles(); r2.setId("r2"); r2.setName("B");
        when(rolesRepo.findAll()).thenReturn(List.of(r1,r2));

        List<GetRoleResponseDto> dtos = rolesService.getAllRoles();

        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting("roleId").containsExactly("r1","r2");
        assertThat(dtos).extracting("roleName").containsExactly("A","B");
        verify(rolesRepo).findAll();
    }

    @Test
    void getAllRoles_emptyList_returnsEmpty() {
        when(rolesRepo.findAll()).thenReturn(Collections.emptyList());
        List<GetRoleResponseDto> dtos = rolesService.getAllRoles();
        assertThat(dtos).isEmpty();
        verify(rolesRepo).findAll();
    }

    // getRole

    @Test
    void getRole_existing_returnsDto() {
        when(rolesRepo.findById("role-1")).thenReturn(Optional.of(existingRole));

        GetRoleResponseDto dto = rolesService.getRole("role-1");

        assertThat(dto.getRoleId()).isEqualTo("role-1");
        assertThat(dto.getRoleName()).isEqualTo("ROLE_EXISTING");
        verify(rolesRepo).findById("role-1");
    }

    @Test
    void getRole_notFound_throws() {
        when(rolesRepo.findById("nope")).thenReturn(Optional.empty());

        CustomApiException ex = assertThrows(CustomApiException.class, () ->
                rolesService.getRole("nope")
        );
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getCustomCode()).isEqualTo("ROLE_NOT_FOUND");
        verify(rolesRepo).findById("nope");
    }

    // updateRole

    @Test
    void updateRole_existing_updatesName() {
        RoleRequestDto req = new RoleRequestDto();
        req.setName("NEW_NAME");
        when(rolesRepo.findById("role-1")).thenReturn(Optional.of(existingRole));
        when(rolesRepo.save(any(Roles.class))).thenAnswer(i -> i.getArgument(0));

        rolesService.updateRole("role-1", req);

        assertThat(existingRole.getName()).isEqualTo("NEW_NAME");
        verify(rolesRepo).findById("role-1");
        verify(rolesRepo).save(existingRole);
    }

    @Test
    void updateRole_notFound_throws() {
        when(rolesRepo.findById("nope")).thenReturn(Optional.empty());

        assertThrows(CustomApiException.class, () ->
                rolesService.updateRole("nope", validReq)
        );
        verify(rolesRepo).findById("nope");
        verify(rolesRepo, never()).save(any());
    }
}
