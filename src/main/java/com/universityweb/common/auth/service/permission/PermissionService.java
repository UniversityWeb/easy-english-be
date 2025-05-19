package com.universityweb.common.auth.service.permission;

public interface PermissionService<ID> {
    boolean isAccessible(String username, ID id);
    boolean canEdit(String username, ID id);
    boolean canDelete(String username, ID id);
}
