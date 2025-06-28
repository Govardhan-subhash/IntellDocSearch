// import React, { useState, useEffect } from "react";

// const ManageFiles = ({ token, show, onClose }) => {
//   const [userFiles, setUserFiles] = useState([]);
//   const [loading, setLoading] = useState(false);
//   const [deletingFile, setDeletingFile] = useState(null);

//   useEffect(() => {
//     if (show && token) {
//       fetchFiles();
//     }
//   }, [show, token]);

//   const fetchFiles = async () => {
//     setLoading(true);
//     try {
//       const res = await fetch(`http://localhost:8082/documents/user/files`, {
//         headers: { Authorization: `Bearer ${token}` },
//       });
//       if (!res.ok) throw new Error("Failed to fetch files");

//       const data = await res.json(); // data is an array of filenames
//       console.log("Fetched files:", data);
//       setUserFiles(data);
//     } catch (err) {
//       console.error("Fetch files error:", err);
//       setUserFiles([]);
//     } finally {
//       setLoading(false);
//     }
//   };

//   const handleDeleteFile = async (fileName) => {
//     if (!window.confirm(`Delete "${fileName}"? This action cannot be undone.`)) return;

//     setDeletingFile(fileName);

//     try {
//       // Adjust URL if your backend expects filename param in a different way
//       const res = await fetch(
//         `http://localhost:8082/documents/user/files?fileName=${encodeURIComponent(fileName)}`,
//         {
//           method: "DELETE",
//           headers: { Authorization: `Bearer ${token}` },
//         }
//       );
//       if (!res.ok) throw new Error("Delete failed");

//       await fetchFiles(); // refresh file list after deletion
//     } catch (err) {
//       alert("Failed to delete file: " + err.message);
//     } finally {
//       setDeletingFile(null);
//     }
//   };

//   if (!show) return null;

//   return (
//     <div
//       className="modal fade show"
//       style={{
//         display: "block",
//         backgroundColor: "rgba(0,0,0,0.5)",
//         zIndex: 1055,
//       }}
//       tabIndex="-1"
//       role="dialog"
//       aria-modal="true"
//     >
//       <div className="modal-dialog" role="document" style={{ maxWidth: "500px" }}>
//         <div className="modal-content">
//           <div className="modal-header">
//             <h5 className="modal-title">Manage Uploaded Files</h5>
//             <button type="button" className="btn-close" onClick={onClose} aria-label="Close"></button>
//           </div>
//           <div className="modal-body">
//             {loading ? (
//               <p>Loading files...</p>
//             ) : userFiles.length === 0 ? (
//               <p>No uploaded files found.</p>
//             ) : (
//               <ul className="list-group">
//                 {userFiles.map((fileName) => (
//                   <li
//                     key={fileName}
//                     className="list-group-item d-flex justify-content-between align-items-center"
//                   >
//                     <span style={{ color: "black" }}>{fileName}</span>
//                     <button
//                       className="btn btn-danger btn-sm"
//                       onClick={() => handleDeleteFile(fileName)}
//                       disabled={deletingFile === fileName}
//                     >
//                       {deletingFile === fileName ? "Deleting..." : "Delete"}
//                     </button>
//                   </li>
//                 ))}
//               </ul>
//             )}
//           </div>
//           <div className="modal-footer">
//             <button
//               type="button"
//               className="btn btn-secondary"
//               onClick={onClose}
//               disabled={loading}
//             >
//               Close
//             </button>
//           </div>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default ManageFiles;



import React, { useState, useEffect, useCallback } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';

const ManageFiles = ({ token, show, onClose }) => {
  const [userFiles, setUserFiles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [deletingFile, setDeletingFile] = useState(null);

  // const fetchFiles = useCallback(async () => {
  //   setLoading(true);
  //   try {
  //     const res = await fetch(`http://localhost:8086/api/documents/user/files`, {
  //       headers: { Authorization: `Bearer ${token}` },
  //     });
  //     if (!res.ok) throw new Error("Failed to fetch files");
  //     const data = await res.json();
  //     console.log("Fetched files data:", data);
  //     setUserFiles(data);
  //   } catch (err) {
  //     console.error("Fetch files error:", err);
  //     setUserFiles([]);
  //   } finally {
  //     setLoading(false);
  //   }
  // }, [token]); // <== token is a dependency

  // useEffect(() => {
  //   if (show && token) {
  //     fetchFiles();
  //   }
  // }, [show, token, fetchFiles]); // <== fetchFiles is now safe to include

  // const handleDeleteFile = async (documentId, fileName) => {
  //   if (!window.confirm(`Delete "${fileName}"? This action cannot be undone.`)) return;

  //   setDeletingFile(documentId);

  //   try {
  //     const res = await fetch(
  //       `http://localhost:8086/api/documents/user/files?documentId=${encodeURIComponent(documentId)}`,
  //       {
  //         method: "DELETE",
  //         headers: { Authorization: `Bearer ${token}` },
  //       }
  //     );
  //     if (!res.ok) throw new Error("Delete failed");

  //     await fetchFiles();
  //   } catch (err) {
  //     alert("Failed to delete file: " + err.message);
  //   } finally {
  //     setDeletingFile(null);
  //   }
  // 
  const fetchFiles = useCallback(async () => {
    setLoading(true);
    try {
      const res = await fetch(`http://localhost:8086/api/documents/user/files`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error("Failed to fetch files");
      const data = await res.json();
      console.log("Fetched files data:", data);
      setUserFiles(data);
    } catch (err) {
      console.error("Fetch files error:", err);
      setUserFiles([]);
    } finally {
      setLoading(false);
    }
  }, [token]);
  
  useEffect(() => {
    if (show && token) {
      fetchFiles();
    }
  }, [show, token, fetchFiles]);
  
  const handleDeleteFile = async (documentId, fileName) => {
    if (!window.confirm(`Delete "${fileName}"? This action cannot be undone.`)) return;
  
    setDeletingFile(documentId);
  
    try {
      const res = await fetch(
        `http://localhost:8086/api/documents/user/files?documentId=${encodeURIComponent(documentId)}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );
      if (!res.ok) throw new Error("Delete failed");
  
      await fetchFiles();
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
      <div className="modal-dialog" role="document" style={{ maxWidth: "500px" }}>
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title">Manage Uploaded Files</h5>
            <button
              type="button"
              className="btn-close"
              onClick={onClose}
              aria-label="Close"
            ></button>
          </div>
          <div className="modal-body">
            {loading ? (
              <p>Loading files...</p>
            ) : userFiles.length === 0 ? (
              <p>No uploaded files found.</p>
            ) : (
              <ul className="list-group">
                {userFiles.map(({ documentId, fileName }) => (
                  <li key={documentId} className="list-group-item d-flex justify-content-between align-items-center">
                    <span>{fileName}</span>
                    <button
                      className="btn btn-danger btn-sm"
                      onClick={() => handleDeleteFile(documentId, fileName)}
                      disabled={deletingFile === documentId}
                    >
                      {deletingFile === documentId ? "Deleting..." : "Delete"}
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
