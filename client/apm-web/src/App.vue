<template>
  <div id="app">
    <page-header class="row" :builds="builds" :build="currentBuild" @select-build="buildSelected"/>
    <div class="row">
      <side-menu class="left"/>
      <router-view class="main" v-bind="currentBuild" :builds="builds"/>
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
    components: {PageHeader, PageFooter, SideMenu },
    data() {
      return {
        builds: [],
        currentBuild: {}
      }
    },
    mounted() {
      this.$http.get('/builds').then((res) => {
        console.log('Res', res);
        this.$data.builds = res.data;
        if (res.data.length && 0 < res.data.length) {
          this.buildSelected(res.data[0].uuid);
        }
      })
    },
    methods: {
      buildSelected(uuid) {
        this.$http.get('/build/' + uuid).then((res) => {
          this.$data.currentBuild = res.data;
        })
      }
    }
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

  .main {
    margin-left: 200px; /* Same as the width of the sidebar */
    padding: 0px 10px;
  }

</style>
