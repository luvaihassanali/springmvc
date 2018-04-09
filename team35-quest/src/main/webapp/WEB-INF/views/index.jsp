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
		Enter name: <input type="text" id="enterName" value="Player"> <button id="setAI" onclick="setAI()">AI Player</button>
	</p>
	<button id="send" onclick="send()">Send</button>
	<button id="print" disabled onclick="print()">Print all
		players</button>
	<button id="proof" onclick="proof()">Proof</button>
	<button id="flip" disabled type="button" onclick="flipStoryDeck()">Flip Story Deck</button>
	<button id="rigger" onclick="riggedGame()" style="display:none">Rigged Game</button>
	<button id="riggerAI" onclick="riggedGameAI()" style="display:none">Rigged AI Game</button>
	<br>
	<br>
	<label>Console</label><br>
	<textarea style="float:left;"rows="50" cols="26" id="serverMsg"></textarea>
		<label id="minBid" style="display:none"> Current minimum bid: </label>
		<button id="doneTournie" onclick="doneTournament()" style="display:none"> Done </button>
		<button id="doneQuest" onclick="doneWeaponsQuestSponsor()"
			style="display: none">Done</button>
		<button id="doneEquipment" onclick="doneEquipment()"
			style="display: none">Done</button>
			<button id="dropOut" onclick="dropOutOfTest()" style="display: none"> Drop out</button>
        <div id="sponsorQuest" style="display: none">
		<label><strong>&nbsp&nbsp&nbspDo you want to sponsor quest?</strong></label>
		<button id="acceptQuestSponsor" onclick="acceptSponsorQuest()">Yes</button>
		<button id="denySponsorQuest" onclick="denySponsorQuest()">No</button>
	</div>
	<div id="askTournament" style="display: none">
		<label><strong>&nbsp&nbsp&nbspDo you want to participate in tournament?</strong></label>
		<button id="acceptTournament" onclick="acceptTournament()">Yes</button>
		<button id="denyTournament" onclick="denyTournament()">No</button>
	</div>
	<div id="acceptQuest" style="display: none">
		<label><strong>&nbsp&nbsp&nbspDo you want to participate in quest?</strong></label>
		<button id="acceptQuestParticipate" onclick="acceptQuestParticipate()">Yes</button>
		<button id="denyQuestParticipate" onclick="denyQuestParticipate()">No</button>
	</div> 
		<div id="merlin" style="display: none">
		<label id="merlinPrompt"><strong>&nbsp&nbsp&nbspYou have Merlin in hand, preview stage?</strong></label>
		<button id="merlinYes" onclick="execMerlin()">Yes</button>
		<button style="display:none" id="merlin1stage" onclick="showStage(1)">1</button>
		<button style="display:none"  id="merlin2stage" onclick="showStage(2)">2</button>
		<button style="display:none"  id="merlin3stage" onclick="showStage(3)">3</button>
		<button style="display:none"  id="merlin4stage" onclick="showStage(4)">4</button>
		<button style="display:none" id="merlin5stage" onclick="showStage(5)">5</button>
	</div> 
	<div id="merlinPreview" style="display:none; margin: 0 auto; font-size: 20px; border-style: solid; border-width: thin; position:relative; background-color: white; width: 1200px; height: 350px; z-index: 2; text-align: center;">
Preview stage - Merlin special 
		<div style="float:right; padding-right:43px;">
			<img id="merlincard1" src="/resources/images/all.png" height="250"
				width="180"> <img id="merlincard2" 
				src="/resources/images/all.png" height="250" width="180"> <img
				id="merlincard3"  src="/resources/images/all.png" height="250"
				width="180"> <img id="merlincard4" 
				src="/resources/images/all.png" height="250" width="180"> <img
				id="merlincard5"  src="/resources/images/all.png" height="250"
				width="180"> <img id="merlincard6" 
				src="/resources/images/all.png" height="250" width="180">
		</div>

</div>
<div id="battleScreen"
		style="display:none; margin: 0 auto; font-size: 20px; border-style: solid; border-width: thin; position:relative; background-color: white; width: 1200px; height: 750px; z-index: 2; text-align: center;">
				<strong> Battle Screen </strong><Br><br><br><br>
		<div id="player1Display" style="display:none; float: left; border-style: solid; border-width: thin; font-size:14px;">
