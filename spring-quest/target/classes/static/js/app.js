//static resources test
var serverMsg = document.getElementById('serverMsg');
var socketConn = new WebSocket('ws://localhost:8080/socketHandler');
var roundOver = false;
var turnTracker = 0;
var numFoeClick;
var numWeaponClick;
var questName, questStages, foeName, foeImage;
var foe1, foe2;
var f1_weapon1, f1_weapon2, f1_weapon3, f1_weapon4;
var f2_weapon1, f2_weapon2, f2_weapon3, f2_weapon4;
var playerPoints, enemyPoints;

var choosingWeapons = false;
//print server message to console
socketConn.onmessage = function(event) {

	var serverMsg = document.getElementById('serverMsg');
	
	//get battle info
	if(event.data.startsWith("Battle info")) {
		console.log(event.data);
		var BattleInfo = event.data.replace('Battle info', '');
		var BattleInfoArray = BattleInfo.split(";");
		console.log(BattleInfoArray);
		
	}
	//its your turn to play
	if(event.data.startsWith("It is your turn")) {
		//get card info
		console.log("ITS MY TURN");
	}
	//no participants for sponsor 
	if(event.data=="No participants") {
		serverMsg.value = "No one wanted to participate, receiving sheilds, going to next turn";
		socketConn.send("flipStoryDeck");
	}
	//choosing equipment for battle
	if(event.data=="Choose equipment") {
		document.getElementById('doneEquipment').style.display = "inline";
		serverMsg.value = "Please click on the equipment you want to choose for battle";
	}
	//ask to participate
	if(event.data==("Ask to participate")) {
		document.getElementById("acceptQuest").style.display = "inline";
	}
    //alert player of quest setup
	if(event.data==("QuestBeingSetup")) {
		serverMsg.value = "Quest is being setup, please wait";

	}
	//accept quest sponsor
	if(event.data.startsWith("QuestInfo")) {

		var questInfo = event.data.replace('QuestInfo','');
		questInfo = questInfo.split(";");
        questName = questInfo[0];
        questStages = questInfo[1];
		foeName = questInfo[2];
		foeImage = questInfo[3];
		serverMsg.value += "\nChoose " + questStages + " foe cards for stage"; 
		setupQuestRound();

	}
		
	
	//increment turn tracker
	if(event.data.startsWith("turnTracker")) {
		var num = event.data.replace('turnTracker','');
		turnTracker = num; 
		return;
	}
	//flip story deck
    if(event.data.startsWith("flipStoryDeck")) {
    	serverMsg.value = "Flipping card from Story Deck";
    	var card = event.data.replace('flipStoryDeck','');
    	$("#storyCard").attr("src",card);
    	return;
    }
    //set hand
    if(event.data.startsWith("setHand")) {
    	serverMsg.value = "Setting player hand, flipping story deck";
    	var handString = event.data.replace('setHand','');
        var handStringArray = handString.split(":");
        $("#card1").attr("src",handStringArray[0]);
        $("#card2").attr("src",handStringArray[1]);
        $("#card3").attr("src",handStringArray[2]);
        $("#card4").attr("src",handStringArray[3]);
        $("#card5").attr("src",handStringArray[4]);
        $("#card6").attr("src",handStringArray[5]);
        $("#card7").attr("src",handStringArray[6]);
        $("#card8").attr("src",handStringArray[7]);
        $("#card9").attr("src",handStringArray[8]);
        $("#card10").attr("src",handStringArray[9]);
        $("#card11").attr("src",handStringArray[10]);
        $("#card12").attr("src",handStringArray[11]);
		return;
    }
    //ask to sponsor quest
    if(event.data==="sponsorQuest") {
    	document.getElementById('sponsorQuest').style.display = 'block';
    	serverMsg.value = "Click to answer below"
    	return;
    }
    
    //if game is full - deny message
    if(event.data.startsWith("Too many players")) {
    	serverMsg.value = event.data;
    }
    //welcome
    if(event.data.startsWith("Welcome")) {
    	serverMsg.value = event.data;
    }

    //player enrollment
    if(event.data.startsWith("You are all set up")) {
    	serverMsg.value = event.data;
    }
    
    //game started
    if(event.data.startsWith("All players have joined, starting game...")) {
        document.getElementById('print').disabled = false;
    	serverMsg.value = event.data;
    }
    //get all player names
    if(event.data.startsWith("clientsString")) {
    	var clientString = event.data.replace('clientsString', '');
    	serverMsg.value = clientString;
    }

}

socketConn.onopen = function (event) {
	  socketConn.send("Player attempting to connect"); 
	  numFoeClick = 0;
};

//deny participation in quest
function denyQuestParticipate() {
	document.getElementById('acceptQuest').style.display = "none";
	socketConn.send("Deny participation");
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = ("Waiting for other players to finish quest...");
}

