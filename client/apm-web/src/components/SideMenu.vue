<template>
  <nav class="sidenav">
    <img src="/static/aergo-logo.png" srcset="/static/aergo-logo@2x.png 2x, /static/aergo-logo@3x.png 3x" class="aergo_logo">
    <div>
      <ul>
        <li :class="{active: active() == item.to}" v-for="item in items()" v-bind:key="item.name">
          <span v-if="active() == item.to" class="activebar" />
          <span class="text">
            <router-link :to="item.to">{{item.name}}</router-link>
          </span>
        </li>
      </ul>
    </div>
    <div class="copyright">
      © 2018 AERGO, Inc. All rights reserved.
    </div>
  </nav>
</template>
<script>
  export default {
    name: 'SideMenu',
    data() {
      return {};
    },
    methods: {
      active() {
        return this.$route.path;
      },
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
    position: relative;
    height: 100%;
    width: 150pt;
    overflow-x: hidden;
    box-shadow: inset -1pt 0 0 0 rgba(0, 0, 0, 0.2);
  }

  .aergo_logo {
    margin: 45pt 0pt 0pt 20pt;
  }
  .sidenav ul {
    position: absolute;
    top: 110pt;
    margin: 0; /* To remove default bottom margin */
    padding: 0; /* To remove default left padding */
  }

  .sidenav li {
    display: block;
    position: relative;
    list-style: none;
    height: 36pt;
    width: 155pt;
    letter-spacing: -0.2pt;
  }

  .sidenav li .text a {
    font-family: NanumSquareOTFR;
    font-size: 12pt;
    font-weight: normal;
    font-style: normal;
    font-stretch: normal;
    color: #576171;
  }
  .sidenav li.active .text a {
    color: #000021;
  }

  .sidenav .activebar {
    position: absolute;
    display: inline-block;
    width: 6pt;
    height: 36pt;
    background-color: #f80f5f;
  }

  .sidenav .text {
    position: absolute;
    left: 20pt;
    top: 12pt;
  }

  .copyright {
    position: absolute;
    left: 20pt;
    bottom: 20pt;
    width: 77pt;
    height: 22pt;
    font-family: NanumSquareOTFB;
    font-size: 8pt;
    font-weight: normal;
    font-style: normal;
    font-stretch: normal;
    line-height: 1.38;
    letter-spacing: normal;
    color: #99a4b4;
  }

</style>
