<template>
  <div id="app">
    <side-menu class="left"/>
    <div>
      <div class="container">
        <div class="row">
          <page-header :builds="builds" :build="currentBuild" @select-build="buildSelected"/>
        </div>
      </div>
      <router-view class="main" v-bind="currentBuild" :builds="builds"/>
    </div>
  </div>
</template>

<script>
  import SideMenu from "./components/SideMenu";
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
      this.socket = new WebSocket("ws://localhost:2000/event");
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
    components: {PageHeader, SideMenu},
    data() {
      return {
        builds: [],
        currentBuild: {}
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
        this.$http.get('/builds').then(res => {
          console.log('==>Response received');
          this.$data.builds = res.data;
          if (res.data.length && 0 < res.data.length) {
            this.buildSelected(res.data[0].uuid);
          }
        })
      },
      buildSelected(uuid) {
        console.log(uuid + ' selected');
        this.$http.get('/build/' + uuid).then(res => {
          console.log('res', res);
          this.$data.currentBuild = res.data;
        }).catch(error => {
            alert('Fail to server request: ' + error.data.message);
        })
      }
    }
  }
</script>

<style>
  @media
  (-webkit-min-device-pixel-ratio: 2),
  (min-resolution: 192dpi) {
    -webkit-min-device-pixel-ratio: 2;
    min--moz-device-pixel-ratio: 2;
  }

  #app {
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;
    display: inline-block;
    height: 100%;
  }

  .left {
    float: left;
  }

  html, body {
    font-family: NanumSquareOTFB;
    position: relative;
    margin: 0;
    height: 100%;
    min-height: 100%;
    -webkit-text-size-adjust: auto;
  }
</style>
