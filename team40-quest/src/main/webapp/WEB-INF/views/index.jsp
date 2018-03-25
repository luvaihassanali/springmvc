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
<link rel="icon" type="image/png" href="/resources/favicon.png" />
</head>
<body>
	<p style="float:right;">${message}</>
	<h1 id="title" class="color1">Welcome to Quest of The Round Table</h1>

	<hr />
<div id="statpane" style="text-align:center;">
<div id="p4stat" style="float:right; border-style:solid; width: 272px; height: 150px;">
Player 4: <label id="p4name"></label><br> <br>
Rank: <label id="p4rank"></label><br> <br>
Cards: <label id="p4cards">0</label><br><br> 
Shields: <label id="p4shields">0</label>
</div>
<div  id="p3stat"style="float:right; border-style:solid; width: 272px; height: 150px;">
Player 3:  <label id="p3name"></label><br><br>
Rank: <label id="p3rank"></label><br><br>
Cards:<label id="p3cards">0</label><br><Br>
Shields:<label id="p3shields">0</label>
</div>
<div id="p2stat" style="float:right; border-style:solid; width: 272px; height: 150px;">
Player 2:  <label id="p2name"></label><br><br>
Rank: <label id="p2rank"></label><br><br>
Cards:<label id="p2cards">0</label><br><br>
Shields:<label id="p2shields">0</label>
</div>
<div id="p1stat" style="float:right; border-style:solid; width: 272px; height: 150px;">
Player 1:  <label id="p1name"></label><br><br>
Rank: <label id="p1rank"></label><br><br>
Cards:<label id="p1cards">0</label><br><br>
Shields:<label id="p1shields">0</label>
</div>
</div>
	<p id="nameparagraph">
		Enter name: <input type="text" id="enterName" value="Mickey Mouse"> <button id="setAI" onclick="setAI()">AI Player</button>
	</p>
	<button id="send" onclick="send()">Send</button>
	<button id="print" disabled onclick="print()">Print all
		players</button>
	<button id="proof" onclick="proof()">Proof</button>
	<button id="flip" disabled type="button" onclick="flipStoryDeck()">Flip Story Deck</button>
	<button id="rigger" onclick="riggedGame()" style="display:none">Rigged Game</button>
	<br>
	<br>
	<label>Console</label>

	<div id="battleScreen"
		style="display: none; margin: 0 auto; font-size: 20px; border-style: solid; position:relative; background-color: white; width: 600px; height: 650px; z-index: 2; text-align: center;">
				<label style="">*****************Battle Screen*****************</label> <br>
		<div id="enemyDisplay" style="float: right;">
			<div>
				<img id="enemyPic" src="/resources/images/all.png" width="180"
					height="250" />
			</div>
			<div>
				<img id="enemyWeaponSpot1" src="/resources/images/all.png"
					width="80" height="120" /> <img id="enemyWeaponSpot2"
					src="/resources/images/all.png" width="80" height="120" /> <img
					id="enemyWeaponSpot3" src="/resources/images/all.png" width="80"
					height="120" />
			</div>
			<div>
				<img id="enemyWeaponSpot4" src="/resources/images/all.png"
					width="80" height="120" /> <img id="enemyWeaponSpot5"
					src="/resources/images/all.png" width="80" height="120" /> <img
					id="enemyWeaponSpot6" src="/resources/images/all.png" width="80"
					height="120" />
			</div>
			<label id="f_win" style="color: green; display: none;"> <strong>
					WINNER </strong>
			</label> <label id="f_lose" style="color: red; display: none;"> <strong>
					LOSER </strong>
			</label>
		</div>
		<div id="playerDisplay" style="float: left;">
			<div>
				<img id="playerPic" src="/resources/images/all.png" width="180"
					height="250" />
			</div>
			<div>
				<img id="playerWeaponSpot1" src="/resources/images/all.png"
					width="80" height="120" /> <img id="playerWeaponSpot2"
					src="/resources/images/all.png" width="80" height="120" /> <img
					id="playerWeaponSpot3" src="/resources/images/all.png" width="80"
					height="120" />
			</div>
			<div>
				<img id="playerWeaponSpot4" src="/resources/images/all.png"
					width="80" height="120" /> <img id="playerWeaponSpot5"
					src="/resources/images/all.png" width="80" height="120" /> <img
					id="playerWeaponSpot6" src="/resources/images/all.png" width="80"
					height="120" />
			</div>
			<label id="p_win" style="color: green; display: none;"> <strong>
					WINNER </strong>
			</label> <label id="p_lose" style="color: red; display: none;"> <strong>
					LOSER </strong>
			</label>
		</div>
	</div>
	<p>
		<textarea rows="2" cols="50" id="serverMsg"></textarea>
		<button id="doneQuest" onclick="doneWeaponsQuestSponsor()"
			style="display: none">Done</button>
		<button id="doneEquipment" onclick="doneEquipment()"
			style="display: none">Done</button>
			<button id="dropOut" onclick="dropOutOfTest()" style="display: none"> Drop out</button>
	</p>
	<div oncontextmenu="discard();return false;" style="float: right" id="playerHand">
	 <div style="float:right">
