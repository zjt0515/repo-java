package com.zjt.codingsandbox.security;

import java.security.Permission;

public class DefaultSecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm) {
        System.out.println(perm.getActions());
        super.checkPermission(perm);
    }

}
