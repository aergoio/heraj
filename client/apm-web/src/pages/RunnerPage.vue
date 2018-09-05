<template>
  <div class="container">
    <div class="row">
      <b-form>
        <b-form-select v-model="selected" :options="functions.map(function(func) {return {value: func.name, text:func.name};})" class="mb-3" />
        <b-button variant="primary" @click="reloadClicked">Reload</b-button>
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
        functions: [],
        selected: null,
        parameters: []
      }
    },
    methods: {
      reloadClicked() {
        this.$http.get("/contract").then(res => {
          this.$data.functions = res.data.contractInterface.functions
        }).catch(error => {
          alert(error.response.data.message);
        });
      }
    }
  }
</script>

<style></style>
