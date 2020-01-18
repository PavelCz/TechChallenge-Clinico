// require express and other modules
const express = require('express');

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
//setInterval(function() {
//  console.log("Writing....")
//  client.write('Hello from node.js\n');
//}, 1000);

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

/**********
 * ROUTES *
 **********/

/*
 * HTML Endpoints
 */

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

/*
 * JSON API Endpoints
 */

/*app.get('/api', (req, res) => {
  	// TODO: Document all your api endpoints below as a simple hardcoded JSON object.
  	res.json({
    	message: 'Welcome to my app api!',
    	documentationUrl: '', //leave this also blank for the first exercise
    	baseUrl: '', //leave this blank for the first exercise
    	endpoints: [
      		{method: 'GET', path: '/', description: 'Shows the form to add a book (homepage)'},
      		{method: 'GET', path: '/api', description: 'Describes all available endpoints'},
      		{method: 'GET', path: '/api/profile', description: 'Data about me'},
      		{method: 'GET', path: '/api/books/', description: 'Get All books information'},

      		{method: 'POST', path: '/api/books/', description: 'Add a new books in the database'},
      		{method: 'POST', path: '/api/books/:id', description: 'Update a book based on its ID'},

      		{method: 'DELETE', path: '/api/books/:id', description: 'Delete a book from the database based on its ID'},
      		// TODO: Write other API end-points description here like above
    	]
  	})
});
// TODO:  Fill the values
app.get('/api/profile', (req, res) => {
  	res.json({
    	'name': 'John Doe',
    	'homeCountry': 'Funland',
    	'degreeProgram': 'informatics',//informatics or CSE.. etc
    	'email': 'indacloud@icloud.com',
    	'deployedURLLink': '',//leave this blank for the first exercise
    	'apiDocumentationURL': '', //leave this also blank for the first exercise
    	'currentCity': 'Oktoberfest',
    	'hobbies': [partying, drinking]
  	})
});
*/

/**********
 * SERVER *
 **********/

// listen on the port 3000
app.listen(process.env.PORT || 3000, () => {
  	console.log('Express server is up and running on http://localhost:3000/');
});
