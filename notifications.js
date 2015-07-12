module.exports = {
	sendEmail: function (sendTo, subject, message) {
		var nodemailer = require('nodemailer');
		var transporter = nodemailer.createTransport({
		service: 'gmail',
		auth: {
			user: 'smartboxmen@gmail.com',
			pass: 'smartbox123'
		}
		});
		transporter.sendMail({
		from: 'smartboxmen@gmail.com',
		to: sendTo,
		subject: subject,
		text: message
		});
	}
};