// import React, { useState, useEffect, useRef } from "react";
// import "bootstrap/dist/css/bootstrap.min.css";
// import ReactMarkdown from "react-markdown";
// import ManageFiles from "../components/ManageFiles";
// import HistorySidebar from "../components/HistorySidebar";
// import SummarizationPage from "./Summarizationpage";

// const RAGChatPage = () => {
//   const [files, setFiles] = useState([]);
//   const [uploading, setUploading] = useState(false);
//   const [messages, setMessages] = useState([
//     {
//       text: "Hi! Upload your files and ask me anything about them.",
//       sender: "bot",
//     },
//   ]);
//   const [input, setInput] = useState("");
//   const [userId, setUserId] = useState(null);
//   const chatEndRef = useRef(null);
//   const [showFileModal, setShowFileModal] = useState(false);

//   useEffect(() => {
//     const storedUserId = localStorage.getItem("userId");
//     if (storedUserId) {
//       setUserId(storedUserId);
//     } else {
//       console.warn("No userId found in localStorage");
//     }
//   }, []);

//   useEffect(() => {
//     chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
//   }, [messages]);

//   const handleFileChange = (e) => {
//     setFiles(Array.from(e.target.files));
//   };

//   const handleUpload = async () => {
//     if (files.length === 0) return;

//     setUploading(true);

//     try {
//       const formData = new FormData();
//       files.forEach((file) => formData.append("file", file));
//       // Remove sending userId in formData

//       const token = localStorage.getItem("token");
//       //  console.log("Token:", token);
//       // Send request **without Authorization header** (API Gateway will handle token)
//       const uploadRes = await fetch("http://localhost:8086/api/documents/upload", {
//         method: "POST",
        
//         headers: {
//           Authorization: `Bearer ${token}`,
//         },
//         body: formData,
//       });
//       console.log("Upload response:", uploadRes);

//       if (!uploadRes.ok) throw new Error("Upload failed");

//       const pineconeForm = new FormData();
//       // Remove user_id here as well
//       console.log("User ID:", userId);
//       pineconeForm.append("user_id", userId); // ✅ Add this line

//       const pineconeRes = await fetch("http://localhost:8086/api/rag/load/", {
//         method: "POST",
//         headers: {
//           Authorization: `Bearer ${token}`,
//         },
//         body: pineconeForm,
//       });

//       if (!pineconeRes.ok) throw new Error("Pinecone load failed");

//       setMessages((msgs) => [
//         ...msgs,
//         {
//           text: `${files.length} file(s) uploaded and processed successfully!`,
//           sender: "bot",
//         },
//       ]);
//       setFiles([]);
//     } catch (error) {
//       console.error(error);
//       setMessages((msgs) => [
//         ...msgs,
//         { text: `Upload error: ${error.message}`, sender: "bot" },
//       ]);
//     } finally {
//       setUploading(false);
//     }
//   };

//   const handleSend = async () => {
//     if (!input.trim()) return;

//     const question = input.trim();
//     setMessages((msgs) => [...msgs, { text: question, sender: "user" }]);
//     setInput("");

//     try {
//       const formData = new FormData();
//       // Remove user_id from formData
//       formData.append("question", question);
//       formData.append("user_id",userId)
//       const token = localStorage.getItem("token");

//       const res = await fetch("http://localhost:8086/api/rag/query/", {
//         method: "POST",
//         headers: {
//           Authorization: `Bearer ${token}`,
//         },
//         body: formData,
//       });
//       console.log("Query response:", res);

//       if (!res.ok) throw new Error("Failed to get answer");

//       const data = await res.json();
//       const answer = data.answer || "No answer found.";

//       setMessages((msgs) => [...msgs, { text: answer, sender: "bot" }]);

//       // For saving chat history, **remove Authorization header**, API Gateway adds user info
//    await fetch("http://localhost:8086/api/documents/chat/history/save", {
//   method: "POST",
//   headers: {
//     "Content-Type": "application/json",
//     Authorization: `Bearer ${localStorage.getItem("token")}`,
//   },
//   body: JSON.stringify({
//     question,
//     response: answer,  // ✅ match Java DTO
//   }),
// });

