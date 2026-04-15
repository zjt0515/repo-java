package com.yupi.oj.judge.codesandbox.impl;

import com.yupi.oj.judge.codesandbox.CodeSandbox;
import com.yupi.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.yupi.oj.model.dto.questionsubmit.JudgeInfo;
import com.yupi.oj.model.enums.JudgeInfoResultsEnum;
import com.yupi.oj.model.enums.SubmitStatusEnum;

import java.util.List;

import lombok.extern.slf4j.Slf4j;


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
