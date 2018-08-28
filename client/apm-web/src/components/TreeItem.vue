<template>
  <li v-on:click.stop="clicked"><span>{{name}}</span>
    <ul v-show="hasChildren()">
      <tree-item v-for="node in children" v-bind:key="node.uuid" v-bind="node" v-on:clicked="childClicked"></tree-item>
    </ul>
  </li>
</template>

<script>
  export default {
    name: 'TreeItem',
    props: ['name', 'children', 'data'],
    methods: {
      hasChildren() {
        return this.children && this.children.length && 0 < this.children.length;
      },
      clicked() {
        this.$emit('clicked', this.data)
      },
      childClicked(data) {
        this.$emit('clicked', data)
      }
    }
  }
</script>

<style>

</style>
