<template>
  <li class="tree-item" :class="[classes, selected ? 'selected' : '']" @click.stop.prevent="clicked">
    <div class="item">
      <div class="image"></div>
      <span class="text">{{name}}</span>
    </div>
    <ul v-show="hasChildren()" class="tree-indent child">
      <tree-item v-for="node in children" v-bind:key="node.uuid" v-bind="node" @click="childClicked" @dblclick="childDblclicked"/>
    </ul>
  </li>
</template>

<script>
  export default {
    name: 'TreeItem',
    props: ['name', 'classes', 'children', 'data'],
    created() {
      this.clickCount = 0;
    },
    data() {
      return {
        selected: false
      }
    },
    methods: {
      setSelection(selected) {
        this.$data.selected = selected;
      },
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
            self.$emit('click', this)
          }, 200)
        } else if (this.clickCount === 2) {
          clearTimeout(this.clickTimer);
          this.clickCount = 0
          console.log('[USER] Double click tree-item:', this.data);
          this.$emit('dblclick', this)
        }
      },
      childClicked(item) {
        this.$emit('click', item);
      },
      childDblclicked(item) {
        this.$emit('dblclick', item);
      }
    }
  }
</script>

<style scoped>
  .tree-item {
    -webkit-user-select: none; /* webkit (safari, chrome) browsers */
    -moz-user-select: none; /* mozilla browsers */
    -khtml-user-select: none; /* webkit (konqueror) browsers */
    -ms-user-select: none; /* IE10+ */
    list-style: none;
  }
  .tree-indent {
    padding-left: 10px;
  }

  .tree-item .item>* {
    display: inline-block;
    vertical-align: middle;
  }

</style>
