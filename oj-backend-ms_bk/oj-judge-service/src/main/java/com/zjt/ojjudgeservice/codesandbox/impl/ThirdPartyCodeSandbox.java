package com.zjt.ojjudgeservice.codesandbox.impl;

import com.zjt.ojjudgeservice.codesandbox.CodeSandbox;
import com.zjt.ojmodel.model.codesandbox.ExecuteCodeRequest;
import com.zjt.ojmodel.model.codesandbox.ExecuteCodeResponse;

public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
