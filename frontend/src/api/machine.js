import { apiFetch } from './client';

export async function startMachine() {
  return apiFetch('/machine/start', { method: 'POST' });
}

export async function stopMachine() {
  return apiFetch('/machine/stop', { method: 'POST' });
}

export async function unlockMachine() {
  return apiFetch('/machine/unlock', { method: 'POST' });
}

export async function lockMachine() {
  return apiFetch('/machine/lock', { method: 'POST' });
}

export async function shutdownHardware() {
  return apiFetch('/machine/shutdownHardware', { method: 'POST' });
}

export async function speedIncrease() {
  return apiFetch('/machine/speed-increase', { method: 'POST' });
}

export async function speedDecrease() {
  return apiFetch('/machine/speed-decrease', { method: 'POST' });
}
