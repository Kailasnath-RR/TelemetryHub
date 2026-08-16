const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export class ApiError extends Error {
  constructor(status, errorType, message, timestamp) {
    super(message || `HTTP Error ${status}`);
    this.name = 'ApiError';
    this.status = status;
    this.errorType = errorType;
    this.timestamp = timestamp;
  }
}

let isRefreshing = false;
let refreshSubscribers = [];

function subscribeTokenRefresh(cb) {
  refreshSubscribers.push(cb);
}

function onRefreshed(token) {
  refreshSubscribers.map((cb) => cb(token));
  refreshSubscribers = [];
}

export async function apiFetch(path, options = {}) {
  const url = `${API_BASE_URL}${path}`;
  const accessToken = localStorage.getItem('accessToken');

  const headers = {
    ...options.headers,
  };

  if (accessToken) {
    headers['Authorization'] = `Bearer ${accessToken}`;
  }

  if (options.body && typeof options.body === 'string' && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json';
  }

  const fetchOptions = {
    ...options,
    headers,
  };

  try {
    let response = await fetch(url, fetchOptions);

    // Handle 401 Unauthorized for non-auth endpoints
    if (response.status === 401 && !path.startsWith('/auth/login') && !path.startsWith('/auth/refresh')) {
      const refreshToken = localStorage.getItem('refreshToken');

      if (refreshToken) {
        if (!isRefreshing) {
          isRefreshing = true;
          try {
            const refreshRes = await fetch(`${API_BASE_URL}/auth/refresh`, {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({ refreshToken }),
            });

            if (refreshRes.ok) {
              const refreshData = await refreshRes.json();
              localStorage.setItem('accessToken', refreshData.accessToken);
              isRefreshing = false;
              onRefreshed(refreshData.accessToken);
            } else {
              isRefreshing = false;
              refreshSubscribers = [];
              localStorage.removeItem('accessToken');
              localStorage.removeItem('refreshToken');
              window.dispatchEvent(new CustomEvent('auth:logout'));
              throw new ApiError(401, 'Unauthorized', 'Session expired. Please login again.');
            }
          } catch (err) {
            isRefreshing = false;
            refreshSubscribers = [];
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            window.dispatchEvent(new CustomEvent('auth:logout'));
            throw err;
          }
        }

        // Wait for token refresh if currently in progress
        const newToken = await new Promise((resolve) => {
          subscribeTokenRefresh((token) => resolve(token));
        });

        if (newToken) {
          headers['Authorization'] = `Bearer ${newToken}`;
          response = await fetch(url, { ...options, headers });
        }
      } else {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.dispatchEvent(new CustomEvent('auth:logout'));
        throw new ApiError(401, 'Unauthorized', 'Please login to continue.');
      }
    }

    if (response.status === 204) {
      return null;
    }

    const contentType = response.headers.get('content-type');
    const isJson = contentType && contentType.includes('application/json');
    const data = isJson ? await response.json() : null;

    if (!response.ok) {
      const errorMessage = data?.message || data?.error || response.statusText || 'Request failed';
      const errorType = data?.error_type || data?.error || 'Error';
      const timestamp = data?.timestamp || null;

      throw new ApiError(response.status, errorType, errorMessage, timestamp);
    }

    return data;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError(500, 'NetworkError', error.message || 'Failed to connect to backend server.');
  }
}
