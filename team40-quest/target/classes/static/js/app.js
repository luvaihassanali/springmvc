var isAI = false;
var strategyType = "";
var PlayerName = "";
var serverMsg = document.getElementById('serverMsg');
var socketConn = new WebSocket('ws://localhost:8080/socketHandler');
var storyCardFaceUp;
var stageCounter = 1;
var stageTracker = 0;
var foes = [];
var weapons = [];
var tempWeaponArr = [];
var tempWeaponArr2 = [];
var tempBidArr = [];
var questInfo;
var participantInfo;
var FoeInfo = "";
var TestInfo = "";
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
// when connection is initiated
socketConn.onopen = function(event) {

};

// messages from server received here
socketConn.onmessage = function(event) {

	var serverMsg = document.getElementById('serverMsg');

	// undisable flip button
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
	// ask to sponsor quest
	if (event.data === "sponsorQuest") {
		document.getElementById('sponsorQuest').style.display = 'block';
		serverMsg.value = "Click to answer below"
		if (isAI) {
			var data = JSON.stringify({
				'AICommand' : "SponsorQuest",
				'name' : PlayerName
			})
			socketConn.send(data);
			document.getElementById('sponsorQuest').style.display = 'none';
		}
	}

	// get current quest info
	if (event.data.startsWith("currentQuestInfo")) {
		questInfo = event.data.replace("currentQuestInfo", "");
		// questInfo = JSON.parse(questInfo);
		questInfo = questInfo.split(",");
		questInfo[0] = questInfo[0].replace("[", "");
		questInfo[questInfo.length - 1] = questInfo[questInfo.length - 1]
				.replace("]", "");
		console.log(questInfo);
	}

	// participation in quest question
	if (event.data == "AskToParticipate") {
		document.getElementById("acceptQuest").style.display = "inline";
		serverMsg.value = "Please accept/decline quest by clicking below"
		if (isAI) {
			var data = JSON.stringify({
				'AICommand' : "AskToParticipateQuest"
			})
			socketConn.send(data);
			document.getElementById("acceptQuest").style.display = "none";
			serverMsg.value = "Wait for other players...";
		}
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
	}
	// getting shields
	if (event.data.startsWith("Getting")) {
		console.log("here");
		serverMsg.value = event.data;
	}
	// pick up cards used for sponsor
	if (event.data.startsWith("SponsorPickup")) {
		serverMsg.value = "Replacing cards used to sponsor quest RIGHT CLICK TO DISCARD EXTRA to move on\n";
		var temp = event.data.replace("SponsorPickup", "");
		sponsorPickup(temp);
	}

	// choosing equipment for battle
	if (event.data == "Choose equipment") {
		console.log(stageCounter);
		console.log(questInfo);
		console.log(questInfo[stageCounter - 1]);
		if (questInfo[stageCounter - 1].includes("Test")) {
			document.getElementById('doneEquipment').style.display = "inline";
			document.getElementById('doneEquipment').disabled = false;
			minBid = TestInfo[1];
			console.log("minBid" + minBid);
			serverMsg.value = "Please click on the cards you wish to bid for test (Click done to drop out)";
			$('body')
					.on(
							'click',
							'#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12, #extra1, #extra2, #extra3, #extra4, #extra5, #extra6, #extra7, #extra8',
							function() {
							
								var cardId = this.src.replace(
										'http://localhost:8080', '');
								cardId = cardId.split('%20').join(' ');
								console.log("adding to bid array" + getNameFromLink(cardId));
								tempBidArr.push(getNameFromLink(cardId));
								var changeImageId = "#" + this.id;
								numCards--;
								$(changeImageId).attr("src",
										"/resources/images/all.png");
								console.log(tempBidArr.length);
								if(tempBidArr.length == 4 ) document.getElementById('doneEquipment').disabled = false;
								console.log(numCards)
							})
			return;
			// get min bid
		} else {

			teamWeaponArr2 = [];
			document.getElementById('doneEquipment').style.display = "inline";
			serverMsg.value = "Please click on the equipment you want to choose for battle";
			$('body')
					.on(
							'click',
							'#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12, #extra1, #extra2, #extra3, #extra4, #extra5, #extra6, #extra7, #extra8',
							function() {

								var cardId = this.src.replace(
										'http://localhost:8080', '');
								cardId = cardId.split('%20').join(' ');
								if (checkForEquipment(this.src) != "card not found") {
									for (var i = 0; i < tempWeaponArr2.length; i++) {
										if ((checkForEquipment(this.src)) == tempWeaponArr2[i]) {
											var serverMsg = document
													.getElementById('serverMsg');
											serverMsg.value += "\nCannot choose repeat weapons";
											return;
										}
									}
									tempWeaponArr2
											.push(checkForEquipment(this.src))
									var changeImageId = "#" + this.id;
									numCards--;
									$(changeImageId).attr("src",
											"/resources/images/all.png");
									if (numCards <= 12) {
										document.getElementById("doneEquipment").disabled = false;
									}
								}

							})
		}
	}

	// get current participant info
	if (event.data.startsWith("currentParticipantInfo")) {
		participantInfo = event.data.replace("currentParticipantInfo", "");
		participantInfo = JSON.parse(participantInfo);
		console.log(participantInfo);
		currentStage = participantInfo.stages;
	}

	//get current playre bids
	if (event.data.startsWith("currentPlayerBids")) {
		var bids = event.data.replace("currentPlayerBids","");
		currentPlayerInfoBids = bids.split(";");
		currentPlayerBids = currentPlayerInfoBids[0];
		console.log(currentPlayerBids);
		if (PlayerName == sponsor) {
			stageCounter = 1;
			sponsor = "";
		}
		displayBattle(stageCounter);
		//going to display battle
	}
	// get current player pts
	if (event.data.startsWith("currentPlayerPoints")) {
		var pts = event.data.replace("currentPlayerPoints", "");
		currentPlayerInfo = pts;
		// console.log(PlayerName);
		// console.log(sponsor);

		if (PlayerName == sponsor) {
			stageCounter = 1;
			sponsor = "";
		}
		displayBattle(stageCounter);
	}

	// get foe info
	if (event.data.startsWith("FoeInfo")) {
		var temp = event.data.replace("FoeInfo", "");
		FoeInfo = temp;
		console.log(FoeInfo);
	}

	// get test info
	if (event.data.startsWith("TestInfo")) {
		var temp = event.data.replace("TestInfo", "");
		TestInfo = temp;
		TestInfo = TestInfo.split("#");
		console.log(TestInfo)
	}

	// new round
	if (event.data == "NextRound") {
		console.log("next round " + stageCounter);
	}

	// get card pick up on starting stage
	if (event.data.startsWith("pickupBeforeStage")) {
		var pickUpLink = event.data.replace("pickupBeforeStage", "");
		$("#extra1").attr("src", "http://localhost:8080" + pickUpLink);
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
		var imageArray = [ card1, card2, card3, card4, card5, card6, card7,
				card8, card9, card10, card11, card12, extra1, extra2, extra3,
				extra4, extra5, extra6, extra7, extra8 ];
		var cardTracker = 0;
		for (var i = 0; i < imageArray.length; i++) {
			var tempCardLink = imageArray[i].replace("http://localhost:8080",
					"");
			tempCardLink = tempCardLink.split('%20').join(' ');
			// console.log(tempCardLink);
			if (tempCardLink != "/resources/images/all.png")
				cardTracker++;
		}
		// console.log(cardTracker);
		numCards = cardTracker;
		if (cardTracker > 12) {
			document.getElementById("doneEquipment").disabled = true;
			serverMsg.value += " You must choose a card to continue (or right-click to discard) ";
		}
	}

	// increase stage counter
	if (event.data == "incStage") {
		stageCounter++;

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
		stageTracker = storyCardFaceUp.stages;
		$("#storyCard").attr("src", "http://localhost:8080" + card.stringFile);
		arrangeHand();

	}
	// set hand
	if (event.data.startsWith("setHand")) {
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

	// if game is full - deny message
	if (event.data.startsWith("Too many players")) {
		serverMsg.value = event.data;
		document.getElementById("send").disabled = true;
		document.getElementById("proof").disabled = true;

	}

	// show rig button
	if (event.data == "showRigger") {
		document.getElementById("rigger").style.display = "block";
	}
	// get all player names
	if (event.data.startsWith("clientsString")) {
		var clientString = event.data.replace('clientsString', '');
		serverMsg.value = clientString;
	}

	// update stat pane
	if (event.data.startsWith("updateStats")) {
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
		serverMsg.value = "Wait for other players...";
	}
	// game started
	if (event.data == "GameReadyToStart") {
		document.getElementById('print').disabled = false;
		serverMsg.value = "All players have joined, starting game, wait for your turn..."
		document.getElementById('rigger').style.display = "none";
	}
}
// pick up x cards
function PickupCards(newCards) {

	if (newCards.startsWith("PickupCardsProsperity")) {
		whichEvent = "Prosperity";
		newCards = newCards.replace("PickupCardsProsperity", "");
	}

	newCards = newCards.split(";");
	newCards.pop();

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

	var numNewCards = newCards.length;

	for (var i = 0; i < imageArrayID.length; i++) {
		if (imageArray[i] == "http://localhost:8080/resources/images/all.png") {
			var imageId = imageArrayID[i];
			$("#" + imageId).attr("src",
					"http://localhost:8080" + newCards.pop());
			if (newCards.length == 0)
				break;
		}
	}

	var cardTracker = 0;
	for (var i = 0; i < imageArray.length; i++) {
		var tempCardLink = imageArray[i].replace("http://localhost:8080", "");
		tempCardLink = tempCardLink.split('%20').join(' ');
		// console.log(tempCardLink);
		if (tempCardLink != "/resources/images/all.png")
			cardTracker++;
	}
	// console.log(numNewCards);
	cardTracker += numNewCards;
	numCards = cardTracker;
	// console.log(cardTracker);
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
}
// sponsor pickup
function sponsorPickup(cards) {

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
	var pickUpLinks = event.data.replace("SponsorPickup", "");

	var pickUpLinksArr = pickUpLinks.split(";");

	pickUpLinksArr.pop();
	var numNewCards = pickUpLinksArr.length;

	for (var i = 0; i < imageArrayID.length; i++) {
		if (imageArray[i] == "http://localhost:8080/resources/images/all.png") {
			var imageId = imageArrayID[i];
			$("#" + imageId).attr("src",
					"http://localhost:8080" + pickUpLinksArr.pop());
			if (pickUpLinksArr.length == 0)
				break;
		}
	}

	var cardTracker = 0;
	for (var i = 0; i < imageArray.length; i++) {
		var tempCardLink = imageArray[i].replace("http://localhost:8080", "");
		tempCardLink = tempCardLink.split('%20').join(' ');
		// console.log(tempCardLink);
		if (tempCardLink != "/resources/images/all.png")
			cardTracker++;
	}
	// console.log(numNewCards);
	cardTracker += numNewCards;
	numCards = cardTracker;
	// console.log(cardTracker);
	if (cardTracker > 12) {
		var serverMsg = document.getElementById('serverMsg');
		serverMsg.value += " Right click extra cards to continue (for discard) ";
		discard();

	}
}
// display current battle
function displayBattle(stage) {
	console.log(questInfo);
	console.log(questInfo[stageCounter - 1]);
	if (questInfo[stageCounter - 1].includes("Test")) {
		console.log("This is a test");
		console.log(tempBidArr.length);
		console.log(minBid);
		var minBidInt = minBid.replace(";", "");
		console.log(minBidInt);
		if(tempBidArr.length >= minBidInt) testResult = true; 
		var datat = JSON.stringify({
			'nextQuestTurn' : testResult,
			'type' : "Test"
		});
		if (PlayerName == participantInfo.name) {
			socketConn.send(datat);
		}
		return;
	} else {
		console.log("This is a battle");
		console.log(FoeInfo);
		console.log(participantInfo);
		console.log(currentPlayerInfo);
		var foePts = FoeInfo.split("#");
		
		var datab = JSON.stringify({
			'nextQuestTurn' : true,
			'type' : "Foe"
		});
		if (PlayerName == participantInfo.name) {
			socketConn.send(datab);
		}
	}
}
/*
 * document.getElementById('battleScreen').style.display = "block"; var
 * playerInfo = currentPlayerInfo.split(";"); console.log(playerInfo);
 * currentPlayerPts = playerInfo[0]; var playerCardLink =
 * getLinkFromName(playerInfo[1]); var weaponArr = tempWeaponArr2;
 * tempWeaponArr2 = []; var weaponLinks = []; for (var j = 0; j <
 * weaponArr.length; j++) { weaponLinks.push(getLinkFromName(weaponArr[j])); }
 * 
 * var foeLinks = []; var foeWeaponNames = questInfo.weapons[stage-1];
 * console.log(foeWeaponNames); var foeWeaponLinks = []; var currentStageWeps =
 * [];
 * 
 * for (var i = 0; i < foeWeaponNames.length; i++) {
 * currentStageWeps.push(getLinkFromName(foeWeaponNames[i])); }
 * 
 * //console.log(currentStageWeps); //console.log(FoeInfo); var foesInfo =
 * FoeInfo.split(";"); for (var i = 0; i < foesInfo.length; i++) { foesInfo[i] =
 * foesInfo[i].split("#"); foeLinks.push(getLinkFromName(foesInfo[i][0])); }
 * foeLinks.pop(); //console.log(foeLinks);
 * 
 * var pequip = []; var fequip = []; for (var i = 0; i < 6; i++) { pequip[i] =
 * all.link; fequip[i] = all.link; }
 * 
 * for (var i = 0; i < weaponLinks.length; i++) { pequip.pop();
 * pequip.unshift(weaponLinks[i]); }
 * 
 * for (var i = 0; i < currentStageWeps.length; i++) { fequip.pop();
 * fequip.unshift(currentStageWeps[i]); } //console.log(fequip);
 * $("#playerPic").attr("src", playerCardLink);
 * $("#playerWeaponSpot1").attr("src", pequip[0]);
 * $("#playerWeaponSpot2").attr("src", pequip[1]);
 * $("#playerWeaponSpot3").attr("src", pequip[2]);
 * $("#playerWeaponSpot4").attr("src", pequip[3]);
 * $("#playerWeaponSpot5").attr("src", pequip[4]);
 * $("#playerWeaponSpot6").attr("src", pequip[5]); $("#enemyPic").attr("src",
 * foeLinks[stage - 1]); $("#enemyWeaponSpot1").attr("src", fequip[0]);
 * $("#enemyWeaponSpot2").attr("src", fequip[1]);
 * $("#enemyWeaponSpot3").attr("src", fequip[2]);
 * $("#enemyWeaponSpot4").attr("src", fequip[3]);
 * $("#enemyWeaponSpot5").attr("src", fequip[4]);
 * $("#enemyWeaponSpot6").attr("src", fequip[5]); var playerWin; var tempPPoints =
 * (Number)(currentPlayerPts); var tempFPoints = (Number)(foesInfo[stage-1][1]);
 * console.log("player points: " + tempPPoints); console.log("foe points: " +
 * tempFPoints); if(tempPPoints >= tempFPoints) { playerWin = true; } else {
 * playerWin = false; } console.log("player won battle: " + playerWin);
 * 
 * if(playerWin) { document.getElementById("p_win").style.display = "block";
 * document.getElementById("f_lose").style.display = "block"; var serverMsg =
 * document.getElementById('serverMsg');
 * if(serverMsg.value.startsWith("Player")) { setTimeout(function(){
 * serverMsg.value = "This battle was won...please wait for next round to
 * complete"; document.getElementById('battleScreen').style.display = "none";
 * document.getElementById("p_win").style.display = "none";
 * document.getElementById("f_lose").style.display = "none";
 * document.getElementById("p_lose").style.display = "none";
 * document.getElementById("f_win").style.display = "none"; }, 5000); } else {
 * setTimeout(function(){ var serverMsg = document.getElementById('serverMsg');
 * serverMsg.value += " Round won, going to next player turn";
 * document.getElementById('battleScreen').style.display = "none";
 * document.getElementById("p_win").style.display = "none";
 * document.getElementById("f_lose").style.display = "none";
 * document.getElementById("p_lose").style.display = "none";
 * document.getElementById("f_win").style.display = "none"; var data =
 * JSON.stringify({ 'nextQuestTurn' : true}); if(PlayerName ==
 * participantInfo.name) { socketConn.send(data); } }, 5000); } } else {
 * document.getElementById("p_lose").style.display = "block";
 * document.getElementById("f_win").style.display = "block";
 * setTimeout(function(){ var serverMsg = document.getElementById('serverMsg');
 * if(serverMsg.value=="Going into battle - ") { var data = JSON.stringify({
 * 'nextQuestTurn' : false}); if(PlayerName == participantInfo.name) {
 * socketConn.send(data);} } serverMsg.value += " Round lost, going to next
 * turn... "; document.getElementById('battleScreen').style.display = "none";
 * document.getElementById("p_win").style.display = "none";
 * document.getElementById("f_lose").style.display = "none";
 * document.getElementById("p_lose").style.display = "none";
 * document.getElementById("f_win").style.display = "none"; }, 5000); } } }
 */

// setup quest if sponsor
function setupQuestRound() {

	if (stageCounter <= stageTracker) {
		var serverMsg = document.getElementById('serverMsg');
		serverMsg.value += "\nChoose foe or test card for stage: "
				+ stageCounter;
		$('body')
				.on(
						'click',
						'#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12',
						function() {
							var cardSrc = this.src.replace(
									'http://localhost:8080', '');
							cardSrc = cardSrc.split('%20').join(' ');
							var cardNameT = checkForCardType(cardSrc, "test");
							if (cardNameT != "card not found") {
								foes.push(cardNameT);
								var changeImageId = "#" + this.id;
								$(changeImageId).attr("src",
										"/resources/images/all.png");
								stageCounter++;
								if (stageCounter > stageTracker) {
									serverMsg.value = "Done setup";
									serverMsg.value += '\nWaiting on results...';

									var questData = JSON.stringify({
										'QuestSetupCards' : foes,
										'weapons' : weapons
									})
									socketConn.send(questData);
									return;
								}
								serverMsg.value = "Choose foe or test card for stage: "
										+ stageCounter;
								return;
							}
							var cardName = checkForCardType(cardSrc, "foe");
							if (cardName != "card not found") {
								foes.push(cardName);
								var changeImageId = "#" + this.id;
								$(changeImageId).attr("src",
										"/resources/images/all.png");
								serverMsg.value = "Choose weapons for foe";
								document.getElementById('doneQuest').style.display = "inline";
								choosingWeapons = true;
								$('body')
										.off(
												'click',
												'#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12');
								$('body')
										.on(
												'click',
												'#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12',
												function() {
													var cardSrc = this.src
															.replace(
																	'http://localhost:8080',
																	'');
													cardSrc = cardSrc.split(
															'%20').join(' ');
													var cardName = checkForCardType(
															cardSrc, "weapon");
													if (cardName != "card not found") {
														for (var i = 0; i < tempWeaponArr.length; i++) {
															if (cardName == tempWeaponArr[i]) {
																var serverMsg = document
																		.getElementById('serverMsg');
																serverMsg.value += "\nCannot choose repeat weapons";
																return;
															}
														}
														tempWeaponArr
																.push(cardName)
														var changeImageId = "#"
																+ this.id;
														$(changeImageId)
																.attr("src",
																		"/resources/images/all.png");
													}
												})
							}
						})
	}
}

// discard function
function discard() {
	$('body')
			.on(
					'contextmenu',
					'#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12, #extra1, #extra2, #extra3, #extra4, #extra5, #extra7, #extra6, #extra7, #extra8',
					function() {
						if (numCards == 12)
							return false;
						var cardSrc = this.src.replace('http://localhost:8080',
								'');
						if (cardSrc == "/resources/images/all.png") {
						} else {
							cardSrc = cardSrc.split('%20').join(' ');
							var discardName = getNameFromLink(cardSrc);
							var data = JSON.stringify({
								'discard' : discardName
							});
							socketConn.send(data);

							if (PlayerName == sponsor) {
								sponsorDiscardTracker++;
								if (sponsorDiscardTracker == stageTracker) {
									var data = JSON.stringify({
										'incTurnRoundOver' : true
									});
									// socketConn.send(data);
									console.log("out here now");
									sponsorDiscardTracker == 0;
								}
							}
						}
						if (this.src != "http://localhost:8080/resources/images/all.png") {
							if (numCards > 12) {
								$(this)
										.attr("src",
												"/resources/images/all.png");
								numCards--;
								if (numCards >= 12) {

									if (whichEvent != "" && numCards == 12) {

										document.getElementById("serverMsg").value = "Wait for other players...";
										if (whichEvent == "Prosperity") {
											console.log("sending prosperity");
											var data = JSON.stringify({

												'doneEventProsperity' : 0
											})
											socketConn.send(data);
											arrangeHand();
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
	stageCounter = 1;
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
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = ("Click on the equipment you wish to use for battle\nReceiving 1 card");
	document.getElementById('doneEquipment').style.display = "inline";
	$('body')
			.on(
					'click',
					'#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12, #extra1, #extra2, #extra3, #extra4, #extra5, #extra7, #extra6, #extra7, #extra8',
					function() {

						if (numCards >= 12) {
							document.getElementById("doneEquipment").disabled = false;
						}

						var cardSrc = this.src.replace('http://localhost:8080',
								'');
						cardSrc = cardSrc.split('%20').join(' ');
						var allyCardName = checkForCardType(cardSrc, "ally");
						var weaponCardName = checkForCardType(cardSrc, "weapon");
						var amourCardName = checkForCardType(cardSrc, "amour");

						if (allyCardName != "card not found") {
							tempWeaponArr2.push(allyCardName)
							var changeImageId = "#" + this.id;
							numCards--;
							$(changeImageId).attr("src",
									"/resources/images/all.png");
						}
						if (weaponCardName != "card not found") {
							tempWeaponArr2.push(weaponCardName)
							var changeImageId = "#" + this.id;
							numCards--;
							$(changeImageId).attr("src",
									"/resources/images/all.png");
						}
						if (amourCardName != "card not found") {
							tempWeaponArr2.push(amourCardName)
							var changeImageId = "#" + this.id;
							numCards--;
							$(changeImageId).attr("src",
									"/resources/images/all.png");
						}

					})
}

// send weapon info - done choosing
function doneEquipment() {
	$('body').off('click');
	document.getElementById('doneEquipment').style.display = "none";
	var serverMsg = document.getElementById('serverMsg');
	console.log(questInfo[stageCounter - 1]);
	if (questInfo[stageCounter - 1].includes("Test")) {
		serverMsg.value = "Placing bids, please wait for other players...- ";
		var data = JSON.stringify({
			'name' : PlayerName,
			'stages' : stageCounter,
			'equipment_info' : tempBidArr,
			'isTest' : true
		});

		socketConn.send(data);
		arrangeHand();
	} else {
		serverMsg.value = "Going into battle - ";
		var data = JSON.stringify({
			'name' : PlayerName,
			'stages' : stageCounter,
			'equipment_info' : tempWeaponArr2
		});

		socketConn.send(data);
		arrangeHand();
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

// finished setting up quest for sponsor
function doneWeaponsQuestSponsor() {
	$('body').off('click');
	var serverMsg = document.getElementById('serverMsg');
	document.getElementById('doneQuest').style.display = "none";

	if (stageCounter == stageTracker) {
		weapons.push(tempWeaponArr);
		tempWeaponArr = [];
		serverMsg.value = "Done setup";
		serverMsg.value += '\nWaiting on results...';
		var questData = JSON.stringify({
			'QuestSetupCards' : foes,
			'weapons' : weapons
		})
		socketConn.send(questData);
	} else {
		serverMsg.value = "Choose next foe";
		weapons.push(tempWeaponArr);
		tempWeaponArr = [];
		stageCounter++;
		setupQuestRound();
	}

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
	document.getElementById('sponsorQuest').style.display = 'none';
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = "You are sponsor, setting up quest...";
	sponsor = PlayerName;
	var data = JSON.stringify({
		'name' : PlayerName,
		'sponsor_quest' : true
	});
	socketConn.send(data);
	setupQuestRound();
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
// flip story card
function flipStoryDeck() {
	var data = JSON.stringify({
		'flipStoryDeck' : 0
	})
	socketConn.send(data);
	document.getElementById('flip').disabled = true;
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
	link : "/resources/images/F Robber Knight.jpg"
};
var saxons = {
	name : "Saxons",
	type : "foe",
	link : "/resources/images/F Saxons.jpg"
};
var boar = {
	name : "Boar",
	type : "foe",
	link : "/resources/images/F Boar.jpg"
};
var thieves = {
	name : "Thieves",
	type : "foe",
	link : "/resources/images/F Thieves.jpg"
};
var greenKnight = {
	name : "Green Knight",
	type : "foe",
	link : "/resources/images/F Green Knight.jpg"
};
var blackKnight = {
	name : "Black Knight",
	type : "foe",
	link : "/resources/images/F Black Knight.jpg"
};
var evilKnight = {
	name : "Evil Knight",
	type : "foe",
	link : "/resources/images/F Evil Knight.jpg"
};
var saxonKnight = {
	name : "Saxon Knight",
	type : "foe",
	link : "/resources/images/F Saxon Knight.jpg"
};
var dragon = {
	name : "Dragon",
	type : "foe",
	link : "/resources/images/F Dragon.jpg"
};
var giant = {
	name : "Giant",
	type : "foe",
	link : "/resources/images/F Giant.jpg"
};
var mordred = {
	name : "Mordred",
	type : "foe",
	link : "/resources/images/F Mordred.jpg"
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
	name : "Sir Lance",
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

function AIDiscard(cardNames) {

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

	for (var j = 0; j < cardNames.length; j++) {
		for (var i = 0; i < imageArrayai.length; i++) {
			var tempSrc = getLinkFromName(cardNames[j]);
			if (tempSrc == imageArrayai[i]) {
				$("#" + imageArrayaiID[i]).attr("src",
						"http://localhost:8080/resources/images/all.png");
				var data = JSON.stringify({
					'discard' : cardNames[j]
				});
				socketConn.send(data);
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
	if (eventData.startsWith("AIremoveFromHand"))
		AIDiscard(eventData.replace("AIremoveFromHand", ""));
}