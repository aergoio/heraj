<template>
  <ul class="tree">
    <tree-item v-for="node in children" v-bind="node" v-bind:key="node.uuid"></tree-item>
  </ul>
</template>

<script>
  import TreeItem from './TreeItem'

  export default {
    name: 'Tree',
    components: {TreeItem},
    props: ['children']
  }

</script>

<style>

</style>
