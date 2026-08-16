import { apiFetch } from './client';

export async function getLatestData() {
  return apiFetch('/telemetry/latest/data', { method: 'GET' });
}

export async function getLatestStatus() {
  return apiFetch('/telemetry/latest/status', { method: 'GET' });
}

export async function getHistory(page = 0, size = 20, filter = {}) {
  const params = new URLSearchParams({ page, size });
  if (filter.adcMin !== undefined && filter.adcMin !== null && filter.adcMin !== '') {
    params.append('adcMin', filter.adcMin);
  }
  if (filter.adcMax !== undefined && filter.adcMax !== null && filter.adcMax !== '') {
    params.append('adcMax', filter.adcMax);
  }

  return apiFetch(`/telemetry/history?${params.toString()}`, { method: 'GET' });
}

export async function getStats(filter = {}) {
  const params = new URLSearchParams();

  if (filter.from) params.append('from', filter.from);
  if (filter.to) params.append('to', filter.to);
  if (filter.minAdc !== undefined && filter.minAdc !== null && filter.minAdc !== '') {
    params.append('minAdc', filter.minAdc);
  }
  if (filter.maxAdc !== undefined && filter.maxAdc !== null && filter.maxAdc !== '') {
    params.append('maxAdc', filter.maxAdc);
  }

  const queryString = params.toString() ? `?${params.toString()}` : '';
  return apiFetch(`/telemetry/stats${queryString}`, { method: 'GET' });
}
