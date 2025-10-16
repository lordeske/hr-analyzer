import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { uploadCvFile } from "../call/cv.jsx";
import { getJobById } from "../call/job.jsx";
import styles from "../styles/apply.module.css";

export default function ApplyCvPage() {
  const { jobId } = useParams();
  const navigate = useNavigate();

  const [file, setFile] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [loadingJob, setLoadingJob] = useState(true);
  const [jobInfo, setJobInfo] = useState(null);

  useEffect(() => {
    let alive = true;

    async function loadJob() {
      if (!jobId) return;
      try {
        setLoadingJob(true);
        const job = await getJobById(Number(jobId));
        if (alive) setJobInfo(job);
      } catch (e) {
        if (alive) {
          console.error("Error fetching job:", e);
          setError(
            e?.response?.data?.message ||
            e.message ||
            "Failed to load job details."
          );
        }
      } finally {
        if (alive) setLoadingJob(false);
      }
    }

    loadJob();
    return () => {
      alive = false;
    };
  }, [jobId]);

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (!file) {
      setError("Please select a file (PDF/DOC/DOCX).");
      return;
    }

    const okTypes = [
      "application/pdf",
      "application/msword",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    ];

    if (!okTypes.includes(file.type)) {
      setError("Allowed file types: PDF, DOC, DOCX.");
      return;
    }

    if (file.size > 10 * 1024 * 1024) {
      setError("File is too large (max 10MB).");
      return;
    }

    try {
      setLoading(true);
      const { testId } = await uploadCvFile({ jobId: Number(jobId), file });
      navigate(`/cv-test-status/${testId}`);
    } catch (e2) {
      console.error("Error uploading CV:", e2);
      setError(
        e2?.response?.data?.message || e2.message || "File upload failed."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        <h1>Test your CV</h1>

        <p>
          Job Title:{" "}
          <strong>
            {loadingJob ? "Loading…" : jobInfo?.title || "—"}
          </strong>{" "}
          | Location:{" "}
          <strong>
            {loadingJob ? "Loading…" : jobInfo?.location || "—"}
          </strong>
        </p>

        {error && <div className={styles.alert}>{error}</div>}

        <form onSubmit={onSubmit} className={styles.form}>
          <input
            type="file"
            accept=".pdf,.doc,.docx"
            onChange={(e) => setFile(e.target.files?.[0] ?? null)}
          />

          <button className={styles.button} type="submit" disabled={loading}>
            {loading ? "Uploading…" : "Upload & Test"}
          </button>

          <button
            type="button"
            className={styles.buttonGhost}
            onClick={() => navigate(-1)}
          >
            Cancel
          </button>
        </form>
      </div>
    </div>
  );
}
