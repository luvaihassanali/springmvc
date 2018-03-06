<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html><html>
<head>
<title>Welcome</title>
<script type="text/javascript">
var socketConn = new WebSocket('ws://localhost:8080/socketHandler');

function getMessage() {
			  socketConn.send("getMessage"); 
}

</script>
<style type="text/css">
body {

}
</style>
</head>
<body>
<button onclick="getMessage()">Get message</button>
${message}
</body>
</html>