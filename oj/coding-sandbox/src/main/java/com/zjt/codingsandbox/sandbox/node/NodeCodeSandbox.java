package com.zjt.codingsandbox.sandbox.node;

import com.zjt.codingsandbox.model.ExecuteCodeRequest;
import com.zjt.codingsandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

@Component
public class NodeCodeSandbox extends NodeCodeSandboxTemplate {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
