import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../call/auth.jsx";  
import styles from "../styles/login.module.css";

export default function LoginPage() {
  const [email, setEmail]       = useState("");
  const [password, setPassword] = useState("");
  const [error, setError]       = useState("");
  const [message, setMessage]   = useState("");
  const [loading, setLoading]   = useState(false);

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");

    
    if (!email || !password) {
      setError("Please enter both email and password.");
      return;
    }

    setLoading(true);

    try {
      const res = await login({ email, password });

      
      console.log("Login success:", res);

      setMessage("Login successful! Redirecting…");

      

      setTimeout(() => navigate("/jobs"), 700);
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.message ||
        "Invalid email or password.";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.loginRoot}>
      <div className={styles.background}>
       
      </div>

      
      {loading && (
        <div className={styles.overlay}>
          <div className={styles.spinner}></div>
          <p>Logging you in…</p>
        </div>
      )}

      <form className={styles.loginForm} onSubmit={handleSubmit}>
        <h3>Login Here</h3>

       
        {error && (
          <div className={`${styles.alert} ${styles.alertError}`}>{error}</div>
        )}
        {message && (
          <div className={`${styles.alert} ${styles.alertOk}`}>{message}</div>
        )}

        <label htmlFor="email">Email</label>
        <input
          type="email"
          id="email"
          placeholder="Email"
          value={email}
          onChange={(e) => {
            setEmail(e.target.value);
            setError("");
            setMessage("");
          }}
          required
          autoComplete="email"
        />

        <label htmlFor="password">Password</label>
        <input
          type="password"
          id="password"
          placeholder="Password"
          value={password}
          onChange={(e) => {
            setPassword(e.target.value);
            setError("");
            setMessage("");
          }}
          required
          autoComplete="current-password"
        />

        <button type="submit" disabled={loading}>
          {loading ? "Please wait…" : "Log In"}
        </button>

        <div className={styles.social}>
          <button
            type="button"
            className={`${styles.socialBtn} ${styles.go}`}
            onClick={() => navigate("/register-hr")}
          >
            Register as HR
          </button>
          <button
            type="button"
            className={`${styles.socialBtn} ${styles.fb}`}
            onClick={() => navigate("/register-candidate")}
          >
            Register as Candidate
          </button>
        </div>
      </form>
    </div>
  );
}
