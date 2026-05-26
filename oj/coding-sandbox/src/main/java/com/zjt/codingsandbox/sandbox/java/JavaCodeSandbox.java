package com.zjt.codingsandbox.sandbox.java;

import com.zjt.codingsandbox.model.ExecuteCodeRequest;
import com.zjt.codingsandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

@Component
public class JavaCodeSandbox extends JavaCodeSandboxTemplate {

    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest) {
        return super.execute(executeCodeRequest);
    }
}
