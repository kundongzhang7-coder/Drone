// stores/shared.js
import { useStorage } from '@vueuse/core';

// 第一个参数是 localStorage 的 key，第二个是默认值
export const detectCount = useStorage('detectCount', 520);
