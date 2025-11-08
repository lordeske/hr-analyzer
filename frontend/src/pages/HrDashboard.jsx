import React, { useEffect, useMemo, useState, useCallback } from "react";
import { Link } from "react-router-dom";
import { getMyJobs, createJob } from "../call/job.jsx";
import styles from "../styles/hr-dashboard.module.css";

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

export default function HrDashboard() {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(20);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState("");
  const [pendingKeyword, setPendingKeyword] = useState("");
  const [showCreate, setShowCreate] = useState(false);
  const [form, setForm] = useState({
    title: "",
    company: "",
    location: "",
    description: "",
    salary: "",
  });

  const fetchJobs = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const res = await getMyJobs({ page, size, sort: "createdAt,desc" });
      setItems(res.items);
      setTotalPages(res.totalPages);
      setTotalElements(res.totalElements);
    } catch (err) {
      setError(err?.message || "Failed to load jobs.");
    } finally {
      setLoading(false);
    }
  }, [page, size]);

  useEffect(() => {
    fetchJobs();
  }, [fetchJobs]);

  const filtered = useMemo(() => {
    const q = pendingKeyword.trim().toLowerCase();
    if (!q) return items;
    return items.filter((j) =>
      [j.title, j.company, j.location, j.description]
        .filter(Boolean)
        .some((v) => String(v).toLowerCase().includes(q))
    );
  }, [items, pendingKeyword]);

  const onOpenCreate = () => {
    setForm({ title: "", company: "", location: "", description: "", salary: "" });
    setShowCreate(true);
  };
  const onCloseCreate = () => setShowCreate(false);

  const onChangeForm = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: name === "salary" ? value.replace(/[^0-9]/g, "") : value }));
  };

  const onSubmitCreate = async (e) => {
    e.preventDefault();
    setError("");
    if (!form.title || !form.company || !form.location || !form.description) {
      setError("Please fill in all fields.");
      return;
    }
    const payload = {
      title: form.title.trim(),
      company: form.company.trim(),
      location: form.location.trim(),
      description: form.description.trim(),
      salary: Number(form.salary || 0),
    };
    try {
      setCreating(true);
      await createJob(payload);
      setShowCreate(false);
      if (page !== 0) {
        setPage(0);
      } else {
        await fetchJobs();
      }
    } catch (err) {
      setError(err?.message || "Failed to create job.");
    } finally {
      setCreating(false);
    }
  };

  const canPrev = page > 0;
  const canNext = page + 1 < totalPages;

  return (
    <div className={styles.page}>
      {loading && (
        <div className={styles.overlay}>
          <div className={styles.spinner} />
          <p>Loading jobs…</p>
        </div>
      )}

      <div className={styles.headerRow}>
        <h1 className={styles.title}>My Job Posts</h1>

        <form className={styles.searchBar} onSubmit={(e) => e.preventDefault()}>
          <input
            type="text"
            className={styles.searchInput}
            placeholder="Search by title, company, tech…"
            value={pendingKeyword}
            onChange={(e) => setPendingKeyword(e.target.value)}
          />
          <button className={`${styles.button} ${styles.buttonPrimary}`} type="submit">Search</button>
          <button
            type="button"
            className={`${styles.button} ${!pendingKeyword ? styles.ghost : ""}`}
            onClick={() => setPendingKeyword("")}
          >
            Clear
          </button>
        </form>

        <div>
          <button
            type="button"
            className={`${styles.button} ${styles.buttonPrimary}`}
            onClick={onOpenCreate}
          >
            + Add Job
          </button>
        </div>
      </div>

      <div className={styles.main}>
        {error && <div className={`${styles.alert} ${styles.alertError}`}>{error}</div>}

        <div className={styles.grid}>
          {filtered.map((job) => (
            <article
              key={job.id || job._id || `${job.title}-${job.company}-${job.createdAt}`}
              className={styles.card}
            >
              <header className={styles.cardHeader}>
                <h2 className={styles.cardTitle}>{job.title}</h2>
                <div className={styles.cardMeta}>
                  <span className={styles.company}>{job.company}</span>
                  <span className={styles.dot}>•</span>
                  <span className={styles.location}>{job.location}</span>
                </div>
              </header>

              <div className={styles.badges}>
                <span className={styles.badge}>Salary: <strong>{formatSalary(job.salary)}</strong></span>
                <span className={styles.badgeMuted}>Posted: {formatDate(job.createdAt)}</span>
                <span className={styles.badgeMuted}>ID: {job.id || job._id || "—"}</span>
              </div>

              <p className={styles.cardExcerpt}>
                {job.description
                  ? String(job.description).slice(0, 160) + (String(job.description).length > 160 ? "…" : "")
                  : "No description."}
              </p>

              <div className={styles.cardActions}>
                <Link to={`/job/${job.id || job._id || "new"}`} className={`${styles.button} ${styles.buttonPrimary}`}>
                  View
                </Link>
              </div>
            </article>
          ))}
        </div>
      </div>

      <div className={styles.paginationBar}>
        <div className={styles.pagination}>
          <button
            className={styles.button}
            disabled={!canPrev || loading}
            onClick={() => setPage((p) => Math.max(0, p - 1))}
          >
            Prev
          </button>
          <span className={styles.pageInfo}>
            Page <strong>{page + 1}</strong>
            {totalPages ? <> / <strong>{totalPages}</strong></> : null} • {totalElements} jobs
          </span>
          <button
            className={styles.button}
            disabled={!canNext || loading}
            onClick={() => setPage((p) => p + 1)}
          >
            Next
          </button>
        </div>
      </div>

      {showCreate && (
        <div className={styles.modal}>
          <form className={styles.modalCard} onSubmit={onSubmitCreate}>
            <h2 className={styles.modalTitle}>Create a new job</h2>

            <div className={styles.modalRow}>
              <label>Title</label>
              <input
                name="title"
                value={form.title}
                onChange={onChangeForm}
                className={styles.modalInput}
                placeholder="e.g. Senior Java Engineer"
                required
              />
            </div>

            <div className={styles.modalRow}>
              <label>Company</label>
              <input
                name="company"
                value={form.company}
                onChange={onChangeForm}
                className={styles.modalInput}
                placeholder="e.g. Acme Inc."
                required
              />
            </div>

            <div className={styles.modalRow}>
              <label>Location</label>
              <input
                name="location"
                value={form.location}
                onChange={onChangeForm}
                className={styles.modalInput}
                placeholder="e.g. Belgrade / Remote"
                required
              />
            </div>

            <div className={styles.modalRow}>
              <label>Description</label>
              <textarea
                name="description"
                value={form.description}
                onChange={onChangeForm}
                className={styles.modalTextarea}
                placeholder="Short description, requirements, stack…"
                required
                rows={3}
              />
            </div>

            <div className={styles.modalRow}>
              <label>Salary (EUR)</label>
              <input
                name="salary"
                inputMode="numeric"
                value={form.salary}
                onChange={onChangeForm}
                className={styles.modalInput}
                placeholder="e.g. 2500"
              />
            </div>

            <div className={styles.modalActions}>
              <button type="button" className={styles.button} onClick={onCloseCreate} disabled={creating}>Cancel</button>
              <button type="submit" className={`${styles.button} ${styles.buttonPrimary}`} disabled={creating}>
                {creating ? "Creating…" : "Create Job"}
              </button>
            </div>
          </form>
        </div>
      )}

      {creating && !showCreate && (
        <div className={styles.overlay}>
          <div className={styles.spinner} />
          <p>Saving…</p>
        </div>
      )}
    </div>
  );
}
