package com.zjt.ojjudgeservice;


import com.zjt.ojjudgeservice.strategy.DefaultJudgeStrategy;
import com.zjt.ojjudgeservice.strategy.java.JavaLanguageJudgeStrategy;
import com.zjt.ojjudgeservice.strategy.JudgeContext;
import com.zjt.ojjudgeservice.strategy.JudgeStrategy;
import com.zjt.ojjudgeservice.strategy.js.JavascriptLanguageJudgeStrategy;
import com.zjt.ojmodel.model.codesandbox.JudgeInfo;
import com.zjt.ojmodel.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();

        // 根据language初始化不同的judgeStrategy子类
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }else if ("javascript".equals(language) || "js".equals(language)){
            judgeStrategy = new JavascriptLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
