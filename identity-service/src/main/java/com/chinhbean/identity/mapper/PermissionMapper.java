package com.chinhbean.identity.mapper;

import org.mapstruct.Mapper;

import com.chinhbean.identity.dto.request.PermissionRequest;
import com.chinhbean.identity.dto.response.PermissionResponse;
import com.chinhbean.identity.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
