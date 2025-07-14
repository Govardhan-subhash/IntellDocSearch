// import React, { useState, useEffect } from "react";
// import "bootstrap/dist/css/bootstrap.min.css";

// const SummarizationPage = () => {
//   const [files, setFiles] = useState([]);
//   const [selectedFile, setSelectedFile] = useState("");
//   const [summary, setSummary] = useState("");
//   const [loading, setLoading] = useState(false);

//   const token = localStorage.getItem("token"); // assumes JWT is stored in localStorage

//   useEffect(() => {
//     if (!token) return;

//     const fetchUserFiles = async () => {
//       try {
//         const response = await fetch("http://localhost:8086/api/documents/user/files", {
//           headers: {
//             Authorization: `Bearer ${token}`,
//           },
//         });

//         if (!response.ok) throw new Error("Failed to fetch files.");

//         const data = await response.json();
//         setFiles(data);
//         if (data.length > 0) setSelectedFile(data[0]);
//       } catch (error) {
//         console.error("Error fetching files:", error);
//         setFiles([]);
//       }
//     };

//     fetchUserFiles();
//   }, [token]);

//   const handleSummarize = async () => {
//     if (!selectedFile) {
//       alert("Please select a document.");
//       return;
//     }
  
//     setLoading(true);
//     setSummary("");
  
//     try {
//       // Fetch chunks for selected file from Spring Boot backend
//       const chunksResponse = await fetch(
//         `http://localhost:8086/api/documents/user/files/${encodeURIComponent(selectedFile)}/chunks`,
//         {
//           headers: {
//             Authorization: `Bearer ${token}`,
//           },
//         }
//       );
  
//       if (!chunksResponse.ok) throw new Error("Failed to fetch document chunks.");
//       const chunksData = await chunksResponse.json();
  
//       // Combine chunk contents or send as array
//       // Option 1: Combine all chunk content into one string
//       const combinedContent = chunksData.map(c => c.content).join("\n\n");
  
//       // Step 2: Send combined content to Python summarization API
//       const summaryResponse = await fetch("http://localhost:8086/api/summarize/raw", {
//         method: "POST",
//         headers: { "Content-Type": "application/json" }, // NO Authorization header needed here
//         body: JSON.stringify({ content: combinedContent }),
//       });
      
      
  
//       if (!summaryResponse.ok) throw new Error("Summarization failed.");
//       const data = await summaryResponse.json();
//       setSummary(data.summary || "No summary returned.");
  
//     } catch (error) {
//       console.error("Error during summarization:", error);
//       setSummary("Error occurred while summarizing the document.");
//     }
  
//     setLoading(false);
//   };
  
//   const handleCopy = () => {
//     navigator.clipboard.writeText(summary);
//     alert("Summary copied to clipboard!");
//   };

//   return (
//     <div className="container mt-5" style={{ maxWidth: "600px" }}>
//       <div className="card shadow-lg border-success">
//         <div className="card-header bg-success text-white text-center">
//           <h2>Document Summarization</h2>
//         </div>
//         <div className="card-body">
//           <div className="mb-3">
//             <label htmlFor="fileSelect" className="form-label">
//               Select a document:
//             </label>
//             <select
//               id="fileSelect"
//               className="form-select"
//               value={selectedFile}
//               onChange={(e) => setSelectedFile(e.target.value)}
//               disabled={loading || files.length === 0}
//             >
//               {files.map((fileName) => (
//                 <option key={fileName} value={fileName}>
//                   {fileName}
//                 </option>
//               ))}
//             </select>
//           </div>

//           <button
//             className="btn btn-primary w-100 mb-3"
//             onClick={handleSummarize}
//             disabled={loading || files.length === 0}
//           >
//             {loading ? (
//               <>
//                 <span
//                   className="spinner-border spinner-border-sm me-2"
//                   role="status"
//                   aria-hidden="true"
//                 ></span>
//                 Summarizing...
//               </>
//             ) : (
//               "Summarize"
//             )}
//           </button>

