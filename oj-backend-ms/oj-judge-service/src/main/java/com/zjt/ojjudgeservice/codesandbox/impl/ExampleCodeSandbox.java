package com.zjt.ojjudgeservice.codesandbox.impl;


import com.zjt.ojjudgeservice.codesandbox.CodeSandbox;
import com.zjt.ojmodel.model.codesandbox.ExecuteCodeRequest;
import com.zjt.ojmodel.model.codesandbox.ExecuteCodeResponse;
import com.zjt.ojmodel.model.codesandbox.JudgeInfo;
import com.zjt.ojmodel.model.enums.JudgeInfoResultsEnum;
import com.zjt.ojmodel.model.enums.SubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("成功");
        executeCodeResponse.setStatus(SubmitStatusEnum.SUCCESS.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoResultsEnum.ACCEPTED.getText());
        judgeInfo.setMemory(1000L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
