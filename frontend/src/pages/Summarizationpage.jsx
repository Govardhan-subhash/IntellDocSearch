import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';

const SummarizationPage = () => {
  const [summary, setSummary] = useState('');

  const handleSummarize = () => {
    setSummary('This is a beautifully formatted summary of your document.');
  };

  return (
    <div className="container mt-5">
      <div className="card shadow-lg">
        <div className="card-header bg-success text-white text-center">
          <h2>Document Summarization</h2>
        </div>
        <div className="card-body">
          <button className="btn btn-primary w-100 mb-3" onClick={handleSummarize}>
            Summarize
          </button>
          {summary && (
            <div className="alert alert-info">
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