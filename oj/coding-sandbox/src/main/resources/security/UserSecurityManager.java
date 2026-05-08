import java.security.Permission;

public class UserSecurityManager extends SecurityManager{
    private static String READABLE_DIR = "/Users/zz/Code/repo-java/oj/coding-sandbox";

    @Override
    public void checkPermission(Permission perm) {
        //super.checkPermission(perm);
    }

    /**
     * 检查执行权限
     */
    @Override
    public void checkExec(String cmd) {
        //throw new SecurityException("权限异常: " + cmd);
    }

    /**
     * 检查读取权限
     * @param file   the system-dependent file name.
     */
    @Override
    public void checkRead(String file) {
        System.out.println(file);
        if (file.contains(READABLE_DIR)){
            return;
        }
        //throw new SecurityException("权限异常: " + file);
    }

    /**
     * 检查写入权限
     * @param file   the system-dependent filename.
     */
    @Override
    public void checkWrite(String file) {
        //throw new SecurityException("权限异常: " + file);
    }

    @Override
    public void checkDelete(String file) {
        //throw new SecurityException("权限异常: " + file);
    }

    @Override
    public void checkConnect(String host, int port) {
        //throw new SecurityException("权限异常: " + host + port);
    }
}
