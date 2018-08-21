<template>
  <ul class="menu-container">
    <li><router-link :to="'/' + $route.params.build + '/build'">Build</router-link></li>
    <li><router-link :to="'/' + $route.params.build + '/lint'">Lint</router-link></li>
    <li><router-link :to="'/' + $route.params.build + '/unittest'">Unit test</router-link></li>
    <li><router-link :to="'/' + $route.params.build + '/deploy'">Deploy</router-link></li>
    <li><router-link :to="'/' + $route.params.build + '/runner'">Runner</router-link></li>
  </ul>
</template>

<script>
  export default {
    name: 'SideMenu'
  }
</script>

<style scoped>
  li {
    font-size: 20px;
    list-style: none;
  }
  .menu-container {
    padding: 80px;
    width: 240px;
    text-align: left;
  }
</style>
