<template>
  <div class="container">
    <div class="card" style="max-width:460px;margin:40px auto;">
      <h2 style="margin-top:0;">Register</h2>
      <p style="margin-top:-8px;color:var(--muted);">Create your vaccine tracker account.</p>

      <div class="row">
        <input class="input" placeholder="Name" v-model="name" />
        <input class="input" placeholder="Email" v-model="email" />
        <input class="input" placeholder="Password" type="password" v-model="password" />

        <!-- ✅ password rules hint -->
        <p style="margin:0;color:var(--muted);font-size:13px;line-height:1.4;">
          Password must be at least <b>8 characters</b> and include
          <b>uppercase</b>, <b>lowercase</b>, <b>number</b>, and a <b>special character</b>.
        </p>

        <button class="btn" @click="doRegister">Create Account</button>

        <p style="color:var(--muted); margin:0;">
          Already have an account?
          <router-link to="/login">Login</router-link>
        </p>

        <p v-if="msg" style="margin:0;color:var(--accent);font-weight:700;">
          {{ msg }}
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
      name: "",
      email: "",
      password: "",
      msg: "",
      error: ""
    };
  },

  methods: {
    // ✅ Strong password validation
    validatePassword(pw) {
      if (!pw || pw.length < 8) return "Password must be at least 8 characters.";

      const hasUpper = /[A-Z]/.test(pw);
      const hasLower = /[a-z]/.test(pw);
      const hasNumber = /[0-9]/.test(pw);
      const hasSpecial = /[^A-Za-z0-9]/.test(pw);

      if (!hasUpper) return "Password must contain at least 1 uppercase letter.";
      if (!hasLower) return "Password must contain at least 1 lowercase letter.";
      if (!hasNumber) return "Password must contain at least 1 number.";
      if (!hasSpecial) return "Password must contain at least 1 special character.";

      return "";
    },

    async doRegister() {
      this.msg = "";
      this.error = "";

      // ✅ required field checks
      if (!this.name.trim()) {
        this.error = "Please enter your name.";
        return;
      }

      if (!this.email.trim()) {
        this.error = "Please enter your email.";
        return;
      }

      // ✅ password strength check
      const pwError = this.validatePassword(this.password);
      if (pwError) {
        this.error = pwError;
        return;
      }

      try {
        await api.post("/auth/register", {
          name: this.name,
          email: this.email,
          password: this.password
        });

        this.msg = "Account created! Please login.";
        setTimeout(() => this.$router.push("/login"), 800);
      } catch (e) {
        this.error = "Registration failed (maybe email already exists)";
      }
    }
  }
};
</script>
