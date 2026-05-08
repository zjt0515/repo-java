package com.zjt.codingsandbox.utils;

import com.zjt.codingsandbox.model.ExecuteCodeResponse;
import com.zjt.codingsandbox.model.JudgeInfo;

import java.util.ArrayList;

public class ResponseUtils {
    /**
     * 构建错误响应
     * @param e
     * @return
     */
    public static ExecuteCodeResponse getErrExecuteCodeResponse(Throwable e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setMessage(e.getMessage());
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
    }
}
