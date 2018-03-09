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

	<h1 id="title" class="color1">Welcome to Quest of The Round Table</h1>
	${message}
	<hr />
	<p id="enterName">
	<label id="name">Enter name</label>
	<textarea rows="1" cols="50" id="clientMsg"></textarea>
	</p>
	
	<button id="send" onclick="send()">Send</button>
	<button id="print" disabled onclick="print()">Print all players</button>
	<button onclick="proof()">Proof</button>
	<button type="button" disabled onclick="flipStoryDeck()">Flip story deck</button>
	<br> <br>
	<label>Console</label>

	<p>
		<textarea rows="2" cols="50" id="serverMsg"></textarea>
	</p>
	<div style="float: right" id="playerHand">
		<div>
			<img id="card1" src="/resources/images/all.png" height="250" width="180"> <img id="card2"
				alt="image" src="/resources/images/all.png" height="250" width="180"> <img id="card3"
				alt="image" src="/resources/images/all.png" height="250" width="180"> <img id="card4"
				alt="image" src="/resources/images/all.png" height="250" width="180"> <img id="card5"
				alt="image" src="/resources/images/all.png" height="250" width="180"> <img id="card6"
				alt="image" src="/resources/images/all.png" height="250" width="180">
		</div>
		<div>
			<img id="card7" src="/resources/images/all.png" height="250" width="180"> <img id="card8"
				alt="image" src="/resources/images/all.png" height="250" width="180"> <img id="card9"
				alt="image" src="/resources/images/all.png" height="250" width="180"> <img id="card10"
				alt="image" src="/resources/images/all.png" height="250" width="180"> <img id="card11"
				alt="image" src="/resources/images/all.png" height="250" width="180"> <img id="card12"
				alt="image" src="/resources/images/all.png" height="250" width="180">


		</div>
	</div>
    <div id="sponsorQuest" style="display:none"><label><strong>Do you want to sponsor quest?</strong></label> <button id="acceptQuestSponsor" onclick="acceptSponsorQuest()">Yes</button> <button id="denySponsorQuest" onclick="denySponsorQuest()">No</button></div>
 	<div>
		<img id="storyCard" alt="image" src="/resources/images/all.png" height="300"
			width="200"> <img alt="image" src="/resources/images/all.png"
			height="300" width="200">
	</div>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Story
	Card&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;S.
	Discard Pile
	<div>
		<img alt="image" src="/resources/images/all.png" height="300" width="200">
		<img alt="image" src="/resources/images/all.png" height="300" width="200">
	</div>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Adventure
	Card&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A.
	Discard Pile
</body>
</html>