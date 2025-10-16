import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import styles from "../styles/jobs-list.module.css";
import { getJobs, advancedSearchJobs } from "../call/job.jsx";

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
  return new Date(iso).toLocaleDateString();
}

export default function JobsList() {
  const navigate = useNavigate();
  const [params, setParams] = useSearchParams();

  const page = Number(params.get("page") ?? 0);
  const size = Number(params.get("size") ?? 12);
  const sort = params.get("sort") ?? "createdAt,desc";
  const q    = params.get("q") ?? ""; 

  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState("");
  const [data, setData]       = useState({ content: [], totalPages: 0, totalElements: 0, number: 0, size });

  const [pendingSize, setPendingSize]       = useState(size);
  const [pendingSort, setPendingSort]       = useState(sort);
  const [pendingKeyword, setPendingKeyword] = useState(q);

  useEffect(() => {
    let mounted = true;
    setLoading(true);
    setError("");

    const run = q.trim()
      ? advancedSearchJobs({ keyword: q, page, size, sort })
      : getJobs({ page, size, sort });

    run
      .then((resp) => {
        if (!mounted) return;
        setData({
          content: resp.content ?? [],
          number: resp.number ?? page,
          size: resp.size ?? size,
          totalPages: resp.totalPages ?? 0,
          totalElements: resp.totalElements ?? (resp.content?.length ?? 0),
        });
      })
      .catch((err) => setError(err?.message || "Failed to load jobs"))
      .finally(() => setLoading(false));

    return () => { mounted = false; };
  }, [page, size, sort, q]);

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

  const handleApplyFilters = (e) => {
    e.preventDefault();
    const sp = new URLSearchParams(params);
    sp.set("page", "0");
    sp.set("size", String(pendingSize));
    sp.set("sort", pendingSort);
   
    setParams(sp, { replace: false });
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    const sp = new URLSearchParams(params);
    sp.set("page", "0"); 
    if (pendingKeyword.trim()) sp.set("q", pendingKeyword.trim());
    else sp.delete("q"); 
    setParams(sp, { replace: false });
  };

  return (
    <div className={styles.page}>
      {loading && (
        <div className={styles.overlay}>
          <div className={styles.spinner} />
          <p>Loading jobs…</p>
        </div>
      )}

      <div className={styles.headerRow}>
        <h1 className={styles.title}>Open Positions</h1>

       
        <form className={styles.searchBar} onSubmit={handleSearchSubmit}>
          <input
            type="text"
            className={styles.searchInput}
            placeholder="Search by title, company, tech…"
            value={pendingKeyword}
            onChange={(e) => setPendingKeyword(e.target.value)}
          />
          <button className={`${styles.button} ${styles.buttonPrimary}`} type="submit">
            Search
          </button>
          {q && (
            <button
              type="button"
              className={styles.button}
              onClick={() => {
                setPendingKeyword("");
                const sp = new URLSearchParams(params);
                sp.delete("q"); sp.set("page", "0");
                setParams(sp, { replace: false });
              }}
            >
              Clear
            </button>
          )}
        </form>

       
        <form className={styles.controls} onSubmit={handleApplyFilters}>
          <label className={styles.controlItem}>
            <span>Page size</span>
            <select value={pendingSize} onChange={(e) => setPendingSize(e.target.value)}>
              <option value={6}>6</option>
              <option value={9}>9</option>
              <option value={12}>12</option>
              <option value={15}>15</option>
            </select>
          </label>

          <label className={styles.controlItem}>
            <span>Sort</span>
            <select value={pendingSort} onChange={(e) => setPendingSort(e.target.value)}>
              <option value="createdAt,desc">Newest</option>
              <option value="createdAt,asc">Oldest</option>
              <option value="salary,desc">Salary (high → low)</option>
              <option value="salary,asc">Salary (low → high)</option>
              <option value="title,asc">Title (A→Z)</option>
              <option value="title,desc">Title (Z→A)</option>
            </select>
          </label>

          <button className={`${styles.button} ${styles.buttonSecondary}`} type="submit">Apply</button>
        </form>
      </div>

      {error && <div className={`${styles.alert} ${styles.alertError}`}>{error}</div>}

      <div className={styles.grid}>
        {data.content.map((job) => (
          <article key={job.id} className={styles.card} onClick={() => navigate(`/job/${job.id}`)} role="button">
            <header className={styles.cardHeader}>
              <h2 className={styles.cardTitle}>{job.title}</h2>
              <div className={styles.cardMeta}>
                <span className={styles.company}>{job.company}</span>
                <span className={styles.dot} aria-hidden>•</span>
                <span className={styles.location}>{job.location}</span>
              </div>
            </header>

            <div className={styles.badges}>
              <span className={styles.badge}>Salary: <strong>{formatSalary(job.salary)}</strong></span>
              <span className={styles.badgeMuted}>Posted: {formatDate(job.createdAt)}</span>
            </div>

            <p className={styles.cardExcerpt}>
              {job.description ? String(job.description).slice(0, 160) + (String(job.description).length > 160 ? "…" : "") : "No description."}
            </p>

            <div className={styles.cardActions}>
              <button className={`${styles.button} ${styles.buttonPrimary}`} onClick={(e) => { e.stopPropagation(); navigate(`/job/${job.id}`); }}>
                View details
              </button>
              <button className={`${styles.button} ${styles.buttonPrimary}`} onClick={(e) => { e.stopPropagation(); navigate(`/apply/${job.id}`); }}>
                Test your CV
              </button>
            </div>
          </article>
        ))}
      </div>

      <div className={styles.pagination}>
        <button className={styles.button} disabled={!canPrev} onClick={() => {
          const sp = new URLSearchParams(params);
          sp.set("page", String(page - 1));
          setParams(sp, { replace: false });
        }}>
          Prev
        </button>
        <span className={styles.pageInfo}>
          Page <strong>{(data.number ?? page) + 1}</strong>
          {data.totalPages ? <> / <strong>{data.totalPages}</strong></> : null}
        </span>
        <button className={styles.button} disabled={!canNext} onClick={() => {
          const sp = new URLSearchParams(params);
          sp.set("page", String(page + 1));
          setParams(sp, { replace: false });
        }}>
          Next
        </button>
      </div>
    </div>
  );
}
