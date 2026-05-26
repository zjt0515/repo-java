package com.zjt.codingsandbox.sandbox;


import com.zjt.codingsandbox.model.ExecuteCodeRequest;
import com.zjt.codingsandbox.model.ExecuteCodeResponse;

public interface CodeSandbox {
    public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest);
}
