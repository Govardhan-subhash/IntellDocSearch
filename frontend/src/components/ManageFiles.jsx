import React, { useState, useEffect } from "react";

const ManageFiles = ({ userId, token, show, onClose }) => {
  const [userFiles, setUserFiles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [deletingFile, setDeletingFile] = useState(null);

  // Fetch files when component mounts or show becomes true
  useEffect(() => {
    if (show && userId && token) {
      fetchFiles();
    }
  }, [show, userId, token]);

  const fetchFiles = async () => {
    setLoading(true);
    try {
        const res = await fetch(
            `http://localhost:8082/documents/user/files`,
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
          );
      if (!res.ok) throw new Error("Failed to fetch files");
      const data = await res.json();
      setUserFiles(data);
    } catch (err) {
      console.error("Fetch files error:", err);
      setUserFiles([]);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteFile = async (fileName) => {
    if (!window.confirm(`Delete "${fileName}"? This action cannot be undone.`))
      return;

    setDeletingFile(fileName);

    try {
        const res = await fetch(
            `http://localhost:8082/documents/user/files/${encodeURIComponent(fileName)}`,
            {
              method: "DELETE",
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
          );
      if (!res.ok) throw new Error("Delete failed");

      // Refresh files list
      fetchFiles();
    } catch (err) {
      alert("Failed to delete file: " + err.message);
    } finally {
      setDeletingFile(null);
    }
  };

  if (!show) return null;

  return (
    <div
      className="modal fade show"
      style={{ display: "block", backgroundColor: "rgba(0,0,0,0.5)" }}
      tabIndex="-1"
      role="dialog"
      aria-modal="true"
    >
      <div
        className="modal-dialog"
        role="document"
        style={{ maxWidth: "500px" }}
      >
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title">Manage Uploaded Files</h5>
            <button
              type="button"
              className="btn-close"
              aria-label="Close"
              onClick={onClose}
            ></button>
          </div>
          <div className="modal-body">
            {loading ? (
              <p>Loading files...</p>
            ) : userFiles.length === 0 ? (
              <p>No uploaded files found.</p>
            ) : (
              <ul className="list-group">
                {userFiles.map((fileName) => (
                  <li
                    key={fileName}
                    className="list-group-item d-flex justify-content-between align-items-center"
                  >
                    <span>{fileName}</span>
                    <button
                      className="btn btn-danger btn-sm"
                      onClick={() => handleDeleteFile(fileName)}
                      disabled={deletingFile === fileName}
                    >
                      {deletingFile === fileName ? "Deleting..." : "Delete"}
                    </button>
                  </li>
                ))}
              </ul>
            )}
          </div>
          <div className="modal-footer">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onClose}
              disabled={loading}
            >
              Close
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ManageFiles;
