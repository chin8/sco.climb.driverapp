import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';
import  HomePage from '../pages/HomePage.vue';
import  VolunteersPage from '../pages/VolunteersPage.vue';
import  VolunteerPage from '../pages/VolunteerPage.vue';
import RouteConfig from '../pages/RouteConfig.vue'
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path:'/home',
    component:HomePage
  },
  {
    path:'/volunteers',
    component:VolunteersPage
  },
  {
    path:'/volunteer/:id',
    component:VolunteerPage
  },
  {
    path:'/RouteConfig',
    component:RouteConfig
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
