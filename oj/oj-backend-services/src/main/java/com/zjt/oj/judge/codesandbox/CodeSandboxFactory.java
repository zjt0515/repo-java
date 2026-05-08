package com.zjt.oj.judge.codesandbox;

import com.zjt.oj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.zjt.oj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.zjt.oj.judge.codesandbox.impl.ThirdPartyCodeSandbox;

public class CodeSandboxFactory {
    public static CodeSandbox newInstance(String type) {
        switch (type) {
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdparty":
                return new ThirdPartyCodeSandbox();
            case "example":
            default:
                return new ExampleCodeSandbox();
        }
    }
}
