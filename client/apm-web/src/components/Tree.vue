<template>
  <ul class="tree tree-indent">
    <tree-item v-for="node in children" v-bind="node" v-bind:key="node.uuid" @click="itemClicked"></tree-item>
  </ul>
</template>

<script>
  import TreeItem from './TreeItem'

  export default {
    name: 'Tree',
    components: {TreeItem},
    props: ['children'],
    data() {
      return {
        selection: null
      }
    },
    methods: {
      itemClicked(item) {
        this.$emit('item-click', item);
        this.select(item)
        this.$emit('item-select', item.data);
      },
      select(child) {
        if (this.selection === child) {
          return ;
        }
        this.selection && this.selection.setSelection(false);
        child && child.setSelection(true);
        this.selection = child;
      }
    }
  }
</script>

<style>

</style>
