package com.zjt.ojjudgeservice.strategy.js;

import cn.hutool.json.JSONUtil;
import com.zjt.ojjudgeservice.strategy.JudgeContext;
import com.zjt.ojjudgeservice.strategy.JudgeStrategy;
import com.zjt.ojmodel.model.codesandbox.JudgeInfo;
import com.zjt.ojmodel.model.dto.question.JudgeCase;
import com.zjt.ojmodel.model.dto.question.JudgeConfig;
import com.zjt.ojmodel.model.entity.Question;
import com.zjt.ojmodel.model.enums.JudgeInfoMessageEnum;


import java.util.List;
import java.util.Optional;

/**
 * Java 程序的判题策略
 */
public class JavascriptLanguageJudgeStrategy implements JudgeStrategy {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        // 获取上下文
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);
        String message = judgeInfo.getMessage();

        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;

        // build response
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);

        if (JudgeInfoMessageEnum.COMPILE_ERROR.getValue().equals(message)){
            judgeInfoResponse.setMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
            return  judgeInfoResponse;
        }else if (JudgeInfoMessageEnum.RUNTIME_ERROR.getValue().equals(message)){
            judgeInfoResponse.setMessage(JudgeInfoMessageEnum.RUNTIME_ERROR.getValue());
            return  judgeInfoResponse;
        }else if (JudgeInfoMessageEnum.SYSTEM_ERROR.getValue().equals(message)){
            judgeInfoResponse.setMessage(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue());
            return  judgeInfoResponse;
        }else if (JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue().equals(message)){
            judgeInfoResponse.setMessage(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue());
            return judgeInfoResponse;
        }else if (JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getValue().equals(message)){
            judgeInfoResponse.setMessage(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getValue());
            return judgeInfoResponse;
        }else if (JudgeInfoMessageEnum.OUTPUT_LIMIT_EXCEEDED.getValue().equals(message)){
            judgeInfoResponse.setMessage(JudgeInfoMessageEnum.OUTPUT_LIMIT_EXCEEDED.getValue());
            return judgeInfoResponse;
        }

        // set judgeinfo.message

        // 先判断沙箱执行的结果输出数量是否和预期输出数量相等
        if (outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        // 依次判断每一项输出和预期输出是否相等
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.getOutput().equals(outputList.get(i))) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }
        // 题目限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();
        // MLE
        if (memory > needMemoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        // TLE
        // 执行开销
        long PROGRAM_TIME_COST = 2000L;
        if ((time - PROGRAM_TIME_COST) > needTimeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}
