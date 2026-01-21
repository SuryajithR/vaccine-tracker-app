<template>
  <div>
    <header class="topbar">
      <div class="container topbar-inner">
        <div class="brand" @click="$router.push('/dashboard')">
          <div class="brand-icon">💉</div>
          <div>
            <div class="brand-title">Vaccine Tracker</div>
            <div class="brand-sub">Immunization Log</div>
          </div>
        </div>

        <div class="nav">
          <span v-if="name" class="hello">Hi, {{ name }}</span>

          <button v-if="token" class="btn secondary" @click="logout">Logout</button>

          <button v-if="!token" class="btn secondary" @click="$router.push('/login')">Login</button>
          <button v-if="!token" class="btn" @click="$router.push('/register')">Register</button>
        </div>
      </div>
    </header>


    <router-view />
  </div>
</template>

<script>
export default {
  data() {
    return {
      token: localStorage.getItem("token"),
      name: localStorage.getItem("name")
    };
  },

  methods: {
    syncAuth() {
      this.token = localStorage.getItem("token");
      this.name = localStorage.getItem("name");
    },

    logout() {
      const ok = confirm("Are you sure you want to logout?");
      if (!ok) return;

      localStorage.removeItem("token");
      localStorage.removeItem("name");
      this.$router.push("/login");
    }

  },

  mounted() {
    // ✅ ensure header updates when app loads
    this.syncAuth();
  },

  watch: {
    // ✅ update header whenever route changes (after login/register navigation)
    $route() {
      this.syncAuth();
    }
  }
};
</script>

