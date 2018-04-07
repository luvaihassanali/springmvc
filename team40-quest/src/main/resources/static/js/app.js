var isAI = false;
var strategyType = "";
var PlayerName = "";
var serverMsg = document.getElementById('serverMsg');
var socketConn = new WebSocket('ws://localhost:8080/socketHandler');
var storyCardFaceUp;
var stageTracker = 0;
var totalStages;
var questSetupCards = [];
var weapons = [];
var tournieEquipment = []
var battleEquipment = [];
var testBids = [];
var questInfo;
var foeInfo;
var participantInfo;
var testInfo;
var roundInitiater = "";
var initSet = false;
var currentPlayerInfo = "";
var currentPlayerInfoBids = "";
var currentPlayerBids = 0;
var currentPlayerPts = 0;
var currentStage = 0;
var sponsor = "";
var numCards = 0;
var whichEvent = "";
var sponsorDiscardTracker = 0;
var stageEvents;
var minBid = 0;
var testResult = false;
var testTracker = 0;
var wildCardStageInc = false;
var serverMsg = document.getElementById('serverMsg');
var allBattleEquipment = [];
var allQuestInfo = [];
var allPlayerPoints = [];
var inTournie = false;
var playerTEquipArr;
// when connection is initiated
socketConn.onopen = function(event) {};

var handCardID;
var handCardSRC;

