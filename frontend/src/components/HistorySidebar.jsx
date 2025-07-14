// // import React, { useEffect, useState } from 'react';
// // import { Modal, Button } from 'react-bootstrap';
// import 'bootstrap/dist/css/bootstrap.min.css';

// // const HistorySidebar = ({ token }) => {
// //   const [history, setHistory] = useState([]);
// //   const [selectedChat, setSelectedChat] = useState(null);
// //   const [showModal, setShowModal] = useState(false);
// //   const [loading, setLoading] = useState(false);
// //   const [error, setError] = useState(null);

// //   useEffect(() => {
// //     const fetchHistory = async () => {
// //       setLoading(true);
// //       setError(null);
// //       try {

// //         const response = await fetch('http://localhost:8082/documents/chat/history', {
// //           headers: { Authorization: `Bearer ${token}` },
// //         });

// //         if (!response.ok) {
// //           throw new Error(`Failed to fetch chat history. Status: ${response.status}`);
// //         }

// //         const text = await response.text();
// //         if (!text) {
// //           setHistory([]);
// //         } else {
// //           const data = JSON.parse(text);
// //           setHistory(data);
// //         }
// //       } catch (err) {
// //         console.error('Error fetching chat history:', err);
// //         setError('Failed to load chat history.');
// //         setHistory([]);
// //       } finally {
// //         setLoading(false);
// //       }
// //     };

// //     if (token) {
// //       fetchHistory();
// //     }
// //   }, [token]);

// //   const handleChatClick = (chat) => {
// //     setSelectedChat(chat);
// //     setShowModal(true);
// //   };

// //   return (
// //     <div className="history-sidebar p-3 border" style={{ maxWidth: '350px' }}>
// //       <h5>Chat History</h5>

// //       {loading && <p>Loading chat history...</p>}
// //       {error && <p className="text-danger">{error}</p>}

// //       {!loading && !error && history.length === 0 && <p>No chat history found.</p>}

// //       {!loading && !error && history.length > 0 && (
// //         <ul className="list-group">
// //           {history.map((chat) => (
// //             <li
// //               key={chat.id}
// //               className="list-group-item list-group-item-action"
// //               onClick={() => handleChatClick(chat)}
// //               style={{ cursor: 'pointer' }}
// //             >
// //               {chat.question}
// //             </li>
// //           ))}
// //         </ul>
// //       )}

// //       <Modal show={showModal} onHide={() => setShowModal(false)} centered>
// //         <Modal.Header closeButton>
// //           <Modal.Title>Chat Response</Modal.Title>
// //         </Modal.Header>
// //         <Modal.Body>
// //           <p><strong>Question:</strong> {selectedChat?.question}</p>
// //           <p><strong>Answer:</strong> {selectedChat?.response}</p>
// //         </Modal.Body>
// //         <Modal.Footer>
// //           <Button variant="secondary" onClick={() => setShowModal(false)}>
// //             Close
// //           </Button>
// //         </Modal.Footer>
// //       </Modal>
// //     </div>
// //   );
// // };

// // export default HistorySidebar;
// import React, { useEffect, useState } from "react";
// import { Modal, Button } from "react-bootstrap";

// const HistorySidebar = ({ userId, token }) => {
//   const [history, setHistory] = useState([]);
//   const [showModal, setShowModal] = useState(false);
//   const [selectedChat, setSelectedChat] = useState(null);

//   useEffect(() => {
//     if (!userId || !token) return;
//     console.log(token);
//     const fetchHistory = async () => {
//       try {
//         const res = await fetch(`http://localhost:8086/api/documents/chat/history`, {
//           method: "GET",
//           headers: {
//             Authorization: `Bearer ${token}`,
//           },
//         });

//         if (!res.ok) throw new Error("Failed to fetch chat history");

//         const data = await res.json();
//         console.log(data);
//         setHistory(data); // Ensure this contains both question and answer
//       } catch (error) {
//         console.error("Error fetching chat history:", error);
//       }
//     };

//     fetchHistory();
//   }, [userId, token]);

//   const handleClick = (chat) => {
//     setSelectedChat(chat);
//     setShowModal(true);
//   };

//   const handleClose = () => {
//     setShowModal(false);
//     setSelectedChat(null);
//   };

//   return (
//     <div className="position-fixed top-0 end-0 p-3" style={{ width: "300px", zIndex: 999 }}>
//       <div className="bg-light border rounded shadow-sm" style={{ maxHeight: "90vh", overflowY: "auto" }}>
//         <h5 className="bg-dark text-white text-center p-2 m-0">Chat History</h5>
//         {history.length === 0 ? (
//           <p className="text-center m-3">No history found</p>
//         ) : (
//           <ul className="list-group list-group-flush">
//             {history.map((chat, index) => (
//               <li
//                 key={index}
//                 className="list-group-item list-group-item-action"
//                 onClick={() => handleClick(chat)}
//                 style={{ cursor: "pointer" }}
//               >
//                 {chat.question}
//               </li>
//             ))}
//           </ul>
//         )}
//       </div>

