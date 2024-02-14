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
import ProfilePage from '../pages/ProfilePage.vue';
import { Auth } from '@/services/AuthService';
import { filter, switchMap, take } from 'rxjs/operators';
import Login from '../pages/auth/LoginPage.vue'
import Redirect from '../pages/auth/Redirect.vue'
import EndRedirect from '../pages/auth/EndRedirect.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/authcallback',
    name: 'Redirect',
    component: Redirect
  },
  {
    path: '/endsession',
    name: 'EndRedirect',
    component: EndRedirect
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
  {
    path:'/profile',
    component:ProfilePage
  },
  {
    path:'/volunteers',
    component:VolunteersPage
  },
  {
    path:'/volunteer/:id',
    component:VolunteerPage
  }
  
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})
router.beforeEach((to, from, next) => {
  if (to.name === 'Home') {
    Auth.Instance.initComplete$.pipe(
      filter(complete => complete===true),
      switchMap(() => Auth.Instance.isAuthenticated$),
      take(1))
      .subscribe((isAuthenticated:boolean) => {
        if (isAuthenticated) {
          next();
        } else {
          next({path: '/login'})
        }
      });
  } else {
    next();
  }
})
export default router
