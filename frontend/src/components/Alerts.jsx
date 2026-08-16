import React, { useState, useEffect } from 'react';

export function Alerts({ realTimeData }) {
  const [alerts, setAlerts] = useState([]);

  useEffect(() => {
    if (!realTimeData) return;

    const adcVal = realTimeData.AdcValue ?? realTimeData.adcValue;
    const timestamp = realTimeData.receivedAt || new Date().toISOString();

    if (adcVal !== undefined && adcVal > 900) {
      const newAlert = {
        id: Date.now() + Math.random(),
        adcValue: adcVal,
        timestamp: timestamp,
        message: `ADC Value (${adcVal}) crossed high voltage threshold (> 900)`,
      };

      setAlerts((prev) => [newAlert, ...prev].slice(0, 10)); // Keep latest 10
    }
  }, [realTimeData]);

  const clearAlerts = () => {
    setAlerts([]);
  };

  return (
    <div className="card dashboard-card">
      <div className="card-header">
        <h3>Alerts</h3>
        {alerts.length > 0 && (
          <button onClick={clearAlerts} className="btn btn-sm btn-secondary">
            Clear Alerts
          </button>
        )}
      </div>

      <div className="alerts-list">
        {alerts.length === 0 ? (
          <div className="alerts-empty">No active alerts recorded.</div>
        ) : (
          alerts.map((alert) => (
            <div key={alert.id} className="alert-item alert-warning">
              <div className="alert-item-header">
                <span className="alert-title">⚠️ ADC Threshold Alert</span>
                <span className="alert-timestamp">{alert.timestamp}</span>
              </div>
              <p className="alert-body">{alert.message}</p>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
