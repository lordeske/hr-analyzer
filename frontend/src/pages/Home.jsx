import { Link } from "react-router-dom";
import styles from "../styles/home.module.css";

export default function Home() {
  return (
    <main className={styles.page}>
      {/* Animirana pozadina */}
      <div className={styles.bgGradient} />
      <div className={`${styles.blob} ${styles.blobA}`} />
      <div className={`${styles.blob} ${styles.blobB}`} />
      <div className={styles.noise} />

      {/* Hero sadržaj */}
      <section className={styles.hero}>
        <div className={styles.badge}>AI powered</div>

        <h1 className={styles.title}>Level up your job hunt</h1>

        <p className={styles.typing} data-text="Test your CV with AI. Get instant insights.">
          <span>Test your CV with AI. Get instant insights.</span>
        </p>

        <p className={styles.subtitle}>
          Upload, analyze and compare your CV against openings. Get a match score and actionable tips — instantly.
        </p>

        {/* Primarne akcije */}
        <div className={styles.actions}>
          <Link to="/login" className={`${styles.btn} ${styles.btnPrimary}`}>
            Log in
          </Link>

          <div className={styles.split}>
            <Link to="/register-candidate" className={`${styles.btn} ${styles.btnGlass}`}>
              Register — Candidate
            </Link>
            <Link to="/register-hr" className={`${styles.btn} ${styles.btnGlass}`}>
              Register — HR
            </Link>
          </div>
        </div>

        {/* Sekundarne akcije (opciono) */}
        <div className={styles.secondary}>
          <Link to="/jobs" className={styles.link}>Browse jobs</Link>
          <span className={styles.dot} />
          <Link to="/my-cvs" className={styles.link}>My CVs</Link>
        </div>
      </section>
    </main>
  );
}
