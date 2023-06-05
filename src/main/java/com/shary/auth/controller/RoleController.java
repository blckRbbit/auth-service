package com.shary.auth.controller;

import com.github.shary2023.docs.RolesApi;
import com.github.shary2023.docs.model.RoleSchema;
import com.shary.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class RoleController implements RolesApi {
    private final RoleService service;

    @Override
    @SneakyThrows
    public ResponseEntity<RoleSchema> createRole(RoleSchema roleSchema) {
        return ResponseEntity.ok(service.createRole(roleSchema));
    }

    @Override
    public ResponseEntity<List<RoleSchema>> getAllRoles() {
        return ResponseEntity.ok(service.getAllRoles());
    }

    @Override
    public ResponseEntity<RoleSchema> getRoleById(Long roleId) {
        return ResponseEntity.ok(service.getRoleById(roleId));
    }
}