$( document ).ready(function() {
    console.log( "ready!" );

	var card1id = document.getElementById("card1").id;
	var card2id = document.getElementById("card2").id;
	var card3id = document.getElementById("card3").id;
	var card4id = document.getElementById("card4").id;
	var card5id = document.getElementById("card5").id;
	var card6id = document.getElementById("card6").id;
	var card7id = document.getElementById("card7").id;
	var card8id = document.getElementById("card8").id;
	var card9id = document.getElementById("card9").id;
	var card10id = document.getElementById("card10").id;
	var card11id = document.getElementById("card11").id;
	var card12id = document.getElementById("card12").id;
	var extra1id = document.getElementById("extra1").id;
	var extra2id = document.getElementById("extra2").id;
	var extra3id = document.getElementById("extra3").id;
	var extra4id = document.getElementById("extra4").id;
	var extra5id = document.getElementById("extra5").id;
	var extra6id = document.getElementById("extra6").id;
	var extra7id = document.getElementById("extra7").id;
	var extra8id = document.getElementById("extra8").id;
	var imageArrayID = [ card1id, card2id, card3id, card4id, card5id, card6id,
			card7id, card8id, card9id, card10id, card11id, card12id, extra1id,
			extra2id, extra3id, extra4id, extra5id, extra6id, extra7id,
			extra8id ];
	handCardID = imageArrayID;
	var card1 = document.getElementById("card1").src;
	var card2 = document.getElementById("card2").src;
	var card3 = document.getElementById("card3").src;
	var card4 = document.getElementById("card4").src;
	var card5 = document.getElementById("card5").src;
	var card6 = document.getElementById("card6").src;
	var card7 = document.getElementById("card7").src;
	var card8 = document.getElementById("card8").src;
	var card9 = document.getElementById("card9").src;
	var card10 = document.getElementById("card10").src;
	var card11 = document.getElementById("card11").src;
	var card12 = document.getElementById("card12").src;
	var extra1 = document.getElementById("extra1").src;
	var extra2 = document.getElementById("extra2").src;
	var extra3 = document.getElementById("extra3").src;
	var extra4 = document.getElementById("extra4").src;
	var extra5 = document.getElementById("extra5").src;
	var extra6 = document.getElementById("extra6").src;
	var extra7 = document.getElementById("extra7").src;
	var extra8 = document.getElementById("extra8").src;
	var imageArray = [ card1, card2, card3, card4, card5, card6, card7, card8,
			card9, card10, card11, card12, extra1, extra2, extra3, extra4,
			extra5, extra6, extra7, extra8 ];
	handCardSRC = imageArray;
});
// messages from server received here
socketConn.onmessage = function(event) {
    var serverMsg = document.getElementById('serverMsg');
	
    //replace test cards if drop out
    if (event.data.startsWith("replaceTestCards")) {
    	replaceTestCards(event.data.replace("replaceTestCards",""));
    }
    //test winner
    if (event.data.startsWith("testWinner")) {
    	var winner = event.data.replace("testWinner","");
    	serverMsg.value += " " + winner + " won the test, going to next stage";
    }
    //lost battle
    if (event.data == "LostBattle") {
    	var loserName = event.data.replace("LostBattle","");
    	serverMsg.value = "Player " + loserName + " lost battle, wait for quest to finish";
    }
    //get all player points for stage
    if (event.data.startsWith("playerPointString")) {
    	var temp = event.data.replace("playerPointString","");
    	console.log("PLAYER POINT STRING-------------------------");
    	console.log(temp);
    	temp = temp.split(";");
    	for(var i=0; i<temp.length; i++) {
    		temp[i] = temp[i].split("#");
    	}
    	 allPlayerPoints = temp;
    	console.log("SHOWING BATTLE SCREEN");
    	console.log(stageTracker);
    	
		document.getElementById("battleScreen").style.display = "block";
    	//console.log(storyCardFaceUp);
    	$("#questPic").attr("src", "http://localhost:8080" + storyCardFaceUp.stringFile);
    	//console.log(storyCardFaceUp.stringFile);
    	console.log(questSetupCards);
    	console.log(stageTracker);
    	var currFoeName = questSetupCards[stageTracker][0];
    	console.log(currFoeName);
    	var currFoePts = 0;
    	var currFoeLink = getLinkFromName(currFoeName);
    	//console.log(currFoeLink);
    	for(var i=0; i<foeInfo.length; i++) {
    		if(foeInfo[i][0] == currFoeName) {
    			var currFoePts = foeInfo[i][1];
    		}
    	}
    	document.getElementById("currStageInfo").innerText = "Current stage information: " + currFoeName + " with points: " + currFoePts;
    	$("#foePic").attr("src", "http://localhost:8080" + currFoeLink);
    	//console.log("current foe name: " + currFoeName);
    	//console.log("current foe points: " + currFoePts);
    	var foeSlot1 =document.getElementById('foeWeaponSpot1').id;
    	var foeSlot2 = document.getElementById('foeWeaponSpot2').id;
    	var foeSlot3 = document.getElementById('foeWeaponSpot3').id;
    	var foeSlot4 = document.getElementById('foeWeaponSpot4').id;
    	var foeSlot5 = document.getElementById('foeWeaponSpot5').id;
    	var foeSlot6 = document.getElementById('foeWeaponSpot6').id;
    	var foeSlots = [foeSlot1, foeSlot2, foeSlot3, foeSlot4, foeSlot5, foeSlot6];
    	console.log(questSetupCards);
    	console.log(stageTracker);
    	if(questSetupCards[stageTracker].length == 1) {
    		
    	} else {
    		var currentFoeWeapons = questSetupCards[stageTracker];
    		console.log(currentFoeWeapons);
    	
    	
    		var currentFoeWeaponLinks = [];
    		for(var i=1; i<currentFoeWeapons.length; i++) {
    			currentFoeWeaponLinks.push(getLinkFromName(currentFoeWeapons[i]));
    	//		console.log(getLinkFromName(currentFoeWeapons[i]));
    		}
    		for(var i=0; i<currentFoeWeaponLinks.length; i++) {
    			var changeImageId = "#" + foeSlots[i];
    		//	console.log(changeImageId);
    			$(changeImageId).attr("src", "http://localhost:8080" + currentFoeWeaponLinks[i]);
    		}
    	}
    	allPlayerPoints.pop();
    	console.log(allPlayerPoints);
    	console.log(allPlayerPoints.length);
    	for(var i=0; i<allPlayerPoints.length; i++) {
    		console.log("starting loop");
    		var base_id = "player" + (i+1);
    		var primitive_id = "p" + (i+1);
    		var playerDiv = base_id + "Display";
    		var playerPic = "#" + base_id + "Pic";
    		var playerPicLink = allPlayerPoints[i][2];
    		var playerPts = allPlayerPoints[i][1];
    		var playerName = allPlayerPoints[i][0];
    		var infoPane = primitive_id + "info";
    		//console.log(playerDiv);
    		//console.log(infoPane);
    		//console.log(playerName);
    		//console.log(playerPts);
    		//console.log(playerPicLink);
    		document.getElementById(playerDiv).style.display = "block";
    		document.getElementById(infoPane).innerText = playerName + " with points: " + playerPts;
    		$(playerPic).attr("src", "http://localhost:8080" + playerPicLink);
    		console.log(allQuestInfo);
    		var playerInfo = allQuestInfo[i+1];
    		var playerInfo2;
    		var a;
    		if(allQuestInfo[0].hasOwnProperty("questSetupCards")) {
    			a=0;
    		} else { var a = -1; }
    		for(a; a<allQuestInfo.length; a++) {
    			if(a+1 == allQuestInfo.length) break;
    			if(allQuestInfo[a+1].name == playerName && allQuestInfo[a+1].stages == stageTracker) {
    				playerInfo2 = allQuestInfo[a+1];
    			}
    		}

    		console.log(playerInfo2);
    		var weaponArr = playerInfo2.equipment_info;
    		console.log(weaponArr);
    		for(var b=0; b<weaponArr.length; b++) {
    			weaponArr[b] = getLinkFromName(weaponArr[b]);
    			var picId = "#" + base_id + "WeaponSpot" + (b+1);
    			$(picId).attr("src", "http://localhost:8080" + weaponArr[b]);
    			//console.log(picId); console.log(weaponArr[b]);
    		}
    		var winid = primitive_id + "_win";
    		var loseid = primitive_id + "_lose";
    		console.log(playerName + playerPts); console.log(currFoePts);
    		if(parseInt(playerPts) >= parseInt(currFoePts)) {
    			document.getElementById(winid).style.display = "block";
    			document.getElementById(loseid).style.display = "none";
    		} else {
    			document.getElementById(winid).style.display = "none";
    			document.getElementById(loseid).style.display = "block";
    			document.getElementById("eliminated").innerText += "\n" + playerName;
    		}
    		    		
    	}

    	setTimeout(function(){ 
    		document.getElementById("battleScreen").style.display = "none";
    		console.log("REMOVING BATTLE SCREEN");
    		var serverMsg = document.getElementById("serverMsg");
    		serverMsg.value += " Battle over, wait for next player";
    		//clear images
    		$("#questPic").attr("src", "http://localhost:8080/resources/images/all.png");
    		$("#foePic").attr("src", "http://localhost:8080/resources/images/all.png");
    		for(var i=0; i<foeSlots.length; i++) {
    			$("#"+foeSlots[i]).attr("src", "http://localhost:8080/resources/images/all.png");
    		}
    		document.getElementById("eliminated").innerText = " Players eliminated:";
    		for(var i=0; i<allPlayerPoints.length; i++) {
    			var base_id_remove = "player" + (i+1);
        		var primitive_id_remove = "p" + (i+1);
        		var playerDiv_remove = base_id_remove + "Display";
        		document.getElementById(playerDiv_remove).style.display = "none";
        		
        		var playerPic_remove = "#" + base_id_remove + "Pic";
        		$(playerPic_remove).attr("src", "http://localhost:8080/resources/images/all.png");
        		var playerInfo;
            	for(var j=0; j<6; j++) { 
            		var id = "#player" + (i+1) + "WeaponSpot" + (j+1);
            		$(id).attr("src", "http://localhost:8080/resources/images/all.png");
            	}
    		}
    	}, 10000);

    	console.log(foeInfo);
    	console.log(testInfo);
    	console.log("battle screen stage tracker: " + stageTracker);    	
    	console.log("end battlescreen info");
    }
    // get all player quest info
    if (event.data.startsWith("allPlayerQuestInfo")) {
    	var temp = event.data.replace("allPlayerQuestInfo","");
    	temp = JSON.parse(temp);
    	allQuestInfo = temp;
    }
    // show stages
    if (event.data == "showStages") {
    	showStages();
    }
	// get AI commands
	if (event.data.startsWith("AI")) {
		parseAICommand(event.data);
	}

	if (event.data == "undisableFlip") {
		document.getElementById('flip').disabled = false;
		serverMsg.value = "It's your turn, press flip story deck button to continue"
		if (isAI == true) {
			document.getElementById('flip').click();
		}
	}
	

	//reset stage tracker for new quest
	if (event.data == "resetStageTracker") {
		console.log("resetting stage tracker");
		stageTracker = 0;
	}
	// ask to sponsor quest
	if (event.data == "sponsorQuest") {
		getSponsor();
	}

	//tournament
	if(event.data == "participateTournament") {
		getTournie();
	}
	// participation in quest question
	if (event.data == "AskToParticipate") {
		getParticipants();
	}

	
	// let others know of participation
	if (event.data.startsWith("AcceptedToParticipate")) {
		var pName = event.data.replace("AcceptedToParticipate","");
		serverMsg.value = "Player " + pName + " accepted to participate in quest";
	}
	if (event.data.startsWith("DeclinedToParticipate")) {
		var pName = event.data.replace("DeclinedToParticipate","");
		serverMsg.value = "Player " + pName + " declined to participate in quest";
	}
	if(event.data.startsWith("AcceptedTournie")) {
		var pname = event.data.replace("AcceptedTournie","");
		serverMsg.value += " Player " + pname + " accepted to participate in tournament";
	}
	if(event.data.startsWith("DeclinedTournie")) {
		var pname = event.data.replace("DeclinedTournie","");
		serverMsg.value += " Player " + pname + " declined to participate in tournament";
		
	}
	// ready to start quest
	if (event.data == "ReadyToStartQuest") {
		console.log("READY TO START QUEST");
	}

	// no participants
	if (event.data == "NoParticipants") {
		serverMsg.value = "No one chose to play in quest, wait for sponsor to pick up cards...";
	}

	// no sponsors
	if (event.data == "NoSponsors") {
		serverMsg.value = "No one sponsored quest, wait for next player - ";
	}

	// when quest over
	if (event.data == "QuestOverWaitForSponsor") {
		serverMsg.value += "\nQuest over, wait for sponsor to pick up cards - ";
		questInfo = [];
		foeInfo = [];
	}
	// getting shields
	if (event.data.startsWith("Getting")) {
		console.log("here");
		serverMsg.value = event.data;
		getCurrHand();
		console.log(handCardSRC);
	
	}
	// pick up cards used for sponsor
	if (event.data.startsWith("SponsorPickup")) {
		serverMsg.value = "Replacing cards used to sponsor quest RIGHT CLICK TO DISCARD EXTRA to move on\n";
		var temp = event.data.replace("SponsorPickup", "");
		sponsorPickup(temp);
	}
	
	//get tournament information
	if(event.data.startsWith("tournieInfo")) {
		console.log(event.data);
		if(event.data.includes("tournament_info")) {
			var data = event.data.replace("tournieInfo", "");
			var data = JSON.parse(data);
			console.log(data);
			playerTEquipArr = data;
		}
		if(event.data.startsWith("tournieInfoPts")) {
			var data = event.data.replace("tournieInfoPts", "");
			var data = data.split(";");
			for(var i=0; i<data.length; i++) {
				data[i] = data[i].split("#");
			}
			console.log(data);
		}
	}
	if(event.data.startsWith("contestantInfo")) {
		var data = event.data.replace("contestantInfo","");
		data = data.split(";");
		for(var i=0; i<data.length; i++) {
			data[i] = data[i].split("#");
		}
		console.log(data);
		data.pop();
		console.log(data);
		var playerTournieArr = data;
    	console.log("SHOWING TOURNAMENT SCREEN");
		document.getElementById("tournieScreen").style.display = "block";
		console.log(playerTournieArr.length);
		for(var i=0; i<playerTournieArr.length; i++) {
			console.log("starting loop");
			var base_id = "player" + (i+1);
    		var primitive_id = "p" + (i+1);
    		var playerDiv = base_id + "Displayt";
    		var playerPic = "#" + base_id + "Pict";
    		var playerPicLink = playerTournieArr[i][2];
    		var playerPts = playerTournieArr[i][1];
    		var playerName = playerTournieArr[i][0];
    		var infoPane = primitive_id + "infot";
    		document.getElementById(playerDiv).style.display = "block";
    		document.getElementById(infoPane).innerText = playerName + " with points: " + playerPts;
    		$(playerPic).attr("src", "http://localhost:8080" + playerPicLink);
    		console.log(playerTEquipArr);
    		console.log(playerName);
    		for(var j=0; j<playerTEquipArr.length; j++) {
    			if(playerTEquipArr[j].name === playerName) {
    				var tequip = playerTEquipArr[j].tournament_info;
    			}
    		}
    		console.log(tequip);
    		for(var k=0; k<tequip.length; k++) {
    			var weaponLink = getLinkFromName(tequip[k]);
    			var picId = "#" + base_id + "WeaponSpot" + (k+1) + "t";
    			$(picId).attr("src", "http://localhost:8080" + weaponLink);
    		}
    		var winid = primitive_id + "_wint";
    		var loseid = primitive_id + "_loset";
    		if(playerName == playerTournieArr[0][0]) {
    			document.getElementById(winid).style.display = "block";
    			document.getElementById(loseid).style.display = "none";
    		} else {
    		if(playerPts == playerTournieArr[0][1]) {
    			document.getElementById(winid).style.display = "block";
    			document.getElementById(loseid).style.display = "none";
    		} else {
    			document.getElementById(winid).style.display = "none";
    			document.getElementById(loseid).style.display = "block";
    		}
    		}
		}
		
		setTimeout(function(){ 
    		document.getElementById("tournieScreen").style.display = "none";
    		console.log("REMOVING TOURNAMENT SCREEN");
    		for(var i=0; i<playerTournieArr.length; i++) {
    			var base_id_remove = "player" + (i+1);
        		var primitive_id_remove = "p" + (i+1);
        		var playerDiv_remove = base_id_remove + "Displayt";
        		document.getElementById(playerDiv_remove).style.display = "none";
        		
        		var playerPic_remove = "#" + base_id_remove + "Pict";
        		$(playerPic_remove).attr("src", "http://localhost:8080/resources/images/all.png");
            	for(var j=0; j<6; j++) { 
            		var id = "#player" + (i+1) + "WeaponSpot" + (j+1) + "t";
            		$(id).attr("src", "http://localhost:8080/resources/images/all.png");
            	}
    		}
    		var serverMsg = document.getElementById("serverMsg");
    		serverMsg.value += " Tournament over, wait for next player";
    	}, 10000);

	}

    // choosing equipment for tournament
	if (event.data == "ChooseEquipmentTournie") {
		chooseEquipmentTournie();
	}
	
	// choosing equipment for battle
	if (event.data == "ChooseEquipment") {
		chooseEquipment();
	}

	// get current participant info
	if (event.data.startsWith("currentParticipantInfo")) {
		parseParticipantInfo(event);
	}
	if(event.data.startsWith("whoBidded")) {
		var bidder = event.data.replace("whoBidded", "");
		bidder = bidder.split("#");
		var serverMsg = document.getElementById("serverMsg");
		serverMsg.value +=  " " + bidder[0] + " just placed " + bidder[1] + " bids";
	}
	//update min bids
	if (event.data.startsWith("updateMinBid")) {
		var newBid = event.data.replace("updateMinBid","");
		minBid = parseInt(newBid);
		console.log(storyCardFaceUp);
		if(minBid == 0) minBid = 3;
		testInfo[0][1] = minBid;
		console.log("UPDATING MINI BID" + minBid);
	}
	//get current player bids
	if (event.data.startsWith("currentPlayerBids")) {
		var bids = event.data.replace("currentPlayerBids","");
		currentPlayerInfoBids = bids.split(";");
		currentPlayerBids = currentPlayerInfoBids[0];
		console.log(currentPlayerBids);
		if (PlayerName == sponsor) {
			stageCounter = 0;
			sponsor = "";
		}

	}
	// get current player pts
	if (event.data.startsWith("currentPlayerPoints")) {
		var pts = event.data.replace("currentPlayerPoints", "");
		currentPlayerInfo = pts;
		console.log(currentPlayerInfo);
		currentPlayerInfo = currentPlayerInfo.split(";");
		currentPlayerInfo[currentPlayerInfo.length-2] = currentPlayerInfo[currentPlayerInfo.length-2].split("#");
		allBattleEquipment.push(currentPlayerInfo);
	}

	// get foe info
	if (event.data.startsWith("FoeInfo")) {
		parseCurrentFoeInfo(event);
	}

	// get test info
	if (event.data.startsWith("TestInfo")) {
		parseTestInfo(event);
	}

	// new round
	if (event.data == "NextRound") {
		console.log("next round " + stageCounter);
	}

	// get card pick up on starting stage
	if (event.data.startsWith("pickupBeforeStage")) {
		pickupBeforeStage(event);
	}

	// increase stage counter
	if (event.data == "incStage") {
		stageTracker++;

	}
	
	if(event.data.startsWith("questSetupCards")) {
		parseQuestInfo(event);
	}
	//get stages
	if(event.data.startsWith("questSetupInProgress")) {
		var tempStages = parseInt(event.data.replace("questSetupInProgress",""));
		totalStages = tempStages;
		setupQuestRound();
	}
	
	//notify sponsor of participants choosing equipment
    if(event.data == "ParticipantsChoosing") {
    	serverMsg.value = "Participants are choosing equipment or bids, please wait";
    }
	//notify participants of quest setup
	if(event.data.startsWith("questIsBeingSetup")) {
		var sponsorName = event.data.replace("questIsBeingSetup", "");
		serverMsg.value = "Player " + sponsorName + " is setting up quest, please wait";
	}
	if(event.data.startsWith("declinedToSponsor")) {
		var notSponsorName = event.data.replace("declinedToSponsor","");
		serverMsg.value = "Player " + notSponsorName + " is declined to sponsor quest, please wait";
	}
	// pick up x cards
	if (event.data.startsWith("PickupCards")) {
		PickupCards(event.data);
	}

	// flip story deck
	if (event.data.startsWith("flipStoryDeck")) {
		serverMsg.value = "Flipping card from Story Deck (wait for other players) -  ";
		var card = event.data.replace('flipStoryDeck', '');
		card = JSON.parse(card);
		storyCardFaceUp = card;
		totalStages = storyCardFaceUp.stages;
		$("#storyCard").attr("src", "http://localhost:8080" + card.stringFile);
		arrangeHand();

	}
	
	// set hand
	if (event.data.startsWith("setHand")) {
		setHand();
	}

	// if game is full - deny message
	if (event.data.startsWith("Too many players")) {
		serverMsg.value = event.data;
		document.getElementById("send").disabled = true;
		document.getElementById("proof").disabled = true;
	}

	// show rig button
	if (event.data == "showRigger") {
		document.getElementById("rigger").style.display = "block";
		document.getElementById("setAI").style.display = "none";
	}
	// get all player names
	if (event.data.startsWith("clientsString")) {
		var clientString = event.data.replace('clientsString', '');
		serverMsg.value = clientString;
	}

	// update stat pane
	if (event.data.startsWith("updateStats")) {
		getStats();
	}

	// get proof of enrollment
	if (event.data.startsWith("You are")) {
		serverMsg.value = event.data;
	}
	// welcome message
	if (event.data.startsWith("Welcome")) {
		serverMsg.value = event.data;
	}

	// wait
	if (event.data == "wait") {
		serverMsg.value = "Wait for other players to finish";
	}
	// game started
	if (event.data == "GameReadyToStart") {
		startGame();
	}
}

