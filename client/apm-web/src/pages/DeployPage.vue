<template>
  <div class="container">
    <div class="row">
      <b-btn v-for="target in targets" :key="target" @click="deployClicked(target)">{{target}}</b-btn>
    </div>
    <div class="row">
      <deploy-progress :text="progress.text" />
    </div>
  </div>
</template>

<script>
  import DeployProgress from '@/components/DeployProgress';

  export default {
    components: {DeployProgress},
    props: ['builds', 'targets', 'text'],
    data() {
      return {
        progress: {
          text: ''
        }
      };
    },
    methods: {
      deployClicked(target) {
        console.log('Deploy ', target)
        if (this.$props.builds && 0 < this.$props.builds.length) {
          const latestBuild = this.$props.builds[0];
          if (latestBuild.success) {
            this.$http.post('/build/' + latestBuild.uuid + '/deploy', {
              target: target
            }).then(res => {
              console.log('Response: ', res)
            })
          } else {
            alert('Build is not successful!!')
          }
        } else {
          alert("No build!!")
        }
      }
    }
  }
</script>

<style></style>
