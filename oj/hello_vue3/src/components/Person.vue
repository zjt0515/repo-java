<template>
  <div class="person">
    <h2>{{ a }}</h2>
    <h2>{{ list }}</h2>

    <ul>
      <li v-for="p in list" :key="p.id">
        {{ p.name }}
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts" name="Person">
import {type Persons } from '@/types';

//defineProps(['a','b'])
let x = defineProps<{list:Persons}>()
//接受list+限制类型+限制必要性+指定默认值

</script>

<style scoped>
  
</style>