//     } catch (error) {
//       console.error(error);
//       setMessages((msgs) => [
//         ...msgs,
//         { text: `Error: ${error.message}`, sender: "bot" },
//       ]);
//     }
//   };

//   return (
//     <div className="container mt-5" style={{ maxWidth: 700 }}>
//       <div className="card shadow-lg mb-4">
//         <div className="card-header bg-primary text-white text-center">
//           <h2>Upload Files (PDF, DOCX, PPTX, TXT)</h2>
//         </div>
//         <div className="card-body">
//           <input
//             type="file"
//             multiple
//             accept=".pdf,.docx,.pptx,.txt"
//             className="form-control mb-3"
//             onChange={handleFileChange}
//             disabled={uploading}
//           />
//           <button
//             className="btn btn-success w-100"
//             onClick={handleUpload}
//             disabled={files.length === 0 || uploading}
//           >
//             {uploading ? "Uploading..." : "Upload & Process Files"}
//           </button>
//           <button
//             className="btn btn-outline-danger w-100 mt-2"
//             onClick={() => setShowFileModal(true)}
//             disabled={uploading || !userId}
//             title={userId ? "Manage Uploaded Files" : "Login required to manage files"}
//           >
//             Manage Uploaded Files
//           </button>
//           {files.length > 0 && (
//             <div className="mt-3">
//               <h6>Selected Files:</h6>
//               <ul className="list-group">
//                 {files.map((file, idx) => (
//                   <li key={idx} className="list-group-item">
//                     {file.name}
//                   </li>
//                 ))}
//               </ul>
//             </div>
//           )}
//         </div>
//       </div>

//       <div className="card shadow-lg">
//         <div className="card-header bg-info text-white text-center">
//           <h2>Chat with the RAG Bot</h2>
//         </div>
//         <div
//           className="card-body"
//           style={{ height: "400px", overflowY: "auto", backgroundColor: "#f8f9fa" }}
//         >
//           {messages.map((msg, index) => (
//             <div
//               key={index}
//               className={`p-2 mb-2 rounded ${
//                 msg.sender === "user"
//                   ? "bg-primary text-white text-end"
//                   : "bg-light text-start"
//               }`}
//               style={{
//                 maxWidth: "80%",
//                 marginLeft: msg.sender === "bot" ? 0 : "auto",
//               }}
//             >
//               {msg.sender === "bot" ? (
//                 <ReactMarkdown>{msg.text}</ReactMarkdown>
//               ) : (
//                 msg.text
//               )}
//             </div>
//           ))}
//           <div ref={chatEndRef} />
//         </div>
//         <div className="card-footer">
//           <div className="input-group">
//             <input
//               type="text"
//               className="form-control"
//               placeholder="Type your question here..."
//               value={input}
//               onChange={(e) => setInput(e.target.value)}
//               onKeyDown={(e) => e.key === "Enter" && handleSend()}
//               disabled={uploading}
//             />
//             <button
//               className="btn btn-primary"
//               onClick={handleSend}
//               disabled={!input.trim() || uploading}
//             >
//               Send
//             </button>
//           </div>
//         </div>
//       </div>
//       <ManageFiles
//         userId={userId}
//         token={localStorage.getItem("token")}
//         show={showFileModal}
//         onClose={() => setShowFileModal(false)}
//       />
//       <HistorySidebar userId={userId} token={localStorage.getItem('token')} />
//       <SummarizationPage  userId={userId} token={localStorage.getItem('token')}/>
//     </div>
//   );
// };

// export default RAGChatPage;



import React, { useState, useEffect, useRef } from "react";
// We'll keep bootstrap for its utility classes but override with our own styles
import "bootstrap/dist/css/bootstrap.min.css"; 
import ReactMarkdown from "react-markdown";

// UI CHANGE: Import components from the new structure
import ManageFiles from "../components/ManageFiles";
import HistorySidebar from "../components/HistorySidebar";
import SummarizationPage from "./Summarizationpage";

// UI CHANGE: Import icons for a much better visual experience
import { FiSend, FiUpload, FiFileText, FiMessageSquare, FiArchive, FiLoader, FiX, FiUser, FiCpu } from 'react-icons/fi';


