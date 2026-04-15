package com.yupi.oj.judge.codesandbox;

import com.yupi.oj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.yupi.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.oj.model.enums.SubmitLangEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

class CodeSandboxTest {
    @Value("${codesandbox.type:example}")
    private String type;

    @Test
    void executeCodeByValue() {
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        String code = "int main";
        String language = SubmitLangEnum.CPP.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder().code(code).language(language).inputList(inputList).build();
        codeSandbox.executeCode(executeCodeRequest);
    }

    @Test
    void executeCodeByProxy() {
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String code = "int main";
        String language = SubmitLangEnum.CPP.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder().code(code).language(language).inputList(inputList).build();
        codeSandbox.executeCode(executeCodeRequest);
    }
}