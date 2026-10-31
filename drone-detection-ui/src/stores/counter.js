import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useCounterStore = defineStore('counter', () => {
  const count = ref(0)
  const doubleCount = computed(() => count.value * 2)
  function increment() {
    count.value++
  }
  return { count, doubleCount, increment }
})

//返回的是一个响应式对象
export const userEmailInfoMapStore=defineStore('userEmailInfoMap',()=>{
  const infoMap=new Map()
  function setInfo(email,answer1,answer2,answer3)
  {
    infoMap.set(email,new Set([['您的姓氏？',answer1],['出生年份',answer2],['所在高校',answer3]]))
  }
  return setInfo
})