<label id="p1info"> player1 name <br> player1 points </label>
			<div>
				<img id="player1Pic" src="/resources/images/all.png" width="100"
					height="150" />
			</div>
			<div>
				<img id="player1WeaponSpot1" src="/resources/images/all.png"
					width="80" height="120" /> <img id="player1WeaponSpot2"
					src="/resources/images/all.png" width="80" height="120" /> <img
					id="player1WeaponSpot3" src="/resources/images/all.png" width="80"
					height="120" />
			</div>
			<div>
				<img id="player1WeaponSpot4" src="/resources/images/all.png"
					width="80" height="120" /> <img id="player1WeaponSpot5"
					src="/resources/images/all.png" width="80" height="120" /> <img
					id="player1WeaponSpot6" src="/resources/images/all.png" width="80"
					height="120" />
			</div>
			<label id="p1_win" style="color: green; display:none;"> <strong>
					WINNER </strong>
			</label> <label id="p1_lose" style="color: red; display: none;"> <strong>
					LOSER </strong>
			</label>
		</div>
		<div id="player2Display" style="display:none; float: left; border-style: solid; border-width: thin; font-size:14px;">
<label id="p2info"> player2 name <br> player2 points </label>
			<div>
				<img id="player2Pic" src="/resources/images/all.png" width="100"
					height="150" />
			</div>
			<div>
				<img id="player2WeaponSpot1" src="/resources/images/all.png"
					width="80" height="120" /> <img id="player2WeaponSpot2"
					src="/resources/images/all.png" width="80" height="120" /> <img
					id="player2WeaponSpot3" src="/resources/images/all.png" width="80"
					height="120" />
			</div>
			<div>
				<img id="player2WeaponSpot4" src="/resources/images/all.png"
					width="80" height="120" /> <img id="player2WeaponSpot5"
					src="/resources/images/all.png" width="80" height="120" /> <img
					id="player2WeaponSpot6" src="/resources/images/all.png" width="80"
					height="120" />
			</div>
			<label id="p2_win" style="color: green; display:none;"> <strong>
					WINNER </strong>
			</label> <label id="p2_lose" style="color: red; display: none;"> <strong>
					LOSER </strong>
			</label>
		</div>
		<div id="player3Display" style="display:none; float: left; border-style: solid; border-width: thin; font-size:14px;">
<label id="p3info"> player3 name <br> player3 points </label>
			<div>
				<img id="player3Pic" src="/resources/images/all.png" width="100"
					height="150" />
			</div>
			<div>
				<img id="player3WeaponSpot1" src="/resources/images/all.png"
					width="80" height="120" /> <img id="player3WeaponSpot2"
					src="/resources/images/all.png" width="80" height="120" /> <img
					id="player3WeaponSpot3" src="/resources/images/all.png" width="80"
					height="120" />
			</div>
			<div>
				<img id="player3WeaponSpot4" src="/resources/images/all.png"
					width="80" height="120" /> <img id="player3WeaponSpot5"
					src="/resources/images/all.png" width="80" height="120" /> <img
					id="player3WeaponSpot6" src="/resources/images/all.png" width="80"
					height="120" />
			</div>
			<label id="p3_win" style="color: green; display:none;"> <strong>
					WINNER </strong>
			</label> <label id="p3_lose" style="color: red; display: none;"> <strong>
					LOSER </strong>
			</label>
		</div>
		<div id="player4Display" style="display:none; float: left; border-style: solid; border-width: thin; font-size:14px;">
