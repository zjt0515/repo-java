package com.zjt.ojjudgeservice.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import com.zjt.ojcommon.common.ErrorCode;
import com.zjt.ojcommon.exception.BusinessException;
import com.zjt.ojjudgeservice.codesandbox.CodeSandbox;
import com.zjt.ojmodel.model.codesandbox.ExecuteCodeRequest;
import com.zjt.ojmodel.model.codesandbox.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

public class RemoteCodeSandbox implements CodeSandbox {
    public static final String AUTH_HEADER = "auth";

    public static final String AUTH_HEADER_SECRET = "ExampleSecretKey";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        String url = "http://host.docker.internal:8115/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url).header(AUTH_HEADER, AUTH_HEADER_SECRET).body(json).execute().body();

        if(StringUtils.isBlank(responseStr)){
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "RemoteCodeSandbox Executecode Error!" + responseStr);
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
