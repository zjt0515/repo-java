<template>
  <div class="person">
    <h2>姓名：{{ name }}</h2>
    <h2>年龄：{{ age }}</h2>
    <button @click="changeName">修改名字</button>
    <button @click="changeAge">修改年龄</button>
    <button @click="showTel">查看联系方式</button>
  </div>
</template>

<script lang="ts">
  export default {
    name: 'Person',
    beforeCreate() {
      console.log('beforcreate')
    },
    setup(){
      console.log(this) //setup中的this是undefine， vue3中弱化this
      // data
      let name = '张三'
      let age = 19
      let tel = '12345678'
      //此时的数据并不是响应的
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
      return {name:name, age:age, tel, changeAge, changeName, showTel}
      //return ()=>'返回单语句箭头渲染函数'
    }
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