<template>
  <div class="container">
    <div class="row">
      <b-form>
        <b-button variant="primary">Execute</b-button>
        <b-button variant="danger" @click="reloadClicked">Reload</b-button>
        <argument v-for="arg of parameters" v-bind="arg" :key="arg"/>
      </b-form>
    </div>
  </div>
</template>

<script>
  import Vue from 'vue'

  const Argument = {
    name: 'Argument',
    props: ['name'],
    data() {
      return {
        value: ''
      };
    },
    template:
      `<b-form-group :label="name" :label-for="'input-' + name">
<b-form-input :id="name" type="text" v-model="name" />
</b-form-group>`
  };
  Vue.component(Argument);

  export default {
    components: { Argument: Argument },
    props: ['targets'],
    data() {
      return {
        parameters: []
      }
    },
    methods: {
      reloadClicked() {
        this.$http.get("/contract").then(function (res) {
          console.log('Response', res.data);
        });
      }
    }
  }
</script>

<style></style>
