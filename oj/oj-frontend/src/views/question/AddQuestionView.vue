<template>
  <div id="addQuestionView">
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
        <el-button type="primary" @click="onSubmit">创建题目</el-button>
      </el-form-item>
    </el-form>

  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import MdEditor from '@/components/MdEditor.vue'
import { QuestionControllerService } from '../../../generated/services/QuestionControllerService'

// do not use same name with ref
const form = reactive({
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
})
const onContentChange = (v: string) => {
  form.content = v;
}
const onAnswerChange = (v: string) => {
  form.answer = v;
}
const onSubmit = async () => {
  console.log(form)
  const res = await QuestionControllerService.addQuestionUsingPost(form)
  if (res.code === 0) {
    ElMessage("添加成功");
  } else {
    ElMessage("添加失败" + res.message);
  }
}
// 标签
import { nextTick, ref } from 'vue'
import { ElInput, ElMessage } from 'element-plus'

const inputValue = ref('')
const inputVisible = ref(false)
const InputTagRef = ref<InstanceType<typeof ElInput>>()

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