function replaceTestCards(eventData) {
	var replacementCards = eventData.split(";");
	console.log(replacementCards);
	replacementCards.pop(); replacementCards.shift(); //popping twice to act as discard for given stage card
	for(var i=0; i<replacementCards.length; i++) {
		console.log(replacementCards[i]);
		replacementCards[i] = replacementCards[i].substr(1);
		replacementCards[i] = replacementCards[i].substr(0, replacementCards[i].length-1);
		console.log(replacementCards[i]);
		replacementCards[i] = getLinkFromName(replacementCards[i]);
		console.log(replacementCards[i]);
	}
	for (var i = 0; i < handCardID.length; i++) {
		if (handCardSRC[i] == "http://localhost:8080/resources/images/all.png") {
			var imageId = handCardID[i];
			$("#" + imageId).attr("src","http://localhost:8080" + replacementCards.pop());
			if (replacementCards.length == 0)
				break;
		}
	}
}

function chooseEquipment() {
	var serverMsg = document.getElementById("serverMsg");
	serverMsg.value = "It is now time to choose equipment for quest";
	
	if(totalStages == stageTracker) {
		console.log("Quest over");
		var data = JSON.stringify({
			'outOfStages' : 0
		})
		socketConn.send(data);
		return;
	}
	console.log(questSetupCards);
	if(questSetupCards[stageTracker][0].includes("Test")) {
		console.log("this is a test: " + questSetupCards[stageTracker]);
		var oldHandSRC = handCardSRC;
		getCurrHand();
		if(isAI) {
			var data = JSON.stringify({
				'AICommand' : "nextBid",
				'name' : PlayerName,
				'stage' : stageTracker,
				'currHand': handCardSRC,
				'oldHand' : oldHandSRC,
				'minBid' : minBid
			}) 
			setTimeout(function(){ socketConn.send(data); 
			serverMsg.value = "Placing bids, please wait for other players...- "; 
			}, 1000);		
			return;
		}
		getTestBids();
		//displayTest(stageTracker);
	} else {
		console.log("this is a battle against " + questSetupCards[stageTracker][0]);
		if(isAI) {
			var data = JSON.stringify({
				'AICommand' : "chooseEquipment",
				'name' : PlayerName,
				'stage' : stageTracker,
				'currHand': handCardSRC,
			}) 
			setTimeout(function(){ 
				socketConn.send(data); 
				serverMsg.value = "Going into battle - wait for other players to finsih for results";
			}, 1000);
			return;
		}
		getBattleEquipment();
		//displayBattle(stageTracker);
		
	}
	
}

function getTestBids() {
	var serverMsg = document.getElementById("serverMsg");
	document.getElementById('doneEquipment').style.display = "inline";
	document.getElementById('doneEquipment').disabled = false;
	console.log(TestInfo);
	minBid = parseInt(testInfo[0][1]);
	console.log("minBid" + parseInt(minBid));
	serverMsg.value = "Please click on the cards you wish to bid for test (Click done to drop out)";
	console.log("SHOW MIN BID")
	document.getElementById("minBid").style.display = "block";
	document.getElementById("minBid").innerText = "Current minimum bid: " + minBid;
	 var x = document.getElementById("doneEquipment").disabled;
	
	 testTracker++;
	 console.log(x);
	if(x == false) { 
		document.getElementById('doneEquipment').disabled = true;
		document.getElementById('dropOut').style.display = "inline";
		document.getElementById('dropOut').disabled = true;
		if(numCards<=12) document.getElementById('dropOut').disabled = false;
		if(testTracker >= 2) { document.getElementById('dropOut').disabled = false; testTracker = 0; }
	
	}
	$('body')
			.on(
					'click',
					'#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12, #extra1, #extra2, #extra3, #extra4, #extra5, #extra6, #extra7, #extra8',
					function() {
						document.getElementById('dropOut').disabled = true;
						var cardId = this.src.replace(
								'http://localhost:8080', '');
						cardId = cardId.split('%20').join(' ');
						if(getNameFromLink(cardId) != "link not found") {
							console.log("adding to bid array" + getNameFromLink(cardId));
							testBids.push(getNameFromLink(cardId));
							var changeImageId = "#" + this.id;
							numCards--;
						}
						$(changeImageId).attr("src",
								"/resources/images/all.png");
						console.log(testBids.length);
						console.log(minBid);
						console.log("RIGHT BEFORE CHECKING TO UNDISABLE^");
						if(testBids.length >= minBid ) document.getElementById('doneEquipment').disabled = false;
						console.log(numCards)
						
					})
}

function chooseEquipmentTournie() {
	console.log("here");
	var serverMsg = document.getElementById("serverMsg");
	serverMsg.value = "It is now time to choose equipment for tournament (right-click to discard)";
	$('body').on('click', '#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12, #extra1, #extra2, #extra3, #extra4, #extra5, #extra6, #extra7, #extra8', function() {
		var cardId = this.src.replace('http://localhost:8080', '');
		cardId = cardId.split('%20').join(' ');
		if (checkForEquipment(this.src) != "card not found") {
			for (var i = 0; i < tournieEquipment.length; i++) {
				if ((checkForEquipment(this.src)) == tournieEquipment[i]) {
					var serverMsg = document.getElementById('serverMsg');
					serverMsg.value += "\nCannot choose repeat weapons";
					return;
				}
			}
			tournieEquipment.push(checkForEquipment(this.src))
			var changeImageId = "#" + this.id;
			numCards--;
			$(changeImageId).attr("src", "/resources/images/all.png");
			if (numCards <= 12) {
				document.getElementById("doneTournie").style.display = "inline";
			}
		}

	})
}

