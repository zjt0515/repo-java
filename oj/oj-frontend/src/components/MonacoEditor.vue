<template>
  <div id="code-editor" ref="monacoEditorRef" style="min-height: 400px;" />
</template>
<script lang="ts" setup name="MonacoEditor">
import * as monaco from 'monaco-editor';
import { onMounted, ref, toRaw } from 'vue';


const monacoEditorRef = ref()//ref
const editor = ref();

interface Props {
  value: string,
  language: string,
  handleChange: (v: string) => void
}
// const props = defineProps({
//   value: String,
//   language: String,
// })
const props = withDefaults(defineProps<Props>(), {
  value: () => '',
  language: 'javascript',
  handleChange: (v: string) => {
  },
})

//const editorContainer = ref();
onMounted(() => {
  if (!monacoEditorRef.value) {
    return;
  }
  editor.value = monaco.editor.create(monacoEditorRef.value, {
    value: props.value || '',
    language: props.language || 'java',
    minimap: {
      enabled: true,
    },
    colorDecorators: true,		//颜色装饰器
    readOnly: false,			//是否开启已读功能
    theme: "vs-dark",			//主题
  });
  // 监听内容变化
  editor.value.onDidChangeModelContent(() => {
    // 调用父组件的更新value值
    props.handleChange(toRaw(editor.value).getValue())
  })


});


</script>

<style lang="scss" scoped></style>