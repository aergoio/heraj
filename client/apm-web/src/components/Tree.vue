<template>
  <ul class="tree">
    <tree-item v-for="node in nodes" v-bind="node" v-bind:key="node.uuid"></tree-item>
  </ul>
</template>

<script>
  import TreeItem from './TreeItem'

  export default {
    name: 'Tree',
    components: {TreeItem},
    data() {
      return {
        nodes: [
          {
            uuid: 'n1',
            text: 'N1',
            children: [
              {
                uuid: 'n1-1',
                text: 'N1-1'
              },
              {
                uuid: 'n1-2',
                text: 'N1-2'
              }
            ]
          },
          {
            uuid: 'n2',
            text: 'N2',
            children: [
              {
                uuid: 'n2-1',
                text: 'N2-1'
              }
            ]
          }
        ]
      };
    }
  }

</script>

<style>

</style>
