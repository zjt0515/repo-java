<!-- 组件结构 -->
<template>
    <h2 ref="title2">你好</h2>
    <button @click="showLog">展示你好</button>
    <Person ref="ren"/>
</template>
<!-- 组件脚本 -->
<script setup lang="ts" name="App">
    import Person from './components/Person.vue'
    import {ref} from 'vue'
    let title2 = ref()
    let ren = ref()
    function showLog(){
        console.log(ren.value)
    }
</script>
<!-- 组件样式 -->
<style scoped>
    .app {
        background-color: #ddd;
        box-shadow: 0 0 10px;
        border-radius: 10px;
        padding: 20px;
    }
</style>