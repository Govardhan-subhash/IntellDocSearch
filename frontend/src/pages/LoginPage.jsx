import React, { useState } from 'react';
import '../styles/LoginRegister.css';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = () => {
    console.log('Login:', { email, password });
  };

  return (
    <div className="container login-container">
      <div className="form-container">
        <h1>Login</h1>
        <form onSubmit={(e) => e.preventDefault()}>
          <label>Email</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Enter your email"
            required
          />
          <label>Password</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter your password"
            required
          />
          <button onClick={handleLogin}>Login</button>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;