function getBattleEquipment() {
	document.getElementById('dropOut').style.display = "none";
	battleEquipment = [];
	var serverMsg = document.getElementById("serverMsg");
	document.getElementById('doneEquipment').style.display = "inline";
	serverMsg.value = "Please click on the equipment you want to choose for battle (right-click to discard)";
	$('body').on('click', '#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12, #extra1, #extra2, #extra3, #extra4, #extra5, #extra6, #extra7, #extra8', function() {
						var cardId = this.src.replace('http://localhost:8080', '');
						cardId = cardId.split('%20').join(' ');
						if (checkForEquipment(this.src) != "card not found") {
							for (var i = 0; i < battleEquipment.length; i++) {
								if ((checkForEquipment(this.src)) == battleEquipment[i]) {
									var serverMsg = document.getElementById('serverMsg');
									serverMsg.value += "\nCannot choose repeat weapons";
									return;
								}
							}
							battleEquipment.push(checkForEquipment(this.src))
							var changeImageId = "#" + this.id;
							numCards--;
							$(changeImageId).attr("src", "/resources/images/all.png");
							if (numCards <= 12) {
								document.getElementById("doneEquipment").disabled = false;
							}
						}

					})
}

function pickupBeforeStage(event) {
	var pickUpLink = event.data.replace("pickupBeforeStage", "");
	$("#extra1").attr("src", "http://localhost:8080" + pickUpLink);
	getCurrHand();
	var cardTracker = 0;
	for (var i = 0; i < handCardSRC.length; i++) {
		var tempCardLink = handCardSRC[i].replace("http://localhost:8080","");
		tempCardLink = tempCardLink.split('%20').join(' ');
		if (tempCardLink != "/resources/images/all.png")
			cardTracker++;
	}
	
	numCards = cardTracker;
	if (cardTracker > 12) {
		document.getElementById("doneEquipment").disabled = true;
		var serverMsg = document.getElementById("serverMsg");
		serverMsg.value = "You must choose a card to continue (or right-click to discard) when it is your turn";
	}
}

function parseTestInfo(event) {
	var temp = event.data.replace("TestInfo", "");
	TestInfo = temp;
	//console.log(temp);
	testInfo = temp.split(";");
	testInfo.pop();
	//console.log(testInfo);
	if(testInfo.length == 0) return;
	for(var i=0; i<testInfo.length; i++) {
		testInfo[i] = testInfo[i].split("#");
	}
	minBid = testInfo[0][1];
	//console.log(testInfo);
}
function parseCurrentFoeInfo(event) {
	var temp = event.data.replace("FoeInfo", "");
	//console.log(temp);
	foeInfo = temp.split(";");
	foeInfo.pop();
	//console.log(foeInfo);
	for(var i=0; i<foeInfo.length; i++) {
		foeInfo[i] = foeInfo[i].split("#");
	}
	//	console.log(foeInfo);
}

function parseParticipantInfo(event) {
	participantInfo = event.data.replace("currentParticipantInfo", "");
	participantInfo = JSON.parse(participantInfo);
	//console.log(participantInfo);
	currentStage = participantInfo.stages;
}
function getParticipants() {
	document.getElementById("acceptQuest").style.display = "inline";
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value += " Please accept/decline quest by clicking below"
	if (isAI) {
		var data = JSON.stringify({
			'AICommand' : "AskToParticipateQuest"
		})
		setTimeout(function(){ socketConn.send(data); }, 1000);
		document.getElementById("acceptQuest").style.display = "none";
		serverMsg.value = "Wait for other players...";
	}
}

function parseQuestInfo(event) {
	console.log(event);
	var temp = event.data.replace("questSetupCards","");
	console.log(temp);
	var array = JSON.parse(temp);
	console.log(array);
	var orderedQuestCards = [];
	var temp = [];
	//console.log(allQuestInfo[0]);
	//console.log(allQuestInfo[0].questSetupCards);
	var questCards = array;
	console.log(questCards.length);
	temp.push(questCards[0]);
	for(var i=1; i<questCards.length; i++) {
		var card = questCards[i];
		var type = getTypeFromName(questCards[i]);
		console.log(card);
		console.log(type);
	
		if(type == "foe" || type == "test") {
			orderedQuestCards.push(temp);
			temp = [];
		}
		temp.push(card);
		console.log(i);
		if(i==questCards.length-1) orderedQuestCards.push(temp);
	}
	console.log(orderedQuestCards);
	questSetupCards = orderedQuestCards;
	if(PlayerName == sponsor) {
		console.log(questSetupCards);
	}
}

function startGame() {
	var serverMsg = document.getElementById('serverMsg');
	document.getElementById('print').disabled = false;
	serverMsg.value = "All players have joined, starting game, wait for your turn..."
	document.getElementById('rigger').style.display = "none";
}

function setHand() {
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = "Setting player hand, flipping story deck, wait for your turn- ";
	var handString = event.data.replace('setHand', '');
	var handStringArray = handString.split(":");
	$("#card1").attr("src", handStringArray[0]);
	$("#card2").attr("src", handStringArray[1]);
	$("#card3").attr("src", handStringArray[2]);
	$("#card4").attr("src", handStringArray[3]);
	$("#card5").attr("src", handStringArray[4]);
	$("#card6").attr("src", handStringArray[5]);
	$("#card7").attr("src", handStringArray[6]);
	$("#card8").attr("src", handStringArray[7]);
	$("#card9").attr("src", handStringArray[8]);
	$("#card10").attr("src", handStringArray[9]);
	$("#card11").attr("src", handStringArray[10]);
	$("#card12").attr("src", handStringArray[11]);
}

function getTournie() {
	document.getElementById("askTournament").style.display = "inline";
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value += "Please accept/decline quest by clicking below"
	if (isAI) {
		var data = JSON.stringify({
			'AICommand' : "AskTournament"
		})
		setTimeout(function(){ socketConn.send(data); }, 1000);
		document.getElementById("askTournament").style.display = "none";
		serverMsg.value = "Wait for other players...";
	}

}
function getSponsor() {
	document.getElementById('sponsorQuest').style.display = 'block';
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = "Click to answer below"
	if (isAI) {
		var data = JSON.stringify({
			'AICommand' : "SponsorQuest",
			'name' : PlayerName
		})
		socketConn.send(data);
		document.getElementById('sponsorQuest').style.display = 'none';
		serverMsg.value = "Doing AI stuff";
	}
}

function getStats() {
	var temp = event.data.replace("updateStats", "");
	temp = temp.split("#");
	for (var i = 0; i < temp.length; i++) {
		temp[i] = temp[i].split(";");
	}
	// console.log(temp);
	document.getElementById('p1name').innerText = temp[0][0];
	document.getElementById('p2name').innerText = temp[1][0];
	document.getElementById('p3name').innerText = temp[2][0];
	document.getElementById('p4name').innerText = temp[3][0];
	document.getElementById('p1rank').innerText = temp[0][1];
	document.getElementById('p2rank').innerText = temp[1][1];
	document.getElementById('p3rank').innerText = temp[2][1];
	document.getElementById('p4rank').innerText = temp[3][1];
	document.getElementById('p1cards').innerText = temp[0][2];
	document.getElementById('p2cards').innerText = temp[1][2];
	document.getElementById('p3cards').innerText = temp[2][2];
	document.getElementById('p4cards').innerText = temp[3][2];
	document.getElementById('p1shields').innerText = temp[0][3];
	document.getElementById('p2shields').innerText = temp[1][3];
	document.getElementById('p3shields').innerText = temp[2][3];
	document.getElementById('p4shields').innerText = temp[3][3];
}

// pick up x cards
function PickupCards(newCards) {
	var testBonus = false;
	console.log("PICKING UP NEW CARDS " + PlayerName);
	getCurrHand();
	if (newCards.startsWith("PickupCardsProsperity")) {
		whichEvent = "Prosperity";
		newCards = newCards.replace("PickupCardsProsperity", "");
	}
	if(newCards.startsWith("PickupCardsTestBonus")) {
		newCards = newCards.replace("PickupCardsTestBonus","");
		testBonus = true;
	}
	//console.log(newCards)
	if(newCards=="null") return;
	newCards = newCards.split(";");
	newCards.pop();
//	console.log("LINE 585");
	//console.log(newCards);
	var numNewCards = newCards.length;
	console.log(numNewCards);
	for (var i = 0; i < handCardID.length; i++) {
		//console.log("begin of loop");
	//	console.log(newCards.length);
		//console.log(newCards);
		if (handCardSRC[i] == "http://localhost:8080/resources/images/all.png") {
			var imageId = handCardID[i];
			if(testBonus) { var tempLink = getLinkFromName(newCards.pop()) } else {
				var tempLink = newCards.pop();
			}
			
			$("#" + imageId).attr("src",
					"http://localhost:8080" + tempLink);
		//	console.log(newCards.length);
		//	console.log(newCards);
			if (newCards.length == 0)
				break;
		}
	}

	var cardTracker = 0;
	console.log("line 614");
	for (var i = 0; i < handCardSRC.length; i++) {
		var tempCardLink = handCardSRC[i].replace("http://localhost:8080", "");
		tempCardLink = tempCardLink.split('%20').join(' ');
	//  console.log(tempCardLink);
		if (tempCardLink != "/resources/images/all.png")
			cardTracker++;
	}

	cardTracker += numNewCards;
	numCards = cardTracker;
	if (cardTracker > 12) {
		var serverMsg = document.getElementById('serverMsg');
		serverMsg.value += "Right click to remove extra cards to continue (for discard)";
		if (isAI == true) {
			var data = JSON.stringify({
				'AICommand' : 'DiscardChoice',
				'numCards' : (cardTracker - 12),
				'name' : PlayerName
			})
			socketConn.send(data);
			return;
		}

		discard();
	}

	if(numCards <= 12 && whichEvent == "Prosperity") {
		console.log("sending prosperity");
		var data = JSON.stringify({

			'doneEventProsperity' : 0
		})
		setTimeout(function(){ socketConn.send(data); }, 1000);
	}

}
// sponsor pickup
function sponsorPickup(cards) {
	getCurrHand();
	var pickUpLinks = event.data.replace("SponsorPickup", "");

	var pickUpLinksArr = pickUpLinks.split(";");

	pickUpLinksArr.pop();
	//console.log(pickUpLinksArr);
	var numNewCards = pickUpLinksArr.length;

	for (var i = 0; i < handCardID.length; i++) {

		if (handCardSRC[i] == "http://localhost:8080/resources/images/all.png") {
			var imageId = handCardID[i];
			$("#" + imageId).attr("src",
					"http://localhost:8080" + pickUpLinksArr.pop());
			if (pickUpLinksArr.length == 0)
				break;
		}
	}

	var cardTracker = 0;
	for (var i = 0; i < handCardSRC.length; i++) {
		var tempCardLink = handCardSRC[i].replace("http://localhost:8080", "");
		tempCardLink = tempCardLink.split('%20').join(' ');
		
		if (tempCardLink != "/resources/images/all.png")
			cardTracker++;
	}
	
	cardTracker += numNewCards;
	numCards = cardTracker;
    console.log("After sponsor pickup, total # of cards is: " + cardTracker);
	if(isAI) {
		var extra1 = document.getElementById("card1").src;
		var extra2 = document.getElementById("card2").src;
		var card1Src = extra1.replace('http://localhost:8080','');
		var card2Src = extra2.replace('http://localhost:8080','');
		card1Src = card1Src.split('%20').join(' ');
		card2Src = card2Src.split('%20').join(' ');
		var discardName1 = getNameFromLink(card1Src);
		var discardName2 = getNameFromLink(card2Src);
		var toRemove = discardName1 + ";" + discardName2 + ";null";
		console.log("LOOK HERE LINE 681");
		console.log(toRemove);
		AIDiscard(toRemove);
		var data = JSON.stringify({
			'incTurnRoundOver' : true
		});
		socketConn.send(data);
		return;

	}
	if (cardTracker > 12) {
		var serverMsg = document.getElementById('serverMsg');
		serverMsg.value += " Right click extra cards to continue (for discard) ";
		discard();

	}
}

