import React from 'react';
import { Link } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';

const DashboardPage = () => {
  return (
    <div className="container mt-5">
      <div className="card shadow-lg">
        <div className="card-header bg-dark text-white text-center">
          <h2>Dashboard</h2>
        </div>
        <div className="card-body">
          <div className="row">
            <div className="col-md-4 mb-3">
              <Link to="/upload" className="btn btn-primary w-100">
                File Upload
              </Link>
            </div>
            <div className="col-md-4 mb-3">
              <Link to="/chat" className="btn btn-secondary w-100">
                Chat Page
              </Link>
            </div>
            <div className="col-md-4 mb-3">
              <Link to="/summarize" className="btn btn-success w-100">
                Summarization
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;