//static resources test
var socketConn = new WebSocket('ws://localhost:8080/socketHandler');

//print server message to console
socketConn.onmessage = function(event) {
	
	var serverMsg = document.getElementById('serverMsg');
	
	//flip story deck
    if(event.data.startsWith("flipStoryDeck")) {
    	serverMsg.value = "intercepted data";
    	var card = event.data.replace('flipStoryDeck','');
    	console.log(card);
    	$("#storyCard").attr("src",card);
    	return;
    }
	serverMsg.value = event.data;
}

socketConn.onopen = function (event) {
	  socketConn.send("Player attempting to connect"); 
};

function flipStoryDeck() {
	socketConn.send("flipStoryDeck");
}

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