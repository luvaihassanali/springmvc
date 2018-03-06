//static resources test
var socketConn = new WebSocket('ws://localhost:8080/socketHandler');

//print server message to console
socketConn.onmessage = function(event) {
	
	var serverMsg = document.getElementById('serverMsg');
	
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
	serverMsg.value = event.data;
}

socketConn.onopen = function (event) {
	  socketConn.send("Player attempting to connect"); 
};

function flipStoryDeck() {
	socketConn.send("flipStoryDeck");
}

function changeColor() {
   var title = document.getElementById('title');
   if (title.className == 'color1') {
   	title.className = 'color2';
   } else {
   	title.className = 'color1';
   }
}
//send name to server
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

//proof of connection
function proof() {
	socketConn.send("Proof");
}