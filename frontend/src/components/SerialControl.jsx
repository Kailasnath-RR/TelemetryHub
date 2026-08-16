import React, { useState, useEffect } from 'react';
import { getSerialStatus, reconnectSerial, disconnectSerial } from '../api/serial';
import { ApiError } from '../api/client';

export function SerialControl({ role }) {
  const [serialState, setSerialState] = useState({ connected: false, portName: 'Unknown' });
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null);
  const [loading, setLoading] = useState(false);
  const [forbidden, setForbidden] = useState(false);

  const fetchStatus = async () => {
    try {
      const data = await getSerialStatus();
      if (data) {
        setSerialState(data);
      }
      setForbidden(false);
    } catch (err) {
      if (err instanceof ApiError && err.status === 403) {
        setForbidden(true);
        setError('You do not have permission to control the serial connection.');
      } else if (err instanceof ApiError) {
        setError(err.message);
      }
    }
  };

  useEffect(() => {
    fetchStatus();
  }, [role]);

  const handleAction = async (actionName, actionFn) => {
    setError(null);
    setMessage(null);
    setLoading(true);

    try {
      await actionFn();
      setMessage(`Serial ${actionName} command executed.`);
      await fetchStatus();
    } catch (err) {
      if (err instanceof ApiError) {
        if (err.status === 403) {
          setForbidden(true);
          setError('You do not have permission to control the serial connection.');
        } else {
          setError(err.message || `Failed to execute serial ${actionName}.`);
        }
      } else {
        setError(`Unexpected error occurred while executing serial ${actionName}.`);
      }
    } finally {
      setLoading(false);
    }
  };

  const isAdmin = role === 'ADMIN';

  return (
    <div className="card dashboard-card">
      <div className="card-header">
        <h3>Serial Connection</h3>
        <span
          className={`status-badge ${
            serialState.connected ? 'status-connected' : 'status-disconnected'
          }`}
        >
          {serialState.connected ? `Connected (${serialState.portName || 'Active'})` : 'Disconnected'}
        </span>
      </div>

      {error && (
        <div className="alert-banner alert-error" role="alert">
          <span className="alert-icon">⚠️</span>
          <span>{error}</span>
        </div>
      )}

      {message && (
        <div className="alert-banner alert-success" role="status">
          <span className="alert-icon">✓</span>
          <span>{message}</span>
        </div>
      )}

      <div className="controls-group">
        <div className="button-row">
          <button
            onClick={() => handleAction('reconnect', reconnectSerial)}
            disabled={loading || !isAdmin || forbidden}
            className="btn btn-action btn-primary"
          >
            {loading ? 'Processing...' : 'Reconnect'}
          </button>

          <button
            onClick={() => handleAction('disconnect', disconnectSerial)}
            disabled={loading || !isAdmin || forbidden}
            className="btn btn-action btn-secondary"
          >
            {loading ? 'Processing...' : 'Disconnect'}
          </button>
        </div>
      </div>
    </div>
  );
}
