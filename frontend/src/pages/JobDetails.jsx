import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import styles from "../styles/job.module.css";
import { getJobById } from "../call/job.jsx";
import { getRole } from "../call/tokenJson.jsx";



function formatSalary(sal) {
  if (sal == null) return "—";
  try {
    return new Intl.NumberFormat(undefined, { style: "currency", currency: "EUR", maximumFractionDigits: 0 }).format(sal);
  } catch {
    return `${sal}`;
  }
}

function formatDate(iso) {
  if (!iso) return "—";
  const d = new Date(iso);
  return d.toLocaleString();
}

export default function JobDetails() {


  const { id } = useParams();
  const navigate = useNavigate();
  const role = getRole();

  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let mounted = true;
    setLoading(true);
    setError("");
    getJobById(id)
      .then((data) => mounted && setJob(data))
      .catch((err) => mounted && setError(err.message || "Error loading job"))
      .finally(() => mounted && setLoading(false));
    return () => (mounted = false);
  }, [id]);

  return (
    <div className={styles.page}>
      {loading && (
        <div className={styles.overlay}>
          <div className={styles.spinner} />
          <p>Loading job…</p>
        </div>
      )}

      <div className={styles.card}>
        {error && <div className={`${styles.alert} ${styles.alertError}`}>{error}</div>}

        {job && (
          <>
            <header className={styles.header}>
              <h1 className={styles.title}>{job.title}</h1>

              <div className={styles.meta}>
                <span className={styles.company}>{job.company}</span>
                <span className={styles.dot} aria-hidden>•</span>
                <span className={styles.location}>{job.location}</span>
              </div>

              <div className={styles.badges}>
                <span className={styles.badge}>
                  Salary: <strong>{formatSalary(job.salary)}</strong>
                </span>
                <span className={styles.badgeMuted}>
                  Posted: {formatDate(job.createdAt)}
                </span>
              </div>
            </header>

            <section className={styles.section}>
              <h2 className={styles.sectionTitle}>Job Description</h2>
              <p className={styles.description}>{job.description || "No description provided."}</p>
            </section>

            <section className={styles.footerRow}>
              <div className={styles.creator}>
                <span className={styles.creatorLabel}>Created by:</span>{" "}
                <span className={styles.creatorName}>{job.createdBy || "—"}</span>
              </div>

              <div className={styles.actions}>

                {role !== "HR" && (

                  <button className={`${styles.button} ${styles.buttonPrimary}`} onClick={(e) => {
                    e.stopPropagation();
                    navigate(`/apply/${job.id}`);
                  }}>
                    Test your CV
                  </button>

                )}

                <button className={`${styles.button} ${styles.buttonPrimary}`} onClick={() => navigate(`/jobs`)}>
                  Back to jobs
                </button>
              </div>
            </section>
          </>
        )}
      </div>
    </div>
  );
}
