<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" type="image/png" href="/resources/favicon.png" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>COMP3004</title>

<link href='<spring:url value="/resources/css/style.css"/>'
	rel="stylesheet" />
<script type="text/javascript"
	src='<spring:url value="/resources/js/jquery.js"/>'></script>
<script type="text/javascript"
	src='<spring:url value="/resources/js/app.js"/>'></script>
<script type="text/javascript"
	src='<spring:url value="/resources/js/stomp.js"/>'></script>
<script type="text/javascript"
	src='<spring:url value="/resources/js/sockjs-0.3.4.js"/>'></script>

</head>
<body onload="disconnect()">
	<h1 id="title" class="color1">Welcome to Quest of the Round Table</h1>
	${message}
	<button onclick="changeColor()">Change Color</button>
	<hr />


	<div>
		<div>
			<input type="text" id="from" placeholder="Choose a nickname" />
		</div>
		<br />
		<div>
			<button id="connect" onclick="connect();">Connect</button>
			<button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button>
			<button disabled="disabled" onclick="print()">Print all players</button>
			<button disabled="disabled" onclick="proof()">Proof</button>
			<button type="button" disabled onclick="flipStoryDeck()">Flip story deck</button>
		</div>
		<br />
		<div id="conversationDiv">
			<input type="text" id="text" placeholder="Write a message..." />
			<button id="sendMessage" onclick="sendMessage();">Send</button>
			<p id="response"></p>
		</div>
	</div>
	<label>Console</label>
	<textarea rows="2" cols="50" id="serverMsg" readonly="readonly"></textarea>
	<div style="float: right" id="playerHand">
		<div>
			<img id="card1" src="/resources/images/all.png" height="250"
				width="180"> <img id="card2" src="/resources/images/all.png"
				height="250" width="180"> <img id="card3"
				src="/resources/images/all.png" height="250" width="180"> <img
				id="card4" src="/resources/images/all.png" height="250" width="180">
			<img id="card5" src="/resources/images/all.png" height="250"
				width="180"> <img id="card6" src="/resources/images/all.png"
				height="250" width="180">
		</div>
		<div>
			<img id="card7" src="/resources/images/all.png" height="250"
				width="180"> <img id="card8" src="/resources/images/all.png"
				height="250" width="180"> <img id="card9"
				src="/resources/images/all.png" height="250" width="180"> <img
				id="card10" src="/resources/images/all.png" height="250" width="180">
			<img id="card11" src="/resources/images/all.png" height="250"
				width="180"> <img id="card12" src="/resources/images/all.png"
				height="250" width="180">


		</div>
	</div>

	<div>
		<img id="storyCard" src="/resources/images/all.png" height="300"
			width="200"> <img src="/resources/images/all.png" height="300"
			width="200">
	</div>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Story
	Card&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;S.
	Discard Pile
	<div>
		<img src="/resources/images/all.png" height="300" width="200"> <img
			src="/resources/images/all.png" height="300" width="200">
	</div>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Adventure
	Card&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A.
	Discard Pile
</body>
</html>