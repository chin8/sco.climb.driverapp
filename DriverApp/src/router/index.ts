import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';
import  HomePage from '../pages/HomePage.vue';
import  VolunteersPage from '../pages/VolunteersPage.vue';
import  VolunteerPage from '../pages/VolunteerPage.vue';
import Institutes from '../pages/configuration/Institutes.vue'
import Schools from '../pages/configuration/Schools.vue'
import Routes from '../pages/configuration/Routes.vue'
import Volunteers from '../pages/configuration/Volunteers.vue'
import Recap from '../pages/configuration/Recap.vue'
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path:'/Institutes',
    component:Institutes
  },
  {
    path:'/Schools',
    component:Schools
  },
  {
    path:'/Routes',
    component:Routes
  },
  {
    path:'/Volunteers',
    component:Volunteers
  },
  {
    path:'/home',
    component:HomePage
  },
  {
    path:'/recap',
    component:Recap
  },
  // {
  //   path:'/volunteers',
  //   component:VolunteersPage
  // },
  // {
  //   path:'/volunteer/:id',
  //   component:VolunteerPage
  // }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