//display current test
function displayTest(curr_stage) {
	//console.log("display test");
}

// display current battle
function displayBattle(curr_stage) {
	//console.log("display battle");
	
}

function setupQuestRound() {
	console.log(totalStages);
	
	var serverMsg = document.getElementById('serverMsg');
    serverMsg.value += "\nChoose foe or test card for stage";	
    
	$('body').on('click','#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12', function() {
							var cardSrc = this.src.replace('http://localhost:8080', '');
							cardSrc = cardSrc.split('%20').join(' ');
							var tempTestCard = checkForCardType(cardSrc, "test");
							if (tempTestCard != "card not found") {
								questSetupCards.push(tempTestCard);
								var changeImageId = "#" + this.id;
								$(changeImageId).attr("src", "/resources/images/all.png");
								totalStages--;
								console.log(totalStages);
								if(totalStages == 0) {
									$('body').off('click');
									document.getElementById('doneQuest').style.display = "inline";
									doneQuestSetup();
								}
								
							}
							var tempFoeCard = checkForCardType(cardSrc, "foe");
							if (tempFoeCard != "card not found") {
								//find a way to stop if card being chosen is not 
								
								//get battle points from name
								//if less don't allot
								console.log(tempFoeCard);
								console.log(getBpFromName(tempFoeCard));
								for(var i=0; i<questSetupCards.length; i++) {
									if(getBpFromName(tempFoeCard) < getBpFromName(questSetupCards[i])) { 
										var serverMsg = document.getElementById('serverMsg');
										serverMsg.value = "Foe battle points must be higher than previous"; 
										return;
										}
								}
								questSetupCards.push(tempFoeCard);
								var changeImageId = "#" + this.id;
								$(changeImageId).attr("src",
										"/resources/images/all.png");
								var serverMsg = document.getElementById('serverMsg');
								$('body').off('click');
								serverMsg.value = "Choose weapons for foe";
								document.getElementById('doneQuest').style.display = "inline";
								totalStages--;
							//	console.log(totalStages);
								$('body').on('click','#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12', function() {
	
								

								var cardSrc = this.src.replace('http://localhost:8080','');
								cardSrc = cardSrc.split('%20').join(' ');
								var cardName = checkForCardType(cardSrc, "weapon");
								if (cardName != "card not found") {
									for (var i = 0; i < questSetupCards.length; i++) {
										if (cardName == questSetupCards[i]) {
											var serverMsg = document.getElementById('serverMsg');
											serverMsg.value = "\nCannot choose repeat weapons";
											return;
										}
									}
									questSetupCards.push(cardName)
									var changeImageId = "#"	+ this.id;
									$(changeImageId).attr("src","/resources/images/all.png");
								}
							})
							}
	})
}
	
// discard function
function discard() {
	$('body').on('contextmenu', '#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12, #extra1, #extra2, #extra3, #extra4, #extra5, #extra7, #extra6, #extra7, #extra8', function() {
				if (numCards == 12)	return false;
				var cardSrc = this.src.replace('http://localhost:8080',	'');
				if (cardSrc == "/resources/images/all.png") {
				} else {
					cardSrc = cardSrc.split('%20').join(' ');
					var discardName = getNameFromLink(cardSrc);
					var data = JSON.stringify({
						'discard' : discardName
					});
					socketConn.send(data);
					arrangeHand();
				}
				if (this.src != "http://localhost:8080/resources/images/all.png") {
					if (numCards > 12) {
						$(this)
								.attr("src",
										"/resources/images/all.png");
						numCards--;
						if (numCards >= 12) {
							if(inTournie) document.getElementById("doneTournie").style.display = "inline";
							if (whichEvent != "" && numCards == 12) {

								document.getElementById("serverMsg").value = "Wait for other players...";
								if (whichEvent == "Prosperity") {
									//console.log("sending prosperity");
									var data = JSON.stringify({

										'doneEventProsperity' : 0
									})
									socketConn.send(data);
									arrangeHand();
									whichEvent = "";
									return false;
								}
							}

							document.getElementById("doneEquipment").disabled = false;
							if (PlayerName === sponsor) {
								document.getElementById("serverMsg").value = "Replacing cards used to sponsor";
							}

							if (numCards == 12
									&& document
											.getElementById("serverMsg").value
											.startsWith("Replacing cards used to sponsor")) {
								var serverMsg = document
										.getElementById("serverMsg");
								serverMsg.value = "Wait for other players...";
								var data = JSON.stringify({
									'incTurnRoundOver' : true
								});

								socketConn.send(data);
								arrangeHand();
							}
						}
					}
				}
			})

return false;
}

// accept to participate in quest
function acceptQuestParticipate() {
	stageCounter = 0;
	document.getElementById('acceptQuest').style.display = "none";
	var data = JSON.stringify({
		'name' : PlayerName,
		'participate_quest' : true
	});
	socketConn.send(data);
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = ("Waiting for others to answer...");
}

function pickWeapons() {
	
}

//drop out of test
function dropOutOfTest() {
	document.getElementById("minBid").style.display = "none";
	$('body').off('click');
	document.getElementById('doneEquipment').style.display = "none";
	document.getElementById('dropOut').style.display = "none";
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = "Dropped out of test, wait for quest to complete";
	var oldBids = testBids;
	testBids = [];
	var data = JSON.stringify({
		'name' : PlayerName,
		'stages' : stageTracker,
		'equipment_info' : testBids,
		'oldBids' : oldBids,
		'isTest' : true
	});

	socketConn.send(data);
	console.log("figure out way to get cards back");
	var oldHandSRC = handCardSRC;
	getCurrHand();
	console.log(handCardSRC);
	console.log(oldHandSRC);
	var replaceTestCardHand = [];
	for(var i=0; i<12; i++) {
		replaceTestCardHand[i] = oldHandSRC[i].replace("http://localhost:8080", "");
		repalceTestCardHand = replaceTestCardHand[i].split('%20').join(' ');
	}
	console.log(replaceTestCardHand);
	console.log("here");
	
	$(card1).attr("src", replaceTestCardHand[0]);
	$(card2).attr("src", replaceTestCardHand[1]);
	$(card3).attr("src", replaceTestCardHand[2]);
	$(card4).attr("src", replaceTestCardHand[3]);
	$(card5).attr("src", replaceTestCardHand[4]);
	$(card6).attr("src", replaceTestCardHand[5]);
	$(card7).attr("src", replaceTestCardHand[6]);
	$(card8).attr("src", replaceTestCardHand[7]);
	$(card9).attr("src", replaceTestCardHand[8]);
	$(card10).attr("src", replaceTestCardHand[9]);
	$(card11).attr("src", replaceTestCardHand[10]);
	$(card12).attr("src", replaceTestCardHand[11]);
	
	var data = JSON.stringify({
		'removeStageCardFromTest' : oldHandSRC[12],
		'name' : PlayerName
	})
	
	socketConn.send(data);
}

