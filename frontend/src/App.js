import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import FileUploadPage from './pages/Fileuploadpage';
import ChatPage from './pages/ChatPage';
import SummarizationPage from './pages/Summarizationpage';
import DashboardPage from './pages/DashboardPage';
import RAGChatPage from './pages/RagChatPage';

const App = () => {
  return (
    <Router>
      <Routes>
        
        <Route path="/" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/upload" element={<FileUploadPage />} />
        <Route path="/chat" element={<ChatPage />} />
        <Route path="/summarize" element={<SummarizationPage />} />
        <Route path="/dashboard" element={<DashboardPage/>} />
        <Route path="/ragchat" element={<RAGChatPage />} />
        <Route path="*" element={<h1>404 Not Found</h1>} />
      </Routes>
    </Router>
  );
};

export default App;