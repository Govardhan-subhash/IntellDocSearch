import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';


const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      // Call backend login API
      const response = await axios.post('http://localhost:8081/auth/login', {
        username: email,
        password: password,
      });

      // Backend returns JWT token string
      const token = response.data;

      // Decode token to get user info
      const decoded = jwtDecode(token);
      const userId = decoded.sub; // user id is in 'sub' claim

      // Store JWT token and userId locally
      localStorage.setItem('token', token);
      localStorage.setItem('userId', userId);

      // Redirect user after successful login
      navigate('/ragchat');
    } catch (err) {
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
            type="text"
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
