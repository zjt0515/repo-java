package com.zjt.ojquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjt.ojcommon.common.ErrorCode;
import com.zjt.ojcommon.constant.CommonConstant;
import com.zjt.ojcommon.exception.BusinessException;
import com.zjt.ojcommon.utils.SqlUtils;
import com.zjt.ojmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.zjt.ojmodel.model.dto.questionsubmit.QuestionSubmitHeatmapRequest;
import com.zjt.ojmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.zjt.ojmodel.model.entity.Question;
import com.zjt.ojmodel.model.entity.QuestionSubmit;
import com.zjt.ojmodel.model.entity.User;
import com.zjt.ojmodel.model.enums.SubmitLangEnum;
import com.zjt.ojmodel.model.enums.SubmitStatusEnum;
import com.zjt.ojmodel.model.vo.QuestionSubmitHeatmapItemVO;
import com.zjt.ojmodel.model.vo.QuestionSubmitHeatmapVO;
import com.zjt.ojmodel.model.vo.QuestionSubmitVO;
import com.zjt.ojquestionservice.mapper.QuestionSubmitMapper;
import com.zjt.ojquestionservice.rabbitmq.MessageProducer;
import com.zjt.ojquestionservice.service.QuestionService;
import com.zjt.ojquestionservice.service.QuestionSubmitService;
import com.zjt.ojserviceclient.service.UserFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author genshinya
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2023-12-13 14:14:59
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    private static final DateTimeFormatter HEATMAP_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final ZoneId HEATMAP_ZONE_ID = ZoneId.of("Asia/Hong_Kong");

    private static final long DEFAULT_HEATMAP_DAYS = 365L;

    private static final long MAX_HEATMAP_DAYS = 366L;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private MessageProducer messageProducer;

    @Resource
    private QuestionSubmitMapper questionSubmitMapper;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest 提交请求入参
     * @param loginUser                用户
     * @return long 提交id
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 参数校验
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() == null
                || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目提交参数错误");
        }
        String language = questionSubmitAddRequest.getLanguage();
        SubmitLangEnum langEnum = SubmitLangEnum.getEnumByValue(language);
        if (langEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        String code = questionSubmitAddRequest.getCode();
        if (StringUtils.isBlank(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交代码不能为空");
        }

        // 判断Question实体是否存在，根据类别获取实体
        Long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        // todo：限流方式限制用户同时提交多条重复题目
        // 先直接插入数据
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(code);
        // 设置初始状态
        questionSubmit.setStatus(SubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);

        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }

        // update question
        Question question1 = questionService.getById(questionId);
        Integer submitNum = question1.getSubmitNum();
        UpdateWrapper<Question> questionUpdateWrapper = new UpdateWrapper<>();
        questionUpdateWrapper.eq("id", questionId);
        questionUpdateWrapper.set("submit_num", submitNum + 1);
        questionService.update(questionUpdateWrapper);

        // 调用代码沙箱判题
        Long questionSubmitId = questionSubmit.getId();

        messageProducer.sendMessage("code_exchange", "my_routingKey", String.valueOf(questionSubmitId));
        //CompletableFuture.runAsync(() -> {
        //    judgeFeignService.doJudge(questionSubmitId);
        //});
        return questionSubmitId;
    }

    /**
     * 工具方法
     * 获取查询提交包装类的QW (用户根据查询字段，根据前端传来的请求对象，得到对应的QueryWrapper)
     * 查询字段：题目id | userid | 运行结果 | 编程语言
     *
     * @param questionSubmitQueryRequest 请求问题
     * @return QueryWrapper<QuestionSubmit>
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        Long userId = questionSubmitQueryRequest.getUserId();
        String language = questionSubmitQueryRequest.getLanguage();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Integer status = questionSubmitQueryRequest.getStatus();

        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "question_id", questionId);
        // 如果用户传入非法状态，则是包括所有状态
        queryWrapper.eq(SubmitStatusEnum.getEnumByValue(status) != null, "status", status);

        queryWrapper.eq("is_delete", 0);
        // 拼接查询顺序条件
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 脱敏类、封装类 submit->submitVO
     * 优化：减少多次查询，例如减少查询登录用户，直接拿接受外层传进来的loginuser
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        // 先转成VO类
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 不是本人代码/管理员 不能看到对应的敏感信息？
        long id = loginUser.getId();
        if (id != questionSubmit.getUserId() && userFeignClient.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> this.getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

    @Override
    public QuestionSubmitHeatmapVO getQuestionSubmitHeatmap(QuestionSubmitHeatmapRequest questionSubmitHeatmapRequest,
                                                            User loginUser) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (questionSubmitHeatmapRequest == null) {
            questionSubmitHeatmapRequest = new QuestionSubmitHeatmapRequest();
        }

        Long targetUserId = questionSubmitHeatmapRequest.getUserId();
        if (targetUserId == null) {
            targetUserId = loginUser.getId();
        }
        if (targetUserId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "userId 不合法");
        }
        if (!targetUserId.equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅本人或管理员可查看提交热力图");
        }

        LocalDate endDate = parseHeatmapDate(questionSubmitHeatmapRequest.getEndDate(), "endDate");
        if (endDate == null) {
            endDate = LocalDate.now(HEATMAP_ZONE_ID);
        }
        LocalDate startDate = parseHeatmapDate(questionSubmitHeatmapRequest.getStartDate(), "startDate");
        if (startDate == null) {
            startDate = endDate.minusDays(DEFAULT_HEATMAP_DAYS - 1);
        }
        if (startDate.isAfter(endDate)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "startDate 不能晚于 endDate");
        }

        long dayCount = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (dayCount > MAX_HEATMAP_DAYS) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "热力图查询范围不能超过 366 天");
        }

        Timestamp startTime = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTime = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());
        List<QuestionSubmitHeatmapItemVO> records = questionSubmitMapper.selectSubmitHeatmap(targetUserId, startTime, endTime);
        Map<String, QuestionSubmitHeatmapItemVO> dateItemMap = records.stream()
                .collect(Collectors.toMap(QuestionSubmitHeatmapItemVO::getDate, item -> item, (oldItem, newItem) -> oldItem));

        List<QuestionSubmitHeatmapItemVO> items = new ArrayList<>();
        long totalCount = 0L;
        long maxCount = 0L;
        for (long i = 0; i < dayCount; i++) {
            String date = startDate.plusDays(i).format(HEATMAP_DATE_FORMATTER);
            QuestionSubmitHeatmapItemVO item = dateItemMap.get(date);
            if (item == null) {
                item = new QuestionSubmitHeatmapItemVO();
                item.setDate(date);
                item.setSubmitCount(0L);
                item.setAcceptedCount(0L);
            }
            if (item.getSubmitCount() == null) {
                item.setSubmitCount(0L);
            }
            if (item.getAcceptedCount() == null) {
                item.setAcceptedCount(0L);
            }
            totalCount += item.getSubmitCount();
            maxCount = Math.max(maxCount, item.getSubmitCount());
            items.add(item);
        }

        QuestionSubmitHeatmapVO heatmapVO = new QuestionSubmitHeatmapVO();
        heatmapVO.setStartDate(startDate.format(HEATMAP_DATE_FORMATTER));
        heatmapVO.setEndDate(endDate.format(HEATMAP_DATE_FORMATTER));
        heatmapVO.setTimezone(HEATMAP_ZONE_ID.getId());
        heatmapVO.setTotalCount(totalCount);
        heatmapVO.setMaxCount(maxCount);
        heatmapVO.setItems(items);
        return heatmapVO;
    }

    private LocalDate parseHeatmapDate(String date, String fieldName) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        try {
            return LocalDate.parse(date, HEATMAP_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, fieldName + " 格式应为 yyyy-MM-dd");
        }
    }

}