//       {/* Modal for showing Q&A */}
//       <Modal show={showModal} onHide={handleClose}>
//         <Modal.Header closeButton>
//           <Modal.Title>Chat Detail</Modal.Title>
//         </Modal.Header>
//         <Modal.Body>
//           {selectedChat && (
//             <>
//               <p><strong>Question:</strong> {selectedChat.question}</p>
//               <p><strong>Answer:</strong> {selectedChat.answer || "No answer available"}</p>
//             </>
//           )}
//         </Modal.Body>
//         <Modal.Footer>
//           <Button variant="secondary" onClick={handleClose}>Close</Button>
//         </Modal.Footer>
//       </Modal>
//     </div>
//   );
// };

// export default HistorySidebar;



import React, { useEffect, useState } from "react";
// UI CHANGE: We no longer need react-bootstrap. We'll use an icon for the close button.
import { FiMessageSquare, FiX } from 'react-icons/fi';

const HistorySidebar = ({ userId, token }) => {
  // --- NO CHANGES TO STATE OR LOGIC ---
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true); // UI CHANGE: Added a loading state for better UX
  const [showModal, setShowModal] = useState(false);
  const [selectedChat, setSelectedChat] = useState(null);

  useEffect(() => {
    if (!userId || !token) {
        setLoading(false);
        return;
    };

    const fetchHistory = async () => {
      setLoading(true);
      try {
        // Using your provided endpoint
        const res = await fetch(`http://localhost:8086/api/documents/chat/history`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!res.ok) throw new Error("Failed to fetch chat history");

        const data = await res.json();
        // UI CHANGE: Reverse the history to show the most recent chats at the top
        setHistory(data.reverse()); 
      } catch (error) {
        console.error("Error fetching chat history:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, [userId, token]);

  // --- NO CHANGES TO EVENT HANDLERS ---
  const handleClick = (chat) => {
    setSelectedChat(chat);
    setShowModal(true);
  };

  const handleClose = () => {
    setShowModal(false);
    setSelectedChat(null);
  };

  // --- UI CHANGE: The entire JSX is new and styled for the sidebar and a custom modal ---
  return (
    <>
      {/* This is the list that will appear IN the sidebar */}
      <div style={styles.container}>
        {loading && <div style={styles.statusText}>Loading...</div>}
        {!loading && history.length === 0 && (
          <div style={styles.statusText}>No history found</div>
        )}
        {history.map((chat, index) => (
          <button
            key={index}
            style={styles.historyItem}
            onClick={() => handleClick(chat)}
            title={chat.question}
          >
            <FiMessageSquare size={16} style={{ flexShrink: 0 }} />
            <span style={styles.historyText}>{chat.question}</span>
          </button>
        ))}
      </div>

      {/* This is our new, custom modal that appears on top of everything */}
      {showModal && selectedChat && (
        <div style={styles.modalBackdrop} onClick={handleClose}>
          <div style={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <div style={styles.modalHeader}>
              <h3 style={styles.modalTitle}>Chat Detail</h3>
              <button style={styles.closeButton} onClick={handleClose}>
                <FiX size={24} />
              </button>
            </div>
            <div style={styles.modalBody}>
              <div style={styles.qaSection}>
                <strong>Question:</strong>
                <p>{selectedChat.question}</p>
              </div>
              <div style={styles.qaSection}>
                <strong>Answer:</strong>
                {/* We can use a pre-wrap to respect newlines in the bot's answer */}
                <p style={{ whiteSpace: 'pre-wrap' }}>{selectedChat.response || "No answer available"}</p>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

// --- UI CHANGE: All new styles object for a consistent look ---
const styles = {
  // Styles for the history list in the sidebar
  container: {
    display: 'flex',
    flexDirection: 'column',
    gap: '8px',
    width: '100%',
  },
  statusText: {
    color: '#999',
    padding: '10px 0',
    fontSize: '14px',
  },
  historyItem: {
    display: 'flex',
    alignItems: 'center',
    gap: '10px',
    padding: '8px 10px',
    borderRadius: '6px',
    width: '100%',
    textAlign: 'left',
    backgroundColor: 'transparent',
    border: 'none',
    color: '#ccc',
    cursor: 'pointer',
    transition: 'background-color 0.2s ease',
  },
  historyText: {
    whiteSpace: 'nowrap',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    fontSize: '14px',
  },

  // Styles for our new custom modal
  modalBackdrop: {
    position: 'fixed',
    top: 0,
    left: 0,
    width: '100vw',
    height: '100vh',
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1000,
  },
  modalContent: {
    width: '90%',
    maxWidth: '600px',
    backgroundColor: '#2d2f3a', // A slightly lighter dark shade
    color: '#fff',
    borderRadius: '12px',
    padding: '24px',
    boxShadow: '0 10px 30px rgba(0, 0, 0, 0.5)',
    display: 'flex',
    flexDirection: 'column',
    maxHeight: '80vh',
  },
  modalHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderBottom: '1px solid #444',
    paddingBottom: '16px',
    marginBottom: '16px',
  },
  modalTitle: {
    margin: 0,
    fontSize: '20px',
  },
  closeButton: {
    background: 'none',
    border: 'none',
    color: '#aaa',
    cursor: 'pointer',
    padding: '5px',
    borderRadius: '50%',
    display: 'flex',
    transition: 'background-color 0.2s, color 0.2s'
  },
  modalBody: {
    overflowY: 'auto',
  },
  qaSection: {
    marginBottom: '20px',
  },
};

export default HistorySidebar;