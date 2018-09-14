<template>
  <div class="container">
    <div class="row">
    </div>
    <div class="row">
      <div class="tabs">
        <div class="tab" :class="{active: activePane === 'result'}" @click="resultClicked">Result</div>
        <div class="tab" :class="{active: activePane === 'dependencies'}" @click="dependenciesClicked">Information</div>
        <b-button class="deploy-button btn-success btn-sm" @click="deployClicked">DEPLOY</b-button>
      </div>
    </div>
    <div class="row">
      <div class="tab-pane">
        <codemirror v-if="activePane === 'result' && 1 != state" :value="result" :options="cmOptions"/>
        <div v-if="activePane === 'result' && 1 == state" class="failure-detail">{{error}}</div>
        <tree v-if="activePane === 'dependencies'" v-bind="dependencies" class="dependency-tree"></tree>
      </div>
    </div>
  </div>

</template>

<script>
  import Vue from 'vue'
  import VueCodemirror from 'vue-codemirror'

  Vue.use(VueCodemirror)

  import 'codemirror/lib/codemirror.css'
  import 'codemirror/theme/base16-dark.css'
  import 'codemirror/mode/lua/lua.js'
  import Tree from '@/components/Tree';

  export default {
    components: {Tree},
    props: ['uuid', 'result', 'state', 'error', 'dependencies'],
    data() {
      return {
        activePane: "result",
        cmOptions: {
          tabSize: 2,
          mode: 'lua',
          theme: 'base16-dark',
          lineNumbers: true,
          line: true,
          readOnly: "nocursor"
        }
      }
    },
    methods: {
      deployClicked() {
        console.log('Deploy clicked: ' + this.$props.uuid);
        this.$http.post('/build/' + this.$props.uuid + '/deploy').then(res => {
          console.log('Deploy result:', res);
          alert('Successful to deploy')
        }).catch(error => {
          console.log('Error:', error)
          alert(error.response.data.message);
        })
      },
      resultClicked() {
        this.$data.activePane = "result";
      },
      dependenciesClicked() {
        this.$data.activePane = "dependencies";
      }
    }
  }
</script>

<style>
  .tabs {
    position: relative;
    height: 40pt;
    width: 745pt;
    display: inline-block;
  }
  .tabs .tab {
    display: inline-block;
    width: 150pt;
    line-height: 40pt;
    font-family: NanumSquareOTFR;
    font-size: 11pt;
    font-weight: normal;
    font-style: normal;
    font-stretch: normal;
    letter-spacing: -0.1pt;
    text-align: center;
    color: #4a4a4a;
    vertical-align: middle;
  }
  .tabs .tab:not(.active) {
    cursor: pointer;
  }
  .tabs .tab.active {
    border-top:solid #171928 3pt;
    letter-spacing: normal;
    color: #000000;
  }
  .deploy-button {
    position: absolute;
    right: 30pt;
    top:10pt;
  }

  #tabs {
    background: #007b5e;
    color: #eee;
  }

  #tabs .container {
    width:750pt;
    height: 350pt;
  }

  .CodeMirror {
    padding: 10pt;
    height: 500pt;
    width: 590pt;
    font-size: 10pt;
  }

  .dependency-tree .tree-item {
    list-style: none;
    background-image: url('/static/file-icon.svg');
    background-size: 10px 14px;
    background-repeat: no-repeat;
    background-position: 4pt 4px;
    padding-left: 18px;
  }

</style>
