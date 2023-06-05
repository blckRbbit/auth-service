package com.shary.auth.util.mapper;

import com.github.shary2023.docs.model.RoleSchema;
import com.shary.auth.repository.entity.user.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {
    Role toRole(RoleSchema schema);

    RoleSchema toSchema(Role role);

    List<RoleSchema> toSchemas(List<Role> roles);

    List<RoleSchema> toSchema(List<Role> allByUserId);
}
