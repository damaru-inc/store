c = require ('./config.js')
console.log(c);
const Messaging = require('./messaging.js');
const messaging = new Messaging(callback);
messaging.sendQuery();

function callback(message) {
    let payload = message.getBinaryAttachment();
    console.log(payload);
}
