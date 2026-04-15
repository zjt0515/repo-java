<!-- 组件结构 -->
<template>
    <h2 a="1+1" :b="1+1" c="x" :d="x" ref="qwe"></h2>
    <Person a="哈哈" :list="personList"/>
</template>
<!-- 组件脚本 -->
<script setup lang="ts" name="App">
    import { reactive } from 'vue';
    import Person from './components/Person.vue'
    import {type Persons } from './types';
    let x = 9
    let personList = reactive<Persons>([
        {id:'1', name:'张三2',age:19},
        {id:'12', name:'张三22',age:19},
        {id:'013', name:'张三333',age:19},
    ])
    console.log(personList)
</script>
<!-- 组件样式 -->
<style scoped>
</style>