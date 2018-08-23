import Vue from 'vue'
import Router from 'vue-router'
import PageContainer from '@/pages/PageContainer'
import BuildPage from '@/pages/BuildPage'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/:build',
      component: PageContainer,
      children: [
        {
          path: 'build',
          component: BuildPage
        },
        {
          path: 'lint',
          component: BuildPage
        },
        {
          path: 'unittest',
          component: BuildPage
        },
        {
          path: 'deploy',
          component: BuildPage
        },
        {
          path: 'runner',
          component: BuildPage
        }
      ]
    }
  ]
})
