import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';


const FileUploadPage = () => {
  const [files, setFiles] = useState([]);

  const handleFileChange = (e) => {
    setFiles([...e.target.files]);
  };

  const handleUpload = () => {
    alert(`${files.length} file(s) uploaded successfully!`);
  };

  return (
    <div className="container file-upload-page mt-5">
      <div className="card shadow-lg">
        <div className="card-header bg-primary text-white text-center">
          <h2>Upload Your Files</h2>
        </div>
        <div className="card-body">
          <div className="mb-3">
            <input
              type="file"
              className="form-control"
              multiple
              onChange={handleFileChange}
            />
          </div>
          <button
            className="btn btn-success w-100"
            onClick={handleUpload}
            disabled={files.length === 0}
          >
            Upload Files
          </button>
        </div>
        {files.length > 0 && (
          <div className="card-footer">
            <h5>Selected Files:</h5>
            <ul className="list-group">
              {files.map((file, index) => (
                <li key={index} className="list-group-item">
                  {file.name}
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
};

export default FileUploadPage;