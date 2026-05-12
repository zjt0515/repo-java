package com.zjt.ojjudgeservice.codesandbox;


import com.zjt.ojjudgeservice.codesandbox.impl.ExampleCodeSandbox;
import com.zjt.ojjudgeservice.codesandbox.impl.RemoteCodeSandbox;
import com.zjt.ojjudgeservice.codesandbox.impl.ThirdPartyCodeSandbox;

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
