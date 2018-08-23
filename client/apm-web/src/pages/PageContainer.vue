<template>
  <router-view v-bind="build"/>
</template>

<script>
  const data = {
    build: null
  };

  export default {
    data() {
      return data;
    },
    updated() {
      console.log('Container updated');
      const buildUuid = this.$route.params.build;
      if (!this.$data.build || this.$data.build.uuid !== buildUuid) {
        console.log('Update is needed');
        this.$http.get('/build/' + buildUuid).then((res) => {
          console.log('Build information:,', res)
          this.$data.build = res.data;
        });
      }
    }
  }
</script>

<style></style>
