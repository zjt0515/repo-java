package com.zjt.ojjudgeservice;

import cn.hutool.json.JSONUtil;
import com.zjt.ojcommon.common.ErrorCode;
import com.zjt.ojcommon.exception.BusinessException;
import com.zjt.ojjudgeservice.codesandbox.impl.RemoteCodeSandbox;
import com.zjt.ojjudgeservice.strategy.JudgeContext;
import com.zjt.ojmodel.model.codesandbox.ExecuteCodeRequest;
import com.zjt.ojmodel.model.codesandbox.ExecuteCodeResponse;
import com.zjt.ojmodel.model.codesandbox.JudgeInfo;
import com.zjt.ojmodel.model.dto.question.JudgeCase;
import com.zjt.ojmodel.model.dto.questionsubmit.QuestionSubmitTestRequest;
import com.zjt.ojmodel.model.entity.Question;
import com.zjt.ojmodel.model.entity.QuestionSubmit;
import com.zjt.ojmodel.model.enums.JudgeInfoMessageEnum;
import com.zjt.ojmodel.model.enums.SubmitStatusEnum;
import com.zjt.ojserviceclient.service.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {
    @Value("${codesandbox.type:example}")
    private String type;

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;

    @Resource
    private RemoteCodeSandbox remoteCodeSandbox;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1. 查找submit，校验信息
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        if (!questionSubmit.getStatus().equals(SubmitStatusEnum.WAITING.getValue())) {
            // 判题状态不为WAITING
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已经进入判题状态");
        }

        // 2. Update QuestionSubmit(Status -> JUDGING)
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setStatus(SubmitStatusEnum.JUDGING.getValue());
        questionSubmitUpdate.setId(questionSubmitId);

        boolean updateStatus = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!updateStatus) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "判题状态更新错误");
        }

        // 3. 调用代码沙箱
        //CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        //codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);

        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = remoteCodeSandbox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();

        // 4. 根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);

        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        // Update questionSubmit
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(SubmitStatusEnum.SUCCESS.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        updateStatus = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if(!updateStatus) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "判题状态更新错误");
        }
        // Update Question (acceptedNum)
        if (JudgeInfoMessageEnum.ACCEPTED.getValue().equals(judgeInfo.getMessage())){
            Question questionById = questionFeignClient.getQuestionById(questionId);
            Integer acceptedNum = questionById.getAcceptedNum();
            questionById.setAcceptedNum(acceptedNum + 1);
            questionFeignClient.updateQuestionById(questionById);
        }
        return questionFeignClient.getQuestionSubmitById(questionSubmitId);
    }

    @Override
    public ExecuteCodeResponse doTest(QuestionSubmitTestRequest questionSubmitTestRequest) {
        // 1. 校验信息
        String language = questionSubmitTestRequest.getLanguage();
        String code = questionSubmitTestRequest.getCode();
        List<String> inputList = questionSubmitTestRequest.getJudgeInputCase();

        if (language == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "语言不存在");
        }
        if (code == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "代码不存在");
        }
        if (inputList == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "样例不存在");
        }

        // 调用代码沙箱
        //CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        //codeSandbox = new CodeSandboxProxy(codeSandbox);

        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = remoteCodeSandbox.executeCode(executeCodeRequest);

        return  executeCodeResponse;
    }
}
