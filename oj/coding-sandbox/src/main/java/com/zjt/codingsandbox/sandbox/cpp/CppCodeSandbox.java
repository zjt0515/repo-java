package com.zjt.codingsandbox.sandbox.cpp;

import com.zjt.codingsandbox.model.ExecuteCodeRequest;
import com.zjt.codingsandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

@Component
public class CppCodeSandbox extends CppCodeSandboxTemplate {

    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest) {
        return super.execute(executeCodeRequest);
    }
}
