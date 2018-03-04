<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Web Socket Ex</title>
<script type="text/javascript">
	//Open the web socket connection to the server
	var socketConn = new WebSocket('ws://localhost:8080/socketHandler');

	//Send Message
	function send() {
		var clientMsg = document.getElementById('clientMsg');
		if (clientMsg.value) {
			socketConn.send(clientMsg.value);
			clientMsg.value = '';
		}
	}

	// Recive Message
	socketConn.onmessage = function(event) {
		var serverMsg = document.getElementById('serverMsg');
		serverMsg.value = event.data;
		socketConn.send("Thanks");
	}
</script>
</head>
<body>
   <h1>Spring MVC WebSocket</h1>
   <hr />
   <label>Message</label>
   <br>
   <textarea rows="8" cols="50" id="clientMsg"></textarea>
   <br>
   <button onclick="send()">Send</button>
   <br>
   <label>Response from Server</label>
   <br>
   <textarea rows="8" cols="50" id="serverMsg" readonly="readonly"></textarea>
</body>
</html>