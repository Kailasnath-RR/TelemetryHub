import React, { useState, useEffect } from 'react';
import { Login } from './components/Login';
import { Header } from './components/Header';
import { MachineControls } from './components/MachineControls';
import { SerialControl } from './components/SerialControl';
import { LiveTelemetry } from './components/LiveTelemetry';
import { Alerts } from './components/Alerts';
import { TelemetryStats } from './components/TelemetryStats';
import { TelemetryHistory } from './components/TelemetryHistory';
import { getMe, logout } from './api/auth';
import { useWebSocket } from './hooks/useWebSocket';

export function App() {
  const [user, setUser] = useState(null);
  const [loadingUser, setLoadingUser] = useState(true);

  const { connectionStatus, latestData, latestStatus } = useWebSocket();

  // Load user details if tokens exist
  useEffect(() => {
    const accessToken = localStorage.getItem('accessToken');

    if (!accessToken) {
      setLoadingUser(false);
      return;
    }

    getMe()
      .then((userData) => {
        setUser(userData);
      })
      .catch(() => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        setUser(null);
      })
      .finally(() => {
        setLoadingUser(false);
      });
  }, []);

  // Listen for forced logouts from client.js
  useEffect(() => {
    const handleAuthLogout = () => {
      setUser(null);
    };

    window.addEventListener('auth:logout', handleAuthLogout);
    return () => {
      window.removeEventListener('auth:logout', handleAuthLogout);
    };
  }, []);

  const handleLoginSuccess = async (loginResponse) => {
    if (loginResponse && loginResponse.accessToken) {
      localStorage.setItem('accessToken', loginResponse.accessToken);
    }
    if (loginResponse && loginResponse.refreshToken) {
      localStorage.setItem('refreshToken', loginResponse.refreshToken);
    }

    try {
      const userData = await getMe();
      setUser(userData);
    } catch (e) {
      console.error('Failed to fetch current user after login:', e);
    }
  };

  const handleLogout = async () => {
    const refreshToken = localStorage.getItem('refreshToken');
    if (refreshToken) {
      try {
        await logout(refreshToken);
      } catch (e) {
        console.error('Error during logout endpoint call:', e);
      }
    }

    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setUser(null);
  };

  if (loadingUser) {
    return (
      <div className="loading-screen">
        <div className="spinner"></div>
        <p>Loading TelemetryHub...</p>
      </div>
    );
  }

  if (!user) {
    return <Login onLoginSuccess={handleLoginSuccess} />;
  }

  return (
    <div className="app-container">
      <Header user={user} onLogout={handleLogout} />

      <main className="dashboard-content">
        <div className="dashboard-grid">
          <MachineControls role={user.role} realTimeStatus={latestStatus} />
          <SerialControl role={user.role} />
          <LiveTelemetry realTimeData={latestData} connectionStatus={connectionStatus} />
          <Alerts realTimeData={latestData} />
          <TelemetryStats />
          <TelemetryHistory />
        </div>
      </main>
    </div>
  );
}

export default App;
