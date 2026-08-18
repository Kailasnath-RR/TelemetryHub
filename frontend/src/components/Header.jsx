import React from 'react';

export function Header({ user, onLogout }) {
  return (
    <header className="app-header">
      <div className="header-brand">
        <h2>TelemetryHub</h2>
      </div>

      <div className="header-user-info">
        <div className="user-details">
          <span className="user-label">User:</span>
          <span className="user-value">{user?.username || 'Unknown'}</span>
          <span className="user-label">Role:</span>
          <span className={`role-badge role-${user?.role?.toLowerCase() || 'viewer'}`}>
            {user?.role || 'N/A'}
          </span>
        </div>

        <button onClick={onLogout} className="btn btn-logout">
          Logout
        </button>
      </div>
    </header>
  );
}
