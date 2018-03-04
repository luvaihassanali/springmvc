<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Web Socket Ex</title>
<script type="text/javascript">
	//Open the web socket connection to the server
	var socketConn = new WebSocket('ws://localhost:8080/socketHandler');
	
	socketConn.onopen = function (event) {
		  socketConn.send("Client attempting to connect"); 
	};
	//send name to server
	function send() {
		var clientMsg = document.getElementById('clientMsg');
		if (clientMsg.value) {
			socketConn.send("Name:" + clientMsg.value);
			document.getElementById('name').innerHTML = clientMsg.value + "'s View";
			clientMsg.value = '';
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
</script>
</head>
<body>
   <h1>Spring MVC WebSocket</h1>
   <hr />
   <label id="name">Enter name</label>
   <br>
   <textarea rows="8" cols="50" id="clientMsg"></textarea>
   <br>
   <button onclick="send()">Send</button>
   <button onclick="print()">Print</button>
   <button onclick="proof()">Proof</button>
   <button onclick="window.location.href='/view2'">View2</button>
   <br>
   <label>Dealer</label>
   <br>
   <textarea rows="8" cols="50" id="serverMsg" readonly="readonly"></textarea>
</body>
</html>