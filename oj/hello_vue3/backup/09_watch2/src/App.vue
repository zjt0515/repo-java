<!-- 组件结构 -->
<template>
    <Person/>
</template>
<!-- 组件脚本 -->
<script lang="ts">
    import Person from './components/Person.vue'
    //默认暴露
    export default {
        name:'App', //组件名
        components:{Person} //注册组件
    }
</script>
<!-- 组件样式 -->
<style>
    .app {
        background-color: #ddd;
        box-shadow: 0 0 10px;
        border-radius: 10px;
        padding: 20px;
    }
</style>