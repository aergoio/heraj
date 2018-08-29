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
    constructor(app) {
      this.app = app;
      this.running = true;
      this.connect();
    }
    connect() {
      if (this.socket) {
        return ;
      }
      if (!this.running) {
        return ;
      }
      this.socket = new WebSocket("ws://localhost:2000");
      this.socket.onopen    = this.onOpen.bind(this);
      this.socket.onerror   = this.onError.bind(this);
      this.socket.onclose   = this.onClose.bind(this);
      this.socket.onmessage = this.onReceive.bind(this);
    }

    close() {
      this.running = false;
      if (this.socket) {
        this.socket.close();
      }
    }

    onOpen() {
      console.log('[WS] Socket opened')
    }
    onError() {
      console.log('[WS] Socket error')
    }

    onClose() {
      console.log('[WS} Socket closed');
      this.socket = null;
      if (this.running) {
        setTimeout(this.connect, 3000);
      }
    }

    onReceive(message) {
      console.log('[WS} Message received', message);
      const data = JSON.parse(message.data);
      console.log('App', this);
      this.app.updateBuilds();
    }
  }

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
    created() {
      this.connection = new Connection(this);
    },
    destroyed() {
      this.connection.close();
    },
    mounted() {
      this.updateBuilds();
    },
    methods: {
      updateBuilds() {
        console.log("=>Start update builds")
        this.$http.get('/builds').then((res) => {
          console.log('==>Response received');
          this.$data.builds = res.data;
          if (res.data.length && 0 < res.data.length) {
            if (!this.$data.currentBuild || !this.$data.currentBuild.uuid) {
              this.buildSelected(res.data[0].uuid);
            }
          }
        })
      },
      buildSelected(uuid) {
        console.log(uuid + ' selected');
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
