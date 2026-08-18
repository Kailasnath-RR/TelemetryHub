import React, { useState } from 'react';
import { login, register } from '../api/auth';
import { ApiError } from '../api/client';

export function Login({ onLoginSuccess }) {
  const [mode, setMode] = useState('login'); // 'login' or 'register'
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [successMsg, setSuccessMsg] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccessMsg(null);

    if (!username.trim() || !password.trim()) {
      setError('Username and password are required.');
      return;
    }

    if (mode === 'register' && password.length < 6) {
      setError('Password must be at least 6 characters long.');
      return;
    }

    setLoading(true);
    try {
      if (mode === 'register') {
        await register(username, password);
        setSuccessMsg('Registration successful! You can now log in.');
        setMode('login');
        setPassword('');
      } else {
        const response = await login(username, password);
        onLoginSuccess(response);
      }
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.message || `${mode === 'register' ? 'Registration' : 'Login'} failed.`);
      } else {
        setError('Unable to connect to the authentication server.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <h1>TelemetryHub</h1>
          <p>{mode === 'login' ? 'Sign in to access machine telemetry' : 'Create a new TelemetryHub account'}</p>
        </div>

        <div className="auth-tab-group">
          <button
            type="button"
            className={`auth-tab ${mode === 'login' ? 'active' : ''}`}
            onClick={() => {
              setMode('login');
              setError(null);
              setSuccessMsg(null);
            }}
          >
            Login
          </button>
          <button
            type="button"
            className={`auth-tab ${mode === 'register' ? 'active' : ''}`}
            onClick={() => {
              setMode('register');
              setError(null);
              setSuccessMsg(null);
            }}
          >
            Register
          </button>
        </div>

        {error && (
          <div className="alert-banner alert-error" role="alert">
            <span className="alert-icon">⚠️</span>
            <span>{error}</span>
          </div>
        )}

        {successMsg && (
          <div className="alert-banner alert-success" role="status">
            <span className="alert-icon">✓</span>
            <span>{successMsg}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Enter your username"
              disabled={loading}
              autoComplete="username"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder={mode === 'register' ? 'At least 6 characters' : 'Enter your password'}
              disabled={loading}
              autoComplete={mode === 'register' ? 'new-password' : 'current-password'}
            />
          </div>

          <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
            {loading
              ? mode === 'register'
                ? 'Registering...'
                : 'Authenticating...'
              : mode === 'register'
              ? 'Register'
              : 'Login'}
          </button>
        </form>
      </div>
    </div>
  );
}
