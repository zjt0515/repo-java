<template>
  <div id="doQuestionView">
    <el-row :gutter="10">
      <el-col :xs="24" :sm="12">
        <el-card v-if="question">
          <MdViewer :value="question.content || '题目内容不存在'" />
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12">
        <el-select v-model="answer.language" placeholder="Select" style="width: 120px">
          <el-option v-for="item in langOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <MonacoEditor :language="answer.language" :value="answer.code" :handle-change="onCodeChange" />
        <el-button type="primary" @click="onSubmit">提交</el-button>
        {{ answer.language }}
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watchEffect } from 'vue'
import MdEditor from '@/components/MdEditor.vue'
import MonacoEditor from '@/components/MonacoEditor.vue'
import MdViewer from '@/components/MdViewer.vue';
import { QuestionSubmitControllerService } from '../../../generated/services/QuestionSubmitControllerService'
import { QuestionVO } from '../../../generated/models/QuestionVO';
import { QuestionSubmitAddRequest } from '../../../generated/models/QuestionSubmitAddRequest';
import { ElMessage } from 'element-plus';
import { QuestionControllerService } from '../../../generated/services/QuestionControllerService';


const question = ref<QuestionVO>();

const answer = ref<QuestionSubmitAddRequest>({
  code: "",
  language: "",
  questionId: 0
});

const onCodeChange = (v: string) => {
  answer.value.code = v;
}
interface Props {
  id: string
}
const props = withDefaults(defineProps<Props>(), {
  id: () => "",
});
const loadData = async () => {
  // 做题时传入questionVO
  const res = await QuestionControllerService.getQuestionVoByIdUsingGet(props.id as any);
  if (res.code === 0) {
    ElMessage("获取成功");
    console.log(question)
    question.value = res.data as QuestionVO;
    // Object.assign(form, res.data)
    // form.id = res.data?.id as number;
    // form.answer = res.data?.answer as string;
    // form.content = res.data?.content as string;
    // form.title = res.data?.title as string;
    // if (res.data?.judgeCase) {
    //   form.judgeCase = JSON.parse(res.data?.judgeCase);
    // }
    // if (res.data?.judgeConfig) {
    //   form.judgeConfig = JSON.parse(res.data?.judgeConfig);
    // }
    // if (res.data?.tags) {
    //   form.tags = JSON.parse(res.data?.tags);
    // }

  } else {
    ElMessage("获取失败, " + res.message);
  }
}
onMounted(() => {
  loadData()
})
const langOptions = [
  {
    value: 'cpp',
    label: 'C++',
  },
  {
    value: 'java',
    label: 'Java',
  },
  {
    value: 'python',
    label: 'Python',
  }
]

// 提交代码
const onSubmit = async () => {
  const res = await QuestionSubmitControllerService.doQuestionSubmitUsingPost({ ...answer as QuestionSubmitAddRequest, questionId: props.id as any })
  if (res.code === 0) {
    ElMessage({
      message: '提交成功',
      type: 'success',
      plain: true,
    })
  } else {
    ElMessage({
      message: "提交失败, " + res.message,
      type: 'error',
      plain: true,
    })
  }
}

</script>