<img id="extra1" src="/resources/images/all.png" height="190" width="133"> 
<img id="extra2" src="/resources/images/all.png" height="190" width="133"> 
<img id="extra3" src="/resources/images/all.png" height="190" width="133"> 
<img id="extra4" src="/resources/images/all.png" height="190" width="133"> 
<img id="extra5" src="/resources/images/all.png" height="190" width="133"> 
<img id="extra6" src="/resources/images/all.png" height="190" width="133"> 
<img id="extra7" src="/resources/images/all.png" height="190" width="133"> 
<img id="extra8" src="/resources/images/all.png" height="190" width="133"> 
				
               </div>
		<div>
			<img id="card1" src="/resources/images/all.png" height="250"
				width="180"> <img id="card2" alt="image"
				src="/resources/images/all.png" height="250" width="180"> <img
				id="card3" alt="image" src="/resources/images/all.png" height="250"
				width="180"> <img id="card4" alt="image"
				src="/resources/images/all.png" height="250" width="180"> <img
				id="card5" alt="image" src="/resources/images/all.png" height="250"
				width="180"> <img id="card6" alt="image"
				src="/resources/images/all.png" height="250" width="180">
		</div>
		<div>
			<img id="card7" src="/resources/images/all.png" height="250"
				width="180"> <img id="card8" alt="image"
				src="/resources/images/all.png" height="250" width="180"> <img
				id="card9" alt="image" src="/resources/images/all.png" height="250"
				width="180"> <img id="card10" alt="image"
				src="/resources/images/all.png" height="250" width="180"> <img
				id="card11" alt="image" src="/resources/images/all.png" height="250"
				width="180"> <img id="card12" alt="image"
				src="/resources/images/all.png" height="250" width="180">


		</div>
	</div>
	<div id="sponsorQuest" style="display: none">
		<label><strong>Do you want to sponsor quest?</strong></label>
		<button id="acceptQuestSponsor" onclick="acceptSponsorQuest()">Yes</button>
		<button id="denySponsorQuest" onclick="denySponsorQuest()">No</button>
	</div>
	<div id="acceptQuest" style="display: none">
		<label><strong>Do you want to participate in quest?</strong></label>
		<button id="acceptQuestParticipate" onclick="acceptQuestParticipate()">Yes</button>
		<button id="denyQuestParticipate" onclick="denyQuestParticipate()">No</button>
	</div>
	<div>
		<img id="storyCard" alt="image" src="/resources/images/card-back.png"
			height="300" width="200"> <img id="storyCardDiscard"
			src="/resources/images/all.png" height="300" width="200">
	</div>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Story
	Card&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;S.
	Discard Pile
	<div>
		<img id="adventureCard" src="/resources/images/card-back.png" height="300"
			width="200"> <img id="adventureCardDiscard" src="/resources/images/all.png"
			height="300" width="200">
	</div>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Adventure
	Card&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A.
	Discard Pile
</body>
</html>