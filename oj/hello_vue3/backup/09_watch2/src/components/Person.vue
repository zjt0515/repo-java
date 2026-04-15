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
  import { ref, watch} from 'vue'
  let person = ref({
    name:'张三',
    age: 19
  })
  function changeAge(){
    person.value.age += 1
  }
  function changeName(){
    person.value.name += '~'
  }
  function changePerson(){
    person.value = { name: '李四', age: 199}
  }
  // 监视ref对象，监视的是对象的地址值
  // 监视ref对象内部属性，手动开启深度监视
  // immdiate作用是:一开始就会
  const stopWatch = watch(person,(newValue, oldValue)=>{
    console.log(newValue,oldValue)
  },{deep:true,immediate:true})
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