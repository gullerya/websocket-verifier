const WebSocket = require('ws');

const ws = new WebSocket('ws://localhost:8585/messaging/test');

ws.on('open', function open() {
	ws.send('ping');
});