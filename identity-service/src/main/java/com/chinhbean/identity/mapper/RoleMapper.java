package com.chinhbean.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.chinhbean.identity.dto.request.RoleRequest;
import com.chinhbean.identity.dto.response.RoleResponse;
import com.chinhbean.identity.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
