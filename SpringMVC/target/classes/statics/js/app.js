//static resources test
var socketConn = new WebSocket('ws://localhost:8080/socketHandler');
var roundOver = false;
var turnTracker = 0;
//print server message to console
socketConn.onmessage = function(event) {
	
	var serverMsg = document.getElementById('serverMsg');
	
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

	serverMsg.value = event.data;
}

socketConn.onopen = function (event) {
	  socketConn.send("Player attempting to connect"); 
};

//accept to sponsor quest
function acceptSponsorQuest() {
	var serverMsg = document.getElementById('serverMsg');
	document.getElementById('sponsorQuest').style.display = 'none';
	serverMsg.value = "You are sponsor, setting up quest...";
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
		console.log("in end of round");
		roundOver = false;
		socketConn.send("flipStoryDeck");
		return;
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
	var clientMsg = document.getElementById('clientMsg');
	if (clientMsg.value) {
		socketConn.send("Name:" + clientMsg.value);
		document.getElementById('name').innerHTML = clientMsg.value + "'s View";
		clientMsg.value = '';
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