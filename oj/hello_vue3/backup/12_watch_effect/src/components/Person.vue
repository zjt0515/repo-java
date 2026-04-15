<template>
  <div class="person">
    <h2>需求：当水温达到60°或水位达到80，水温不能再高</h2>
    <h2>温度{{ temp }}°</h2>
    <h2>水位{{ height }}cm</h2>
    <button @click="changeHeight">水位变化</button>
    <button @click="changeTemp">温度变化</button>
  </div>
</template>

<script setup lang="ts" name="Person">
import { ref, watch, watchEffect } from 'vue';

let height = ref(0)
let temp = ref(0)
function changeHeight(){
  height.value += 10
}
function changeTemp(){
  temp.value += 10
}
// watch实现
// watch([temp, height],(val)=>{
//   let [tempVal, heightVal] = val
//   if(tempVal >= 100 || heightVal >= 30){
//     console.log('不能再升高了')
//   }
// })
// watchEffect实现
watchEffect(()=>{
  if(temp.value >= 10||height.value >= 30){
    console.log('不能再高了')
  }
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