//send tournament info
function doneTournament() {
	document.getElementById('doneTournie').style.display = "none";
	$('body').off('click');
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = "Sending challenge, please wait for other players...- ";
	var data = JSON.stringify({
		'name' : PlayerName,
		'tournament_info' : tournieEquipment
	});
	tournieEquipment = [];
	socketConn.send(data);
	arrangeHand();
}
// send weapon info - done choosing
function doneEquipment() {
	document.getElementById("minBid").style.display = "none";
	document.getElementById('dropOut').style.display = "none";
	$('body').off('click');
	document.getElementById('doneEquipment').style.display = "none";
	document.getElementById('dropOut').style.display = "none";
	var serverMsg = document.getElementById('serverMsg');
	//console.log(battleEquipment);
	console.log(questSetupCards);
	//console.log(allBattleEquipment);
	console.log(stageTracker);
	if (questSetupCards[stageTracker][0].includes("Test")) {
		serverMsg.value = "Placing bids, please wait for other players...- ";
		if(testBids.length == 0) serverMsg.value = "Dropping out of test, please wait for other players";
	
		var data = JSON.stringify({
			'name' : PlayerName,
			'stages' : stageTracker,
			'equipment_info' : testBids,
			'isTest' : true
		});

		socketConn.send(data);
		arrangeHand();

	} else {
		serverMsg.value = "Going into battle - wait for other players to finsih for results";
		var data = JSON.stringify({
			'name' : PlayerName,
			'stages' : stageTracker,
			'equipment_info' : battleEquipment,
			'isTest' : false
		});
		
		socketConn.send(data);
		arrangeHand();
		battleEquipment = [];
		
	}

}

// deny participation in quest
function denyQuestParticipate() {
	document.getElementById('acceptQuest').style.display = "none";
	var data = JSON.stringify({
		'name' : PlayerName,
		'participate_quest' : false
	});
	socketConn.send(data);
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = ("Waiting for other players to finish quest...");
}

//accept tournament
function acceptTournament() {
	document.getElementById('askTournament').style.display = "none";
	var data = JSON.stringify({
		'name' : PlayerName,
		'participate_tournament' : true
	});
	socketConn.send(data);
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = ("Waiting for others to answer...");
	inTournie = true;
}

//dent 

function denyTournament() {
	document.getElementById('askTournament').style.display = "none";
	var data = JSON.stringify({
		'name' : PlayerName,
		'participate_tournament' : false
	});
	socketConn.send(data);
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = ("Waiting for other players to finish tournament...");
}
// finished setting up quest for sponsor
function doneWeaponsQuestSponsor() {
	console.log(totalStages);
	if(totalStages == 0) {
		$('body').off('click');
		document.getElementById('doneQuest').style.display = "inline";
		doneQuestSetup();
		return;
	}
	$('body').off('click');
	setupQuestRound();
	document.getElementById('doneQuest').style.display = "none";
}

function doneQuestSetup() {
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = "Quest setup complete, wait for other players";
	document.getElementById('doneQuest').style.display = "none";
	console.log(questSetupCards);
	var data = JSON.stringify({ 'questSetupCards' : questSetupCards});
	socketConn.send(data);
}

function showStages() {
	console.log(questSetupCards);
	console.log(allBattleEquipment);
}
//flip story card
function flipStoryDeck() {
	var data = JSON.stringify({
		'flipStoryDeck' : 0
	})
	socketConn.send(data); 
	document.getElementById('flip').disabled = true;
}

// get name from link
function getNameFromLink(link) {
	for (var i = 0; i < cardTypeList.length; i++) {
		if (cardTypeList[i].link == link) {
			return cardTypeList[i].name;
		}
	}
	return "link not found";
}
// get link from name
function getLinkFromName(name) {
	for (var i = 0; i < cardTypeList.length; i++) {
		// console.log(cardTypeList[i]);
		// console.log(name);
		if (cardTypeList[i].name === name)
			return cardTypeList[i].link;
	}
	return "card not found";
}

//get type 
function getTypeFromName(name) {
	for(var i=0; i<cardTypeList.length; i++) {
		if(cardTypeList[i].name === name) {
			return cardTypeList[i].type;
		}
	}
	return "card not found";
}
//get bp from name
function getBpFromName(name) {
	console.log(storyCardFaceUp);
	console.log(storyCardFaceUp.foe);
	console.log(name);
	for (var i = 0; i < cardTypeList.length; i++) {
		// console.log(cardTypeList[i]);
		// console.log(name);
		if (cardTypeList[i].name === name)
			if (cardTypeList[i].hasOwnProperty('bp')) {
			    if(storyCardFaceUp.foe === name) return cardTypeList[i].bonusbp;
			    return cardTypeList[i].bp;
			}
	}
	return "card not found";
}
// check card type
function checkForCardType(cardSrc, type) {
	for (var i = 0; i < cardTypeList.length; i++) {
		if (cardSrc == cardTypeList[i].link) {
			if (cardTypeList[i].type == type) {
				return cardTypeList[i].name;
			}
		}
	}
	return "card not found";
}

