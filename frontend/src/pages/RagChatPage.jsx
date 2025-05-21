import React, { useState, useEffect, useRef } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import ManageFiles from "../components/ManageFiles"; // import the new component


const RAGChatPage = () => {
  const [files, setFiles] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [messages, setMessages] = useState([
    {
      text: "Hi! Upload your files and ask me anything about them.",
      sender: "bot",
    },
  ]);
  const [input, setInput] = useState("");
  const [userId, setUserId] = useState(null);
  const chatEndRef = useRef(null);
  const [showFileModal, setShowFileModal] = useState(false);
  

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
  }, [messages]);

  const handleFileChange = (e) => {
    setFiles(Array.from(e.target.files));
  };

  const handleUpload = async () => {
    if (!userId) {
      alert("User ID is missing. Please log in.");
      return;
    }
    
    const token = localStorage.getItem("token");
    if (!token) {
      alert("JWT token is missing. Please log in.");
      return;
    }

    if (files.length === 0) return;

    setUploading(true);

    try {
      const formData = new FormData();
      files.forEach((file) => formData.append("file", file));
      formData.append("userId", userId);
      console.log(userId);

      const uploadRes = await fetch("http://localhost:8082/documents/upload", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      });

      if (!uploadRes.ok) throw new Error("Upload failed");
      const pineconeForm = new FormData();
      pineconeForm.append("user_id", userId); // MUST match FastAPI param
      const pineconeRes = await fetch("http://127.0.0.1:8083/load/", {
        method: "POST",
        body: pineconeForm,
      });
           
      console.log("Pinecone response:", pineconeRes);
      if (!pineconeRes.ok) throw new Error("Pinecone load failed");

      setMessages((msgs) => [
        ...msgs,
        {
          text: `${files.length} file(s) uploaded and processed successfully!`,
          sender: "bot",
        },
      ]);
      setFiles([]);
    } catch (error) {
      console.error(error);
      setMessages((msgs) => [
        ...msgs,
        { text: `Upload error: ${error.message}`, sender: "bot" },
      ]);
    } finally {
      setUploading(false);
    }
  };

  const handleSend = async () => {
    if (!userId) {
      alert("User ID is missing. Please log in.");
      return;
    }
  
    if (!input.trim()) return;
  
    const question = input.trim();
  
    setMessages((msgs) => [...msgs, { text: question, sender: "user" }]);
    setInput("");
  
    try {
      const formData = new FormData();
      formData.append("user_id", userId);
      formData.append("question", question);
  
      const res = await fetch("http://localhost:8083/query/", {
        method: "POST",
        body: formData, // form data sent
      });
  
      if (!res.ok) throw new Error("Failed to get answer");
  
      const data = await res.json();
      setMessages((msgs) => [
        ...msgs,
        { text: data.answer || "No answer found.", sender: "bot" },
      ]);
    } catch (error) {
      console.error(error);
      setMessages((msgs) => [
        ...msgs,
        { text: `Error: ${error.message}`, sender: "bot" },
      ]);
    }
  };
  

  return (
    <div className="container mt-5" style={{ maxWidth: 700 }}>
      {/* Upload Panel */}
      <div className="card shadow-lg mb-4">
        <div className="card-header bg-primary text-white text-center">
          <h2>Upload Files (PDF, DOCX, PPTX, TXT)</h2>
        </div>
        <div className="card-body">
          <input
            type="file"
            multiple
            accept=".pdf,.docx,.pptx,.txt"
            className="form-control mb-3"
            onChange={handleFileChange}
            disabled={uploading}
          />
          <button
            className="btn btn-success w-100"
            onClick={handleUpload}
            disabled={files.length === 0 || uploading}
          >
            {uploading ? "Uploading..." : "Upload & Process Files"}
          </button>
    {/* NEW: Button to open ManageFiles modal */}
    <button
            className="btn btn-outline-danger w-100"
            onClick={() => setShowFileModal(true)}
            disabled={uploading || !userId}
            title={userId ? "Manage Uploaded Files" : "Login required to manage files"}
          >
            Manage Uploaded Files
          </button>
          {files.length > 0 && (
            <div className="mt-3">
              <h6>Selected Files:</h6>
              <ul className="list-group">
                {files.map((file, idx) => (
                  <li key={idx} className="list-group-item">
                    {file.name}
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      </div>

      {/* Chat Panel */}
      <div className="card shadow-lg">
        <div className="card-header bg-info text-white text-center">
          <h2>Chat with the RAG Bot</h2>
        </div>
        <div
          className="card-body"
          style={{ height: "400px", overflowY: "auto", backgroundColor: "#f8f9fa" }}
        >
          {messages.map((msg, index) => (
            <div
              key={index}
              className={`p-2 mb-2 rounded ${
                msg.sender === "user"
                  ? "bg-primary text-white text-end"
                  : "bg-light text-start"
              }`}
              style={{
                maxWidth: "80%",
                marginLeft: msg.sender === "bot" ? 0 : "auto",
              }}
            >
              {msg.text}
            </div>
          ))}
          <div ref={chatEndRef} />
        </div>
        <div className="card-footer">
          <div className="input-group">
            <input
              type="text"
              className="form-control"
              placeholder="Type your question here..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleSend()}
              disabled={uploading}
            />
            <button
              className="btn btn-primary"
              onClick={handleSend}
              disabled={!input.trim() || uploading}
            >
              Send
            </button>
          </div>
        </div>
      </div>
      <ManageFiles
        userId={userId}
        token={localStorage.getItem("token")}
        show={showFileModal}
        onClose={() => setShowFileModal(false)}
      />
    </div>
  );
};

export default RAGChatPage;
