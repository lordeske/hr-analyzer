import React from "react";
import styles from "../styles/login.module.css";

export default function LoginPage() {
  const handleSubmit = (e) => e.preventDefault();

  return (
    <div className={styles.loginRoot}>
      <div className={styles.background}>
      </div>

      <form className={styles.loginForm} onSubmit={handleSubmit}>
        <h3>Login Here</h3>

        <label htmlFor="username">Email</label>
        <input type="text" id="username" placeholder="Email" />

        <label htmlFor="password">Password</label>
        <input type="password" id="password" placeholder="Password" />

        <button type="submit">Log In</button>

        <div className={styles.social}>
          <button type="button" className={`${styles.socialBtn} ${styles.go}`}>
            <i></i> Register as HR
          </button>
          <button type="button" className={`${styles.socialBtn} ${styles.fb}`}>
            <i></i> Register as Empl
          </button>
        </div>
      </form>
    </div>
  );
}
