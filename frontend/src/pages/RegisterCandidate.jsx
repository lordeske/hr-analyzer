import React from "react";
import styles from "../styles/register.module.css";

export default function RegisterCandidate() {
  const handleSubmit = (e) => e.preventDefault();

  return (
    <div className={styles.loginRoot}>
      
      <div className={styles.background} />

      <form
        className={`${styles.loginForm} ${styles.loginFormWide}`}
        onSubmit={handleSubmit}
      >
        <h3>Register as Candidate</h3>

        <div className={styles.formGrid}>
          <div className={styles.formField}>
            <label htmlFor="firstName">First Name</label>
            <input type="text" id="firstName" placeholder="First name" />
          </div>

          <div className={styles.formField}>
            <label htmlFor="lastName">Last Name</label>
            <input type="text" id="lastName" placeholder="Last name" />
          </div>

          <div className={`${styles.formField} ${styles.span2}`}>
            <label htmlFor="email">Email</label>
            <input type="email" id="email" placeholder="Email" />
          </div>

          <div className={styles.formField}>
            <label htmlFor="phone">Phone</label>
            <input type="tel" id="phone" placeholder="Phone" />
          </div>

          <div className={styles.formField}>
            <label htmlFor="password">Password</label>
            <input type="password" id="password" placeholder="Password" />
          </div>


          
        </div>

        <button type="submit">Register</button>

        <div className={styles.actionsRow}>
          <button type="button" className={`${styles.socialBtn} ${styles.go}`}>
            Register as HR
          </button>
          <button type="button" className={`${styles.socialBtn} ${styles.fb}`}>
            Login page
          </button>
        </div>
      </form>
    </div>
  );
}
