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
			threeMinutesCycles: 1,
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

	async runTestPlan() {
		this.log(logMessage('INFO', 'session started'));
		this.connect();

		try {
			let cnt = 1;
			while (this.options.threeMinutesCycles--) {
				this.log(logMessage('INFO', 'CYCLE START: stating 3 minutes cycle no. ' + cnt));

				this.log(logMessage('INFO', 'waiting 10 secs...'));
				await sleep(10000);
				this.log(logMessage('INFO', 'sending small text message'));
				this.ws.send('textToReturn:Hello World', {binary: false});

				this.log(logMessage('INFO', 'waiting 20 secs...'));
				await sleep(20000);
				this.log(logMessage('INFO', 'sending 128K binary'));
				this.ws.send(Int8Array.from([0, 128].concat(new Array(128 * 1024).fill(64, 0, 128 * 1024))), {binary: true});

				this.log(logMessage('INFO', 'waiting 40 secs...'));
				await sleep(40000);
				this.log(logMessage('INFO', 'sending 128K character text'));
				this.ws.send('textToReturn:' + 'y'.repeat(128000), {binary: false});

				this.ws.send(Int8Array.of(110), {binary: true});
				await new Promise((res) => {
					this.ws.on('message', data => {
						console.log(data);
						this.ws.removeAllListeners('message');
						res();
					});
				});

				this.log(logMessage('INFO', 'CYCLE END: cycle no. ' + cnt++ + ' finished'));
			}
		} catch (e) {
			console.error('TestSession failed with ', e);
		}

		this.end();
	}

	end() {
		this.finished = true;
		if (this.ws.readyState === this.ws.OPEN) {
			if (this.errors.length === this.options.maxErrors) {
				this.ws.close(1011, 'exiting due to max (' + this.options.maxErrors + ') encountered; errors: ' + JSON.stringify(this.errors));
			} else {
				this.ws.close(1000, 'done' + (this.errors.length ? (' (errors: ' + JSON.stringify(this.errors) + ')') : ''));
			}
		} else if (this.ws.readyState === this.ws.CONNECTING) {
			this.ws.terminate();
		}
		this.log(logMessage('INFO', 'session finished'));
	}
};

function logMessage(type, content) {
	return new Date().toISOString() + ' ' + type + ' ' + content;
}

async function sleep(millisToSleep) {
	await new Promise(res => {
		setTimeout(res, millisToSleep);
	});
}