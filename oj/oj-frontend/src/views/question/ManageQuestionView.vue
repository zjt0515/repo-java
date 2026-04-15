<template>
  <div id="manageQuestionView">
    <el-table :data="dataList" stripe style="width: 100%">
      <el-table-column label="ID" prop="id" />
      <el-table-column label="标题" prop="title" />
      <el-table-column label="内容" prop="content" />
      <el-table-column label="答案" prop="answer" />
      <el-table-column label="样例" prop="judgeCase" />
      <el-table-column label="限制" prop="judgeConfig" />
      <el-table-column label="标签" prop="tags" />
      <el-table-column align="right">
        <template #header>
          <el-input v-model="search" size="small" placeholder="Type to search" />
        </template>
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.$index, scope.row)">
            Edit
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">
            Delete
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-pagination v-model:current-page="searchParams.current" v-model:page-size="searchParams.pageSize"
      :page-sizes="[10, 20, 50, 100]" :small="false" :disabled="false" :background="false"
      layout="sizes, prev, pager, next, jumper" :page-count="pageCounts" @size-change="handleSizeChange"
      @current-change="handleCurrentChange" />
  </div>

</template>

<script lang="ts" setup>
import { ElMessage } from 'element-plus';
import { QuestionControllerService } from '../../../generated/services/QuestionControllerService';
import { computed, onMounted, ref, watch, watchEffect } from 'vue'
import { Question } from '../../../generated/models/Question';
import { useRouter } from 'vue-router';
interface question {
  id: number
  title: string
  content: string
  answer: string
  judgeCase: string
  judgeConfig: string
  tags: string
}

const total = ref(0)
const pageCounts = computed(() => Math.ceil(total.value / searchParams.value.pageSize))

const dataList = ref([] as question[])

// 分页请求数据参数
const searchParams = ref({
  pageSize: 2,
  current: 1
})
// 分页请求参数 变化
const handleSizeChange = (size: number) => {
  console.log(size)
  searchParams.value = {
    ...searchParams.value,
    pageSize: size
  }
}
const handleCurrentChange = (current: number) => {
  // 错误写法：无法被监视到 searchParams.value.current = current
  searchParams.value = {
    ...searchParams.value,
    current: current
  }
}
// 请求题目数据
const loadData = async () => {
  const res = await QuestionControllerService.listQuestionByPageUsingPost(
    searchParams.value
  )
  if (res.code === 0) {
    dataList.value = (res.data.records)
    // bug: 后端接口返回的total是string？
    total.value = Number(res.data.total)
  } else {
    ElMessage('请求失败,' + res.message)
  }
}
onMounted(() => {
  loadData()
})
watchEffect(() => {
  loadData()
})
const search = ref('')
// const filterTableData = computed(() =>
//   tableData.filter(
//     (data) =>
//       !search.value ||
//       data.name.toLowerCase().includes(search.value.toLowerCase())
//   )
// )
// 编辑题目
const router = useRouter()
const handleEdit = async (index: number, row: Question) => {
  //跳转到编辑问题页面
  router.push({
    path: '/update/question',
    query: {
      id: row.id
    }
  })
}
const handleDelete = async (row: Question) => {
  const res = await QuestionControllerService.deleteQuestionUsingPost({ id: row.id })
  if (res.code === 0) {
    ElMessage("删除成功")
    // dataList.value.slice
  } else {
    ElMessage("删除失败" + res.message)
  }
}

</script>
