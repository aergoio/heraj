import Vue from 'vue'
import Router from 'vue-router'
import BuildPage from '@/pages/BuildPage'
import HelloWorld from '@/pages/HelloWorld'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/:build/build',
      component: BuildPage
    },
    {
      path: '/:build/lint',
      component: BuildPage
    },
    {
      path: '/:build/unittest',
      component: BuildPage
    },
    {
      path: '/:build/deploy',
      component: BuildPage
    },
    {
      path: '/:build/runner',
      component: BuildPage
    },
    {
      path: '/',
      name: 'HelloWorld',
      component: HelloWorld
    }
  ]
})
