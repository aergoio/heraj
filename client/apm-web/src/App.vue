<template>
  <div id="app">
    <page-header class="row" />
    <div class="row">
      <side-menu class="left"/>
      <router-view/>
    </div>
    <page-footer class="row"/>
  </div>
</template>

<script>
  import SideMenu from "./components/SideMenu";
  import PageFooter from "./components/PageFooter";
  import PageHeader from "./components/PageHeader";

  export default {
    name: 'App',
    components: {PageHeader, PageFooter, SideMenu}
  }
</script>

<style>
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;
  }

  .left {
    float: left;
  }

  html, body {
    position: relative;
    margin: 0;
    min-height: 100%;
  }

  .row {
    width: 1024px;
  }
</style>