// accept to sponsor quest
function acceptSponsorQuest() {
	questSetupCards = [];
	allQuestInfo = [];
	document.getElementById('sponsorQuest').style.display = 'none';
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = "You are sponsor, setting up quest...";
	sponsor = PlayerName;
	var data = JSON.stringify({
		'name' : PlayerName,
		'sponsor_quest' : true
	});
	socketConn.send(data);
}
// deny to sponsor quest
function denySponsorQuest() {
	document.getElementById('sponsorQuest').style.display = 'none';
	var data = JSON.stringify({
		'name' : PlayerName,
		'sponsor_quest' : false
	});
	socketConn.send(data);
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = "Waiting for other players...";
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
// set AI player
function setAI() {
	var random = Math.floor(Math.random() * 1000);
	var name = "AI_Player_" + random;
	PlayerName = name;
	var data = JSON.stringify({
		'AI' : name
	})
	socketConn.send(data);
	isAI = true;
	strategyType = "Strategy2";
	document.getElementById('title').innerHTML = "Welcome to the Quest of The Round Table - "
			+ name + "'s View";
	changeColor();
	document.getElementById('nameparagraph').style.display = "none";
	document.getElementById('send').style.display = "none";
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = "Waiting for other players...";

}
// send name to server -> adds client to player list
function send() {
	var clientMsg = document.getElementById('enterName');
	if (clientMsg.value) {
		PlayerName = clientMsg.value;
		var data = JSON.stringify({
			'newName' : $("#enterName").val()
		})
		socketConn.send(data);
		document.getElementById('title').innerHTML = "Welcome to the Quest of The Round Table - "
				+ clientMsg.value + "'s View";
		changeColor();
		document.getElementById('nameparagraph').style.display = "none";
		document.getElementById('send').style.display = "none";
		document.getElementById('rigger').style.display = "none";
		var serverMsg = document.getElementById('serverMsg');
		serverMsg.value = "Waiting for other players...(to create AI player(s), open a new browser window and click AI Player button)";
	}
}

// print all clients connected
function print() {
	var data = JSON.stringify({
		'print' : 0
	})
	socketConn.send(data);
}

// proof of connection - prints deck proof also
function proof() {
	var data = JSON.stringify({
		'proof' : 0
	})
	socketConn.send(data);
}

function checkForEquipment(ImageLink) {
	// console.log(ImageLink);
	var cardSrc = ImageLink.replace('http://localhost:8080', '');
	cardSrc = cardSrc.split('%20').join(' ');
	// console.log(cardSrc);
	// console.log("checking for equipment");
	for (var i = 0; i < cardTypeList.length; i++) {
		if (cardTypeList[i].link == cardSrc) {
			// console.log("is in list");
			if (cardTypeList[i].type == "ally")
				return cardTypeList[i].name;
			if (cardTypeList[i].type == "weapon")
				return cardTypeList[i].name;
			if (cardTypeList[i].type == "amour")
				return cardTypeList[i].name;
		}

	}
	return "card not found";
}

function arrangeHand() {
	var card1id = document.getElementById("card1").id;
	var card2id = document.getElementById("card2").id;
	var card3id = document.getElementById("card3").id;
	var card4id = document.getElementById("card4").id;
	var card5id = document.getElementById("card5").id;
	var card6id = document.getElementById("card6").id;
	var card7id = document.getElementById("card7").id;
	var card8id = document.getElementById("card8").id;
	var card9id = document.getElementById("card9").id;
	var card10id = document.getElementById("card10").id;
	var card11id = document.getElementById("card11").id;
	var card12id = document.getElementById("card12").id;
	var extra1id = document.getElementById("extra1").id;
	var extra2id = document.getElementById("extra2").id;
	var extra3id = document.getElementById("extra3").id;
	var extra4id = document.getElementById("extra4").id;
	var extra5id = document.getElementById("extra5").id;
	var extra6id = document.getElementById("extra6").id;
	var extra7id = document.getElementById("extra7").id;
	var extra8id = document.getElementById("extra8").id;
	var mainArrayID = [ card1id, card2id, card3id, card4id, card5id, card6id,
			card7id, card8id, card9id, card10id, card11id, card12id ];
	var extraArrayID = [ extra1id, extra2id, extra3id, extra4id, extra5id,
			extra6id, extra7id, extra8id ];
	var card1 = document.getElementById("card1").src;
	var card2 = document.getElementById("card2").src;
	var card3 = document.getElementById("card3").src;
	var card4 = document.getElementById("card4").src;
	var card5 = document.getElementById("card5").src;
	var card6 = document.getElementById("card6").src;
	var card7 = document.getElementById("card7").src;
	var card8 = document.getElementById("card8").src;
	var card9 = document.getElementById("card9").src;
	var card10 = document.getElementById("card10").src;
	var card11 = document.getElementById("card11").src;
	var card12 = document.getElementById("card12").src;
	var extra1 = document.getElementById("extra1").src;
	var extra2 = document.getElementById("extra2").src;
	var extra3 = document.getElementById("extra3").src;
	var extra4 = document.getElementById("extra4").src;
	var extra5 = document.getElementById("extra5").src;
	var extra6 = document.getElementById("extra6").src;
	var extra7 = document.getElementById("extra7").src;
	var extra8 = document.getElementById("extra8").src;
	var mainArray = [ card1, card2, card3, card4, card5, card6, card7, card8,
			card9, card10, card11, card12 ];
	var extraArray = [ extra1, extra2, extra3, extra4, extra5, extra6, extra7,
			extra8 ];

	for (var i = 0; i < extraArrayID.length; i++) {
		if (extraArray[i] != "http://localhost:8080/resources/images/all.png") {
			for (var j = 0; j < mainArrayID.length; j++) {
				if (mainArray[j] == "http://localhost:8080/resources/images/all.png") {
					$("#" + mainArrayID[j]).attr("src", extraArray[i]);
					$("#" + extraArrayID[i]).attr("src",
							"http://localhost:8080/resources/images/all.png");
					if (extraArray[i + 1] != "http://localhost:8080/resources/images/all.png")
						arrangeHand();
					return;
				}
			}
		}
	}
}

function riggedGame() {
	send();
	document.getElementById('rigger').style.display = "none";
	var version = 42;
	var data = JSON.stringify({
		'riggedGame' : version
	})
	socketConn.send(data);
}

// static card resources for spring application
var all = {
	name : "All",
	type : "misc",
	link : "/resources/images/all.png"
};
var back = {
	name : "back",
	type : "misc",
	link : "/resources/images/card-back.png"
};
var horse = {
	name : "Horse",
	type : "weapon",
	link : "/resources/images/W Horse.jpg"
};
var sword = {
	name : "Sword",
	type : "weapon",
	link : "/resources/images/W Sword.jpg"
};
var dagger = {
	name : "Dagger",
	type : "weapon",
	link : "/resources/images/W Dagger.jpg"
};
var lance = {
	name : "Lance",
	type : "weapon",
	link : "/resources/images/W Lance.jpg"

};
var battleAx = {
	name : "Battle-ax",
	type : "weapon",
	link : "/resources/images/W Battle-ax.jpg"

};
var excalibur = {
	name : "Excalibur",
	type : "weapon",
	link : "/resources/images/W Excalibur.jpg"

};
var robberKnight = {
	name : "Robber Knight",
	type : "foe",
	link : "/resources/images/F Robber Knight.jpg",
	bp: 15,
	bonusbp: 0
};
var saxons = {
	name : "Saxons",
	type : "foe",
	link : "/resources/images/F Saxons.jpg",
	bp: 15,
	bonusbp: 25
};
var boar = {
	name : "Boar",
	type : "foe",
	link : "/resources/images/F Boar.jpg",
	bp: 5,
	bonusbp: 15
};
var thieves = {
	name : "Thieves",
	type : "foe",
	link : "/resources/images/F Thieves.jpg",
	bp: 5,
	bonusbp: 0
};
var greenKnight = {
	name : "Green Knight",
	type : "foe",
	link : "/resources/images/F Green Knight.jpg",
	bp: 24,
	bonusbp: 40
};
var blackKnight = {
	name : "Black Knight",
	type : "foe",
	link : "/resources/images/F Black Knight.jpg",
	bp: 25,
	bonusbp: 35
};
var evilKnight = {
	name : "Evil Knight",
	type : "foe",
	link : "/resources/images/F Evil Knight.jpg",
	bp: 20,
	bonusbp: 30
};
var saxonKnight = {
	name : "Saxon Knight",
	type : "foe",
	link : "/resources/images/F Saxon Knight.jpg",
	bp: 15,
	bonusbp: 25
};
var dragon = {
	name : "Dragon",
	type : "foe",
	link : "/resources/images/F Dragon.jpg",
	bp: 50,
	bonusbp: 70
};
var giant = {
	name : "Giant",
	type : "foe",
	link : "/resources/images/F Giant.jpg",
	bp: 40,
	bonusbp: 0 
};
var mordred = {
	name : "Mordred",
	type : "foe",
	link : "/resources/images/F Mordred.jpg",
	bp: 30,
	bonusbp: 0
};
var sirG = {
	name : "Sir Gawain",
	type : "ally",
	link : "/resources/images/A Sir Gawain.jpg"
};
var sirPe = {
	name : "King Pellinore",
	type : "ally",
	link : "/resources/images/A King Pellinore.jpg"
};
var sirP = {
	name : "Sir Percival",
	type : "ally",
	link : "/resources/images/A Sir Percival.jpg"
};
var sirT = {
	name : "Sir Tristan",
	type : "ally",
	link : "/resources/images/A Sir Tristan.jpg"
};
var sirL = {
	name : "Sir Lancelot",
	type : "ally",
	link : "/resources/images/A Sir Lancelot.jpg"
};
var sirGa = {
	name : "Sir Galahad",
	type : "ally",
	link : "/resources/images/A Sir Galahad.jpg"
};
var queenG = {
	name : "Queen Guinevere",
	type : "ally",
	link : "/resources/images/A Queen Guinevere.jpg"
};
var queenI = {
	name : "Queen Iseult",
	type : "ally",
	link : "/resources/images/A Queen Iseult.jpg"
};
var arthur = {
	name : "King Arthur",
	type : "ally",
	link : "/resources/images/A King Arthur.jpg"
};
var merlin = {
	name : "Merlin",
	type : "ally",
	link : "/resources/images/A Merlin.jpg"
};
var amour = {
	name : "Amour",
	type : "amour",
	link : "/resources/images/Amour.jpg"
};
var chivalrousDeed = {
	name : "Chivalrous Deed",
	type : "event",
	link : "/resources/images/E Chivalrous Deed.jpg"
};
var courtCamelot = {
	name : "Court Called to Camelot",
	type : "event",
	link : "/resources/images/E Court Called Camelot.jpg"
};
var callToArms = {
	name : "King's Call to Arms",
	type : "event",
	link : "/resources/images/E King's Call to Arms.jpg"
};
var recognition = {
	name : "King's Recognition",
	type : "event",
	link : "/resources/images/E King's Recognition.jpg"
};
var plague = {
	name : "Plague",
	type : "event",
	link : "/resources/images/E Plague.jpg"
};
var pox = {
	name : "Pox",
	type : "event",
	link : "/resources/images/E Pox.jpg"
};
var prosperity = {
	name : "Prosperity Throughout the Realm",
	type : "event",
	link : "/resources/images/E Prosperity Throughout the Realm.jpg"
};
var queensFavor = {
	name : "Queen's Favor",
	type : "event",
	link : "/resources/images/E Queen's Favor.jpg"
};
var testMorgan = {
	name : "Test of Morgan Le Fey",
	type : "test",
	link : "/resources/images/T Test of Morgan Le Fey.jpg"
};
var testTemp = {
	name : "Test of Temptation",
	type : "test",
	link : "/resources/images/T Test of Temptation.jpg"
};
var testBeast = {
	name : "Test of the Questing Beast",
	type : "test",
	link : "/resources/images/T Test of the Questing Beast.jpg"
};
var testValor = {
	name : "Test of Valor",
	type : "test",
	link : "/resources/images/T Test of Valor.jpg"
};
var arthurQuest = {
	name : "Vanquish King Arthur's Enemies",
	type : "quest",
	link : "/resources/images/Q Arthur.jpg"
};
var beastQuest = {
	name : "Search for the Questing Beast",
	type : "quest",
	link : "/resources/images/Q Beast.jpg"
};
var boarQuest = {
	name : "Boar Hunt",
	type : "quest",
	link : "/resources/images/Q Boar.jpg"
};
var dragonQuest = {
	name : "Slay the Dragon",
	type : "quest",
	link : "/resources/images/Q Dragon.jpg"
};
var forestQuest = {
	name : "Journey through the Enchanted Forest",
	type : "quest",
	link : "/resources/images/Q Forest.jpg"
};
var grailQuest = {
	name : "Search for the Holy Grail",
	type : "quest",
	link : "/resources/images/Q Grail.jpg"
};
var gkQuest = {
	name : "Test of the Green Knight",
	type : "quest",
	link : "/resources/images/Q Green.jpg"
};
var honorQuest = {
	name : "Defend the Queen's Honor",
	type : "quest",
	link : "/resources/images/Q Honor.jpg"
};
var maidenQuest = {
	name : "Rescue the Fair Maiden",
	type : "quest",
	link : "/resources/images/Q Maiden.jpg"
};
var saxonQuest = {
	name : "Repel the Saxon Raiders",
	type : "quest",
	link : "/resources/images/Q Saxon.jpg"
};
var squire = {
	name : "Squire",
	type : "rank",
	link : "/resources/images/R Squire.jpg"
};
var knight = {
	name : "Knight",
	type : "rank",
	link : "/resources/images/R Knight.jpg"
};
var cKnight = {
	name : "Champion Knight",
	type : "rank",
	link : "/resources/images/R Champion Knight.jpg"
};
var camelot = {
	name : "At Camelot",
	type : "tournament",
	link : "/resources/images/T1.jpg"
};
var orkney = {
	name : "At Ornkey",
	type : "tournament",
	link : "/resources/images/T2.jpg"
};
var tintagel = {
	name : "At Tintagel",
	type : "tournament",
	link : "/resources/images/T3.jpg"
};
var york = {
	name : "At York",
	type : "tournament",
	link : "/resources/images/T4.jpg"
};
var cardTypeList = [ back, horse, sword, lance, dagger, battleAx, excalibur,
		robberKnight, saxons, boar, thieves, greenKnight, blackKnight,
		evilKnight, saxonKnight, dragon, giant, mordred, sirG, sirPe, sirP,
		sirT, sirL, sirGa, queenG, queenI, arthur, merlin, amour,
		chivalrousDeed, courtCamelot, callToArms, recognition, plague, pox,
		prosperity, queensFavor, testMorgan, testTemp, testBeast, testValor,
		arthurQuest, beastQuest, dragonQuest, forestQuest, grailQuest, gkQuest,
		honorQuest, maidenQuest, saxonQuest, squire, knight, cKnight, camelot,
		orkney, tintagel, york ];

function AIRemoveFromScreen(cardNames) {
	var send = false;
	if(cardNames.startsWith("BP")) {
		cardNames = cardNames.replace("BP", "");
		send = true;
	}
	console.log("Remove from screen: " + cardNames);
	var removeLink = [];
	removeLinks = cardNames.split(";");
	console.log(removeLinks);
	removeLinks.pop();
	for(var i=0; i<removeLinks.length; i++) {
		removeLinks[i] = getLinkFromName(removeLinks[i]);
		for(var j=0; j<handCardSRC.length; j++) {
			var currHandCard = handCardSRC[j];
			currHandCard = currHandCard.replace("http://localhost:8080","");
			currHandCard = currHandCard.split('%20').join(' ');
			console.log(currHandCard);
			if(currHandCard === removeLinks[i]) {
				console.log("replace card here");
				var imageId = handCardID[j];
				console.log(handCardID[j]);
				$("#" + imageId).attr("src", "http://localhost:8080/resources/images/all.png");
				break;
			}
		} 
	}
	getCurrHand();
	console.log(handCardSRC);
	arrangeHand();
	if(send==true) {
		console.log("SEND battle equipment");
		console.log(cardNames);
		if(cardNames.includes(";")) {
			cardNames = cardNames.split(";");
		} else {
			var temp = cardNames;
			cardNames = [temp];
		}
		for(var i=0; i<cardNames.length; i++) {
			if(cardNames[i] == "") {
				cardNames.splice(i, 1);
			}
		}
		var serverMsg = document.getElementById("serverMsg");
		serverMsg.value = "Going into battle - wait for other players to finsih for results";
		var data = JSON.stringify({
			'name' : PlayerName,
			'stages' : stageTracker,
			'equipment_info' : cardNames,
			'isTest' : false
		});
		
		console.log("After sending");
		console.log(cardNames);
		setTimeout(function(){ socketConn.send(data); }, 1000);	
		arrangeHand();
		send == false; 
	}
}
function AIDiscard(cardNames) {
	console.log("ENTERING AI DISCARD\n" + cardNames);
	if (cardNames.startsWith("Prosperity")) {
		cardNames = cardNames.replace("Prosperity", "");
		whichEvent = "Prosperity";
	}

	var card1aiID = $('#card1').attr('id');
	var card2aiID = $('#card2').attr('id');
	var card3aiID = $('#card3').attr('id');
	var card4aiID = $('#card4').attr('id');
	var card5aiID = $('#card5').attr('id');
	var card6aiID = $('#card6').attr('id');
	var card7aiID = $('#card7').attr('id');
	var card8aiID = $('#card8').attr('id');
	var card9aiID = $('#card9').attr('id');
	var card10aiID = $('#card10').attr('id');
	var card11aiID = $('#card11').attr('id');
	var card12aiID = $('#card12').attr('id');
	var extra1aiID = $('#extra1').attr('id');
	var extra2aiID = $('#extra2').attr('id');
	var extra3aiID = $('#extra3').attr('id');
	var extra4aiID = $('#extra4').attr('id');
	var extra5aiID = $('#extra5').attr('id');
	var extra6aiID = $('#extra6').attr('id');
	var extra7aiID = $('#extra7').attr('id');
	var extra8aiID = $('#extra8').attr('id');
	var imageArrayaiID = [ card1aiID, card2aiID, card3aiID, card4aiID,
			card5aiID, card6aiID, card7aiID, card8aiID, card9aiID, card10aiID,
			card11aiID, card12aiID, extra1aiID, extra2aiID, extra3aiID,
			extra4aiID, extra5aiID, extra6aiID, extra7aiID, extra8aiID ];
	var card1ai = $('#card1').attr('src');
	var card2ai = $('#card2').attr('src');
	var card3ai = $('#card3').attr('src');
	var card4ai = $('#card4').attr('src');
	var card5ai = $('#card5').attr('src');
	var card6ai = $('#card6').attr('src');
	var card7ai = $('#card7').attr('src');
	var card8ai = $('#card8').attr('src');
	var card9ai = $('#card9').attr('src');
	var card10ai = $('#card10').attr('src');
	var card11ai = $('#card11').attr('src');
	var card12ai = $('#card12').attr('src');
	var extra1ai = $('#extra1').attr('src');
	var extra2ai = $('#extra2').attr('src');
	var extra3ai = $('#extra3').attr('src');
	var extra4ai = $('#extra4').attr('src');
	var extra5ai = $('#extra5').attr('src');
	var extra6ai = $('#extra6').attr('src');
	var extra7ai = $('#extra7').attr('src');
	var extra8ai = $('#extra8').attr('src');
	var imageArrayai = [ card1ai, card2ai, card3ai, card4ai, card5ai, card6ai,
			card7ai, card8ai, card9ai, card10ai, card11ai, card12ai, extra1ai,
			extra2ai, extra3ai, extra4ai, extra5ai, extra6ai, extra7ai,
			extra8ai ];
	cardNames = cardNames.split(";");
	cardNames.pop();
	console.log(cardNames);
	console.log(imageArrayai);
	//console.log(handCardSRC);
	console.log("ai discard 1672 changing num cards" + numCards);
	for (var j = 0; j < cardNames.length; j++) {
		for (var i = 0; i < imageArrayai.length; i++) {
			var tempSrc = getLinkFromName(cardNames[j]);
			console.log(tempSrc);
			if(imageArrayai[i].includes("%")) imageArrayai[i] = imageArrayai[i].split('%20').join(' ');
			console.log(imageArrayai[i]);
	
			if(imageArrayai[i].startsWith("http")) imageArrayai[i] = imageArrayai[i].replace("http://localhost:8080","");
			if (tempSrc == imageArrayai[i]) {
		
				$("#" + imageArrayaiID[i]).attr("src",
						"http://localhost:8080/resources/images/all.png");
				console.log(cardNames);
				console.log(cardNames[j]);
				console.log(j);
				var dataDiscard = JSON.stringify({
	
					'discard' : cardNames[j]
				});
				socketConn.send(dataDiscard);
				arrangeHand();
				break;
			}
		}
	}

	if (whichEvent != "") {
		document.getElementById("serverMsg").value = "Wait for other players...";
		if (whichEvent == "Prosperity") {
			var data = JSON.stringify({
				'doneEventProsperity' : 0
			})
			socketConn.send(data);
			arrangeHand();
			return;
		}
	}
}

function parseAICommand(eventData) {
	if(eventData.startsWith("AIDropOut"))
		AIDropOut();
	if (eventData.startsWith("AIremoveFromHand"))
		AIDiscard(eventData.replace("AIremoveFromHand", ""));
	if(eventData.startsWith("AIBidList"))
		AIPlaceBid(event.data.replace("AIBidList", ""));
	if(eventData.startsWith("AIRemoveFromScreen"))
		AIRemoveFromScreen(event.data.replace("AIRemoveFromScreen",""))
}

function AIDropOut() {
	console.log("time to drop out");
	$("#extra1").attr("src", "http://localhost:8080/resources/images/all.png");
	document.getElementById("serverMsg").value = "Dropped out of test, wait for quest to complete";
}

function AIPlaceBid(eventData) {
	console.log(eventData);
	var bidList = eventData.split(";");
	bidList.pop();
	console.log(bidList);
	var data = JSON.stringify({
		'name' : PlayerName,
		'stages' : stageTracker,
		'equipment_info' : bidList,
		'isTest' : true
	});

	setTimeout(function(){ socketConn.send(data); }, 1000);	
}
function getCurrHand() {
	var card1id = document.getElementById("card1").id;
	var card2id = document.getElementById("card2").id;
	var card3id = document.getElementById("card3").id;
	var card4id = document.getElementById("card4").id;
	var card5id = document.getElementById("card5").id;
	var card6id = document.getElementById("card6").id;
	var card7id = document.getElementById("card7").id;
	var card8id = document.getElementById("card8").id;
	var card9id = document.getElementById("card9").id;
	var card10id = document.getElementById("card10").id;
	var card11id = document.getElementById("card11").id;
	var card12id = document.getElementById("card12").id;
	var extra1id = document.getElementById("extra1").id;
	var extra2id = document.getElementById("extra2").id;
	var extra3id = document.getElementById("extra3").id;
	var extra4id = document.getElementById("extra4").id;
	var extra5id = document.getElementById("extra5").id;
	var extra6id = document.getElementById("extra6").id;
	var extra7id = document.getElementById("extra7").id;
	var extra8id = document.getElementById("extra8").id;
	var imageArrayID = [ card1id, card2id, card3id, card4id, card5id, card6id,
			card7id, card8id, card9id, card10id, card11id, card12id, extra1id,
			extra2id, extra3id, extra4id, extra5id, extra6id, extra7id,
			extra8id ];
	handCardID = imageArrayID;
	var card1 = document.getElementById("card1").src;
	var card2 = document.getElementById("card2").src;
	var card3 = document.getElementById("card3").src;
	var card4 = document.getElementById("card4").src;
	var card5 = document.getElementById("card5").src;
	var card6 = document.getElementById("card6").src;
	var card7 = document.getElementById("card7").src;
	var card8 = document.getElementById("card8").src;
	var card9 = document.getElementById("card9").src;
	var card10 = document.getElementById("card10").src;
	var card11 = document.getElementById("card11").src;
	var card12 = document.getElementById("card12").src;
	var extra1 = document.getElementById("extra1").src;
	var extra2 = document.getElementById("extra2").src;
	var extra3 = document.getElementById("extra3").src;
	var extra4 = document.getElementById("extra4").src;
	var extra5 = document.getElementById("extra5").src;
	var extra6 = document.getElementById("extra6").src;
	var extra7 = document.getElementById("extra7").src;
	var extra8 = document.getElementById("extra8").src;
	var imageArray = [ card1, card2, card3, card4, card5, card6, card7, card8,
			card9, card10, card11, card12, extra1, extra2, extra3, extra4,
			extra5, extra6, extra7, extra8 ];
	handCardSRC = imageArray;
}