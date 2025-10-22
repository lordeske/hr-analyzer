import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import styles from "../styles/my-cvs.module.css";
import { getLoggedUsersCvsSim } from "../call/cv.jsx";

function formatScore(score) {
  if (score == null) return "—";
  try {
    return new Intl.NumberFormat(undefined, {
      maximumFractionDigits: 0,
    }).format(Number(score));
  } catch {
    return String(score);
  }
}

function formatSortLabel(s) {
  switch (s) {
    case "uploadTime,desc": return "Newest first";
    case "uploadTime,asc":  return "Oldest first";
    case "matchScore,desc": return "Match score (high → low)";
    case "matchScore,asc":  return "Match score (low → high)";
    default: return s;
  }
}

export default function MyCvs() {
  const navigate = useNavigate();
  const [params, setParams] = useSearchParams();

  const page = Number(params.get("page") ?? 0);
  const size = Number(params.get("size") ?? 12);
  const sort = params.get("sort") ?? "uploadTime,desc";

  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState("");
  const [data, setData]       = useState({
    content: [],
    totalPages: 0,
    totalElements: 0,
    number: 0,
    size,
  });

  const [pendingSize, setPendingSize] = useState(size);
  const [pendingSort, setPendingSort] = useState(sort);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    setError("");

    getLoggedUsersCvsSim({ page, size, sort })
      .then((res) => {
        if (!alive) return;
        setData({
          content: res?.content ?? [],
          totalPages: res?.totalPages ?? 0,
          totalElements: res?.totalElements ?? 0,
          number: res?.number ?? page,
          size: res?.size ?? size,
        });
      })
      .catch((err) => {
        if (!alive) return;
        setError(err.message || "Failed to load your CVs.");
      })
      .finally(() => alive && setLoading(false));

    return () => { alive = false; };
  }, [page, size, sort]);

  const canPrev = useMemo(() => page > 0, [page]);
  const canNext = useMemo(
    () => (data.totalPages ? page < data.totalPages - 1 : data.content.length === size),
    [page, data, size]
  );

  const goTo = (p, s = size, so = sort) => {
    const sp = new URLSearchParams(params);
    sp.set("page", String(p));
    sp.set("size", String(s));
    sp.set("sort", so);
    setParams(sp, { replace: false });
  };

  const handleApply = (e) => {
    e.preventDefault();
    goTo(0, Number(pendingSize), pendingSort);
  };

  return (
    <div className={styles.page}>
      {loading && (
        <div className={styles.overlay}>
          <div className={styles.spinner} />
          <p>Loading your CVs…</p>
        </div>
      )}

      {/* Header */}
      <div className={styles.headerRow}>
        <h1 className={styles.title}>My CVs</h1>

        <form className={styles.controls} onSubmit={handleApply}>
          <label className={styles.controlItem}>
            <span>Page size</span>
            <select
              value={pendingSize}
              onChange={(e) => setPendingSize(e.target.value)}
            >
              <option value={6}>6</option>
              <option value={12}>12</option>
              <option value={18}>18</option>
              <option value={24}>24</option>
            </select>
          </label>

          <label className={styles.controlItem}>
            <span>Sort by</span>
            <select
              value={pendingSort}
              onChange={(e) => setPendingSort(e.target.value)}
              title={formatSortLabel(pendingSort)}
            >
              <option value="uploadTime,desc">Newest first</option>
              <option value="uploadTime,asc">Oldest first</option>
              <option value="matchScore,desc">Match score (high → low)</option>
              <option value="matchScore,asc">Match score (low → high)</option>
            </select>
          </label>

          <button className={`${styles.button} ${styles.buttonPrimary}`} type="submit">
            Apply
          </button>

          <button
            type="button"
            className={`${styles.button} ${styles.buttonPrimary}`}
            onClick={() => navigate("/jobs")}
          >
            View all jobs
          </button>
        </form>
      </div>

      {error && <div className={`${styles.alert} ${styles.alertError}`}>{error}</div>}

      {!loading && !error && data.content.length === 0 && (
        <div className={styles.empty}>
          <p>No CVs found yet.</p>
        </div>
      )}

      {/* Grid of cards */}
      <div className={styles.grid}>
        {data.content.map((item) => (
          <article key={item.cvId} className={styles.card}>
            <header className={styles.cardHeader}>
              <h2 className={styles.cardTitle}>{item.jobTitle}</h2>
              <div className={styles.cardMeta}>
                <span className={styles.company}>{item.companyName}</span>
              </div>
            </header>

            <div className={styles.scoreRow}>
              <div className={styles.scoreLabel}>Match</div>
              <div className={styles.scoreValue}>
                {formatScore(item.matchScore)}<span className={styles.of}>/100</span>
              </div>
            </div>
            <div className={styles.scoreBar}>
              <div
                className={styles.scoreFill}
                style={{
                  width: `${Math.max(0, Math.min(100, Number(item.matchScore) || 0))}%`,
                }}
              />
            </div>

            <div className={styles.cardActions}>
              <button
                className={`${styles.button} ${styles.buttonPrimary}`}
                onClick={() => navigate(`/cv/${item.cvId}`)}
              >
                View result
              </button>
              <button
                className={`${styles.button} ${styles.buttonPrimary}`}
                onClick={() => navigate(`/job/${item.jobId}`)}
              >
                View job
              </button>
            </div>
          </article>
        ))}
      </div>

      <div className={styles.pagination}>
        <button className={`${styles.button} ${styles.buttonPrimary}`} disabled={!canPrev} onClick={() => goTo(page - 1)}>
          Prev
        </button>
        <span className={styles.pageInfo}>
          Page <strong>{(data.number ?? page) + 1}</strong>
          {data.totalPages ? <> / <strong>{data.totalPages}</strong></> : null}
        </span>
        <button className={`${styles.button} ${styles.buttonPrimary}`} disabled={!canNext} onClick={() => goTo(page + 1)}>
          Next
        </button>
      </div>
    </div>
  );
}