<label id="p4info"> player4 name <br> player4 points </label>
			<div>
				<img id="player4Pic" src="/resources/images/all.png" width="100"
					height="150" />
			</div>
			<div>
				<img id="player4WeaponSpot1" src="/resources/images/all.png"
					width="80" height="120" /> <img id="player4WeaponSpot2"
					src="/resources/images/all.png" width="80" height="120" /> <img
					id="player4WeaponSpot3" src="/resources/images/all.png" width="80"
					height="120" />
			</div>
			<div>
				<img id="player4WeaponSpot4" src="/resources/images/all.png"
					width="80" height="120" /> <img id="player4WeaponSpot5"
					src="/resources/images/all.png" width="80" height="120" /> <img
					id="player4WeaponSpot6" src="/resources/images/all.png" width="80"
					height="120" />
			</div>
			<label id="p4_win" style="color: green; display:none;"> <strong>
					WINNER </strong> <br> 
			</label> <label id="p4_lose" style="color: red; display: none;"> <strong>
					LOSER </strong> <br> 
			</label>
		</div>
                  <div style="border-style:none; border-width:thin; width: 800px; height:250px; position:absolute; bottom:0;"> 
     <br> <br>
 <label id="currStageInfo"> Current stage information: </label><br> <br>
		<img id="questPic" src="/resources/images/all.png" width="100"
					height="150" />
<img id="foePic" src="/resources/images/all.png" width="100"
					height="150" />
		<img id="foeWeaponSpot1" src="/resources/images/all.png"
					width="80" height="120" /> <img id="foeWeaponSpot2"
					src="/resources/images/all.png" width="80" height="120" /> <img
					id="foeWeaponSpot3" src="/resources/images/all.png" width="80"
					height="120" />
		<img id="foeWeaponSpot4" src="/resources/images/all.png"
					width="80" height="120" /> <img id="foeWeaponSpot5"
					src="/resources/images/all.png" width="80" height="120" /> <img
					id="foeWeaponSpot6" src="/resources/images/all.png" width="80"
					height="120" />
               </div>
<div style="border-style:none; border-width:thin; width: 250px; height:200px; position:absolute; bottom:0; right:0;"> 
<label id="eliminated"> Players eliminated:</label>
</div>
	        </div>
 <div id="tournieScreen"
		style="display:none; margin: 0 auto; font-size: 20px; border-style: solid; border-width: thin; position:relative; background-color: white; width: 1400px; height: 750px; z-index: 2; text-align: center;">
				<strong> Tournament Screen </strong><Br><br><br><br>
		<div id="player1Displayt" style="display:none; float: left; border-style: solid; border-width: thin; font-size:14px;">
<label id="p1infot"> player1 name <br> player1 points </label>
			<div>
				<img id="player1Pict" src="/resources/images/all.png" width="150"
					height="220" />
			</div>
			<div>
				<img id="player1WeaponSpot1t" src="/resources/images/all.png"
					width="100" height="140" /> <img id="player1WeaponSpot2t"
					src="/resources/images/all.png" width="100" height="140" /> <img
					id="player1WeaponSpot3t" src="/resources/images/all.png" width="100"
					height="140" />
			</div>
			<div>
				<img id="player1WeaponSpot4t" src="/resources/images/all.png"
					width="100" height="140" /> <img id="player1WeaponSpot5t"
					src="/resources/images/all.png" width="100" height="140" /> <img
					id="player1WeaponSpot6t" src="/resources/images/all.png" width="100"
					height="140" />
			</div>
			<label id="p1_wint" style="color: green; display:none;"> <strong>
					WINNER </strong>
			</label> <label id="p1_loset" style="color: red; display: none;"> <strong>
					LOSER </strong>
			</label>
		</div>
		<div id="player2Displayt" style="display:none; float: left; border-style: solid; border-width: thin; font-size:14px;">
<label id="p2infot"> player2 name <br> player2 points </label>
			<div>
				<img id="player2Pict" src="/resources/images/all.png" width="150"
					height="220" />
			</div>
			<div>
				<img id="player2WeaponSpot1t" src="/resources/images/all.png"
					width="100" height="140" /> <img id="player2WeaponSpot2t"
					src="/resources/images/all.png" width="100" height="140" /> <img
					id="player2WeaponSpot3t" src="/resources/images/all.png" width="100"
					height="140" />
			</div>
			<div>
				<img id="player2WeaponSpot4t" src="/resources/images/all.png"
					width="100" height="140" /> <img id="player2WeaponSpot5t"
					src="/resources/images/all.png" width="100" height="140" /> <img
					id="player2WeaponSpot6t" src="/resources/images/all.png" width="100"
					height="140" />
			</div>
			<label id="p2_wint" style="color: green; display:none;"> <strong>
					WINNER </strong>
			</label> <label id="p2_loset" style="color: red; display: none;"> <strong>
					LOSER </strong>
			</label>
		</div>
		<div id="player3Displayt" style="display:none; float: left; border-style: solid; border-width: thin; font-size:14px;">
