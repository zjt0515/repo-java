<template>
  <div id="questionsView">
    <!-- 搜索 -->
    <el-form :inline="true" :model="searchParams" class=" demo-form-inline">
      <el-form-item label="题目">
        <el-input v-model="searchParams.title" placeholder="题目" clearable />
      </el-form-item>
      <el-form-item label="标签" style="min-width: 280px">
        <el-select v-model="searchParams.tags" multiple filterable allow-create default-first-option
          :reserve-keyword="false" placeholder="标签">
          <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">查询</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格 -->
    <el-table :data="dataList" stripe style="width: 100%">
      <el-table-column label="ID" prop="id" />
      <el-table-column label="题目" prop="title">
        <template #default="scope">
          <el-link @click="doQuestion(scope)">
            {{ scope.row.title }}
          </el-link>
        </template>
      </el-table-column>

      <el-table-column label="标签" prop="tags">
        <template #default="scope">
          <el-tag v-for="tag in scope.row.tags " :key="tag" effect="plain">
            {{ tag }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="通过率" prop="submitNum">
        <template #default="scope">
          {{ scope.row.acceptedNum ? scope.row.submitNum / scope.row.acceptedNum : 0 + `% ( ${scope.row.submitNum}
          / ${scope.row.acceptedNum} )` }}
        </template>
      </el-table-column>
      <el-table-column align="right">
        <template #header>
          <el-input v-model="search" size="small" placeholder="Type to search" />
        </template>
        <template #default="scope">
          <!-- <el-button size="small" @click="handleEdit(scope.$index, scope.row)">
            Edit
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">
            Delete
          </el-button> -->
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
import { useRoute, useRouter } from 'vue-router';
import { QuestionQueryRequest } from '../../../generated/models/QuestionQueryRequest';
interface questionVO {
  id: number
  title: string
  tags: string
  submitNum: number
  acceptednum: number
}

const total = ref(0)
const pageCounts = computed(() => Math.ceil(searchParams.value.pageSize ? total.value / searchParams.value.pageSize : 0))

const dataList = ref([] as questionVO[])

// 分页请求数据参数
const searchParams = ref<QuestionQueryRequest>({
  pageSize: 2,
  current: 1,
  tags: []
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
  const res = await QuestionControllerService.listQuestionVoByPageUsingPost(
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
const onSubmit = () => {
  loadData()
}
const router = useRouter()
// 点击题目后进入做题页面
const doQuestion = (scope) => {
  router.push({
    path: `/question/${scope.row.id}`
  })
}
const search = ref('')
// const filterTableData = computed(() =>
//   tableData.filter(
//     (data) =>
//       !search.value ||
//       data.name.toLowerCase().includes(search.value.toLowerCase())
//   )
// )


</script>

<style scoped>
#questionsView {
  max-width: 1280px;
  margin: 0 auto;
}
</style>>
