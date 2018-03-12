//static card resources for spring application
var all = { name: "All", type: "misc" };
var back = { name: "back", type: "misc" };
var horse = { name: "Horse", type: "weapon" };
var sword = { name: "Sword", type: "weapon"};
var dagger = { name: "Dagger", type: "weapon"};
var lance = { name: "Lance", type: "weapon"};
var battleAx = { name: "Battle-ax", type: "weapon"};
var excalibur = { name: "Excalibur", type: "weapon"};
var robberKnight = { name: "Robber Knight", type: "foe"};
var saxons = { name: "Saxons", type: "foe"};
var boar = { name: "Boar", type: "foe"};
var thieves = { name: "Thieves", type: "foe"};
var greenKnight = { name: "Green Knight", type: "foe"};
var blackKnight = { name: "Black Knight", type: "foe"};
var evilKnight = { name: "Evil Knight", type: "foe"};
var saxonKnight = { name: "Saxon Knight", type: "foe"};
var dragon = { name: "Dragon", type: "foe"};
var giant = { name: "Giant", type: "foe"};
var mordred = { name: "Mordred", type: "foe"};
var sirG = { name: "Sir Gawain", type: "ally"};
var sirPe = { name: "King Pellinore", type: "ally"};
var sirP = { name: "Sir Percival", type: "ally"};
var sirT = { name: "Sir Tristan", type: "ally"};
var sirL = { name: "Sir Lance", type: "ally"};
var sirGa = { name: "Sir Galahad", type: "ally"};
var queenG = { name: "Queen Guinevere" , type: "ally"};
var queenI = { name: "Queen Iseult", type: "ally"};
var arthur = { name: "King Arthur", type: "ally"};
var merlin = { name: "Merlin", type: "ally"};
var amour = { name: "Amour", type: "amour"};
var chivalrousDeed = { name: "Chivalrous Deed", type: "event"};
var courtCamelot = { name: "Court Called to Camelot", type: "event"};
var callToArms = { name: "King's Call to Arms", type: "event"};
var recognition = { name: "King's Recognition", type: "event"};
var plague = { name: "Plague", type: "event"};
var pox = { name: "Pox", type: "event"};
var prosperity = { name: "Prosperity Throughout the Realm", type: "event"};
var queensFavor = { name: "Queen's Favor", type: "event"};
var testMorgan = { name: "Test of Morgan Le Fey", type: "test"};
var testTemp = { name: "Test of Temptation", type: "test"};
var testBeast = { name: "Test of the Questing Beast", type: "test"};
var testValor = { name: "Test of Valor", type: "test"};
var arthurQuest = { name: "Vanquish King Arthur's Enemies", type: "quest"};
var beastQuest = { name: "Search for the Questing Beast", type: "quest"};
var boarQuest = { name: "Boar Hunt", type: "quest"};
var dragonQuest = { name: "Slay the Dragon", type: "quest"};
var forestQuest = { name: "Journey through the Enchanted Forest", type: "quest"};
var grailQuest = { name: "Search for the Holy Grail", type: "quest"};
var gkQuest = { name: "Test of the Green Knight", type: "quest"};
var honorQuest = { name: "Defend the Queen's Honor", type: "quest"};
var maidenQuest = { name: "Rescue the Fair Maiden", type: "quest"};
var saxonQuest = { name: "Repel the Saxon Raiders", type: "quest"};
var squire = { name: "Squire", type: "rank"};
var knight = { name: "Knight", type: "rank"};
var cKnight = { name: "Champion Knight", type: "rank"};
var camelot = { name: "At Camelot", type: "tournament"};
var orkney = { name: "At Ornkey", type: "tournament"};
var tintagel = { name: "At Tintagel", type: "tournament"};
var york = { name: "At York", type: "tournament"};
var cardList = [ back, horse, sword, dagger, battleAx, excalibur, robberKnight, saxons, boar, thieves, greenKnight, blackKnight, evilKnight, saxonKnight, dragon, giant, mordred, sirG, sirPe, sirP, sirT, sirL, sirGa, queenG, queenI, arthur, merlin, amour, chivalrousDeed, courtCamelot, callToArms, recognition, plague, pox, prosperity, queensFavor, testMorgan, testTemp, testBeast, testValor, arthurQuest, beastQuest, dragonQuest, forestQuest, grailQuest, gkQuest, honorQuest, maidenQuest, saxonQuest, squire, knight, cKnight, camelot, orkney, tintagel, york];

var PlayerName = "";
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

//when connection is initiated
socketConn.onopen = function(event) {

};

//messages from server received here
socketConn.onmessage = function(event) {

	console.log(event);
	
	var serverMsg = document.getElementById('serverMsg');

	// flip story deck
	if (event.data.startsWith("flipStoryDeck")) {
		serverMsg.value = "Flipping card from Story Deck";
		var card = event.data.replace('flipStoryDeck', '');
		console.log(card);
		card = JSON.parse(card);
		console.log(card);
		$("#storyCard").attr("src", "http://localhost:8080" + card.stringFile);

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
	// welcome message
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
}

// accept to sponsor quest
function acceptSponsorQuest() {
	document.getElementById('sponsorQuest').style.display = 'none';
	var data = JSON.stringify({'name': PlayerName, 'sponsor_quest': true});
	socketConn.send(data);
}
// deny to sponsor quest
function denySponsorQuest() {
	document.getElementById('sponsorQuest').style.display = 'none';
	var data = JSON.stringify({'name': PlayerName, 'sponsor_quest': false});
	socketConn.send(data);
	var serverMsg = document.getElementById('serverMsg');
	serverMsg.value = "Waiting for other players...";
}
// flip story card
function flipStoryDeck() {
	//socketConn.send("flipStoryDeck");
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
		PlayerName = clientMsg.value;
		var data = JSON.stringify({'name': $("#enterName").val()})
		socketConn.send(data);
		document.getElementById('title').innerHTML = "Welcome to the Quest of The Round Table - "
				+ clientMsg.value + "'s View";
		changeColor();
		document.getElementById('enterName').style.display = "none";
		document.getElementById('send').style.display = "none";
	}
}

// print all clients connected
function print() {
	//socketConn.send("Print");
}

// proof of connection - prints deck proof also
function proof() {
	//socketConn.send("Proof");
}


