import { apiFetch } from './client';

export async function getSerialStatus() {
  return apiFetch('/serial/status', { method: 'GET' });
}

export async function reconnectSerial() {
  return apiFetch('/serial/reconnect', { method: 'POST' });
}

export async function disconnectSerial() {
  return apiFetch('/serial/disconnect', { method: 'POST' });
}
