<template>
  <div class="person">
    <h2>求和：{{ person.age }}</h2>
    <h2>求和：{{ person.name }}</h2>
    <button @click="changeAge">age+1</button>
    <button @click="changeName">修改名字</button>
    <button @click="changePerson">换人</button>
  </div>
</template>

<script setup lang="ts" name="Person">
  import { reactive, ref, watch} from 'vue'
  let person = reactive({
    name:'张三',
    age: 19
  })
  function changeAge(){
    person.age += 1
  }
  function changeName(){
    person.name += '~'
  }
  // function changePerson(){
  //   person = { name: '李四', age: 199}
  // }
  function changePerson(){
    Object.assign(person, {name:'李四',age:199})
  }
  // reactive默认开启深度监视
  const stopWatch = watch(person,(newValue, oldValue)=>{
    console.log(newValue,oldValue)
  })
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