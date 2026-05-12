package com.zjt.codingsandbox.controller;


import cn.hutool.core.util.StrUtil;
import com.zjt.codingsandbox.sandbox.java.*;
import com.zjt.codingsandbox.model.ExecuteCodeRequest;
import com.zjt.codingsandbox.model.ExecuteCodeResponse;
import com.zjt.codingsandbox.sandbox.node.NodeCodeSandbox;
import com.zjt.codingsandbox.sandbox.node.NodeCodeSandboxDocker;
import com.zjt.codingsandbox.sandbox.node.NodeCodeSandboxTemplate;
import com.zjt.codingsandbox.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController("/")
public class MainController {

    @Resource
    private DockerCodeSandbox dockerCodeSandbox;

    @Resource
    private NativeCodeSandbox nativeCodeSandbox;

    @Resource
    private JavaCodeSandbox javaCodeSandbox;

    @Resource
    private  JavaCodeSandboxDocker javaCodeSandboxDocker;

    @Resource
    private NodeCodeSandbox nodeCodeSandbox;

    @Resource
    private NodeCodeSandboxDocker nodeCodeSandboxDocker;

    public static final String AUTH_HEADER = "auth";

    public static final String AUTH_HEADER_SECRET = "ExampleSecretKey";


    @PostMapping("/executeCode")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request,
                                           HttpServletResponse response) {
        // 鉴权
        String authHeader = request.getHeader(AUTH_HEADER);
        if (!AUTH_HEADER_SECRET.equals(authHeader)) {
            response.setStatus(403);
            return null;
        }

        // 参数校验
        if (executeCodeRequest == null) {
            throw new RuntimeException("ExecuteCodeRequest is null!");
        }
        String code = executeCodeRequest.getCode();
        List<String> inputList = executeCodeRequest.getInputList();
        String language = executeCodeRequest.getLanguage();
        if (StrUtil.isAllBlank(code, language) || inputList == null) {
            return ResponseUtils.getErrExecuteCodeResponse(new RuntimeException("请求参数错误，代码或输入用例或语言不能为空"));
        }

        // 选择代码沙箱，并执行
        if ("java".equals(language)) {
            //return nativeCodeSandbox.executeCode(executeCodeRequest);
            return javaCodeSandboxDocker.executeCode(executeCodeRequest);
            //return javaCodeSandbox.executeCode(executeCodeRequest);
            //return  dockerCodeSandbox.executeCode(executeCodeRequest);
        } else if ("javascript".equals(language) || "js".equals(language)) {
            //return nodeCodeSandbox.executeCode(executeCodeRequest);
            return nodeCodeSandboxDocker.executeCode(executeCodeRequest);
        }
        return null;
    }
}
