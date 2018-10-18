const
	TestSession = require('./test-session.js'),
	options = require('./options.json');

process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

const testSession = new TestSession(options);

console.log('starting TestSession...');
testSession.runTestPlan()
	.then(data => {
		console.log('done with ' + data.errors.length + ' errors');
		if (data.errors.length) {
			console.log(JSON.stringify(data, null, 4));
		}
	})
	.catch(e => console.error('TestSession failed with ', e));