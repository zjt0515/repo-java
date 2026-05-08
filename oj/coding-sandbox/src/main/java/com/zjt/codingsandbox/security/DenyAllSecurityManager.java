package com.zjt.codingsandbox.security;

import java.security.Permission;

public class DenyAllSecurityManager extends SecurityManager{
    @Override
    public void checkPermission(Permission perm) {
        throw new SecurityException("权限异常：" + perm.toString());
    }
}
