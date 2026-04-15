<template>
  <div class="person">
    <h2>brand：{{ car.brand }}</h2>
    <h2>price：{{ car.price }}</h2>
    <button @click="changePrice">修改价格</button>

    <hr>

    <ul>
      <li v-for="game in games" :key="game.id">{{  game.name }}</li>
    </ul>

    <button @click="changeFirstGame">修改第一个游戏名</button>
    <h2>测试：{{ obj.a_company.b_store.name }}</h2>
    <button @click="changeStoreName">测试</button>
  </div>
</template>

<script setup lang="ts" name="Person">
  import { reactive } from 'vue';
  let car = reactive({
    brand: '奔驰',
    price: 100,
  })
  let games = reactive([
    {id: '1', name: '王者'},
    {id: '2', name: '元神'},
  ])
  let obj = reactive({
    a_company:{
      b_store:{
        name:666
      }
    }
  })
  console.log(car)
  function changePrice(){
    car.price++;
  }
  function changeFirstGame(){
    games[0].name = 'CF'
  }
</script>

<style scoped>

</style>