<template>
  <li class="tree-item" @click.stop.prevent="clicked"><span>{{name}}</span>
    <ul v-show="hasChildren()">
      <tree-item v-for="node in children" v-bind:key="node.uuid" v-bind="node" @click="childClicked" @dblclick="childDblclicked"></tree-item>
    </ul>
  </li>
</template>

<script>
  export default {
    name: 'TreeItem',
    props: ['name', 'children', 'data'],
    created() {
      this.clickCount = 0;
    },
    methods: {
      hasChildren() {
        return this.children && this.children.length && 0 < this.children.length;
      },
      clicked() {
        this.clickCount++;
        if (this.clickCount === 1) {
          const self = this;
          this.clickTimer = setTimeout(() => {
            this.clickCount = 0;
            console.log('[USER] Click tree-item:', this.data);
            self.$emit('click', this.data)
          }, 200)
        } else if (this.clickCount === 2) {
          clearTimeout(this.clickTimer);
          this.clickCount = 0
          console.log('[USER] Double click tree-item:', this.data);
          this.$emit('dblclick', this.data)
        }
      },
      childClicked(data) {
        this.$emit('click', data);
      },
      childDblclicked(data) {
        this.$emit('dblclick', data);
      }
    }
  }
</script>

<style>
  .tree-item {
    -webkit-user-select: none; /* webkit (safari, chrome) browsers */
    -moz-user-select: none; /* mozilla browsers */
    -khtml-user-select: none; /* webkit (konqueror) browsers */
    -ms-user-select: none; /* IE10+ */
  }

</style>
