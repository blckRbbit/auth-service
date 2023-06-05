package com.shary.auth.repository.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

@Table
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "roles")
@SequenceGenerator(allocationSize = 1, name = "role_seq", sequenceName = "role_seq")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @NotNull(message = "Id cannot be null.")
    private Long id;

    @NotNull(message = "Role name cannot be null.")
    @NotBlank(message = "Role name cannot be empty.")
    @Min(message = "The minimum length of the role name must be 2", value = 2)
    @Max(message = "The maximum length of the role name must be 20", value = 20)
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Collection<SharyUser> users = new HashSet<>();

    public Role(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return name.equals(role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
