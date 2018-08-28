<template>
  <section id="tabs">
    <div class="container">
      <b-tabs>
        <b-tab title="Result">
          <codemirror :value="result" :options="cmOptions"/>
        </b-tab>
        <b-tab title="Dependencies">
          <tree v-bind="dependencies"></tree>
        </b-tab>
      </b-tabs>
    </div>
  </section>
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
    props: ['result', 'dependencies'],
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
    width: 100%;
    height: 300px;
  }


</style>
