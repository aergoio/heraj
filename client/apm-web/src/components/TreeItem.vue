<template>
  <li><span>{{text}}</span>
    <ul v-show="hasChildren()">
      <tree-item v-for="node in children" v-bind:key="node.uuid" v-bind="node"></tree-item>
    </ul>
  </li>
</template>

<script>
  export default {
    name: 'TreeItem',
    props: ['text', 'children'],
    methods: {
      hasChildren() {
        return this.children && this.children.length && 0 < this.children.length;
      }
    }
  }
</script>

<style>

</style>
