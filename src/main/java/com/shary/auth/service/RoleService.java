package com.shary.auth.service;

import com.github.shary2023.core.exception.ResourceNotFoundException;
import com.github.shary2023.docs.model.RoleSchema;
import com.shary.auth.repository.RoleRepository;
import com.shary.auth.repository.entity.user.Role;
import com.shary.auth.util.mapper.RoleMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository repository;
    private final RoleMapper mapper;

    @Value("${app.messages.errors.role.by-id-not-found}")
    private String roleByIdNotFoundError;

    @Value("${app.messages.errors.role.by-name-not-found}")
    private String roleByNameNotFoundError;

    @Transactional
    public RoleSchema createRole(@Valid RoleSchema schema) throws MethodArgumentNotValidException {
        return mapper.toSchema(repository.save(mapper.toRole(schema)));
    }

    public List<RoleSchema> getAllRoles() {
        return mapper.toSchemas(repository.findAll());
    }

    public RoleSchema getRoleById(Long roleId) {
        return mapper.toSchema(repository.findById(roleId).orElseThrow(
                        () -> new ResourceNotFoundException(
                                String.format(roleByIdNotFoundError, roleId)
                        )
                )
        );
    }

    public Role findRoleByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new ResourceNotFoundException(
                        String.format(roleByNameNotFoundError, name)
                )
        );
    }
}
