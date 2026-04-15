import { ref } from "vue";

export default function(){
  let sum = ref(0)
  function add(){
    sum.value += 1
  }
  function minus(){
    sum.value -= 1
  }
  return {sum, add, minus}
}