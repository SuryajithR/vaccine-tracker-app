<template>
  <div class="container">
    <div class="card" style="max-width:460px;margin:40px auto;">
      <h2 style="margin-top:0;">Login</h2>
      <p style="margin-top:-8px;color:var(--muted);">Access your immunization log.</p>

      <div class="row">
        <input class="input" placeholder="Email" v-model="email" />
        <input class="input" placeholder="Password" type="password" v-model="password" />
        <button class="btn" @click="doLogin">Login</button>
        <p style="color:var(--muted); margin:0;">
          No account?
          <router-link to="/register">Register</router-link>
        </p>

        <p v-if="error" style="margin:0;color:var(--danger);font-weight:700;">
          {{ error }}
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import api from "../api";

export default {
  data() {
    return {
      email: "",
      password: "",
      error: ""
    };
  },
  methods: {
    async doLogin() {
      this.error = "";
      try {
        const res = await api.post("/auth/login", {
          email: this.email,
          password: this.password
        });
        localStorage.setItem("token", res.data.token);
        localStorage.setItem("name", res.data.name);
        this.$router.push("/dashboard");
      } catch (e) {
        this.error = "Invalid email or password";
      }
    }
  }
};
</script>
