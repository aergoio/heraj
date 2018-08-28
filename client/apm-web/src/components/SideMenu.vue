<template>
  <nav class="sidenav">
    <ul class="menu-container">
      <li v-for="item in items()" v-bind:key="item.name">
        <router-link :to="item.to">{{item.name}}</router-link>
      </li>
    </ul>
  </nav>
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
            to: '/build'
          },
          {
            name: 'Unit test',
            to: '/unittest'
          },
          {
            name: 'Deploy',
            to: '/deploy'
          },
          {
            name: 'Runner',
            to: '/runner'
          }
        ];
      }
    }
  }
</script>

<style scoped>
  .sidenav {
    height: 100%; /* Full-height: remove this if you want "auto" height */
    width: 160px; /* Set the width of the sidebar */
    position: fixed; /* Fixed Sidebar (stay in place on scroll) */
    z-index: 1; /* Stay on top */
    top: 80px; /* Stay at the top */
    left: 0;
    background-color: #111; /* Black */
    overflow-x: hidden; /* Disable horizontal scroll */
    padding-top: 20px;
  }

  /* The navigation menu links */
  .sidenav a {
    padding: 6px 8px 6px 16px;
    text-decoration: none;
    font-size: 25px;
    color: #818181;
    display: block;
  }

  /* When you mouse over the navigation links, change their color */
  .sidenav a:hover {
    color: #f1f1f1;
  }

  /* On smaller screens, where height is less than 450px, change the style of the sidebar (less padding and a smaller font size) */
  @media screen and (max-height: 450px) {
    .sidenav {padding-top: 15px;}
    .sidenav a {font-size: 18px;}
  }

  li {
    font-size: 20px;
    list-style: none;
  }

</style>
