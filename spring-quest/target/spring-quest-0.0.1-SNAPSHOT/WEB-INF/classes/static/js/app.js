var stompClient = null;



function setConnected(connected) {
	document.getElementById('connect').disabled = connected;
}

function connect() {
	var socket = new SockJS('/info');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		var from = document.getElementById('from').value;
		var data = "joined game";
		stompClient.send("/data/info", {}, JSON.stringify({
			'from' : from,
			'data' : data
		}));
		setConnected(true);
		document.getElementById('title').innerHTML = "Welcome to Quest of The Round Table - " + document.getElementById('from').value + "'s View";
		console.log('Connected: ' + frame);	
		stompClient.subscribe('/topic/gameInfo', function(dataOutput) {
			showDataOutput(JSON.parse(dataOutput.body));
		});
	});
	document.getElementById('serverMsg').value = "Waiting for other players"
}

function disconnect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	setConnected(false);
	console.log("Disconnected");
}

function sendData() {
	var from = document.getElementById('from').value;
	var data = document.getElementById('data').value;
	stompClient.send("/data/info", {}, JSON.stringify({
		'from' : from,
		'data' : data
	}));
}

function showDataOutput(dataOutput) {
	console.log("Data recieved: <" + dataOutput.data + "> from <" + dataOutput.from + ">");
}



//flip story card
function flipStoryDeck() {

}
// static resource test, changes title back and forth from red and black
function changeColor() {
	var title = document.getElementById('title');
	if (title.className == 'color1') {
		title.className = 'color2';
	} else {
		title.className = 'color1';
	}
}

// send
function send() {

}
// print
function print() {

}
// proof
function proof() {

}