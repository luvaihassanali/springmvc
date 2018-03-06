<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>COMP3004</title>

<link href='<spring:url value="/resources/css/style.css"/>'
	rel="stylesheet" />
<script type="text/javascript"
	src='<spring:url value="/resources/js/jquery.js"/>'></script>
<script type="text/javascript"
	src='<spring:url value="/resources/js/app.js"/>'></script>

</head>
<body>

	<h1 id="title" class="color1">Welcome to Quest of the Round Table</h1>
	${message}
	<button onclick="changeColor()">Change Color</button>
	<hr />
	<label id="name">Enter name</label>
	<br>
	<textarea rows="1" cols="50" id="clientMsg"></textarea>
	<br>
	<button id="send" onclick="send()">Send</button>
	<button onclick="print()">Print all players</button>
	<button onclick="proof()">Proof</button>
	<button onclick="window.location.href='/view2'">Testing View2</button>
	<button onclick="flipStoryDeck()">Flip story deck</button>
	<br>
	<label>Console</label>
	<br>
	<p>
		<textarea rows="2" cols="50" id="serverMsg" readonly="readonly"></textarea>
	</p>
	<div style="float: right" id="playerHand">
		<div>
			<img id="card1" src="/images/all.png" height="250" width="180"> <img id="card2"
				alt="image" src="/images/all.png" height="250" width="180"> <img id="card3"
				alt="image" src="/images/all.png" height="250" width="180"> <img id="card4"
				alt="image" src="/images/all.png" height="250" width="180"> <img id="card5"
				alt="image" src="/images/all.png" height="250" width="180"> <img id="card6"
				alt="image" src="/images/all.png" height="250" width="180">
		</div>
		<div>
			<img id="card7" src="/images/all.png" height="250" width="180"> <img id="card8"
				alt="image" src="/images/all.png" height="250" width="180"> <img id="card9"
				alt="image" src="/images/all.png" height="250" width="180"> <img id="card10"
				alt="image" src="/images/all.png" height="250" width="180"> <img id="card11"
				alt="image" src="/images/all.png" height="250" width="180"> <img id="card12"
				alt="image" src="/images/all.png" height="250" width="180">


		</div>
	</div>

	<div>
		<img id="storyCard" alt="image" src="/images/all.png" height="300"
			width="200"> <img alt="image" src="/images/all.png"
			height="300" width="200">
	</div>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Story
	Card&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;S.
	Discard Pile
	<div>
		<img alt="image" src="/images/all.png" height="300" width="200">
		<img alt="image" src="/images/all.png" height="300" width="200">
	</div>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Adventure
	Card&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A.
	Discard Pile
</body>
</html>