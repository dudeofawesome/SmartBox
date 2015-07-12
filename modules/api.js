var WEBSITE_PORT = 8080;

var fs = require('fs');

var express = require('express');
var app = express();
var server;
var bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended: true
}));

var index;
var messaging;

module.exports = {
    init: function () {
        loadPages();
        this.startServer();
        return this;
    },
    startServer: function () {
        app.post('/config', function (req, res) {
            res.send('uhhhh');
        });
        app.post('/setGCMid', function (req, res) {
            if (req.body.GCMid !== undefined) {
                var saveData = function () {
                    var data = {};
                    data.GCMid = req.body.GCMid;
                    fs.writeFile('../data/store.json', JSON.stringify(data), function () {

                    });
                };

                if (fs.existsSync('../data/')) {
                    saveData();
                } else {
                    fs.mkDir('../data/', function () {
                        saveData();
                    });
                }
                res.send('success');
            } else {
                res.send('fail');
            }
        });
        var istheremail = false;
        app.get('/setmail', function (req, res) {
            istheremail = !istheremail;
            res.send(istheremail ? "there's mail" : "there's no mail");
        });
        app.get('/getData', function (req, res) {
            res.send(JSON.stringify(module.exports.data));
        });

        server = app.listen(WEBSITE_PORT, function () {
            console.log('API server listening on *:' + WEBSITE_PORT);
        });
    },
    stopServer: function () {
        server.close();
        return true;
    },
    getApp: function () {
        return app;
    },
    getServer: function () {
        return server;
    },
    data: {
        mail: false,
        mailPosition: {front: true, middle: true, back: false},
        flagUp: false,
        doorOpen: false
    }
};
