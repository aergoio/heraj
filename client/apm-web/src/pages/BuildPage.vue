<template>
  <b-tabs>
    <b-tab title="Result">
      <codemirror :value="code" :options="cmOptions"/>
    </b-tab>
    <b-tab title="Dependencies">
    </b-tab>
  </b-tabs>
</template>

<script>
  import Vue from 'vue'
  import VueCodemirror from 'vue-codemirror'
  Vue.use(VueCodemirror)

  import 'codemirror/lib/codemirror.css'
  import 'codemirror/theme/base16-dark.css'
  import 'codemirror/mode/lua/lua.js'
  export default {
    name: 'BuildPage',
    components: {

    },
    data () {
      return {
        code:
          `function abc()
  run()
end`,
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
  b-tab {
    text-align: left;
  }
  .CodeMirror {
    width: 750px;
    height: 300px;
  }
</style>
