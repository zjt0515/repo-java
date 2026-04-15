<template>
  <div>
    <h2>{{ props.title }}</h2>
    <h2>{{ props.context }}</h2>
  </div>
</template>
<script setup lang="ts" name="LoveTalk">
  const props = defineProps(['title','context'])
</script>