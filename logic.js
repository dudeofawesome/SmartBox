var maxBrightness = 10; // Minimum brightness to trip sensor
var youveGotMail = false; 
var checkFrequencyms = 1000;
var led = []; 
var lightSensor = [];
var groveSensor = require('jsupm_grove');
var doorSensor;

(function setup()
{
	console.log("setup() has been called.");

	led[0] = new groveSensor.GroveLed(2);
	lightSensor[0] = new groveSensor.GroveLight(0);
	doorSensor = new groveSensor.GroveButton(3);

	setInterval(loop, checkFrequencyms);
})();

function loop()
{
	console.log(doorSensor.value());
	return;
	console.log("loop() has been called.");

	if (readDoorClosed())
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

function readDoorClosed()
{
	// To-Do
	var x = doorSensor.value;
	console.log("readDoorClosed() returned "+x+".");
	return x;

	// https://software.intel.com/en-us/iot/hardware/sensors/grove-button
}

//////////////////////////////////////////////////////
//////////////////// END HARDWARE ///////L////////////
//////////////////////////////////////////////////////

function sendNotification()
{
	// To-Do
	console.log("sendNotification() has been called.\nYou've got mail!");
}