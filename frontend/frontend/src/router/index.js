import { createRouter, createWebHistory } from 'vue-router'

import Login from "../views/Login.vue";
import Register from "../views/Register.vue";
import Dashboard from "../views/Dashboard.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", redirect: "/dashboard" },
    { path: "/login", component: Login },
    { path: "/register", component: Register },
    { path: "/dashboard", component: Dashboard },
  ]
});

router.beforeEach((to) => {
  const token = localStorage.getItem("token");
  if (!token && to.path === "/dashboard") return "/login";
  if (token && (to.path === "/login" || to.path === "/register")) return "/dashboard";
});

export default router;
