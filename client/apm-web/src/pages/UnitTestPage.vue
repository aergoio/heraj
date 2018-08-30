<template>
  <div class="container">
    <div class="row">
      <div class="col-6">
        Runs <span>{{successes}}</span>
      </div>

      <div class="col-6">
        <span class="progress">
          <div class="progress-bar" :class="{'bg-success': (failures == 0), 'bg-danger': (failures != 0)}" role="progressbar" style="width: 100%"
               aria-valuenow="100" aria-valuemin="0" aria-valuemax="100"></div>
        </span>
      </div>
    </div>
    <div class="row">
      <div class="col-6">
        <tree v-bind="reports" @item-click="itemSelected"></tree>
      </div>
      <div class="col-6">
        <b-form-textarea v-bind="null == selectedItem?'':selectedItem.errorMessage" :rows="20" :max-rows="20" />
      </div>
    </div>

  </div>
</template>

<script>
  import Tree from "../components/Tree";
  export default {
    components: {Tree},
    props: ['unitTestReport'],
    data() {
      return {
        selectedItem: null
      }
    },
    computed: {
      successes() {
        if (this.$props.unitTestReport) {
          let sum = 0;
          for (let suite of this.$props.unitTestReport) {
            sum += suite.successes
          }
          return sum;
        }
        return 0;
      },
      failures() {
        if (this.$props.unitTestReport) {
          let sum = 0;
          for (let suite of this.$props.unitTestReport) {
            sum += suite.failures
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
        this.$data.selectedItem = item;
      }
    }
  }

</script>

<style></style>
