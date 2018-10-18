const
	fs = require('fs'),
	path = require('path'),
	fsEx = require('fs-extra'),
	WebSocket = require('ws');

module.exports = class TestSession {
	constructor(options) {
		if (!options || typeof options !== 'object') {
			throw new Error('invalid options');
		}

		//	ensure options
		this.options = Object.assign({
			wsUrl: 'ws://localhost:8686/messaging/test',
			maxErrors: 5
		}, options);
		this.finished = false;
		this.errors = [];

		//	init logger
		let sessionLogFile = 'session-' + new Date().toISOString().substr(0, 19).replace(/:/g, '-') + '.log',
			sessionLogPath = path.join('.', 'sessions', sessionLogFile);
		fsEx.ensureFileSync(sessionLogPath);
		this.log = new console.Console(fs.createWriteStream(sessionLogPath)).log;
		this.log(logMessage('INFO', 'TestSessions initialized with effective options: ' + JSON.stringify(this.options)));
	}

	connect() {
		this.ws = new WebSocket(this.options.wsUrl);
		this.ws.on('open', () => this.onOpen());
		this.ws.on('close', () => this.onClose());
		this.ws.on('error', error => this.onError(error));
		this.ws.on('message', data => this.onMessage(data));
	}

	onOpen() {
		this.log(logMessage('INFO', 'WS opened to ' + this.options.wsUrl));
	}


	onClose() {
		if (!this.finished) {
			this.log(logMessage('WARN', 'WS closed prematurely, reconnecting...'));
			this.connect();
		}
	}

	onError(error) {
		this.errors.push(error);
		this.log(logMessage('FAIL', 'WS error ' + this.errors.length + ' of max ' + this.options.maxErrors + ' - ' + error));
		if (this.errors.length === this.options.maxErrors) {
			this.log(logMessage('FAIL', '\trun out of max errors (' + this.options.maxErrors + '), exiting'));
			this.end();
		}
	}

	onMessage(data) {
		this.log(data);
	}

	runTestPlan() {
		this.log(logMessage('INFO', 'session started'));
		this.connect();


	}

	end() {
		this.finished = true;
		if (this.ws.readyState === this.ws.OPEN) {
			if (this.errors.length === this.options.maxErrors) {
				this.ws.close(1011, 'exiting due to max (' + this.options.maxErrors + ') encountered; errors: ' + JSON.stringify(this.errors));
			} else {
				this.ws.close(1000, 'done' + (this.errors.length ? (' (errors: ' + JSON.stringify(this.errors) + ')') : ''));
			}
		}
		this.log(logMessage('INFO', 'session finished'));
	}
};

function logMessage(type, content) {
	return new Date().toISOString() + ' ' + type + ' ' + content;
}