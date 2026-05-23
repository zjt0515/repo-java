package com.zjt.ojjudgeservice.codesandbox;


import com.zjt.ojmodel.model.codesandbox.ExecuteCodeRequest;
import com.zjt.ojmodel.model.codesandbox.ExecuteCodeResponse;

public interface CodeSandbox {
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