//accept to participate in quest
function acceptQuestParticipate() {
	document.getElementById('acceptQuest').style.display = "none";
	socketConn.send("Accept participation");
	$('body').on('click','#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12',function(){
 		console.log("card clicked");
 		var cardId = this.src.replace('http://localhost:8080', '');
 		cardId = cardId.split('%20').join(' ');
 		console.log(cardId);
 		if(checkForEquipment(cardId)) {
 			console.log("equipmentID: " + this.id);
 			socketConn.send("equipmentID: " + this.src);
	 		var changeImageId = "#" + this.id;
	 		console.log(changeImageId);
	 		$(changeImageId).attr("src","/resources/images/all.png");
 		}
	})
}

//set up quest round
function setupQuestRound() {
	$('body').on('click','#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12',function(){
 		console.log("card clicked");
 		var cardId = this.src.replace('http://localhost:8080', '');
 		cardId = cardId.split('%20').join(' ');
 		console.log(cardId);
		if(checkForFoe(cardId)) {
	 		console.log("quest_foeID: " + this.id);
	 		socketConn.send("quest_foeID" + this.src);
	 		var changeImageId = "#" + this.id;
	 		console.log(changeImageId);
	 		$(changeImageId).attr("src","/resources/images/all.png");
	 		numFoeClick++;
	 		var serverMsg = document.getElementById('serverMsg');
	 		serverMsg.value = "Choose weapons for foe";
	 		document.getElementById('doneQuest').style.display = "inline";
	 		choosingWeapons = true;
	 		$('body').off('click','#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12');
	 		$('body').on('click','#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12',function(){
	 			console.log("card clicked");
	 	 		var cardId = this.src.replace('http://localhost:8080', '');
	 	 		cardId = cardId.split('%20').join(' ');
	 			if(checkForWeapon(cardId)) {
	 		 		console.log("quest_weaponID: " + this.id);
	 		 		socketConn.send("quest_weaponID" + this.src);
	 		 		var changeImageId = "#" + this.id;
	 		 		console.log(changeImageId);
	 		 		$(changeImageId).attr("src","/resources/images/all.png");
	 			}
	 		})
		} // check for foe
	}) //body off
	
}

//done picking up equipment before battle
function doneEquipment() {
	document.getElementById('doneEquipment').style.display = "none";
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = "Going into battle";
	$('body').off('click','#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12');
	startBattle();
	socketConn.send("Show battle");
}

//start battle
function startBattle() { 
	document.getElementById('battleScreen').style.display = "block";
	
}

//done picking cards for weapon selection in quest setup
function doneWeapons() {
	choosingWeapons = false;
	var serverMsg = document.getElementById('serverMsg');
	document.getElementById('doneQuest').style.display = "none";
	console.log(numFoeClick);
	console.log(questStages);
	if(numFoeClick == questStages) {
		serverMsg.value = "Done setup";
		$('body').off('click','#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12');
		serverMsg.value += '\nWaiting on results...';
		socketConn.send("Ask to participate");
	} else {
		serverMsg.value = "Choose next foe";
		$('body').off('click','#card1, #card2, #card3, #card4, #card5, #card6, #card7, #card8, #card9, #card10, #card11, #card12');
		setupQuestRound();
	}
}
//accept to sponsor quest
function acceptSponsorQuest() {
	var serverMsg = document.getElementById('serverMsg');
	document.getElementById('sponsorQuest').style.display = 'none';
	serverMsg.value = "You are sponsor, setting up quest...";
	socketConn.send("acceptSponsorQuest");
	
}
//deny to sponsor quest
function denySponsorQuest() {
	var serverMsg = document.getElementById('serverMsg');
	socketConn.send("incTurn");
	document.getElementById('sponsorQuest').style.display = 'none';
	serverMsg.value = "Waiting on other players...";
	console.log(turnTracker);
	if(turnTracker==3) roundOver = true;
	console.log(roundOver);
	if(roundOver==false) {
	socketConn.send("askNextPlayerToSponsor");
	} else {
		roundOver = false;
		socketConn.send("askNextPlayerToSponsorFinishLog");
		
	}
}
//flip story card
function flipStoryDeck() {
	socketConn.send("flipStoryDeck");
}
//static resource test, changes title back and forth from red and black
function changeColor() {
   var title = document.getElementById('title');
   if (title.className == 'color1') {
   	title.className = 'color2';
   } else {
   	title.className = 'color1';
   }
}

//send name to server -> adds client to player list
function send() {
	var clientMsg = document.getElementById('enterName');
	if (clientMsg.value) {
		socketConn.send("Name:" + clientMsg.value);
		document.getElementById('title').innerHTML = "Welcome to the Quest of The Round Table - " + clientMsg.value + "'s View";
		changeColor();
		document.getElementById('enterName').style.display = "none";
		document.getElementById('send').style.display = "none";
	}
}

//print all clients connected
function print() {
	socketConn.send("Print");
}

//proof of connection - prints deck proof also 
function proof() {
	socketConn.send("Proof");
}


