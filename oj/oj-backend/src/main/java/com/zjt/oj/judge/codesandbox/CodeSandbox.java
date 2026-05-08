package com.zjt.oj.judge.codesandbox;

import com.zjt.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.zjt.oj.judge.codesandbox.model.ExecuteCodeResponse;

public interface CodeSandbox {
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
