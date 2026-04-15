<template>
  <div id="updateQuestionView">
    <h4>当前编辑的题目id为: {{ form.id }}</h4>
    <el-form :model="form" label-width="auto" style="max-width: 600px">
      <el-form-item label="标题">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="题目信息">
        <MdEditor :value="form.content" :handleChange="onContentChange" />
      </el-form-item>
      <el-form-item label="答案">
        <MdEditor :value="form.answer" :handleChange="onAnswerChange" />
      </el-form-item>

      <!-- judgeConfig -->
      <el-form-item label="时间限制">
        <el-input-number v-model="form.judgeConfig.timeLimit" :step="100" />
      </el-form-item>
      <el-form-item label="内存限制">
        <el-input-number v-model="form.judgeConfig.memoryLimit" :step="100" />
      </el-form-item>
      <el-form-item label="堆栈限制">
        <el-input-number v-model="form.judgeConfig.stackLimit" :step="100" />
      </el-form-item>

      <!-- 标签 -->
      <el-form-item label="标签">
        <div class="flex gap-2">
          <el-tag v-for="tag in form.tags" :key="tag" closable :disable-transitions="false" @close="handleClose(tag)">
            {{ tag }}
          </el-tag>
          <el-input v-if="inputVisible" ref="InputRef" v-model="inputValue" class="w-20" size="small"
            @keyup.enter="handleInputConfirm" @blur="handleInputConfirm" />
          <el-button v-else class="button-new-tag" size="small" @click="showInputTag">
            + 新增标签
          </el-button>
        </div>
      </el-form-item>
      <!-- 样例 -->
      <el-form-item v-for="(judgeCase, index) in form.judgeCase" :key="judgeCase" :label="'样例' + index">
        <el-input v-model="judgeCase.input" />
        <el-input v-model="judgeCase.output" />
        <el-button type="danger" class="mt-2" @click.prevent="removeJudgeCase(judgeCase)" style="margin-top: 32px;">
          Delete case
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-button @click="addJudgeCase">New Case</el-button>
      </el-form-item>
      <el-form />
      <!-- 提交题目 -->
      <el-form-item>
        <el-button type="primary" @click="onSubmit">更新题目</el-button>
      </el-form-item>
    </el-form>

  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, watchEffect } from 'vue'
import MdEditor from '@/components/MdEditor.vue'
import { QuestionControllerService } from '../../../generated/services/QuestionControllerService'

const form = reactive({
  id: -1,
  "answer": "",
  "content": "",
  "judgeCase": [
    {
      "input": "",
      "output": ""
    }
  ],
  "judgeConfig": {
    "memoryLimit": 0,
    "stackLimit": 0,
    "timeLimit": 0
  },
  "tags": [] as string[],
  "title": ""
});

const onContentChange = (v: string) => {
  form.content = v;
}
const onAnswerChange = (v: string) => {
  form.answer = v;
}
// 更新题目请求发送
const onSubmit = async () => {
  console.log(form)
  const res = await QuestionControllerService.updateQuestionUsingPost(form as any)
  if (res.code === 0) {
    ElMessage("更新成功");
  } else {
    ElMessage("更新失败, " + res.message);
  }
}
// 标签
import { nextTick, ref } from 'vue'
import { ElInput, ElMessage } from 'element-plus'
import { useRoute } from 'vue-router';
import { watch } from 'fs';

const inputValue = ref('')
const inputVisible = ref(false)
const InputTagRef = ref<InstanceType<typeof ElInput>>()

const route = useRoute()
const loadData = async () => {
  const id = route.query.id;
  const res = await QuestionControllerService.getQuestionByIdUsingGet(id as any);
  if (res.code === 0) {

    ElMessage("获取成功");
    console.log(res.data)
    // Object.assign(form, res.data)
    form.id = res.data?.id as number;
    form.answer = res.data?.answer as string;
    form.content = res.data?.content as string;
    form.title = res.data?.title as string;
    if (res.data?.judgeCase) {
      form.judgeCase = JSON.parse(res.data?.judgeCase);
    }
    if (res.data?.judgeConfig) {
      form.judgeConfig = JSON.parse(res.data?.judgeConfig);
    }
    if (res.data?.tags) {
      form.tags = JSON.parse(res.data?.tags);
    }

  } else {
    ElMessage("获取失败, " + res.message);
  }
}
onMounted(() => {
  loadData()
})


// 添加标签
const handleClose = (tag: string) => {
  form.tags.splice(form.tags.indexOf(tag), 1)
}

const showInputTag = () => {
  inputVisible.value = true
  nextTick(() => {
    InputTagRef.value!.input!.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    form.tags.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
}
// 动态添加表单
interface JudgeCase {
  input: string
  output: string
}
const removeJudgeCase = (item: JudgeCase) => {
  const index = form.judgeCase.indexOf(item)
  if (index !== -1) {
    form.judgeCase.splice(index, 1)
  }
}
const addJudgeCase = () => {
  form.judgeCase.push({
    input: '',
    output: '',
  })
}


</script>