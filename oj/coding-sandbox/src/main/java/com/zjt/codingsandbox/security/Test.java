package com.zjt.codingsandbox.security;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        System.setSecurityManager(new UserSecurityManager());

        List<String> strings = FileUtil.readLines(new File("/Users/zz/Code/repo-java/oj/coding-sandbox/src/main/resources/testcoding/simple/Main.java"), StandardCharsets.UTF_8);

        System.out.println(strings);
    }
}
