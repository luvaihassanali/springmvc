//static resources test

var all = { link: "/resources/images/all.png"  };
var back = { link: "/resources/images/card-back.png"  };
var horseImage = { link: "/resources/images/W Horse.jpg"  };
var swordImage = { link: "/resources/images/W Sword.jpg"  };
var daggerImage = { link: "/resources/images/W Dagger.jpg"  };
var lanceImage = { link: "/resources/images/W Lance.jpg"  };
var battleAxImage = { link: "/resources/images/W Battle-ax.jpg"  };
var excaliburImage = { link: "/resources/images/W Excalibur.jpg"  };
var robberKnightImage = { link: "/resources/images/F Robber Knight.jpg"  };
var saxonsImage = { link: "/resources/images/F Saxons.jpg"  };
var boarImage = { link: "/resources/images/F Boar.jpg"  };
var thievesImage = { link: "/resources/images/F Thieves.jpg"  };
var greenKnightImage = { link: "/resources/images/F Green Knight.jpg"  };
var blackKnightImage = { link: "/resources/images/F Black Knight.jpg"  };
var evilKnightImage = { link: "/resources/images/F Evil Knight.jpg"  };
var saxonKnightImage = { link: "/resources/images/F Saxon Knight.jpg"  };
var dragonImage = { link: "/resources/images/F Dragon.jpg"  };
var giantImage = { link: "/resources/images/F Giant.jpg"  };
var mordredImage = { link: "/resources/images/F Mordred.jpg"  };
var sirGImage = { link: "/resources/images/A Sir Gawain.jpg"  };
var sirPeImage = { link: "/resources/images/A King Pellinore.jpg"  };
var sirPImage = { link: "/resources/images/A Sir Percival.jpg"  };
var sirTImage = { link: "/resources/images/A Sir Tristan.jpg"  };
var sirLImage = { link: "/resources/images/A Sir Lancelot.jpg"  };
var sirGaImage = { link: "/resources/images/A Sir Galahad.jpg"  };
var queenGImage = { link: "/resources/images/A Queen Guinevere.jpg"  };
var queenIImage = { link: "/resources/images/A Queen Iseult.jpg"  };
var arthurImage = { link: "/resources/images/A King Arthur.jpg"  };
var merlinImage = { link: "/resources/images/A Merlin.jpg"  };
var amourImage = { link: "/resources/images/Amour.jpg"  };
var chivalrousDeedImage = { link: "/resources/images/E Chivalrous Deed.jpg"  };
var courtCamelotImage = { link: "/resources/images/E Court Called Camelot.jpg"  };
var callToArmsImage = { link: "/resources/images/E King's Call to Arms.jpg"  };
var recognitionImage = { link: "/resources/images/E King's Recognition.jpg"  };
var plagueImage = { link: "/resources/images/E Plague.jpg"  };
var poxImage = { link: "/resources/images/E Pox.jpg"  };
var prosperityImage = { link: "/resources/images/E Prosperity Throughout the Realm.jpg"  };
var queensFavorImage = { link: "/resources/images/E Queen's Favor.jpg"  };
var testMorganImage = { link: "/resources/images/T Test of Morgan Le Fey.jpg", min_bid: 3 };
var testTempImage = { link: "/resources/images/T Test of the Temptation.jpg", min_bid: 0 };
var testBeastImage = { link: "/resources/images/T Test of the Questing Beast.jpg", min_bid: 4 };
var testValorImage = { link: "/resources/images/T Test of Valor.jpg", min_bid: 0  };
var arthurQuestImage = { link: "/resources/images/Q Arthur.jpg", foe:"", stages: 3 };
var beastQuestImage = { link: "/resources/images/Q Beast.jpg", foe:"", stages:  4 };
var boarQuestImage = { link: "/resources/images/Q Boar.jpg", foe:"boarImage", stages: 2};
var dragonQuestImage = { link: "/resources/images/Q Dragon.jpg", foe:"dragonImage", stages: 3 };
var forestQuestImage = { link: "/resources/images/Q Forest.jpg", foe:"", stages: 3 };
var grailQuestImage = { link: "/resources/images/Q Grail.jpg", foe:"All", stages: 5 };
var gkQuestImage = { link: "/resources/images/Q Green.jpg", foe:"greenKnightImage", stages: 4 };
var honorQuestImage = { link: "/resources/images/Q Honor.jpg", foe:"All", stages: 4 };
var maidenQuestImage = { link: "/resources/images/Q Maiden.jpg", foe:"blackKnightImage", stages: 3 };
var saxonQuestImage = { link: "/resources/images/Q Saxon.jpg", foe:"saxonsImage;saxonKnightImage", stages: 2 };
var squireImage = { link: "/resources/images/R Squire.jpg", points: 5};
var knightImage = { link: "/resources/images/R Knight.jpg", points: 7};
var cKnightImage = { link: "/resources/images/R Champion Knight.jpg", points:10  };
var camelotImage = { link: "/resources/images/T1.jpg", bid: 3  };
var orkneyImage = { link: "/resources/images/T2.jpg", bid: 2  };
var tintagelImage = { link: "/resources/images/T3.jpg", bid: 1 };
var yorkImage = { link: "/resources/images/T4.jpg", bid: 0 };

