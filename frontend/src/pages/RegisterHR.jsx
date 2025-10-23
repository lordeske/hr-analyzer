import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { RegisterHrApi } from "../call/auth.jsx";
import styles from "../styles/register.module.css";

export default function RegisterCandidate() {
  const [firstName, setFirstName] = useState("");
  const [lastName,  setLastName]  = useState("");
  const [phone,     setPhone]     = useState("");
  const [email,     setEmail]     = useState("");
  const [password,  setPassword]  = useState("");

  const [error,   setError]   = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleInputClear = (setter) => (e) => {
    setter(e.target.value);
    setError("");
    setMessage("");
  };

  const getAxiosErrorMessage = (err) => {
   
    return (
      err?.response?.data?.message ||
      err?.response?.data?.error ||
      err?.message ||
      "Unexpected error"
    );
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");

    
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setError("Please enter a valid email.");
      return;
    }
    if (password.length < 6) {
      setError("Password must be at least 6 characters.");
      return;
    }

    setLoading(true);

    const registerRequest = { firstName, lastName, email, phone, password };

    try {
      const res = await RegisterHrApi(registerRequest);
      setMessage("Account created successfully. Redirecting to login…");

      
      setTimeout(() => navigate("/login"), 900);
    } catch (err) {
      const msg = getAxiosErrorMessage(err);
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.registerRoot}>
      <div className={styles.background} />

      
      {loading && (
        <div className={styles.overlay} aria-live="polite" aria-busy="true">
          <div className={styles.spinner} />
          <p>Creating your account…</p>
        </div>
      )}

      <form
        className={`${styles.loginForm} ${styles.loginFormWide}`}
        onSubmit={handleSubmit}
        aria-describedby={error ? "form-error" : undefined}
      >
        <h3>Register as Hr</h3>

       
        {error && (
          <div id="form-error" className={`${styles.alert} ${styles.alertError}`} role="alert">
            {error}
          </div>
        )}
        {message && (
          <div className={`${styles.alert} ${styles.alertOk}`} role="status">
            {message}
          </div>
        )}

        <div className={styles.formGrid}>
          <div className={styles.formField}>
            <label htmlFor="firstName">First Name</label>
            <input
              type="text" id="firstName" value={firstName}
              onChange={handleInputClear(setFirstName)} required placeholder="First name"
            />
          </div>

          <div className={styles.formField}>
            <label htmlFor="lastName">Last Name</label>
            <input
              type="text" id="lastName" value={lastName}
              onChange={handleInputClear(setLastName)} required placeholder="Last name"
            />
          </div>

          <div className={`${styles.formField} ${styles.span2}`}>
            <label htmlFor="email">Email</label>
            <input
              type="email" id="email" value={email}
              onChange={handleInputClear(setEmail)} required placeholder="Email"
              autoComplete="email"
            />
          </div>

          <div className={styles.formField}>
            <label htmlFor="phone">Phone</label>
            <input
              type="tel" id="phone" value={phone}
              onChange={handleInputClear(setPhone)} required placeholder="Phone"
              autoComplete="tel"
            />
          </div>

          <div className={styles.formField}>
            <label htmlFor="password">Password</label>
            <input
              type="password" id="password" value={password}
              onChange={handleInputClear(setPassword)} required placeholder="Password"
              autoComplete="new-password"
            />
          </div>
        </div>

        <button type="submit" disabled={loading} aria-disabled={loading}>
          {loading ? "Please wait…" : "Register"}
        </button>

        <div className={styles.actionsRow}>
          <button type="button" className={`${styles.socialBtn} ${styles.go}`} onClick={() => navigate("/register-candidate")}>
            Register as Candidate
          </button>
          <button type="button" className={`${styles.socialBtn} ${styles.fb}`} onClick={() => navigate("/login")}>
            Login page
          </button>
        </div>
      </form>
    </div>
  );
}
