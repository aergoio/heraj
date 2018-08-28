<template>
  <div id="app">
    <page-header class="row" :builds="builds" :build="currentBuild" @select-build="buildSelected"/>
    <div class="row">
      <side-menu class="left"/>
      <router-view class="main" v-bind="currentBuild" :builds="builds" :targets="targets" @add-target="targetAdded"/>
    </div>
    <page-footer class="row"/>
  </div>
</template>

<script>
  import SideMenu from "./components/SideMenu";
  import PageFooter from "./components/PageFooter";
  import PageHeader from "./components/PageHeader";
  class Connection {
    constructor() {
      this.connect();
    }
    connect() {
      if (this.socket) {
        return ;
      }
      this.socket = new WebSocket("ws://localhost:2000");
      this.socket.onopen = this.onOpen
      this.socket.onerror = this.onError
      this.socket.onclose = this.onClose
      this.socket.onmessage = this.onReceive
    }

    onOpen() {
      console.log('Socket opened')
    }
    onError() {
      console.log('Socket error')
    }

    onClose() {
      console.log('Socket closed')
      this.socket = null;
      setTimeout(this.connect, 3000);
    }

    onReceive(message) {
      console.log('Message received', message)
    }
  }

  const connection = new Connection();

  export default {
    name: 'App',
    components: {PageHeader, PageFooter, SideMenu },
    data() {
      return {
        builds: [],
        currentBuild: {},
        targets: [],
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
      },
      targetAdded(name) {
        this.$data.targets.push({name: name});
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