var back = "'/resources/images/card-back.png'";
var horseImage = "/resources/images/W Horse.jpg";
var swordImage = "/resources/images/W Sword.jpg";
var daggerImage = "/resources/images/W Dagger.jpg";
var lanceImage = "/resources/images/W Lance.jpg";
var battleAxImage = "/resources/images/W Battle-ax.jpg";
var excaliburImage = "/resources/images/W Excalibur.jpg";
var robberKnightImage = "/resources/images/F Robber Knight.jpg";
var saxonsImage = "/resources/images/F Saxons.jpg";
var boarImage = "/resources/images/F Boar.jpg";
var thievesImage = "/resources/images/F Thieves.jpg";
var greenKnightImage = "/resources/images/F Green Knight.jpg";
var blackKnightImage = "/resources/images/F Black Knight.jpg";
var evilKnightImage = "/resources/images/F Evil Knight.jpg";
var saxonKnightImage = "/resources/images/F Saxon Knight.jpg";
var dragonImage = "/resources/images/F Dragon.jpg";
var giantImage = "/resources/images/F Giant.jpg";
var mordredImage = "/resources/images/F Mordred.jpg";
var allImage = "/resources/images/all.png";
var sirGImage = "/resources/images/A Sir Gawain.jpg";
var sirPeImage = "/resources/images/A King Pellinore.jpg";
var sirPImage = "/resources/images/A Sir Percival.jpg";
var sirTImage = "/resources/images/A Sir Tristan.jpg";
var sirLImage = "/resources/images/A Sir Lancelot.jpg";
var sirGaImage = "/resources/images/A Sir Galahad.jpg";
var queenGImage = "/resources/images/A Queen Guinevere.jpg";
var queenIImage = "/resources/images/A Queen Iseult.jpg";
var arthurImage = "/resources/images/A King Arthur.jpg";
var merlinImage = "/resources/images/A Merlin.jpg";
var amourImage = "/resources/images/Amour.jpg";
var chivalrousDeedImage = "/resources/images/E Chivalrous Deed.jpg";
var courtCamelotImage = "/resources/images/E Court Called Camelot.jpg";
var callToArmsImage = "/resources/images/E King's Call to Arms.jpg";
var recognitionImage = "/resources/images/E King's Recognition.jpg";
var plagueImage = "/resources/images/E Plague.jpg";
var poxImage = "/resources/images/E Pox.jpg";
var prosperityImage = "/resources/images/E Prosperity Throughout the Realm.jpg";
var queensFavorImage = "/resources/images/E Queen's Favor.jpg";
var testMorganImage = "/resources/images/T Test of Morgan Le Fey.jpg";
var testTempImage = "/resources/images/T Test of the Questing Beast.jpg";
var testBeastImage = "/resources/images/T Test of the Questing Beast.jpg";
var testValorImage = "/resources/images/T Test of Valor.jpg";
var arthurQuestImage = "/resources/images/Q Arthur.jpg";
var beastQuestImage = "/resources/images/Q Beast.jpg";
var boarQuestImage = "/resources/images/Q Boar.jpg";
var dragonQuestImage = "/resources/images/Q Dragon.jpg";
var forestQuestImage = "/resources/images/Q Forest.jpg";
var grailQuestImage = "/resources/images/Q Grail.jpg";
var gkQuestImage = "/resources/images/Q Green.jpg";
var honorQuestImage = "/resources/images/Q Honor.jpg";
var maidenQuestImage = "/resources/images/Q Maiden.jpg";
var saxonQuestImage = "/resources/images/Q Saxon.jpg";
var squireImage = "/resources/images/R Squire.jpg";
var knightImage = "/resources/images/R Knight.jpg";
var cKnightImage = "/resources/images/R Champion Knight.jpg";
var camelotImage = "/resources/images/T1.jpg";
var orkneyImage = "/resources/images/T2.jpg";
var tintagelImage = "/resources/images/T3.jpg";
var yorkImage = "/resources/images/T4.jpg";

var foeList  = [ boarImage, robberKnightImage, saxonsImage, thievesImage, greenKnightImage, blackKnightImage, evilKnightImage, saxonKnightImage, dragonImage, giantImage, mordredImage ];
var weaponList = [ horseImage, swordImage, daggerImage, lanceImage, battleAxImage, excaliburImage ]
var equipmentList = [ horseImage, swordImage, daggerImage, lanceImage, battleAxImage, excaliburImage, amourImage, sirGImage, sirPeImage, sirPImage, sirTImage, sirLImage, sirGaImage, queenIImage, arthurImage, merlinImage ];
function checkForFoe(ImageLink) {
	console.log("checking for foe");
	for(var i=0; i<foeList.length;i++) {
			if(foeList[i] == ImageLink) return true;
		}
		return false;
}

function checkForWeapon(ImageLink) {
	console.log("checking for weapon");
	for(var i=0; i<weaponList.length; i++) {
		if(weaponList[i] == ImageLink) return true;
	}
	return false;
}

function checkForEquipment(ImageLink) {
	console.log("checking for equipment");
	for(var i=0; i<equipmentList.length; i++) {
		if(equipmentList[i] == ImageLink) return true;
	}
	return false;
}
