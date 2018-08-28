<template>
  <div class="container">
    <div class="row">
      <div class="col-6">
        Runs <span>{{successes}}</span>
      </div>

      <div class="col-6">
        <span class="progress">
          <div class="progress-bar bg-success" role="progressbar" style="width: 100%"
               aria-valuenow="100" aria-valuemin="0" aria-valuemax="100"></div>
        </span>
      </div>
    </div>
    <div class="row">
      <div class="col-6">
        <tree v-bind="reports" @item-clicked="itemSelected"></tree>
      </div>

    </div>

  </div>
</template>

<script>
  import Tree from "../components/Tree";
  export default {
    components: {Tree},
    props: ['unitTestReport'],
    computed: {
      successes() {
        if (this.$props.unitTestReport) {
          var sum = 0;
          for (let suite of this.$props.unitTestReport) {
            sum += suite.tests
          }
          return sum;
        }
        return 0;
      },
      reports() {
        return {
          children: (this.$props.unitTestReport || []).map(suite => {
            return {
              name: suite.name,
              data: suite,
              children: (suite.testCases || []).map(testCase => {
                return {
                  name: testCase.name,
                  data: testCase
                }
              })
            }
          })
        }
      }
    },
    methods: {
      itemSelected(item) {
        console.log('Item :',item)
      }
    }
  }

</script>

<style></style>
