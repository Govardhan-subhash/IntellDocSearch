// import React, { useEffect, useState } from 'react';
// import { Modal, Button } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';

// const HistorySidebar = ({ token }) => {
//   const [history, setHistory] = useState([]);
//   const [selectedChat, setSelectedChat] = useState(null);
//   const [showModal, setShowModal] = useState(false);
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState(null);

//   useEffect(() => {
//     const fetchHistory = async () => {
//       setLoading(true);
//       setError(null);
//       try {

//         const response = await fetch('http://localhost:8082/documents/chat/history', {
//           headers: { Authorization: `Bearer ${token}` },
//         });

//         if (!response.ok) {
//           throw new Error(`Failed to fetch chat history. Status: ${response.status}`);
//         }

//         const text = await response.text();
//         if (!text) {
//           setHistory([]);
//         } else {
//           const data = JSON.parse(text);
//           setHistory(data);
//         }
//       } catch (err) {
//         console.error('Error fetching chat history:', err);
//         setError('Failed to load chat history.');
//         setHistory([]);
//       } finally {
//         setLoading(false);
//       }
//     };

//     if (token) {
//       fetchHistory();
//     }
//   }, [token]);

//   const handleChatClick = (chat) => {
//     setSelectedChat(chat);
//     setShowModal(true);
//   };

//   return (
//     <div className="history-sidebar p-3 border" style={{ maxWidth: '350px' }}>
//       <h5>Chat History</h5>

//       {loading && <p>Loading chat history...</p>}
//       {error && <p className="text-danger">{error}</p>}

//       {!loading && !error && history.length === 0 && <p>No chat history found.</p>}

//       {!loading && !error && history.length > 0 && (
//         <ul className="list-group">
//           {history.map((chat) => (
//             <li
//               key={chat.id}
//               className="list-group-item list-group-item-action"
//               onClick={() => handleChatClick(chat)}
//               style={{ cursor: 'pointer' }}
//             >
//               {chat.question}
//             </li>
//           ))}
//         </ul>
//       )}

//       <Modal show={showModal} onHide={() => setShowModal(false)} centered>
//         <Modal.Header closeButton>
//           <Modal.Title>Chat Response</Modal.Title>
//         </Modal.Header>
//         <Modal.Body>
//           <p><strong>Question:</strong> {selectedChat?.question}</p>
//           <p><strong>Answer:</strong> {selectedChat?.response}</p>
//         </Modal.Body>
//         <Modal.Footer>
//           <Button variant="secondary" onClick={() => setShowModal(false)}>
//             Close
//           </Button>
//         </Modal.Footer>
//       </Modal>
//     </div>
//   );
// };

// export default HistorySidebar;
import React, { useEffect, useState } from "react";
import { Modal, Button } from "react-bootstrap";

const HistorySidebar = ({ userId, token }) => {
  const [history, setHistory] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [selectedChat, setSelectedChat] = useState(null);

  useEffect(() => {
    if (!userId || !token) return;

    const fetchHistory = async () => {
      try {
        const res = await fetch(`http://localhost:8086/api/documents/chat/history`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!res.ok) throw new Error("Failed to fetch chat history");

        const data = await res.json();
        console.log(data);
        setHistory(data); // Ensure this contains both question and answer
      } catch (error) {
        console.error("Error fetching chat history:", error);
      }
    };

    fetchHistory();
  }, [userId, token]);

  const handleClick = (chat) => {
    setSelectedChat(chat);
    setShowModal(true);
  };

  const handleClose = () => {
    setShowModal(false);
    setSelectedChat(null);
  };

  return (
    <div className="position-fixed top-0 end-0 p-3" style={{ width: "300px", zIndex: 999 }}>
      <div className="bg-light border rounded shadow-sm" style={{ maxHeight: "90vh", overflowY: "auto" }}>
        <h5 className="bg-dark text-white text-center p-2 m-0">Chat History</h5>
        {history.length === 0 ? (
          <p className="text-center m-3">No history found</p>
        ) : (
          <ul className="list-group list-group-flush">
            {history.map((chat, index) => (
              <li
                key={index}
                className="list-group-item list-group-item-action"
                onClick={() => handleClick(chat)}
                style={{ cursor: "pointer" }}
              >
                {chat.question}
              </li>
            ))}
          </ul>
        )}
      </div>

      {/* Modal for showing Q&A */}
      <Modal show={showModal} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>Chat Detail</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {selectedChat && (
            <>
              <p><strong>Question:</strong> {selectedChat.question}</p>
              <p><strong>Answer:</strong> {selectedChat.answer || "No answer available"}</p>
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleClose}>Close</Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default HistorySidebar;

