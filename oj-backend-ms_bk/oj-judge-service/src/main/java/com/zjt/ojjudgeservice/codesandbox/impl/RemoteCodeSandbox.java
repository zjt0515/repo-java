package com.zjt.ojjudgeservice.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import com.zjt.ojcommon.common.ErrorCode;
import com.zjt.ojcommon.exception.BusinessException;
import com.zjt.ojjudgeservice.codesandbox.CodeSandbox;
import com.zjt.ojmodel.model.codesandbox.ExecuteCodeRequest;
import com.zjt.ojmodel.model.codesandbox.ExecuteCodeResponse;
import com.zjt.ojmodel.model.codesandbox.JudgeInfo;
import com.zjt.ojmodel.model.enums.JudgeInfoMessageEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RemoteCodeSandbox implements CodeSandbox {
    public static final String AUTH_HEADER = "auth";

    public static final String AUTH_HEADER_SECRET = "ExampleSecretKey";

    /** 沙箱服务地址 */
    @Value("${codesandbox.url:http://localhost:8115/executeCode}")
    private String CODESANDBOX_URL;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(CODESANDBOX_URL).header(AUTH_HEADER, AUTH_HEADER_SECRET).body(json).execute().body();

        if(StringUtils.isBlank(responseStr)){
            //throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "RemoteCodeSandbox Executecode Error!" + responseStr);
            ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
            executeCodeResponse.setStatus(3);
            JudgeInfo judgeInfo = new JudgeInfo();
            judgeInfo.setMessage(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue());
            executeCodeResponse.setJudgeInfo(judgeInfo);
            return executeCodeResponse;
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
