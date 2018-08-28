<template>
  <div class="container">
    <div class="row">
      <b-btn v-b-modal.modal1>Add new target</b-btn>
      <b-modal id="modal1" title="New target" @ok="okClicked" @shown="resetModal">
        <b-form-group label="Your Name:" label-for="targetInput">
          <b-form-input id="targetInput" type="text" v-model="form.name" required placeholder="localhost:7845" />
        </b-form-group>
      </b-modal>
      <b-btn v-for="target in targets" :key="target.name" @click="deployClicked(target.name)">{{target.name}}</b-btn>
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
    props: ['builds', 'text'],
    data() {
      return {
        targets: [],
        form: {
          name: ''
        },
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
            this.$http.post('/build/' + uuid + '/build', {
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
      },
      resetModal() {
        this.$data.form.name = '';
      },
      okClicked() {
        this.$data.targets.push({name: this.$data.form.name});
      },
    }
  }
</script>

<style></style>
