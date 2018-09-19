<template>
  <div class="container" id="unittest-page">
    <div class="row title">
      <div class="col-6 runs">
        Runs <span class="total-successes">{{theNumberOfSuccesses}}</span> / <span class="total-runs">{{theNumberOfTests}}</span>
      </div>

      <div class="col-6">
        <span class="progress">
          <div class="progress-bar" :class="{'bg-success': 0 == state, 'bg-danger': 0 != state}" role="progressbar" style="width: 100%"
               aria-valuenow="100" aria-valuemin="0" aria-valuemax="100"></div>
        </span>
      </div>
    </div>
    <div class="row body">
      <div class="col-6">
        <tree v-bind="reports" class="test-tree" @item-select="itemSelected"></tree>
      </div>
      <div class="col-6">
        <div class="failure-title">
          <img src="/static/failure-icon.svg" style="width: 10pt; height: 7pt;">
          <span>Failure message</span>
        </div>
        <div class="failure-detail">{{errorMessage}}</div>
      </div>
    </div>

  </div>
</template>

<script>
  import Tree from "../components/Tree";
  export default {
    components: {Tree},
    props: ['unitTestReport', 'state'],
    data() {
      return {
        errorMessage: ''
      }
    },
    computed: {
      theNumberOfSuccesses() {
        if (this.$props.unitTestReport) {
          let sum = 0;
          for (let suite of this.$props.unitTestReport) {
            sum += suite.theNumberOfSuccesses
          }
          return sum;
        }
        return 0;
      },
      theNumberOfTests() {
        if (this.$props.unitTestReport) {
          let sum = 0;
          for (let suite of this.$props.unitTestReport) {
            sum += suite.theNumberOfTests
          }
          return sum;
        }
        return 0;
      },
      reports() {
        return {
          children: (this.$props.unitTestReport || []).map(file => {
            return {
              name: file.name,
              data: file,
              classes: file.success ? [] : ['error'],
              children: (file.children || []).map(testSuite => {
                return {
                  name: testSuite.name,
                  data: testSuite,
                  children: (testSuite.children || []).map(testCase => {
                    return {
                      name: testCase.name,
                      data: testCase,
                      classes: testCase.success ? [] : ['error']
                    }
                  })
                };
              })
            };
          })
        };
      }
    },
    methods: {
      itemSelected(data) {
        this.$data.errorMessage = (null == data.errorMessage)?'':data.errorMessage;
      }
    }
  }

</script>

<style>
  #unittest-page .title {
    height: 40pt;
    padding: 13pt 0pt;
    background-color: #f4f4f4;
  }
  .runs {
    display: inline-block;
    vertical-align: middle;
    font-size: 9pt;
    font-weight: normal;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: normal;
    color: #4a4a4a;
  }

  #unittest-page .body {
    padding: 10px 0px;
  }

  #unittest-page .failure-detail {
    width: 100%;
    height: 200pt;
    overflow: auto;
    outline: none;
    background-color: transparent;
  }

  .total-runs,.total-successes {
    font-size: 13pt;
    color: #000000;
  }

  .test-tree .tree-item .item {
    line-height: 22pt;
  }

  .test-tree .tree-item>.item>.image {
    width: 16pt;
    height: 18pt;
    background-repeat: no-repeat;
    background-image: url('/static/unit-icon-okay.svg');
    background-position: 4pt 4px;
    background-size: 8pt 10pt;
  }

  .test-tree .tree-item.error>.item>.image {
    background-image: url('/static/unit-icon-error.svg');
  }

  .test-tree .tree-item.selected>.item {
    background-color: #d4d6da;
  }

  #unittest-page .failure-title {
    height: 20pt;
    font-size: 10pt;
    font-weight: normal;
    font-style: normal;
    font-stretch: normal;
    line-height: 20pt;
    letter-spacing: normal;
    color: #000000;
  }

</style>
