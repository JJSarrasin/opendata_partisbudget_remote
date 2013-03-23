OpenData Partis-budget Remote
============================

Mini application developed during a 12 hours hackaton in Sierre for driving a graphical representation on websocket.

http://make.opendata.ch/wiki/project:partisbudgets

Available commands
==================
- Place the smartphone in direction of the sky: it sends "UP" command
- Place the smartphone in direction of the bottom: it sends "DOWN" command
- Turn the smartphone on the left: it sends "LEFT" command
- Turn the smartphone on the right: it sends "RIGHT" command
- Select a canton in the list: it sends an abbreviation of the canton (i.e "VS" for Valais)

![Screenshot](https://github.com/JJSarrasin/opendata_partisbudget_remote/blob/master/screenshot.png?raw=true)

Minimal server in Node.JS:
```
var app = require('http').createServer(handler)
  , io = require('socket.io').listen(app)
  , fs = require('fs')

app.listen(80);

function handler (req, res) {
  	console.log('handled');
  	fs.readFile(__dirname + '/index.html',
  	function (err, data) {
    	if (err) {
      	res.writeHead(500);
      	return res.end('Error loading index.html');
    	}

    	res.writeHead(200);
    	res.end(data);
  	});
}

io.sockets.on('connection', function (socket) {
	console.log(socket);
  	socket.on('message', function (data) {
    	console.log(data);
    	socket.broadcast.emit(data);
  	});
});
```

Minimal client in Node.JS:
```
<script src="/socket.io/socket.io.js"></script>
<script>
  	var socket = io.connect('http://localhost');

	socket.on('connect', function () {
		socket.send('hi');

    	socket.on('message', function (msg) {
    		console.log(msg);
    	});
  	});
</script>
```
