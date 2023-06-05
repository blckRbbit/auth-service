package com.shary.auth.repository;

import com.shary.auth.repository.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    @Query(value = "SELECT * FROM users_roles WHERE user_id = :id", nativeQuery = true)
    List<Role> getAllByUserId(@Param("id") Long userId);

}
