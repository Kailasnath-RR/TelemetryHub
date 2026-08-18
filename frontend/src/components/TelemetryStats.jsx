import React, { useState, useEffect } from 'react';
import { getStats } from '../api/telemetry';

export function TelemetryStats() {
  const [stats, setStats] = useState(null);
  const [minAdc, setMinAdc] = useState('');
  const [maxAdc, setMaxAdc] = useState('');
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const formatLocalDateTime = (datetimeStr) => {
    if (!datetimeStr) return undefined;
    // Format YYYY-MM-DDTHH:mm or YYYY-MM-DDTHH:mm:ss for Java LocalDateTime parsing
    if (datetimeStr.length === 16) {
      return `${datetimeStr}:00`;
    }
    return datetimeStr;
  };

  const fetchStats = async () => {
    setLoading(true);
    setError(null);
    try {
      const filter = {};
      if (minAdc !== '') filter.minAdc = parseInt(minAdc, 10);
      if (maxAdc !== '') filter.maxAdc = parseInt(maxAdc, 10);
      if (from !== '') filter.from = formatLocalDateTime(from);
      if (to !== '') filter.to = formatLocalDateTime(to);

      const data = await getStats(filter);
      if (data) {
        setStats(data);
      }
    } catch (err) {
      if (err.message && err.message.includes('could not determine data type')) {
        setError('Backend Query Error: PostgreSQL parameter type ambiguity in findStats. Please pass explicit filter values.');
      } else {
        setError(err.message || 'Failed to fetch telemetry stats.');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStats();
  }, []);

  const handleFilterSubmit = (e) => {
    e.preventDefault();
    fetchStats();
  };

  const handleClearFilters = () => {
    setMinAdc('');
    setMaxAdc('');
    setFrom('');
    setTo('');
  };

  return (
    <div className="card dashboard-card">
      <div className="card-header">
        <h3>Telemetry Statistics</h3>
        <button onClick={fetchStats} className="btn btn-sm btn-secondary" disabled={loading}>
          {loading ? 'Refreshing...' : 'Refresh Stats'}
        </button>
      </div>

      <form onSubmit={handleFilterSubmit} style={{ marginBottom: '16px' }} noValidate>
        <div style={{ display: 'flex', gap: '10px', alignItems: 'center', flexWrap: 'wrap' }}>
          <div className="form-group inline-group">
            <label htmlFor="statsFrom">From:</label>
            <input
              id="statsFrom"
              type="datetime-local"
              step="1"
              value={from}
              onChange={(e) => setFrom(e.target.value)}
            />
          </div>

          <div className="form-group inline-group">
            <label htmlFor="statsTo">To:</label>
            <input
              id="statsTo"
              type="datetime-local"
              step="1"
              value={to}
              onChange={(e) => setTo(e.target.value)}
            />
          </div>

          <div className="form-group inline-group">
            <label htmlFor="statsMinAdc">Min ADC:</label>
            <input
              id="statsMinAdc"
              type="number"
              value={minAdc}
              onChange={(e) => setMinAdc(e.target.value)}
              placeholder="0"
            />
          </div>

          <div className="form-group inline-group">
            <label htmlFor="statsMaxAdc">Max ADC:</label>
            <input
              id="statsMaxAdc"
              type="number"
              value={maxAdc}
              onChange={(e) => setMaxAdc(e.target.value)}
              placeholder="1023"
            />
          </div>

          <div style={{ display: 'flex', gap: '6px' }}>
            <button type="submit" className="btn btn-sm btn-primary" disabled={loading}>
              Apply Filters
            </button>
            {(from || to || minAdc || maxAdc) && (
              <button type="button" onClick={handleClearFilters} className="btn btn-sm btn-secondary">
                Clear
              </button>
            )}
          </div>
        </div>
      </form>

      {error ? (
        <div className="alert-banner alert-error" role="alert">
          <span className="alert-icon">⚠️</span>
          <span>{error}</span>
        </div>
      ) : (
        <div className="telemetry-grid">
          <div className="metric-box">
            <span className="metric-label">Average ADC</span>
            <span className="metric-value">
              {stats?.averageAdc !== null && stats?.averageAdc !== undefined
                ? Number(stats.averageAdc).toFixed(1)
                : '—'}
            </span>
          </div>

          <div className="metric-box">
            <span className="metric-label">Min ADC</span>
            <span className="metric-value">{stats?.minAdc ?? '—'}</span>
          </div>

          <div className="metric-box">
            <span className="metric-label">Max ADC</span>
            <span className="metric-value">{stats?.maxAdc ?? '—'}</span>
          </div>

          <div className="metric-box full-width">
            <span className="metric-label">Total Recorded Samples</span>
            <span className="metric-value">{stats?.totalSamples ?? '—'}</span>
          </div>
        </div>
      )}
    </div>
  );
}
