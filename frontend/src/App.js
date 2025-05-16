import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import FileUploadPage from './pages/Fileuploadpage';
import ChatPage from './pages/ChatPage';
import SummarizationPage from './pages/Summarizationpage';
import DashboardPage from './pages/DashboardPage';

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
      </Routes>
    </Router>
  );
};

export default App;