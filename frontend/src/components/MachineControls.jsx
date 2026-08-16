import React, { useState, useEffect } from 'react';
import {
  startMachine,
  stopMachine,
  lockMachine,
  unlockMachine,
  shutdownHardware,
  speedIncrease,
  speedDecrease,
} from '../api/machine';
import { getLatestStatus } from '../api/telemetry';
import { ApiError } from '../api/client';

export function MachineControls({ role, realTimeStatus }) {
  const [machineState, setMachineState] = useState('FETCHING...');
  const [actionError, setActionError] = useState(null);
  const [actionSuccess, setActionSuccess] = useState(null);
  const [loadingAction, setLoadingAction] = useState(null);

  // Update state when WebSocket broadcasts status
  useEffect(() => {
    if (realTimeStatus && realTimeStatus.State) {
      setMachineState(realTimeStatus.State);
    }
  }, [realTimeStatus]);

  // Initial fetch of machine status if WebSocket hasn't broadcast yet
  useEffect(() => {
    let isMounted = true;
    getLatestStatus()
      .then((data) => {
        if (isMounted && data && data.State) {
          setMachineState(data.State);
        } else if (isMounted && !realTimeStatus) {
          setMachineState('UNKNOWN');
        }
      })
      .catch(() => {
        if (isMounted && !realTimeStatus) {
          setMachineState('UNKNOWN');
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

  const handleAction = async (actionName, actionFn) => {
    setActionError(null);
    setActionSuccess(null);
    setLoadingAction(actionName);

    try {
      await actionFn();
      setActionSuccess(`Action '${actionName}' executed successfully.`);
      // Refresh status if possible
      const statusData = await getLatestStatus().catch(() => null);
      if (statusData && statusData.State) {
        setMachineState(statusData.State);
      }
    } catch (err) {
      if (err instanceof ApiError) {
        if (err.status === 403) {
          setActionError('Forbidden: You do not have permission to execute machine controls.');
        } else {
          setActionError(err.message || `Failed to execute ${actionName}.`);
        }
      } else {
        setActionError(`Unexpected error occurred while executing ${actionName}.`);
      }
    } finally {
      setLoadingAction(null);
    }
  };

  const isViewer = role === 'VIEWER';

  return (
    <div className="card dashboard-card">
      <div className="card-header">
        <h3>Machine Status</h3>
        <span className={`status-badge state-${machineState?.toLowerCase() || 'unknown'}`}>
          {machineState}
        </span>
      </div>

      {actionError && (
        <div className="alert-banner alert-error" role="alert">
          <span className="alert-icon">⚠️</span>
          <span>{actionError}</span>
        </div>
      )}

      {actionSuccess && (
        <div className="alert-banner alert-success" role="status">
          <span className="alert-icon">✓</span>
          <span>{actionSuccess}</span>
        </div>
      )}

      <div className="controls-group">
        <div className="button-grid">
          <button
            onClick={() => handleAction('Start', startMachine)}
            disabled={loadingAction !== null || isViewer}
            className="btn btn-action btn-start"
          >
            {loadingAction === 'Start' ? 'Starting...' : 'Start'}
          </button>

          <button
            onClick={() => handleAction('Stop', stopMachine)}
            disabled={loadingAction !== null || isViewer}
            className="btn btn-action btn-stop"
          >
            {loadingAction === 'Stop' ? 'Stopping...' : 'Stop'}
          </button>

          <button
            onClick={() => handleAction('Lock', lockMachine)}
            disabled={loadingAction !== null || isViewer}
            className="btn btn-action btn-lock"
          >
            {loadingAction === 'Lock' ? 'Locking...' : 'Lock'}
          </button>

          <button
            onClick={() => handleAction('Unlock', unlockMachine)}
            disabled={loadingAction !== null || isViewer}
            className="btn btn-action btn-unlock"
          >
            {loadingAction === 'Unlock' ? 'Unlocking...' : 'Unlock'}
          </button>

          <button
            onClick={() => handleAction('Speed +', speedIncrease)}
            disabled={loadingAction !== null || isViewer}
            className="btn btn-action btn-speed"
          >
            {loadingAction === 'Speed +' ? 'Increasing...' : 'Speed +'}
          </button>

          <button
            onClick={() => handleAction('Speed -', speedDecrease)}
            disabled={loadingAction !== null || isViewer}
            className="btn btn-action btn-speed"
          >
            {loadingAction === 'Speed -' ? 'Decreasing...' : 'Speed -'}
          </button>

          <button
            onClick={() => handleAction('Shutdown', shutdownHardware)}
            disabled={loadingAction !== null || isViewer}
            className="btn btn-action btn-danger"
          >
            {loadingAction === 'Shutdown' ? 'Shutting down...' : 'Shutdown'}
          </button>
        </div>
      </div>
    </div>
  );
}
