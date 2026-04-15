package com.yupi.oj.judge.codesandbox;

import com.yupi.oj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.yupi.oj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.yupi.oj.judge.codesandbox.impl.ThirdPartyCodeSandbox;

public class CodeSandboxFactory {
    public static CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdparty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