<label id="p3infot"> player3 name <br> player3 points </label>
			<div>
				<img id="player3Pict" src="/resources/images/all.png" width="150"
					height="220" />
			</div>
			<div>
				<img id="player3WeaponSpot1t" src="/resources/images/all.png"
					width="100" height="140" /> <img id="player3WeaponSpot2t"
					src="/resources/images/all.png" width="100" height="140" /> <img
					id="player3WeaponSpot3t" src="/resources/images/all.png" width="100"
					height="140" />
			</div>
			<div>
				<img id="player3WeaponSpot4t" src="/resources/images/all.png"
					width="100" height="140" /> <img id="player3WeaponSpot5t"
					src="/resources/images/all.png" width="100" height="140" /> <img
					id="player3WeaponSpot6t" src="/resources/images/all.png" width="100"
					height="140" />
			</div>
			<label id="p3_wint" style="color: green; display:none;"> <strong>
					WINNER </strong>
			</label> <label id="p3_loset" style="color: red; display: none;"> <strong>
					LOSER </strong>
			</label>
		</div>
		<div id="player4Displayt" style="display:none; float: left; border-style: solid; border-width: thin; font-size:14px;">
<label id="p4infot"> player4 name <br> player4 points </label>
			<div>
				<img id="player4Pict" src="/resources/images/all.png" width="150"
					height="220" />
			</div>
			<div>
				<img id="player4WeaponSpot1t" src="/resources/images/all.png"
					width="100" height="140" /> <img id="player4WeaponSpot2t"
					src="/resources/images/all.png" width="100" height="140" /> <img
					id="player4WeaponSpot3t" src="/resources/images/all.png" width="100"
					height="140" />
			</div>
			<div>
				<img id="player4WeaponSpot4t" src="/resources/images/all.png"
					width="100" height="140" /> <img id="player4WeaponSpot5t"
					src="/resources/images/all.png" width="100" height="140" /> <img
					id="player4WeaponSpot6t" src="/resources/images/all.png" width="100"
					height="140" />
			</div>
			<label id="p4_wint" style="color: green; display:none;"> <strong>
					WINNER </strong> <br> 
			</label> <label id="p4_loset" style="color: red; display: none;"> <strong>
					LOSER </strong> <br> 
			</label>
		</div>

	        </div>
	<div oncontextmenu="discard();return false;" style="float: right" id="playerHand">
	 <div style="float:right"> <br>
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
				width="180"> <img id="card2" 
				src="/resources/images/all.png" height="250" width="180"> <img
				id="card3"  src="/resources/images/all.png" height="250"
				width="180"> <img id="card4" 
				src="/resources/images/all.png" height="250" width="180"> <img
				id="card5"  src="/resources/images/all.png" height="250"
				width="180"> <img id="card6" 
				src="/resources/images/all.png" height="250" width="180">
		</div>
		<div>
			<img id="card7" src="/resources/images/all.png" height="250"
				width="180"> <img id="card8" 
				src="/resources/images/all.png" height="250" width="180"> <img
				id="card9"  src="/resources/images/all.png" height="250"
				width="180"> <img id="card10" 
				src="/resources/images/all.png" height="250" width="180"> <img
				id="card11"  src="/resources/images/all.png" height="250"
				width="180"> <img id="card12" 
				src="/resources/images/all.png" height="250" width="180">


		</div>
	</div>
<div style="float:right;">
	<div>
		<figure><img id="storyCard"  src="/resources/images/card-back.png"
			height="300" width="200"> <img id="storyCardDiscard"
			src="/resources/images/all.png" height="300" width="200"> <figcaption>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbspStory Cards</figcaption></figure> 
	</div>
	
	<div>
		<figure><img id="adventureCard" src="/resources/images/card-back.png" height="300"
			width="200"> <img id="adventureCardDiscard" src="/resources/images/all.png"
			height="300" width="200"> <figcaption>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbspAdventure Cards&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbspRank</figcaption></figure> 


	</div>
</div>

</body>
</html>