const RAGChatPage = () => {
  // --- NO CHANGES TO STATE OR LOGIC ---
  const [files, setFiles] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [messages, setMessages] = useState([
    {
      text: "Hi! I'm your AI Assistant. Upload your documents and ask me anything about them.",
      sender: "bot",
    },
  ]);
  const [input, setInput] = useState("");
  const [userId, setUserId] = useState(null);
  const chatEndRef = useRef(null);
  const fileInputRef = useRef(null); // UI CHANGE: Ref for the hidden file input
  const [showFileModal, setShowFileModal] = useState(false);
  const [isBotTyping, setIsBotTyping] = useState(false); // UI CHANGE: For bot typing indicator
  const [activeView, setActiveView] = useState('chat'); // UI CHANGE: To switch between Chat and Summarization

  useEffect(() => {
    const storedUserId = localStorage.getItem("userId");
    if (storedUserId) {
      setUserId(storedUserId);
    } else {
      console.warn("No userId found in localStorage");
    }
  }, []);

  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, isBotTyping]);

  const handleFileChange = (e) => {
    const selectedFiles = Array.from(e.target.files);
    if (selectedFiles.length > 0) {
      setFiles(selectedFiles);
      // Immediately start the upload process after selection
      handleUpload(selectedFiles);
    }
  };

  const handleUpload = async (filesToUpload) => {
    if (filesToUpload.length === 0) return;

    setUploading(true);
    setMessages(msgs => [...msgs, {
      text: `Uploading ${filesToUpload.length} file(s)...`,
      sender: 'bot',
      type: 'status'
    }]);

    try {
      const formData = new FormData();
      filesToUpload.forEach((file) => formData.append("file", file));
      const token = localStorage.getItem("token");

      const uploadRes = await fetch("http://localhost:8086/api/documents/upload", {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
        body: formData,
      });
      if (!uploadRes.ok) throw new Error("Upload failed");

      const pineconeForm = new FormData();
      pineconeForm.append("user_id", userId);

      const pineconeRes = await fetch("http://localhost:8086/api/rag/load/", {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
        body: pineconeForm,
      });

      if (!pineconeRes.ok) throw new Error("Processing failed");

      setMessages((msgs) => [
        ...msgs.filter(m => m.type !== 'status'), // remove status message
        {
          text: `${filesToUpload.length} file(s) uploaded and processed successfully! You can now ask questions about them.`,
          sender: "bot",
        },
      ]);
      setFiles([]);
    } catch (error) {
      console.error(error);
      setMessages((msgs) => [
        ...msgs.filter(m => m.type !== 'status'),
        { text: `Error: ${error.message}`, sender: "bot", type: 'error' },
      ]);
    } finally {
      setUploading(false);
      // Clear the file input value to allow re-uploading the same file
      if(fileInputRef.current) {
        fileInputRef.current.value = "";
      }
    }
  };

  const handleSend = async () => {
    if (!input.trim()) return;

    const question = input.trim();
    setMessages((msgs) => [...msgs, { text: question, sender: "user" }]);
    setInput("");
    setIsBotTyping(true);

    try {
      const formData = new FormData();
      formData.append("question", question);
      formData.append("user_id", userId);
      const token = localStorage.getItem("token");

      const res = await fetch("http://localhost:8086/api/rag/query/", {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
        body: formData,
      });

      if (!res.ok) throw new Error("Failed to get answer");

      const data = await res.json();
      const answer = data.answer || "Sorry, I couldn't find an answer.";
      
      setIsBotTyping(false);
      setMessages((msgs) => [...msgs, { text: answer, sender: "bot" }]);

      await fetch("http://localhost:8086/api/documents/chat/history/save", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
        body: JSON.stringify({
          question,
          response: answer,
        }),
      });

    } catch (error) {
      console.error(error);
      setIsBotTyping(false);
      setMessages((msgs) => [
        ...msgs,
        { text: `Error: ${error.message}`, sender: "bot", type: 'error' },
      ]);
    }
  };

  // --- UI CHANGE: The entire JSX return is new ---
  return (
    <div style={styles.appContainer}>
      {/* --- SIDEBAR --- */}
      <div style={styles.sidebar}>
        <div style={styles.sidebarHeader}>
          <FiMessageSquare size={24} />
          <h1 style={styles.sidebarTitle}>RAG AI</h1>
        </div>
        <div style={styles.sidebarMenu}>
          <button style={styles.menuButton} onClick={() => window.location.reload()}>
            <FiFileText /> New Chat
          </button>
          <button style={styles.menuButton} onClick={() => setShowFileModal(true)} disabled={!userId}>
            <FiArchive /> Manage Files
          </button>
          {/* Toggle between Chat and Summarization views */}
           <button 
             style={activeView === 'chat' ? styles.activeMenuButton : styles.menuButton}
             onClick={() => setActiveView('chat')}
           >
             <FiMessageSquare /> Chat
           </button>
           <button 
             style={activeView === 'summarization' ? styles.activeMenuButton : styles.menuButton}
             onClick={() => setActiveView('summarization')}
           >
             <FiFileText /> Summarization
           </button>
        </div>
        <div style={styles.historyContainer}>
          <h2 style={styles.historyTitle}>Chat History</h2>
          <HistorySidebar userId={userId} token={localStorage.getItem('token')} />
        </div>
      </div>

      {/* --- MAIN CONTENT AREA --- */}
      <div style={styles.mainContent}>
        {activeView === 'chat' && (
          <div style={styles.chatContainer}>
            {/* Message Display Area */}
            <div style={styles.messagesList}>
              {messages.map((msg, index) => (
                <div key={index} style={styles.messageRow(msg.sender)}>
                   <div style={styles.messageBubble(msg.sender, msg.type)}>
                     <div style={styles.avatar(msg.sender)}>
                       {msg.sender === 'bot' ? <FiCpu size={20} /> : <FiUser size={20} />}
                     </div>
                     <div style={styles.messageText}>
                       <ReactMarkdown>{msg.text}</ReactMarkdown>
                     </div>
                   </div>
                </div>
              ))}
              {isBotTyping && (
                <div style={styles.messageRow('bot')}>
                  <div style={styles.messageBubble('bot', 'typing')}>
                    <div style={styles.avatar('bot')}><FiCpu size={20}/></div>
                    <div style={styles.typingIndicator}>
                      <span></span><span></span><span></span>
                    </div>
                  </div>
                </div>
              )}
              <div ref={chatEndRef} />
            </div>

            {/* Input Area */}
            <div style={styles.chatInputWrapper}>
              {/* Hidden file input */}
              <input
                type="file"
                multiple
                accept=".pdf,.docx,.pptx,.txt"
                onChange={handleFileChange}
                ref={fileInputRef}
                style={{ display: 'none' }}
                disabled={uploading}
              />
              <button 
                style={styles.iconButton} 
                onClick={() => fileInputRef.current.click()}
                disabled={uploading}
                title="Upload Files"
              >
                {uploading ? <FiLoader className="spinner" size={22} /> : <FiUpload size={22} />}
              </button>
              <input
                type="text"
                style={styles.chatInput}
                placeholder="Type your question here, or upload a file to begin..."
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && handleSend()}
                disabled={uploading || isBotTyping}
              />
              <button 
                style={styles.sendButton} 
                onClick={handleSend}
                disabled={!input.trim() || uploading || isBotTyping}
              >
                <FiSend size={20} />
              </button>
            </div>
          </div>
        )}
        {activeView === 'summarization' && (
          <div style={styles.pageView}>
            <h2>Document Summarization</h2>
            <SummarizationPage  userId={userId} token={localStorage.getItem('token')}/>
          </div>
        )}
      </div>

      {/* Modal remains unchanged functionally, but will look better in the new layout */}
      <ManageFiles
        userId={userId}
        token={localStorage.getItem("token")}
        show={showFileModal}
        onClose={() => setShowFileModal(false)}
      />
    </div>
  );
};

