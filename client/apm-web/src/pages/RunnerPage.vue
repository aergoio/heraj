<template>
  <div class="contract-execution-container">
    <b-form>
      <b-row>
        <b-col sm="8"><b-form-select size="sm" v-model="selected" :options="functions.map(function(func) {return {value: func, text:func.name};})" class="mb-3"/></b-col>
        <b-col sm="1"><b-button variant="primary" size="sm" @click="reloadClicked">Reload</b-button></b-col>
        <b-col sm="1"><b-button :variant="(null == selected.argumentNames)?'':'primary'" size="sm" @click="executeClicked">Execute</b-button></b-col>
        <b-col sm="1"><b-button :variant="(null == selected.argumentNames)?'':'primary'" size="sm" @click="queryClicked">Query</b-button></b-col>
      </b-row>
      <argument v-for="arg of selected.argumentNames" :key="arg" :name="arg" @value-change="valueChanged"/>
    </b-form>
    <b-modal id="query-result" :title="contractResultTitle" v-model="resultOpen" hide-footer>
      <div class="contract-result">{{contractResult}}</div>
      <b-btn class="mt-3" variant="outline-danger" block @click="resultOpen = false">Close</b-btn>
    </b-modal>
  </div>
</template>

<script>
  import Vue from 'vue'
  import qs from 'qs'

  const Argument = {
    name: 'Argument',
    props: ['name'],
    data() {
      return {
        value: ''
      };
    },
    template:
      `<b-form-group :label="name" :label-for="'input-' + name" horizontal breakpoint="lg" :label-cols="2">
<b-form-input :id="'input-' + name" type="text" size="sm" v-model="value" @change="valueChanged"/>
</b-form-group>`,
    methods: {
      valueChanged(value) {
        this.$emit('value-change', this.$props.name, value);
      }
    }
  };
  Vue.component(Argument);

  export default {
    components: { Argument: Argument },
    data() {
      return {
        buildUuid: null,
        contractTransactionHash: null,
        selected: {},
        values: {},
        resultOpen: false,
        contractResultTitle: '',
        contractResult: '',
        functions: []
      }
    },
    methods: {
      reloadClicked() {
        this.$http.get("/contract").then(res => {
          console.log('Response:', res.data);
          this.$data.buildUuid = res.data.buildUuid;
          this.$data.contractTransactionHash = res.data.encodedContractTransactionHash;
          this.$data.functions = res.data.contractInterface.functions;
          this.$data.selected = res.data.contractInterface.functions[0];
          this.$data.values = {};
        }).catch(error => {
          alert(error.response.data.message);
        });
      },
      valueChanged(key, value) {
        this.$data.values[key] = value;
      },
      executeClicked() {
        const parameters = {
          arguments: this.$data.selected.argumentNames.map(argumentName => {
            return this.$data.values[argumentName] || '';
          })
        };
        this.$http.post(
          '/contract/' + this.$data.contractTransactionHash + '/' + this.$data.selected.name,
          parameters
        ).then(res => {
          this.$data.contractResultTitle = 'Transaction hash:';
          this.$data.contractResult = res.data.contractTransactionHash;
          this.$data.resultOpen = true;
        });
      },

      queryClicked() {
        const parameters = {
          params: {
            arguments: this.$data.selected.argumentNames.map(argumentName => {
              return this.$data.values[argumentName] || '';
            })
          },
          paramsSerializer(params) {
            return qs.stringify(params, {arrayFormat: 'repeat'})
          },
        };
        console.log('Parameters', parameters);
        this.$http.get(
          '/contract/' + this.$data.contractTransactionHash + '/' + this.$data.selected.name,
          parameters
        ).then(res => {
          this.$data.contractResultTitle = ''
          this.$data.contractResult = res.data.result;
          this.$data.resultOpen = true;
        });
      }
    }
  }
</script>

<style>
  .contract-execution-container {
    padding-top: 10pt;
    padding-left: 160pt;
  }
  .contract-result {
    word-break: break-all;
  }
</style>
