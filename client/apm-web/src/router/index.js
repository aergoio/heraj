import Vue from 'vue'
import Router from 'vue-router'
import BuildPage from '@/pages/BuildPage'
import UnitTestPage from '@/pages/UnitTestPage'
import DeployPage from '@/pages/DeployPage'
import RunnerPage from '@/pages/RunnerPage'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/build',
      component: BuildPage
    },
    {
      path: '/unittest',
      component: UnitTestPage
    },
    {
      path: '/deploy',
      component: DeployPage
    },
    {
      path: '/runner',
      component: RunnerPage
    }
  ]
})
