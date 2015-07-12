var gcm = require('node-gcm');

var message = new gcm.Message();

message.addData('key1','Hello Louis!');
message.addNotification('title','LOUIS CAN YOU SEE THIS?');
message.addNotification('body', 'SEXY BODY TEXT');
message.addNotification('icon', 'ic_launcher');

var regIds = ['455344791002'];

// Set up the sender with your API key
var sender = new gcm.Sender('AIzaSyBYApY9TzcfEBiTw41ApPV7Ab0Y8z49zJI');

// Now the sender can be used to send messages
sender.send(message, regIds, function (err, result) {
	if(err) console.log(err);
	else 	console.log(result);
});

sender.sendNoRetry(message, regIds, function (err, result) {
	if(err) console.log(err);
	else 	console.log(result);	
});