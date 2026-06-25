<template>
  <router-view />
</template>

<style>
/* 全局重置，禁止 body 滚动 */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden;  /* 禁止整个页面滚动 */
}
</style>