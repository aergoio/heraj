<template>
  <ul class="tree">
    <tree-item v-for="node in children" v-bind="node" v-bind:key="node.uuid" v-on:clicked="itemClicked"></tree-item>
  </ul>
</template>

<script>
  import TreeItem from './TreeItem'

  export default {
    name: 'Tree',
    components: {TreeItem},
    props: ['children'],
    methods: {
      itemClicked(data) {
        this.$emit('item-clicked', data)
      }
    }
  }
</script>

<style>

</style>
