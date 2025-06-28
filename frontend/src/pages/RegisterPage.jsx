import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const RegisterPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [role, setRole] = useState('USER'); // Default role
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleRegister = async () => {
    if (password !== confirmPassword) {
      setError('Passwords do not match!');
      return;
    }

    try {
      console.log('Request Payload:', {
        username: email,
        password: password,
        role: role,
      });
    
      const response = await axios.post('http://localhost:8086/api/auth/register', {
        username: email,
        password: password,
        role: role,
      });
    
      console.log('Response:', response.data);
    
      // Check if response contains error property
      if (response.data.error) {
        setError(response.data.error);
        setSuccess('');
      } else {
        setSuccess('Registration successful! Redirecting to login...');
        setError('');
        setTimeout(() => navigate('/'), 2000); // Redirect after 2 seconds
      }
    } catch (err) {
      console.error('Error:', err.response || err.message);
      setError(err.response?.data || 'Registration failed!');
      setSuccess('');
    }
};  

  return (
    <div className="container register-container mt-5">
      <div className="form-container shadow-lg p-4">
        <h1 className="text-center mb-4">Register</h1>
        <form onSubmit={(e) => e.preventDefault()}>
          <div className="mb-3">
            <label>Email</label>
            <input
              type="text"
              className="form-control"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Enter your email"
              required
            />
          </div>
          <div className="mb-3">
            <label>Password</label>
            <input
              type="password"
              className="form-control"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter your password"
              required
            />
          </div>
          <div className="mb-3">
            <label>Confirm Password</label>
            <input
              type="password"
              className="form-control"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Confirm your password"
              required
            />
          </div>
          <div className="mb-3">
            <label>Role</label>
            <select
              className="form-control"
              value={role}
              onChange={(e) => setRole(e.target.value)}
              required
            >
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>
          {error && <p style={{ color: 'red' }}>{error}</p>}
          {success && <p style={{ color: 'green' }}>{success}</p>}
          <button className="btn btn-primary w-100" onClick={handleRegister}>
            Register
          </button>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage;