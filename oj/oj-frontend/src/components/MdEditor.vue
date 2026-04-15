<template>
  <Editor :value="value" :mode="mode" :plugins="plugins" @change="handleChange" />
</template>

<script setup lang="ts" name="MdEdit">
import gfm from '@bytemd/plugin-gfm'
import highlight from '@bytemd/plugin-highlight'
import { Editor } from '@bytemd/vue-next'
import 'bytemd/dist/index.css'
import { ref } from 'vue';
const plugins = [
  gfm(),
  highlight()
  // Add more plugins here
]
interface Props {
  value: string;
  mode?: string;
  handleChange: (v: string) => void
}
// 组件初始值
const props = withDefaults(defineProps<Props>(), {
  value: () => '',
  mode: () => "split",
  handleChange: () => { },
})

</script>