var foeList = [ boarImage, robberKnightImage, saxonsImage, thievesImage,
		greenKnightImage, blackKnightImage, evilKnightImage, saxonKnightImage,
		dragonImage, giantImage, mordredImage ];
var weaponList = [ horseImage, swordImage, daggerImage, lanceImage,
		battleAxImage, excaliburImage ]
var equipmentList = [ horseImage, swordImage, daggerImage, lanceImage,
		battleAxImage, excaliburImage, amourImage, sirGImage, sirPeImage,
		sirPImage, sirTImage, sirLImage, sirGaImage, queenIImage, arthurImage,
		merlinImage ];

var serverMsg = document.getElementById('serverMsg');
var socketConn = new WebSocket('ws://localhost:8080/socketHandler');
var roundOver = false;
var turnTracker = 0;
var numFoeClick;
var numWeaponClick;
var questName, questStages, foeName, foeImage;
var player;
var foe1, foe2;
var p_equip = [];
var f1_weapons = [];
var f2_weapons = [];
for (var i = 0; i < 6; i++) {
	f1_weapons[i] = all.link;
	f2_weapons[i] = all.link;
	p_equip[i] = all.link;
}
var playerPoints, enemyPoints;
var currentStage = 1;
var choosingWeapons = false;
// print server message to console3
socketConn.onmessage = function(event) {

	var serverMsg = document.getElementById('serverMsg');

	// player watching quest
	if (event.data.includes("is going through quest")) {
		serverMsg.value = event.data + " please wait";
	}

	// get battle info
	if (event.data.startsWith("Battle info")) {
		var BattleInfo = event.data.replace('Battle info', '');
		var BattleInfoArray = BattleInfo.split(";");
		parseBattleInfo(BattleInfoArray);

	}
	// its your turn to play
	if (event.data.startsWith("It is your turn")) {
		// get card info
		console.log("ITS MY TURN");
	}
	// no participants for sponsor
	if (event.data == "No participants") {
		serverMsg.value = "No one wanted to participate, receiving sheilds, going to next turn";
		socketConn.send("flipStoryDeck");
	}
	// choosing equipment for battle
	if (event.data == "Choose equipment") {
		document.getElementById('doneEquipment').style.display = "inline";
		serverMsg.value = "Please click on the equipment you want to choose for battle";
	}
	// ask to participate
	if (event.data == ("Ask to participate")) {
		document.getElementById("acceptQuest").style.display = "inline";
		serverMsg.value = "Please accept/decline quest by clicking below"
	}
	// alert player of quest setup
	if (event.data == ("QuestBeingSetup")) {
		serverMsg.value = "Quest is being setup, please wait";

	}
	// accept quest sponsor
	if (event.data.startsWith("QuestInfo")) {

		var questInfo = event.data.replace('QuestInfo', '');
		questInfo = questInfo.split(";");
		questName = questInfo[0];
		questStages = questInfo[1];
		foeName = questInfo[2];
		foeImage = questInfo[3];
		serverMsg.value += "\nChoose " + questStages + " foe cards for stage";
		setupQuestRound();

	}

	// increment turn tracker
	if (event.data.startsWith("turnTracker")) {
		var num = event.data.replace('turnTracker', '');
		turnTracker = num;

	}
	// flip story deck
	if (event.data.startsWith("flipStoryDeck")) {
		serverMsg.value = "Flipping card from Story Deck";
		var card = event.data.replace('flipStoryDeck', '');
		$("#storyCard").attr("src", card);
	}
	// set hand
	if (event.data.startsWith("setHand")) {
		serverMsg.value = "Setting player hand, flipping story deck";
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
	// ask to sponsor quest
	if (event.data === "sponsorQuest") {
		document.getElementById('sponsorQuest').style.display = 'block';
		serverMsg.value = "Click to answer below"
	}

	// if game is full - deny message
	if (event.data.startsWith("Too many players")) {
		serverMsg.value = event.data;
	}
	// welcome
	if (event.data.startsWith("Welcome")) {
		serverMsg.value = event.data;
	}

	// player enrollment
	if (event.data.startsWith("You are all set up")) {
		serverMsg.value = event.data;
	}

	// game started
	if (event.data.startsWith("All players have joined, starting game...")) {
		document.getElementById('print').disabled = false;
		serverMsg.value = event.data;
	}
	// get all player names
	if (event.data.startsWith("clientsString")) {
		var clientString = event.data.replace('clientsString', '');
		serverMsg.value = clientString;
	}

}

socketConn.onopen = function(event) {

	socketConn.send("Player attempting to connect");
	numFoeClick = 0;

};

// parse battle info
function parseBattleInfo(BattleInfoArray) {
	// console.log(BattleInfoArray);
	var i = 0;
	foe1 = BattleInfoArray[i].replace("1quest_foeID", "");
	i++;
	while (BattleInfoArray[i].startsWith("1quest_weaponID")) {
		f1_weapons.pop();
		f1_weapons.unshift(BattleInfoArray[i].replace("1quest_weaponID", ""));
		i++;
	}
	foe2 = BattleInfoArray[i].replace("2quest_foeID", "");
	i++;
	while (BattleInfoArray[i].startsWith("2quest_weaponID")) {
		f2_weapons.pop();
		f2_weapons.unshift(BattleInfoArray[i].replace("2quest_weaponID", ""));
		i++;
	}
	player = BattleInfoArray[i].replace("player_rank", "http://localhost:8080");
	player = player.replace(" ", "%20");
	i++;
	while (BattleInfoArray[i].startsWith("equipmentID")) {
		p_equip.pop();
		p_equip.unshift(BattleInfoArray[i].replace("equipmentID", ""));
		i++;
	}

	displayBattle(currentStage);

}

// display battle on screen
function displayBattle(stage) {
	document.getElementById('battleScreen').style.display = "block";
	if (stage == 1) {
		$("#playerPic").attr("src", player);
		$("#playerWeaponSpot1").attr("src", p_equip[0]);
		$("#playerWeaponSpot2").attr("src", p_equip[1]);
		$("#playerWeaponSpot3").attr("src", p_equip[2]);
		$("#playerWeaponSpot4").attr("src", p_equip[3]);
		$("#playerWeaponSpot5").attr("src", p_equip[4]);
		$("#playerWeaponSpot6").attr("src", p_equip[5]);
		$("#enemyPic").attr("src", foe1);
		$("#enemyWeaponSpot1").attr("src", f1_weapons[0]);
		$("#enemyWeaponSpot2").attr("src", f1_weapons[1]);
		$("#enemyWeaponSpot3").attr("src", f1_weapons[2]);
		$("#enemyWeaponSpot4").attr("src", f1_weapons[3]);
		$("#enemyWeaponSpot5").attr("src", f1_weapons[4]);
		$("#enemyWeaponSpot6").attr("src", f1_weapons[5]);
	}
	if (stage == 2) {
		$("#playerPic").attr("src", player);
		$("#playerWeaponSpot1").attr("src", p_equip[0]);
		$("#playerWeaponSpot2").attr("src", p_equip[1]);
		$("#playerWeaponSpot3").attr("src", p_equip[2]);
		$("#playerWeaponSpot4").attr("src", p_equip[3]);
		$("#playerWeaponSpot5").attr("src", p_equip[4]);
		$("#playerWeaponSpot6").attr("src", p_equip[5]);
		$("#enemyPic").attr("src", foe2);
		$("#enemyWeaponSpot1").attr("src", f2_weapons[0]);
		$("#enemyWeaponSpot2").attr("src", f2_weapons[1]);
		$("#enemyWeaponSpot3").attr("src", f2_weapons[2]);
		$("#enemyWeaponSpot4").attr("src", f2_weapons[3]);
		$("#enemyWeaponSpot5").attr("src", f2_weapons[4]);
		$("#enemyWeaponSpot6").attr("src", f2_weapons[5]);
	}

	currentStage++;
	var BattleInfoClean = "";

	BattleInfoClean += player + ";";
	for (var i = 0; i < p_equip.length; i++) {
		BattleInfoClean += p_equip[i] + ";";
	}
	BattleInfoClean += foe1 + ";";
	for (var i = 0; i < f1_weapons.length; i++) {
		BattleInfoClean += f1_weapons[i] + ";";
	}
	BattleInfoClean += foe2 + ";";
	for (var i = 0; i < f2_weapons.length; i++) {
		BattleInfoClean += f2_weapons[2] + ";";
	}
	calculateBattlePoints(BattleInfoClean);
}

//calculateBattlePoints
function calculateBattlePoints(BattlePointInformation){
	var BattleSpecs = BattlePointInformation.split(";");
	console.log(BattleSpecs); //here
	console.log(testMorganImage.min_bid)
}
// start battle
function startBattle(playerPoints, enemyPoints) {

}

// deny participation in quest
function denyQuestParticipate() {
	document.getElementById('acceptQuest').style.display = "none";
	socketConn.send("Deny participation");
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = ("Waiting for other players to finish quest...");
}

// accept to participate in quest
function acceptQuestParticipate() {
	document.getElementById('acceptQuest').style.display = "none";
	socketConn.send("Accept participation");
	$('body')
			.on(
					'click',
					'#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12',
					function() {
						console.log("card clicked");
						var cardId = this.src.replace('http://localhost:8080',
								'');
						cardId = cardId.split('%20').join(' ');
						console.log(cardId);
						if (checkForEquipment(cardId)) {
							console.log("equipmentID: " + this.id);
							socketConn.send("equipmentID" + this.src);
							var changeImageId = "#" + this.id;
							console.log(changeImageId);
							$(changeImageId).attr("src",
									"/resources/images/all.png");
						}
					})
}

// set up quest round
function setupQuestRound() {
	$('body')
			.on(
					'click',
					'#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12',
					function() {
						console.log("card clicked");
						var cardId = this.src.replace('http://localhost:8080',
								'');
						cardId = cardId.split('%20').join(' ');
						console.log(cardId);
						if (checkForFoe(cardId)) {
							console.log("quest_foeID: " + this.id);
							socketConn.send("quest_foeID" + this.src);
							var changeImageId = "#" + this.id;
							console.log(changeImageId);
							$(changeImageId).attr("src",
									"/resources/images/all.png");
							numFoeClick++;
							var serverMsg = document
									.getElementById('serverMsg');
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
												console.log("card clicked");
												var cardId = this.src
														.replace(
																'http://localhost:8080',
																'');
												cardId = cardId.split('%20')
														.join(' ');
												if (checkForWeapon(cardId)) {
													console
															.log("quest_weaponID: "
																	+ this.id);
													socketConn
															.send("quest_weaponID"
																	+ this.src);
													var changeImageId = "#"
															+ this.id;
													console.log(changeImageId);
													$(changeImageId)
															.attr("src",
																	"/resources/images/all.png");
												}
											})
						} // check for foe
					}) // body off

}

