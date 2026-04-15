<template>
  <div class="person">
    <h2>姓名:{{ person.name }}</h2>
    <h2>年龄:{{ person.age }}</h2>
    <h2>汽车1:{{ person.car.c1 }}</h2>
    <h2>汽车2:{{ person.car.c2 }}</h2>
    <button @click="changeName">修改名字</button>
    <button @click="changeAge">修改年龄</button>
    <button @click="changeCar1">修改汽车1</button>
    <button @click="changeCar2">修改汽车2</button>
    <button @click="changeCar">修改所有车</button>
  </div>
</template>

<script setup lang="ts" name="Person">
  import { reactive, ref, watch} from 'vue'
  // 数据
  let person = reactive({
    name:'张三',
    age: 18,
    car:{
      c1:'宝马',
      c2:'奥迪'
    }
  })
  function changeName(){
    person.name += '~'
  }
  function changeAge(){
    person.age += 1
  }
  function changeCar1(){
    person.car.c1 = '迈凯伦'
  }
  function changeCar2(){
    person.car.c2 = '大众'
  }
  function changeCar(){
    person.car = {c1:'电竞整车1',c2:'电竞整车2'}
  }
  watch([()=>person.name,person.car], (newValue,oldValue)=>{
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