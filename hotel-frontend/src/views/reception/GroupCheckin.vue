<template>
  <div style="padding:40px;text-align:center">
    <p style="margin-bottom:20px;color:#666">团队入住已合并到入住办理页面，正在跳转...</p>
  </div>
</template>

<script setup>
import { onMounted } from "vue"
import { useRouter } from "vue-router"
const router = useRouter()
onMounted(() => { router.replace("/reception/checkin") })
</script>
