var maxBrightness = 10; // Minimum brightness to trip sensor
var youveGotMail = false; 
var checkFrequencyms = 1000;
var led = []; 
var lightSensor = [];
var groveSensor = require('jsupm_grove');
var doorSensor;

var notifications = require('./modules/notifications');
var api = require('./modules/api');

var emails = ["Josh@Gibbs.tk","vsriram@ucdavis.edu","b1hiker@gmail.com","louis@orleans.io"];

(function setup()
{
	console.log("setup() has been called.");

	led[0] = new groveSensor.GroveLed(2); // Plug into pin D2 
	lightSensor[0] = new groveSensor.GroveLight(0); // Plug into pin A0
	doorSensor = new groveSensor.GroveButton(3); // Plug into pin D3

	setInterval(loop, checkFrequencyms);

	api.startServer();
})();

function loop()
{
	console.log("loop() has been called.");

	// if the door is closed, check for mail
	if (!readDoorOpen())
	{
		checkMail();
	}
}

function checkMail()
{	
	console.log("checkMail() has been called.");

	lightsOn();
	var lightValue = readLightSensor();
	lightsOff();


	var prevYouveGotMail = youveGotMail;
	if (lightValue < maxBrightness)
		youveGotMail = true;
	else if (lightValue >= maxBrightness)
		youveGotMail = false

	console.log("prevYouveGotMail = "+prevYouveGotMail);
	console.log("    youveGotMail = "+youveGotMail);

	if (prevYouveGotMail != youveGotMail)
		changeState();
}

function changeState()
{
	console.log("changeState() has been called.")
	if (youveGotMail)
		sendNotification();
}

//////////////////////////////////////////////////////
/////////////////// BEGIN HARDWARE ///////////////////
//////////////////////////////////////////////////////

function lightsOn()

{
	console.log("lightsOn() has been called.");

	for (i in led)
		led[i].on();   

	// https://software.intel.com/en-us/iot/hardware/sensors/grove-led        
}

function lightsOff()
{
	console.log("lightsOff() has been called.");

	for (i in led)
		led[i].off();

	// https://software.intel.com/en-us/iot/hardware/sensors/grove-led
}

function readLightSensor()
{
	// Returns the lowest value of all the light sensors
	var lowest = 0; 
	for (i in lightSensor)
	{
		if (lightSensor[i].value() < lightSensor[lowest].value())
			lowest = i;
	}

	var x = lightSensor[lowest].value();
	console.log("readLightSensor() returned "+x+".");
	return x;

	// https://software.intel.com/en-us/iot/hardware/sensors/grove-light-sensor
}

function readDoorOpen()
{
	// To-Do
	var x = doorSensor.value();
	console.log("readDoorOpen() returned "+x+".");
	return x;

	// https://software.intel.com/en-us/iot/hardware/sensors/grove-button
}

//////////////////////////////////////////////////////
//////////////////// END HARDWARE ///////L////////////
//////////////////////////////////////////////////////

function sendNotification()
{
	console.log("sendNotification() has been called.\nYou've got mail!");
	var date = new Date();
	var subject = "Your mail on "+((date.getMonth())+1)+"/"+date.getDate()+"/"+date.getFullYear();
	var body = "Your mail was delivered on on " + date;
	for (i in emails)
		notifications.sendEmail(emails[i],subject,body);
}

