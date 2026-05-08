package com.zjt.codingsandbox.security;

import java.security.Permission;

public class UserSecurityManager extends SecurityManager{
    private static String READABLE_DIR = "/Users/zz/Code/repo-java/oj/coding-sandbox";

    @Override
    public void checkPermission(Permission perm) {
        //super.checkPermission(perm);
    }

    /**
     *
     */
    @Override
    public void checkExec(String cmd) {
        throw new SecurityException("权限异常: " + cmd);
    }

    /**
     *
     * @param file   the system-dependent file name.
     */
    @Override
    public void checkRead(String file) {
        System.out.println(file);
        throw new SecurityException("权限异常: " + file);
    }

    /**
     *
     * @param file   the system-dependent filename.
     */
    @Override
    public void checkWrite(String file) {
        if (file.contains(READABLE_DIR)){
            return;
        }
        throw new SecurityException("权限异常: " + file);
    }

    @Override
    public void checkDelete(String file) {
        throw new SecurityException("权限异常: " + file);
    }

    @Override
    public void checkConnect(String host, int port) {
        throw new SecurityException("权限异常: " + host + port);
    }
}
