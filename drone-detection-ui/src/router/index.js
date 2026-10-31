import { createRouter, createWebHistory } from 'vue-router'


//导入欢迎界面
import Home from '../views/Home.vue'
import Register from '../views/Register.vue'
import Login from '../views/Login.vue'
import test from '../__tests__/test.vue'
import DashBoard from '../views/DashBoard.vue'
import Forget from '../views/Forget.vue'
import QuestionVerfy from '../views/QuestionVerfy.vue'
import EmailQuestionsSet from '../views/EmailQuestionsSet.vue'
import CheckPassword from '../views/CheckPassword.vue'
import HomePage from '../views/HomePage.vue'
import PersonalCenter from '../views/PersonalCenter.vue'
import VideoDetection from '../views/VideoDetection.vue'
import DetectionPanel from '../views/DetectionPanel.vue'
import ProductIntroduction from '../views/ProductIntroduction.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [{ path: '/', name: 'home', component: Home },
    {path: '/register',name:'Register',component:Register},
    {path:'/login',name:'Login',component:Login},
    {path:'/test',name:'test',component:test},
    {path:'/dashBoard',name:'DashBoard',component:DashBoard},
    {path:'/forget',name:'Forget',component:Forget},
    { path:'/questionVerfy',name:'QuestionVerfy',component:QuestionVerfy },
    {path:'/emailquestionsset',name:'EmailQuestionsSet',component:EmailQuestionsSet},
    {path:'/checkpassword',name:'CheckPassword',component:CheckPassword},
    {path:'/homePage',name:'HomePage',component:HomePage},
    {path:'/personalcenter',name:'PersonalCenter',component:PersonalCenter},
    {path:'/detectionPanel',name:'DetectionPanel',component:DetectionPanel},
    {path:'/videoDetection',name:'VideoDetection',component:VideoDetection},
    {path:'/productIntroduction',name:'ProductIntroduction',component:ProductIntroduction},
  ]})

export default router
