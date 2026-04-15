<template>
  <div class="person">
    <h2>姓名：{{ name }}</h2>
    <h2>年龄：{{ age }}</h2>
    <h2>地址：{{ address }}</h2>
    <button @click="changeName">修改名字</button>
    <button @click="changeAge">修改年龄</button>
    <button @click="showTel">查看联系方式</button>
  </div>
</template>

<script setup lang="ts" name="Person1234">
  // data
  let name = '张三'
  let age = 19
  let tel = '12345678'
  let address = '南京'
  // methods
  function changeName(){
    name = 'zhangsan'
  }
  function changeAge(){
    age++
  }
  function showTel(){
    alert(tel)
  }
</script>

<style scoped>
  .person {
    background-color: skyblue;
    box-shadow: 0 0 10px;
    border-radius: 10px;
    padding: 20px;
  }
  button {
    margin: 0 10px;
  }
  
</style>