// --- UI CHANGE: All new styles object for a clean, modern look ---
const styles = {
  appContainer: {
    display: 'flex',
    height: '100vh',
    backgroundColor: '#f4f7f9',
    fontFamily: "'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif",
  },
  sidebar: {
    width: '260px',
    backgroundColor: '#202123',
    color: '#fff',
    display: 'flex',
    flexDirection: 'column',
    padding: '20px',
    borderRight: '1px solid #333',
  },
  sidebarHeader: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    paddingBottom: '20px',
    borderBottom: '1px solid #444',
  },
  sidebarTitle: {
    fontSize: '24px',
    margin: 0,
  },
  sidebarMenu: {
    marginTop: '20px',
    display: 'flex',
    flexDirection: 'column',
    gap: '10px',
  },
  menuButton: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    background: 'transparent',
    border: '1px solid #555',
    color: '#eee',
    padding: '10px 15px',
    borderRadius: '8px',
    textAlign: 'left',
    cursor: 'pointer',
    fontSize: '16px',
    transition: 'background-color 0.2s',
  },
  activeMenuButton: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    background: '#343541',
    border: '1px solid #555',
    color: '#fff',
    padding: '10px 15px',
    borderRadius: '8px',
    textAlign: 'left',
    cursor: 'pointer',
    fontSize: '16px',
    transition: 'background-color 0.2s',
  },
  historyContainer: {
    flex: 1,
    overflowY: 'auto',
    marginTop: '20px',
    paddingTop: '10px',
    borderTop: '1px solid #444',
  },
  historyTitle: {
    fontSize: '14px',
    textTransform: 'uppercase',
    color: '#999',
    marginBottom: '10px',
  },
  mainContent: {
    flex: 1,
    display: 'flex',
    flexDirection: 'column',
  },
  chatContainer: {
    display: 'flex',
    flexDirection: 'column',
    height: '100%',
  },
  pageView: {
    padding: '40px',
    overflowY: 'auto',
  },
  messagesList: {
    flex: 1,
    padding: '20px 40px',
    overflowY: 'auto',
    display: 'flex',
    flexDirection: 'column',
    gap: '20px',
  },
  messageRow: (sender) => ({
    display: 'flex',
    justifyContent: sender === 'user' ? 'flex-end' : 'flex-start',
  }),
  messageBubble: (sender, type) => ({
    display: 'flex',
    gap: '15px',
    maxWidth: '75%',
    padding: '15px 20px',
    borderRadius: '20px',
    backgroundColor: sender === 'user' ? '#007bff' : '#ffffff',
    color: sender === 'user' ? '#fff' : '#333',
    boxShadow: '0 2px 5px rgba(0,0,0,0.05)',
    border: '1px solid #e0e0e0',
    ...(sender === 'user' && { border: 'none' }),
    ...(type === 'error' && { backgroundColor: '#ffebee', color: '#c62828', border: '1px solid #c62828' }),
  }),
  avatar: (sender) => ({
    width: '40px',
    height: '40px',
    borderRadius: '50%',
    backgroundColor: sender === 'user' ? 'rgba(255,255,255,0.2)' : '#f1f1f1',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    flexShrink: 0,
    color: sender === 'user' ? '#fff' : '#007bff',
  }),
  messageText: {
    paddingTop: '3px',
    lineHeight: 1.6,
  },
  chatInputWrapper: {
    padding: '20px 40px',
    backgroundColor: '#ffffff',
    borderTop: '1px solid #ddd',
    display: 'flex',
    alignItems: 'center',
    gap: '15px',
  },
  iconButton: {
    background: 'transparent',
    border: 'none',
    color: '#555',
    cursor: 'pointer',
    padding: '10px',
    borderRadius: '50%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    transition: 'background-color 0.2s'
  },
  chatInput: {
    flex: 1,
    border: 'none',
    outline: 'none',
    fontSize: '16px',
    backgroundColor: 'transparent',
    padding: '10px 0',
  },
  sendButton: {
    background: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '50%',
    width: '44px',
    height: '44px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    cursor: 'pointer',
    transition: 'background-color 0.2s',
  },
  typingIndicator: {
    display: 'flex',
    alignItems: 'center',
    padding: '10px',
  },
};
// You might want to add this CSS to your global stylesheet for animations
/*
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
.spinner {
  animation: spin 1s linear infinite;
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1.0); }
}
.typing-indicator span {
  width: 8px;
  height: 8px;
  margin: 0 2px;
  background-color: #999;
  border-radius: 50%;
  display: inline-block;
  animation: bounce 1.4s infinite ease-in-out both;
}
.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }
*/
// I included a simple inline style for the spinner, but the above is better.
export default RAGChatPage;