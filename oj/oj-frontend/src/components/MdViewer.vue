<template>
  <Viewer :value="value" :plugins="plugins" />
</template>

<script setup lang="ts" name="MdEdit">
import gfm from '@bytemd/plugin-gfm'
import highlight from '@bytemd/plugin-highlight'
import { Viewer } from '@bytemd/vue-next'
import 'bytemd/dist/index.css'
const plugins = [
  gfm(),
  highlight()
]
interface Props {
  value: string;
}
// 组件初始值
const props = withDefaults(defineProps<Props>(), {
  value: () => '',
})

</script>