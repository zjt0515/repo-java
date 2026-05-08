package com.zjt.oj.judge.codesandbox.impl;

import com.zjt.oj.judge.codesandbox.CodeSandbox;
import com.zjt.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.zjt.oj.judge.codesandbox.model.ExecuteCodeResponse;

public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
