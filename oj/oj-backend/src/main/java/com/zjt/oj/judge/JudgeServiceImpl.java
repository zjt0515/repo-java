package com.zjt.oj.judge;

import cn.hutool.json.JSONUtil;
import com.zjt.oj.common.ErrorCode;
import com.zjt.oj.exception.BusinessException;
import com.zjt.oj.judge.codesandbox.CodeSandbox;
import com.zjt.oj.judge.codesandbox.CodeSandboxFactory;
import com.zjt.oj.judge.codesandbox.CodeSandboxProxy;
import com.zjt.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.zjt.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.zjt.oj.model.dto.question.JudgeCase;
import com.zjt.oj.model.dto.question.JudgeConfig;
import com.zjt.oj.judge.codesandbox.model.JudgeInfo;
import com.zjt.oj.model.entity.Question;
import com.zjt.oj.model.entity.QuestionSubmit;
import com.zjt.oj.model.enums.JudgeInfoResultsEnum;
import com.zjt.oj.model.enums.SubmitStatusEnum;
import com.zjt.oj.model.vo.QuestionVO;
import com.zjt.oj.service.QuestionService;
import com.zjt.oj.service.QuestionSubmitService;
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
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionService questionService;

    @Override
    public QuestionVO doJudge(long questionSubmitId) {
        // 1.传入 题目id | 代码 | 语言
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Question question = questionService.getById(questionSubmit.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        if (!questionSubmit.getStatus().equals(SubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目不处于等待判题状态");
        }
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setStatus(SubmitStatusEnum.JUDGING.getValue());
        questionSubmitUpdate.setId(questionSubmitId);
        boolean b = questionSubmitService.updateById(questionSubmitUpdate);
        if (!b) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目提交状态更新错误");
        }
        // 2. 调用沙箱，获取结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);

        // 获取输入测试用例
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList).build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        // 根据结果，判断题目运行
        // 1) 判断输出结果数量
        JudgeInfoResultsEnum judgeInfoResultsEnum = JudgeInfoResultsEnum.WAITING;
        List<String> outputList = executeCodeResponse.getOutputList();
        if (outputList.size() != inputList.size()){
            judgeInfoResultsEnum = JudgeInfoResultsEnum.WRONG_ANSWER;
            return null;
        }
        // 2) 依次判断每个输出结果是否和输出样例一致
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.equals(outputList.get(i))){
                judgeInfoResultsEnum = JudgeInfoResultsEnum.WRONG_ANSWER;
                return null;
            }
        }


        // 3) 限制判断
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
        String message = judgeInfo.getMessage();
        Long memory = judgeInfo.getMemory();
        Long time = judgeInfo.getTime();
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long expectedTimeLimit = judgeConfig.getTimeLimit();
        Long expectedMemoryLimit = judgeConfig.getMemoryLimit();
        if (time > expectedTimeLimit){
            judgeInfoResultsEnum = JudgeInfoResultsEnum.TIME_LIMIT_EXCEEDED;
            return null;
        }
        if (memory > expectedMemoryLimit){
            judgeInfoResultsEnum = JudgeInfoResultsEnum.MEMORY_LIMIT_EXCEED;
            return null;
        }

        // Long id = questionSubmit.getId();
        //
        // String judgeInfo = questionSubmit.getJudgeInfo();
        // Integer status = questionSubmit.getStatus();
        // Long questionId = questionSubmit.getQuestionId();
        // Long userId = questionSubmit.getUserId();
        // Date createTime = questionSubmit.getCreateTime();
        // Date updateTime = questionSubmit.getUpdateTime();
        // Integer isDelete = questionSubmit.getIsDelete();


        // 3. 根据结果，返回VO
        return null;
    }
}
