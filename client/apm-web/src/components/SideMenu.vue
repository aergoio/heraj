<template>
  <ul class="menu-container">
    <li v-for="item in items()" v-bind:key="item.name">
      <router-link :to="item.to">{{item.name}}</router-link>
    </li>
  </ul>
</template>
<script>
  export default {
    name: 'SideMenu',
    data() {
      return {};
    },
    methods: {
      items() {
        return [
          {
            name: 'Build',
            to: '/' + this.$route.params.build + '/build'
          },
          {
            name: 'Lint',
            to: '/' + this.$route.params.build + '/lint'
          },
          {
            name: 'Unit test',
            to: '/' + this.$route.params.build + '/unittest'
          },
          {
            name: 'Deploy',
            to: '/' + this.$route.params.build + '/deploy'
          },
          {
            name: 'Runner',
            to: '/' + this.$route.params.build + '/runner'
          }
        ];
      }
    }
  }
</script>

<style scoped>
  li {
    font-size: 20px;
    list-style: none;
  }

  .menu-container {
    padding: 80px;
    width: 240px;
    text-align: left;
  }
</style>
