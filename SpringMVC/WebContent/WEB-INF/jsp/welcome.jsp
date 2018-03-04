<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<title>Spring MVC - welcome</title>
<style type="text/css">
body {
	
}
</style>
</head>
<body>
<h1>${message}</h1>
<p><a href="/SpringMVC/">Click here to go to back to index</a></p>
<p><a href="anotherView.html">Click here to go to anotherView view</a></p>
<hr />
<label id="name">Enter name</label>
<br>
<textarea rows="8" cols="50" id="clientMsg"></textarea>
<br>
<button onclick="send()">Send</button>
<button onclick="print()">Print</button>
<button onclick="proof()">Proof</button>
<br>
<label>Dealer</label>
<br>
<textarea rows="8" cols="50" id="serverMsg" readonly="readonly"></textarea>
</body>
</html>