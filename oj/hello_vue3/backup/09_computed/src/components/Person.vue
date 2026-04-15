<template>
  <div class="person">
    姓：<input type="text" v-model="firstName"> <br>
    名：<input type="text" v-model="lastName"> <br>
    <button @click="changeFullName">修改全名</button>
    全名：<span>{{  fullName }}</span> <br>
  </div>
</template>

<script setup lang="ts" name="Person">
  import { ref, computed} from 'vue'
  let firstName = ref('张')
  let lastName = ref('三')
  let fullName = computed({
    get(){
      return firstName.value.slice(0, 1).toUpperCase() + firstName.value.slice(1) + '-' + lastName.value
    },
    set(val){
      const [s1, s2] = val.split('-')
      firstName.value = s1;
      lastName.value = s2;
    }
  })
  function changeFullName(){
    fullName.value = 'li-si'
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