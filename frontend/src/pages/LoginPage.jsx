import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      // Make API call to the backend login endpoint
      const response = await axios.post('http://localhost:8081/auth/login', {
        username: email, // Backend expects "username"
        password: password,
      });

      // Extract the JWT token from the response
      const token = response.data;

      // Store the token in localStorage
      localStorage.setItem('jwtToken', token);

      // Navigate to the upload page
      navigate('/upload');
    } catch (err) {
      // Handle errors (e.g., invalid credentials)
      setError('Invalid email or password');
    }
  };

  const registerHandle = () => {
    navigate('/register');
  };

  return (
    <div className="container login-container">
      <div className="form-container shadow-lg">
        <h1 className="text-center">Login</h1>
        <form onSubmit={(e) => e.preventDefault()}>
          <label>Email</label>
          <input
            type="email"
            className="form-control mb-3"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Enter your email"
            required
          />
          <label>Password</label>
          <input
            type="password"
            className="form-control mb-3"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter your password"
            required
          />
          {error && <p style={{ color: 'red' }}>{error}</p>}
          <div className="d-block">
            <button className="btn btn-primary w-25" onClick={handleLogin}>
              Login
            </button>
            <button className="btn btn-secondary w-25" onClick={registerHandle}>
              Register
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;