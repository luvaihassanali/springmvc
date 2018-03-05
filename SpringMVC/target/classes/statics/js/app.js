//static resources test
var socketConn = new WebSocket('ws://localhost:8080/socketHandler');

socketConn.onopen = function (event) {
	  socketConn.send("Client attempting to connect"); 
};

function changeColor() {
   var title = document.getElementById('title');
   if (title.className == 'color1') {
   	title.className = 'color2';
   } else {
   	title.className = 'color1';
   }
}
//send name to server
function send() {
	var clientMsg = document.getElementById('clientMsg');
	if (clientMsg.value) {
		socketConn.send("Name:" + clientMsg.value);
		document.getElementById('name').innerHTML = clientMsg.value + "'s View";
		clientMsg.value = '';
		document.getElementById('send').style.display = "none";
	}
}

//print all clients connected
function print() {
	socketConn.send("Print");
}

//proof of connection
function proof() {
	socketConn.send("Proof");
}

// print server message to console
socketConn.onmessage = function(event) {
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = event.data;
}