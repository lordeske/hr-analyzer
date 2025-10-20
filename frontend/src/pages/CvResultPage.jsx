import React, { useEffect, useRef, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import styles from "../styles/cv-result.module.css";
import { getCvById } from "../call/cv.jsx";

const isPending = (s) => String(s || "").toLowerCase() === "pending";
const isCompleted = (s) => String(s || "").toLowerCase() === "completed";

export default function CvResultPage() {
  const { id } = useParams(); 
  const navigate = useNavigate();

  const [cv, setCv] = useState(null);
  const [loading, setLoading] = useState(true);   
  const [error, setError] = useState("");
  const [lastUpdated, setLastUpdated] = useState(null);

  const timerRef = useRef(null);

  const fetchOnce = async () => {
    try {
      const data = await getCvById(Number(id));
      setCv(data);
      setLastUpdated(new Date());
      setError("");
      if (isCompleted(data?.status)) {
       
        if (timerRef.current) clearInterval(timerRef.current);
        timerRef.current = null;
      }
    } catch (e) {
      
      const msg = e?.response?.data?.message || e.message || "Failed to load CV.";
      setError(msg);
     
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    fetchOnce(); 
   
    timerRef.current = setInterval(fetchOnce, 3000);
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
      timerRef.current = null;
    };
   
  }, [id]);

  const status = cv?.status;

  return (
    <div className={styles.page}>
      {(loading && !cv) && (
        <div className={styles.overlay}>
          <div className={styles.spinner} />
          <p>Loading CV…</p>
        </div>
      )}

      <div className={styles.card}>
        <header className={styles.header}>
          <h1 className={styles.title}>CV Test Result</h1>
          <div className={styles.badgeRow}>
            <span className={`${styles.badge} ${isCompleted(status) ? styles.badgeOk : styles.badgeInfo}`}>
              {status || "Pending"}
            </span>
            <span className={styles.muted}>ID: <strong>{id}</strong></span>
            {lastUpdated && (
              <span className={styles.muted}>
                Last update: {lastUpdated.toLocaleTimeString()}
              </span>
            )}
          </div>
        </header>

        {error && <div className={`${styles.alert} ${styles.alertError}`}>{error}</div>}

       
        {cv && isPending(status) && (
          <>
            <section className={styles.section}>
              <h2 className={styles.sectionTitle}>Processing…</h2>
              <p className={styles.par}>
                Vaš CV je u obradi. Ova stranica će se automatski osvežavati dok status ne bude <strong>Completed</strong>.
              </p>
            </section>

            <section className={styles.section}>
              <h2 className={styles.sectionTitle}>Known info</h2>
              <div className={styles.kvGrid}>
                <KV k="Job ID" v={cv.jobId} />
                <KV k="Job Title" v={cv.jobTitle} />
                <KV k="Email" v={cv.email} />
                <KV k="Phone" v={cv.phoneNumber} />
                <KV k="Uploaded at" v={cv.uploadTime} />
              </div>
            </section>
          </>
        )}

       
        {cv && isCompleted(status) && (
          <>
            <section className={styles.section}>
              <h2 className={styles.sectionTitle}>Candidate</h2>
              <div className={styles.kvGrid}>
                <KV k="First name" v={cv.candidateFirstName} />
                <KV k="Last name" v={cv.candidateLastName} />
                <KV k="Email" v={cv.email} />
                <KV k="Phone" v={cv.phoneNumber} />
              </div>
            </section>

            <section className={styles.section}>
              <h2 className={styles.sectionTitle}>Job</h2>
              <div className={styles.kvGrid}>
                <KV k="Job ID" v={cv.jobId} />
                <KV k="Job Title" v={cv.jobTitle} />
                <KV k="Uploaded at" v={cv.uploadTime} />
                <KV k="Match score" v={cv.matchScore} />
              </div>
            </section>

            {Array.isArray(cv.suggestion) && cv.suggestion.length > 0 && (
              <section className={styles.section}>
                <h2 className={styles.sectionTitle}>Suggestions</h2>
                <ul className={styles.list}>
                  {cv.suggestion.map((s, i) => (
                    <li key={i}>{String(s)}</li>
                  ))}
                </ul>
              </section>
            )}

            <section className={styles.actionsRow}>
              <Link className={styles.button} to="/jobs">Back to jobs</Link>
              {cv.jobId && (
                <button className={styles.button } onClick={() => navigate(`/job/${cv.jobId}`)}>
                  View job
                </button>
              )}
            </section>
          </>
        )}

       
        {cv && !isPending(status) && !isCompleted(status) && (
          <section className={styles.section}>
            <h2 className={styles.sectionTitle}>Raw response</h2>
            <pre className={styles.code}>{JSON.stringify(cv, null, 2)}</pre>
          </section>
        )}
      </div>
    </div>
  );
}

function KV({ k, v }) {
  const formattedValue = (() => {
    if (k.toLowerCase().includes("upload")) {
      try {
        return new Date(v).toLocaleString("sr-RS", {
          day: "2-digit",
          month: "2-digit",
          year: "numeric",
          hour: "2-digit",
          minute: "2-digit",
        });
      } catch {
        return v;
      }
    }
    return v ?? "—";
  })();

  return (
    <div className={styles.kv}>
      <div className={styles.k}>{k}</div>
      <div className={styles.v}>{formattedValue}</div>
    </div>
  );
}