// done picking up equipment before battle
function doneEquipment() {
	document.getElementById('doneEquipment').style.display = "none";
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = "Going into battle";
	$('body')
			.off(
					'click',
					'#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12');
	socketConn.send("Show battle");
}

// done picking cards for weapon selection in quest setup
function doneWeapons() {
	choosingWeapons = false;
	var serverMsg = document.getElementById('serverMsg');
	document.getElementById('doneQuest').style.display = "none";
	//console.log(numFoeClick);
	//console.log(questStages);
	if (numFoeClick == questStages) {
		serverMsg.value = "Done setup";
		$('body')
				.off(
						'click',
						'#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12');
		serverMsg.value += '\nWaiting on results...';
		socketConn.send("Ask to participate");
	} else {
		serverMsg.value = "Choose next foe";
		$('body')
				.off(
						'click',
						'#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12');
		setupQuestRound();
	}
}
// accept to sponsor quest
function acceptSponsorQuest() {
	var serverMsg = document.getElementById('serverMsg');
	document.getElementById('sponsorQuest').style.display = 'none';
	serverMsg.value = "You are sponsor, setting up quest...";
	socketConn.send("acceptSponsorQuest");

}
// deny to sponsor quest
function denySponsorQuest() {
	var serverMsg = document.getElementById('serverMsg');
	socketConn.send("incTurn");
	document.getElementById('sponsorQuest').style.display = 'none';
	serverMsg.value = "Waiting on other players...";
	//console.log(turnTracker);
	if (turnTracker == 3)
		roundOver = true;
	console.log(roundOver);
	if (roundOver == false) {
		socketConn.send("askNextPlayerToSponsor");
	} else {
		roundOver = false;
		socketConn.send("askNextPlayerToSponsorFinishLog");

	}
}
// flip story card
function flipStoryDeck() {
	socketConn.send("flipStoryDeck");
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

// send name to server -> adds client to player list
function send() {
	var clientMsg = document.getElementById('enterName');
	if (clientMsg.value) {
		socketConn.send("Name:" + clientMsg.value);
		document.getElementById('title').innerHTML = "Welcome to the Quest of The Round Table - "
				+ clientMsg.value + "'s View";
		changeColor();
		document.getElementById('enterName').style.display = "none";
		document.getElementById('send').style.display = "none";
	}
}

// print all clients connected
function print() {
	socketConn.send("Print");
}

// proof of connection - prints deck proof also
function proof() {
	socketConn.send("Proof");
}

function checkForFoe(ImageLink) {
	console.log("checking for foe");
	for (var i = 0; i < foeList.length; i++) {
		if (foeList[i] == ImageLink)
			return true;
	}
	return false;
}

function checkForWeapon(ImageLink) {
	console.log("checking for weapon");
	for (var i = 0; i < weaponList.length; i++) {
		if (weaponList[i] == ImageLink)
			return true;
	}
	return false;
}

function checkForEquipment(ImageLink) {
	console.log("checking for equipment");
	for (var i = 0; i < equipmentList.length; i++) {
		if (equipmentList[i] == ImageLink)
			return true;
	}
	return false;
}
