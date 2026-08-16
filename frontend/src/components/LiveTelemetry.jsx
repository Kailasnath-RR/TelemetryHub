import React, { useState, useEffect } from 'react';
import { getLatestData } from '../api/telemetry';

export function LiveTelemetry({ realTimeData, connectionStatus }) {
  const [telemetry, setTelemetry] = useState(null);

  // Update when real-time WebSocket data arrives
  useEffect(() => {
    if (realTimeData) {
      setTelemetry(realTimeData);
    }
  }, [realTimeData]);

  // Initial fetch if WebSocket hasn't broadcast yet
  useEffect(() => {
    let isMounted = true;
    getLatestData()
      .then((data) => {
        if (isMounted && data && !realTimeData) {
          setTelemetry(data);
        }
      })
      .catch(() => {
        // Silent fallback for initial fetch
      });

    return () => {
      isMounted = false;
    };
  }, []);

  const getStatusClass = (status) => {
    switch (status) {
      case 'Connected':
        return 'ws-connected';
      case 'Connecting':
        return 'ws-connecting';
      case 'Disconnected':
      default:
        return 'ws-disconnected';
    }
  };

  return (
    <div className="card dashboard-card">
      <div className="card-header">
        <h3>Live Telemetry</h3>
        <div className="ws-status-container">
          <span className="ws-label">WebSocket:</span>
          <span className={`ws-badge ${getStatusClass(connectionStatus)}`}>
            {connectionStatus}
          </span>
        </div>
      </div>

      <div className="telemetry-grid">
        <div className="metric-box">
          <span className="metric-label">ADC Value</span>
          <span className="metric-value adc-value">{telemetry?.AdcValue ?? telemetry?.adcValue ?? '—'}</span>
        </div>

        <div className="metric-box">
          <span className="metric-label">Count</span>
          <span className="metric-value">{telemetry?.Count ?? telemetry?.count ?? '—'}</span>
        </div>

        <div className="metric-box">
          <span className="metric-label">Sample Period</span>
          <span className="metric-value">{telemetry?.SamplePeriod ?? telemetry?.samplePeriod ?? '—'} ms</span>
        </div>

        <div className="metric-box full-width">
          <span className="metric-label">Received At</span>
          <span className="metric-value timestamp">{telemetry?.receivedAt || '—'}</span>
        </div>
      </div>
    </div>
  );
}
