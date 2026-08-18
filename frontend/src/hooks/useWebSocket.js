import { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';

const getWsUrl = () => {
  const baseUrl = import.meta.env.VITE_API_BASE_URL;
  if (baseUrl) {
    const wsProtocol = baseUrl.startsWith('https') ? 'wss' : 'ws';
    const host = baseUrl.replace(/^https?:\/\//, '');
    return `${wsProtocol}://${host}/ws`;
  }
  const wsProtocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
  return `${wsProtocol}://${window.location.host}/ws`;
};

export function useWebSocket() {
  const [connectionStatus, setConnectionStatus] = useState('Connecting');
  const [latestData, setLatestData] = useState(null);
  const [latestStatus, setLatestStatus] = useState(null);
  const clientRef = useRef(null);

  useEffect(() => {
    const brokerURL = getWsUrl();

    const client = new Client({
      brokerURL,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        setConnectionStatus('Connected');

        // Subscribe to real-time telemetry stream
        client.subscribe('/topic/telemetry', (message) => {
          try {
            const data = JSON.parse(message.body);
            setLatestData(data);
          } catch (e) {
            console.error('Failed to parse telemetry frame:', e);
          }
        });

        // Subscribe to real-time status stream
        client.subscribe('/topic/status', (message) => {
          try {
            const status = JSON.parse(message.body);
            setLatestStatus(status);
          } catch (e) {
            console.error('Failed to parse status frame:', e);
          }
        });
      },
      onDisconnect: () => {
        setConnectionStatus('Disconnected');
      },
      onStompError: (frame) => {
        console.error('STOMP protocol error:', frame.headers['message']);
        setConnectionStatus('Disconnected');
      },
      onWebSocketClose: () => {
        setConnectionStatus('Disconnected');
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
      }
    };
  }, []);

  return {
    connectionStatus,
    latestData,
    latestStatus,
  };
}
