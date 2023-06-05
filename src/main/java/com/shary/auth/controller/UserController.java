package com.shary.auth.controller;

import com.github.shary2023.docs.UsersApi;
import com.github.shary2023.docs.model.*;
import com.shary.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {
    private final UserService service;

    @Override
    @SneakyThrows
    @Transactional
    public ResponseEntity<UserResponseSchema> createGuest(GuestSchema guestSchema) {
        return ResponseEntity.ok(service.createGuest(guestSchema));
    }

    @Override
    @SneakyThrows
    public ResponseEntity<UserResponseSchema> createRenter(RenterSchema renterSchema) {
        return ResponseEntity.ok(service.createRenter(renterSchema));
    }

    @Override
    @SneakyThrows
    public ResponseEntity<UserResponseSchema> createOwner(OwnerSchema ownerSchema) {
        return ResponseEntity.ok(service.createOwner(ownerSchema));
    }

    @Override
    @SneakyThrows
    public ResponseEntity<UserResponseSchema> createModerator(UserSchema userSchema) {
        return ResponseEntity.ok(service.createModerator(userSchema));
    }

    @Override
    @SneakyThrows
    public ResponseEntity<UserResponseSchema> createAdmin(UserSchema userSchema) {
        return ResponseEntity.ok(service.createAdmin(userSchema));
    }

    @Override
    public ResponseEntity<List<UserResponseSchema>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @Override
    public ResponseEntity<UserResponseSchema> getUserById(Long userId) {
        return ResponseEntity.ok(service.getUserById(userId));
    }

    @Override
    public ResponseEntity<UserSchema> getUserProfile(String phone) {
        return ResponseEntity.ok(service.getUserProfile(phone));
    }

    @Override
    @SneakyThrows
    @Transactional
    public ResponseEntity<UserResponseSchema> changeUserById(Long userId, UserSchema userSchema) {
        return ResponseEntity.ok(service.update(userId, userSchema));
    }

    @Override
    public ResponseEntity<UserSchema> changeUserProfile(String phone, UserSchema userSchema) {
        return ResponseEntity.ok(service.changeUserProfile(phone, userSchema));
    }

    @Override
    @Transactional
    public ResponseEntity<Boolean> deleteUser(Long userId) {
        return ResponseEntity.ok(service.deleteUser(userId));
    }
}
