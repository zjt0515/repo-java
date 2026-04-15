<template>
  <div class="person">
  </div>
</template>

<script setup lang="ts" name="Person">
  import {type PersonInter,type Persons } from '@/types'
  import { ref } from 'vue'
  // let person:PersonInter = {id:'1', name:'steve', age:10}
  let personList:Persons = [
    { id: '0', name: '张三', age:20 },
    { id: '1', name: '张三', age:201 },
    { id: '2', name: '张三', age:2022}
  ]

</script>

<style scoped>
  
</style>