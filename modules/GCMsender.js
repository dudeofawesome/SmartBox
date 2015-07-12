var gcm = require('node-gcm');

var data = {};

module.exports = {
	init: function () {
        if (fs.existsSync('../data/store.json')) {
            data = JSON.parse(fs.readFileSync('../data/store.json').toString())
        }
	},
	sendGCM: function (message) {
		if (data.GCMid) {
 
			var message = new gcm.Message();
			message.addData('mail arrived', Date());
			 
			var regIds = [data.GCMid];
			 
			// Set up the sender with you API key 
			var sender = new gcm.Sender('AIzaSyBYApY9TzcfEBiTw41ApPV7Ab0Y8z49zJI');
			 
			//Now the sender can be used to send messages 
			sender.send(message, regIds, function (err, result) {
			    if (err) {
			    	console.error(err);
			    } else {
			    	console.log(result);
			    }
			});
			 
			sender.sendNoRetry(message, regIds, function (err, result) {
			    if (err) {
			    	console.error(err);
			    } else {
			    	console.log(result);
			    }
			});
		}
	}
};