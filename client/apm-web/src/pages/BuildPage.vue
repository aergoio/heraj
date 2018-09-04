<template>
  <div class="container">
    <div class="row">
      <b-button @click="deployClicked">DEPLOY</b-button>
    </div>
    <div class="row">
      <section id="tabs">
        <div class="container">
          <div class="row">
            <b-tabs>
              <b-tab title="Result">
                <codemirror :value="result" :options="cmOptions"/>
              </b-tab>
              <b-tab title="Dependencies">
                <tree v-bind="dependencies"></tree>
              </b-tab>
            </b-tabs>
          </div>
        </div>
      </section>
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
    props: ['uuid', 'result', 'dependencies'],
    data() {
      return {
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
        console.log('Deploy clicked');
        this.$http.post('/build/' + this.$props.uuid + '/deploy').then(function (res) {
          console.log('Response:', res);
        })
      }
    }
  }
</script>

<style>
  section {
    padding: 10px 0;
  }

  #tabs {
    background: #007b5e;
    color: #eee;
  }

  #tabs .container {
    width:750px;
    height: 350px;
  }

  #tabs .nav-tabs .nav-item.show .nav-link, .nav-tabs .nav-link.active {
    color: #f3f3f3;
    background-color: transparent;
    border-color: transparent transparent #f3f3f3;
    border-bottom: 4px solid !important;
    font-size: 20px;
    font-weight: bold;
  }

  #tabs .nav-tabs .nav-link {
    border: 1px solid transparent;
    border-top-left-radius: .25rem;
    border-top-right-radius: .25rem;
    color: #eee;
    font-size: 20px;
  }

  .CodeMirror {
    padding: 10px;
    height: 300px;
  }


</style>
