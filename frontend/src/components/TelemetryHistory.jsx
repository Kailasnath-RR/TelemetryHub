import React, { useState, useEffect } from 'react';
import { getHistory } from '../api/telemetry';

export function TelemetryHistory() {
  const [pageData, setPageData] = useState(null);
  const [page, setPage] = useState(0);
  const [pageSize] = useState(10);
  const [adcMin, setAdcMin] = useState('');
  const [adcMax, setAdcMax] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchHistory = async (currentPage = page) => {
    setLoading(true);
    setError(null);

    const filter = {};
    if (adcMin !== '') filter.adcMin = parseInt(adcMin, 10);
    if (adcMax !== '') filter.adcMax = parseInt(adcMax, 10);

    try {
      const result = await getHistory(currentPage, pageSize, filter);
      setPageData(result);
    } catch (err) {
      setError(err.message || 'Failed to load telemetry history.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory(page);
  }, [page]);

  const handleFilterSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchHistory(0);
  };

  const handleClearFilter = () => {
    setAdcMin('');
    setAdcMax('');
    setPage(0);
    fetchHistory(0);
  };

  return (
    <div className="card dashboard-card full-width-card">
      <div className="card-header">
        <h3>Telemetry History</h3>
        <span className="user-label">
          Total Entries: {pageData?.totalElements ?? 0}
        </span>
      </div>

      <form onSubmit={handleFilterSubmit} className="history-filter-form">
        <div className="filter-inputs">
          <div className="form-group inline-group">
            <label htmlFor="adcMin">Min ADC:</label>
            <input
              id="adcMin"
              type="number"
              value={adcMin}
              onChange={(e) => setAdcMin(e.target.value)}
              placeholder="e.g. 100"
            />
          </div>

          <div className="form-group inline-group">
            <label htmlFor="adcMax">Max ADC:</label>
            <input
              id="adcMax"
              type="number"
              value={adcMax}
              onChange={(e) => setAdcMax(e.target.value)}
              placeholder="e.g. 900"
            />
          </div>

          <button type="submit" className="btn btn-sm btn-primary" disabled={loading}>
            Filter
          </button>

          {(adcMin !== '' || adcMax !== '') && (
            <button type="button" onClick={handleClearFilter} className="btn btn-sm btn-secondary">
              Clear
            </button>
          )}
        </div>
      </form>

      {error && <div className="alert-banner alert-error">{error}</div>}

      <div className="table-responsive">
        <table className="history-table">
          <thead>
            <tr>
              <th>Count</th>
              <th>ADC Value</th>
              <th>Sample Period</th>
              <th>Received At</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="4" className="text-center">Loading history...</td>
              </tr>
            ) : pageData?.data?.length > 0 ? (
              pageData.data.map((row, idx) => (
                <tr key={idx}>
                  <td>{row.Count ?? row.count}</td>
                  <td className="adc-value">{row.AdcValue ?? row.adcValue}</td>
                  <td>{row.SamplePeriod ?? row.samplePeriod} ms</td>
                  <td className="timestamp">{row.receivedAt}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="4" className="text-center">No telemetry history records found.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {pageData && pageData.totalPages > 1 && (
        <div className="pagination-bar">
          <button
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={!pageData.hasPrevious || loading}
            className="btn btn-sm btn-secondary"
          >
            Previous
          </button>

          <span className="pagination-info">
            Page {pageData.page + 1} of {pageData.totalPages}
          </span>

          <button
            onClick={() => setPage((p) => p + 1)}
            disabled={!pageData.hasNext || loading}
            className="btn btn-sm btn-secondary"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}
