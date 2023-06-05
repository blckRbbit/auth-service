package com.shary.auth.service;

import com.github.shary2023.core.exception.BadCredentialsException;
import com.github.shary2023.core.exception.ResourceNotFoundException;
import com.github.shary2023.core.exception.UserAllReadyExistsException;
import com.github.shary2023.docs.model.*;
import com.shary.auth.repository.UserRepository;
import com.shary.auth.repository.entity.user.Role;
import com.shary.auth.repository.entity.user.SharyUser;
import com.shary.auth.repository.entity.user.support.RolesNames;
import com.shary.auth.util.component.patcher.Patcher;
import com.shary.auth.util.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository repository;
    private final RoleService roleService;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final Patcher<SharyUser> patcher;

    @Value("${app.messages.errors.user.by-id-not-found}")
    private String userByIdNotFoundError;

    @Value("${app.messages.errors.user.by-email-not-found}")
    private String userByEmailNotFoundError;

    @Value("${app.messages.errors.user.by-phone-not-found}")
    private String userByPhoneNotFoundError;

    private Role DBA;
    private Role ADMIN;
    private Role MODERATOR;

    @PostConstruct
    private void initRoles() {
        DBA = roleService.findRoleByName(RolesNames.DBA.name);
        ADMIN = roleService.findRoleByName(RolesNames.ADMIN.name);
        MODERATOR = roleService.findRoleByName(RolesNames.MODERATOR.name);
    }


    @Transactional
    public UserResponseSchema createAdmin(@Valid UserSchema schema) throws MethodArgumentNotValidException {
        createDetails(schema, RolesNames.ADMIN.name);
        return mapper.schemaToResponse(schema);
    }

    @Transactional
    public UserResponseSchema createModerator(@Valid UserSchema schema) throws MethodArgumentNotValidException {
        createDetails(schema, RolesNames.MODERATOR.name);
        return mapper.schemaToResponse(schema);
    }

    private Optional<SharyUser> findByPhone(String phone) {
        return repository.findByPhoneNumber(phone);
    }

    @Transactional
    public UserResponseSchema createGuest(@Valid GuestSchema guest)
            throws UserAllReadyExistsException, MethodArgumentNotValidException {
        if (findByPhone(guest.getPhoneNumber()).isPresent()) {
            throw new UserAllReadyExistsException();
        }
        if (guest.getId() == null) {
            guest.setId(getLastUserId() + 1);
        }
        SharyUser user = mapper.toUser(guest);
        user.setPassword(passwordEncoder.encode(guest.getPassword()));
        user = addRole(user, RolesNames.GUEST.name);
        log.debug("New user registered, phone {}", user.getPhoneNumber());
        return mapper.toResponse(user);
    }


    //todo may be code in this method is temporary
    private Long getLastUserId() {
        List<SharyUser> users = repository.findAll();
        return Collections.max(users).getId();
    }

    /**
     * Create a user who has rented at least one thing.
     * @param owner - user who has rented at least one thing.
     * @return owner
     */
    @Transactional
    public UserResponseSchema createOwner(@Valid OwnerSchema owner)
            throws MethodArgumentNotValidException {
        Long id = owner.getId();
        String password = owner.getPassword();
        SharyUser sharyUser = getUser(id);
        boolean isRenter = sharyUser.isRenter();
        Map<String, Object> update = patcher.toUpdateMap(mapper.toUser(owner));

        SharyUser updatedSharyUser = updateTo(owner.getId(), update);
        fillUserFields(updatedSharyUser, sharyUser.getRoles(), true, isRenter);

        if (password != null) {
            updatedSharyUser.setPassword(passwordEncoder.encode(password));
        }

        createDetails(mapper.toSchema(updatedSharyUser), RolesNames.OWNER.name);
        return mapper.toResponse(updatedSharyUser);
    }

    /**
     * Create a user who has rented at least one thing.
     * @param renter - a user who has rented at least one thing.
     * @return renter
     */
    @Transactional
    public UserResponseSchema createRenter(@Valid RenterSchema renter)
            throws MethodArgumentNotValidException {

        Long id = renter.getId();
        String password = renter.getPassword();
        SharyUser sharyUser = getUser(id);
        boolean isOwner = sharyUser.isOwner();
        Map<String, Object> update = patcher.toUpdateMap(mapper.toUser(renter));

        SharyUser updatedSharyUser = updateTo(renter.getId(), update);
        fillUserFields(updatedSharyUser, sharyUser.getRoles(), isOwner, true);

        if (password != null) {
            updatedSharyUser.setPassword(passwordEncoder.encode(password));
        }

        createDetails(mapper.toSchema(updatedSharyUser), RolesNames.RENTER.name);
        return mapper.toResponse(updatedSharyUser);
    }

    @Transactional
    private void createDetails(@Valid UserSchema schema, String roleName) throws MethodArgumentNotValidException {
        SharyUser user;
        if (findByPhone(schema.getPhoneNumber()).isPresent()) {
            user = getUser(schema.getPhoneNumber());
        } else {
            user = mapper.toUser(schema);
        }

        if (schema.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(schema.getPassword()));
        }
        addRole(user, roleName);
        log.debug("New user registered, phone {}", user.getPhoneNumber());
    }

    @Transactional
    private SharyUser addRole(SharyUser user, String roleName) {
        Collection<Role> roles = getUser(user.getId()).getRoles();
        Role role = roleService.findRoleByName(roleName);
        if (roles.contains(role)) {
            return user;
        }
        roles.add(roleService.findRoleByName(roleName));
        user.setRoles(roles);
        return repository.save(user);
    }

    private void fillUserFields(SharyUser updatedUser, Collection<Role> roles, boolean isOwner, boolean isRenter) {
        updatedUser.setOwner(isOwner);
        updatedUser.setRenter(isRenter);
        updatedUser.setRoles(roles);
    }

    /**
     * Get all registered users from DB
     * @return list DTO of user
     */
    public List<UserResponseSchema> getAllUsers() {
        return mapper.toResponse(repository.findAll());
    }

    /**
     * Get user by his ID from DB
     * @param userId - unique user ID
     * @return DTO of user
     */
    public UserResponseSchema getUserById(Long userId) {
        String phone = getUser(userId).getPhoneNumber();
        if (isAuthoritiesRight(phone)) {
            return mapper.toResponse(repository.findById(userId).orElseThrow(
                    () -> new ResourceNotFoundException(String.format(userByIdNotFoundError, userId))));
        }
        throw new BadCredentialsException("Not enough rights for get it");
    }

    public UserSchema getUserProfile(String phone) {
        if (isAuthoritiesRight(phone)) {
            return mapper.toSchema(getUser(phone));
        }
        throw new BadCredentialsException("Not enough rights for get it");
    }

    /**
     * Updates only those entity fields that came in Map<String, Object> data
     *
     * @param userId     - user ID
     * @param userSchema - DTO with update fields for entity in the database
     * @return updated user entity
     */
    public UserResponseSchema update(Long userId, UserSchema userSchema) throws MethodArgumentNotValidException {
        SharyUser sharyUser = repository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(
                String.format(userByIdNotFoundError, userId))
        );

        userSchemaBooleanFieldsCorrect(userSchema, sharyUser);

        Map<String, Object> data = patcher.toUpdateMap(mapper.toUser(userSchema));

        if (updatedEmailIsNotCorrect(data, sharyUser)) {
            data.remove("email");
        }

        if (updatedPhoneIsNotCorrect(data, sharyUser)) {
            data.remove("phoneNumber");
        }
        patcher.patch(sharyUser, data);
        return mapper.toResponse(sharyUser);
    }

    private void userSchemaBooleanFieldsCorrect(UserSchema userSchema, SharyUser sharyUser) {
        if (userSchema.getOwner() == null) userSchema.setOwner(sharyUser.isOwner());
        if (userSchema.getRenter() == null) userSchema.setRenter(sharyUser.isRenter());
        if (userSchema.getIsValid() == null) userSchema.setIsValid(sharyUser.isValid());
    }

    /**
     * The method updates the User entity in the database to be convertible
     * to any of the models (Owner or Renter)
     * @param id   user entity ID
     * @param data - map with update fields for entity in the database
     */
    private SharyUser updateTo(@NotNull Long id, @NotNull Map<String, Object> data) {
        SharyUser sharyUser = getUser(id);
        Collection<Role> roles = sharyUser.getRoles();
        if (updatedEmailIsNotCorrect(data, sharyUser)) {
            data.remove("email");
        }
        if (updatedPhoneIsNotCorrect(data, sharyUser)) {
            data.remove("phoneNumber");
        }
        patcher.patch(sharyUser, data);
        updateTo(sharyUser);
        sharyUser.setRoles(roles);
        return sharyUser;
    }

    /**
     * The method updates the User entity in the database to be convertible
     * to any of the models (Owner or Renter)
     * @param sharyUser - user entity in the database
     */
    private void updateTo(SharyUser sharyUser) {
        if (isOwner(sharyUser)) {
            @Valid OwnerSchema owner = mapper.toOwner(sharyUser);
            repository.update(owner).orElseThrow();
        } else if (sharyUser.isRenter() && !sharyUser.isOwner()) {
            @Valid RenterSchema renter = mapper.toRenter(sharyUser);
            repository.update(renter).orElseThrow();
        }
    }

    /**
     * The new email of user can not be equals old user email and
     * can not be null.
     * @param data      - updated data map
     * @param sharyUser - entity of user
     * @return boolean
     */
    private boolean updatedEmailIsNotCorrect(Map<String, Object> data, SharyUser sharyUser) {
        return data.get("email") == null || data.get("email").equals(sharyUser.getEmail());
    }

    /**
     * The new phone of user can not be equals old user phone and
     * can not be null.
     * @param data      - updated data map
     * @param sharyUser - entity of user
     * @return boolean
     */
    private boolean updatedPhoneIsNotCorrect(Map<String, Object> data, SharyUser sharyUser) {
        return data.get("phoneNumber") == null || data.get("phoneNumber").equals(sharyUser.getPhoneNumber());
    }

    /**
     * Whether the user is the owner of the item
     * @param sharyUser - entity of user from DB
     * @return boolean
     */
    private boolean isOwner(SharyUser sharyUser) {
        return sharyUser.isOwner() || (sharyUser.isOwner() && sharyUser.isRenter());
    }

    @Transactional
    public UserSchema changeUserProfile(String phone, @Valid UserSchema update)
            throws BadCredentialsException {
        SharyUser user = getUser(phone);
        if (isAuthoritiesRight(phone)) {
            patcher.patch(user, patcher.toUpdateMap(mapper.toUser(update)));
            if (update.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(update.getPassword()));
            }
            return mapper.toSchema(repository.save(user));
        }
        throw new BadCredentialsException("Not enough rights for update this");
    }

    private boolean isAuthoritiesRight(String phone) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authPhone = auth.getName();
        if (!authPhone.equals(phone)) {
            if (isDba(phone)) {
                return isDba(authPhone);
            } else if (isAdmin(phone)) {
                return (isAdmin(authPhone) || isDba(authPhone));
            } else if (isModerator(phone)) {
                return (isModerator(authPhone) || isAdmin(authPhone) || isDba(authPhone));
            } else return true;
        }
        return true;
    }

    private boolean isModerator(String phone) {
        SharyUser user = getUser(phone);
        if (user.getRoles().contains(MODERATOR)) return true;
        if (user.getRoles().contains(ADMIN)) return true;
        return user.getRoles().contains(DBA);
    }

    private boolean isAdmin(String phone) {
        SharyUser user = getUser(phone);
        if (user.getRoles().contains(DBA)) return true;
        return user.getRoles().contains(ADMIN);
    }

    private boolean isDba(String phone) {
        SharyUser user = getUser(phone);
        return user.getRoles().contains(DBA);
    }

    /**
     * Delete the entity of user by ID
     *
     * @param id - unique user id
     * @return boolean
     */
    public boolean deleteUser(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        SharyUser user = getUser(phone);
        return new User(user.getPhoneNumber(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    private SharyUser getUser(String phone) {
        return repository.findByPhoneNumber(phone).orElseThrow(
                () -> new ResourceNotFoundException(
                        String.format(userByPhoneNotFoundError, phone)
                )
        );
    }

    private SharyUser getUser(Long id) {
        return repository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                String.format(userByIdNotFoundError, id)
                        )
                );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(
                        role.getName()))
                .collect(Collectors.toList());
    }
}
