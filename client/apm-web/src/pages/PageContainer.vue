<template>
  <router-view v-bind="build"/>
</template>

<script>
  var data = {};
  export default {
    data() {
      return {
        build: null
      }
    },
    methods: {
      refresh() {
        const buildUuid = this.$route.params.build;
        if (!this.$data.build || this.$data.build.uuid !== buildUuid) {
          this.$http.get('/build/' + buildUuid).then((res) => {
            this.$data.build = res.data;
            console.log('Current build:', this.$data.build)
          })
        }
      }
    },
    updated() {
      this.refresh();
    },
    mounted() {
      this.refresh();
    }
  }
</script>

<style></style>