//           {summary && (
//             <div
//               className="alert alert-info position-relative"
//               style={{ whiteSpace: "pre-wrap", maxHeight: "300px", overflowY: "auto" }}
//             >
//               <button
//                 type="button"
//                 className="btn btn-sm btn-outline-secondary position-absolute top-0 end-0 m-2"
//                 onClick={handleCopy}
//               >
//                 Copy
//               </button>
//               <h5>Summary:</h5>
//               <p>{summary}</p>
//             </div>
//           )}
//         </div>
//       </div>
//     </div>
//   );
// };

// export default SummarizationPage;


import React, { useState, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";

const SummarizationPage = () => {
  const [files, setFiles] = useState([]);
  const [selectedFile, setSelectedFile] = useState("");
  const [summary, setSummary] = useState("");
  const [loading, setLoading] = useState(false);

  const token = localStorage.getItem("token"); // assumes JWT is stored in localStorage

  useEffect(() => {
    if (!token) return;

    const fetchUserFiles = async () => {
      try {
        const response = await fetch("http://localhost:8086/api/documents/user/files", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) throw new Error("Failed to fetch files.");

        const data = await response.json();
        setFiles(data);
        if (data.length > 0) setSelectedFile(data[0].fileName); // 🔧 store fileName, not object
      } catch (error) {
        console.error("Error fetching files:", error);
        setFiles([]);
      }
    };

    fetchUserFiles();
  }, [token]);

  const handleSummarize = async () => {
    if (!selectedFile) {
      alert("Please select a document.");
      return;
    }

    setLoading(true);
    setSummary("");

    try {
      // Step 1: Fetch chunks for selected file from Spring Boot backend
      const chunksResponse = await fetch(
        `http://localhost:8086/api/documents/user/files/${encodeURIComponent(selectedFile)}/chunks`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!chunksResponse.ok) throw new Error("Failed to fetch document chunks.");
      const chunksData = await chunksResponse.json();

      // Step 2: Combine content and send to Python summarization API
      const combinedContent = chunksData.map((c) => c.content).join("\n\n");

      const summaryResponse = await fetch("http://localhost:8086/api/summarize/raw", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,

        },
        body: JSON.stringify({ content: combinedContent }),
      });

      if (!summaryResponse.ok) throw new Error("Summarization failed.");
      const data = await summaryResponse.json();
      setSummary(data.summary || "No summary returned.");
    } catch (error) {
      console.error("Error during summarization:", error);
      setSummary("Error occurred while summarizing the document.");
    }

    setLoading(false);
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(summary);
    alert("Summary copied to clipboard!");
  };

  return (
    <div className="container mt-5" style={{ maxWidth: "600px" }}>
      <div className="card shadow-lg border-success">
        <div className="card-header bg-success text-white text-center">
          <h2>Document Summarization</h2>
        </div>
        <div className="card-body">
          <div className="mb-3">
            <label htmlFor="fileSelect" className="form-label">
              Select a document:
            </label>
            <select
              id="fileSelect"
              className="form-select"
              value={selectedFile}
              onChange={(e) => setSelectedFile(e.target.value)}
              disabled={loading || files.length === 0}
            >
              {files.map((file) => (
                <option key={file.documentId} value={file.fileName}>
                  {file.fileName}
                </option>
              ))}
            </select>
          </div>

          <button
            className="btn btn-primary w-100 mb-3"
            onClick={handleSummarize}
            disabled={loading || files.length === 0}
          >
            {loading ? (
              <>
                <span
                  className="spinner-border spinner-border-sm me-2"
                  role="status"
                  aria-hidden="true"
                ></span>
                Summarizing...
              </>
            ) : (
              "Summarize"
            )}
          </button>

          {summary && (
            <div
              className="alert alert-info position-relative"
              style={{ whiteSpace: "pre-wrap", maxHeight: "300px", overflowY: "auto" }}
            >
              <button
                type="button"
                className="btn btn-sm btn-outline-secondary position-absolute top-0 end-0 m-2"
                onClick={handleCopy}
              >
                Copy
              </button>
              <h5>Summary:</h5>
              <p>{summary}</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default SummarizationPage;
