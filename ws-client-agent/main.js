const
	TestSession = require('./test-session.js'),
	options = require('./options.json');

process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

const testSession = new TestSession(options);

console.log('starting TestSession...');
testSession.runTestPlan()
	.then(report => {
		console.log('done with ' + report.errors.length + ' errors');
		if (report.errors.length) {
			console.log(JSON.stringify(report, null, 4));
		}
		console.log('for the full report see "' + report.logPath + '"');
	})
	.catch(e => console.error('TestSession failed with ', e));