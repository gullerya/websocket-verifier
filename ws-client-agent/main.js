const
	TestSession = require('./test-session.js'),
	options = require('./options.json');

process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

const testSession = new TestSession(options);

console.log('starting TestSession...');
testSession.runTestPlan()
	.then(() => console.log('done'))
	.catch(e => console.error('TestSession failed with ', e));