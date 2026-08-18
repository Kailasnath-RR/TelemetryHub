import { apiFetch } from './client';

export async function register(username, password) {
  return apiFetch('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });
}

export async function login(username, password) {

  return apiFetch('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });
}

export async function getMe() {
  return apiFetch('/auth/me', {
    method: 'GET',
  });
}

export async function refreshToken(token) {
  return apiFetch('/auth/refresh', {
    method: 'POST',
    body: JSON.stringify({ refreshToken: token }),
  });
}

export async function logout(refreshToken) {
  return apiFetch('/auth/logout', {
    method: 'POST',
    body: JSON.stringify({ refreshToken }),
  });
}
