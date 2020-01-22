// require express and other modules
const express = require('express');
const request = require('request');

const app = express();
// Express Body Parser
app.use(express.urlencoded({extended: true}));
app.use(express.json());

// Set Static File Directory
app.use(express.static(__dirname + '/public'));

var net = require('net');
var fs = require("fs");

var lastmsg = "";

var client = net.connect(8080, 'localhost');
client.setEncoding('utf8');

setTimeout(function() {
  console.log("Writing....")
  client.write('Hello from node.js\n');
}, 1000);

client.on('data', function(data) {
  if (lastmsg !== arguments) {
    console.log("Received:", arguments);
    lastmsg = data.toString();
    //var JSONObject = JSON.parse(lastmsg);
    fs.writeFile( "received.json", lastmsg, "utf8", (err) => {
      if (err) throw err;
      console.log("It's saved!");
    });
  }
})

//client.end();

/*
 * HTML Endpoints
 */

app.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  next();
});

app.get('/', function homepage(req, res) {
	res.sendFile(__dirname + '/views/index.html');
});

app.get('/received.json', function json(req, res) {
  res.sendFile(__dirname + '/received.json');
});

app.post('/answers', function json(req, res, next) {
    var username = req.body;
    console.log(username);
    client.write(JSON.stringify(username) + '\n', 'utf8', function(error, data){
      if (error) throw error; 
      console.log("Success sending answer!");
    });
});

app.post('/received.json', function json(req, res, next) {
    var username = req.body;
    console.log(username);
    fs.writeFile( "received.json", "", "utf8", (err) => {
      if (err) throw err;
      console.log("File Cleared!");
    });
});

/**********
 * SERVER *
 **********/

// listen on the port 3000
app.listen(process.env.PORT || 3000, () => {
  	console.log('Express server is up and running on http://localhost:3000/');
});
