var stompClient = null;



function setConnected(connected) {
	document.getElementById('connect').disabled = connected;
	//document.getElementById('disconnect').disabled = !connected;
	document.getElementById('conversationDiv').style.visibility = connected ? 'visible'
			: 'hidden';
	document.getElementById('response').innerHTML = '';
}

function connect() {
	var socket = new SockJS('/info');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		var from = document.getElementById('from').value;
		var text = "joined game";
		stompClient.send("/data/info", {}, JSON.stringify({
			'from' : from,
			'text' : text
		}));
		setConnected(true);
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
	var text = document.getElementById('text').value;
	stompClient.send("/data/info", {}, JSON.stringify({
		'from' : from,
		'text' : text
	}));
}

function showDataOutput(dataOutput) {
	var response = document.getElementById('response');
	var p = document.createElement('p');
	p.style.wordWrap = 'break-word';
	p.appendChild(document.createTextNode(dataOutput.from + ": "
			+ dataOutput.text + " (" + dataOutput.time + ")"));
	response.appendChild(p);
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