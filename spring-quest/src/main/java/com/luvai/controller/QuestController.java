package com.luvai.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.luvai.model.Game;
import com.luvai.model.Player;
import com.luvai.model.AdventureCards.FoeCard;
import com.luvai.model.StoryCards.QuestCard;

public class QuestController extends SocketHandler {
	private static final Logger logger = LogManager.getLogger(QuestController.class);

	public static ArrayList<StageController> stageArray = new ArrayList<StageController>();
	ArrayList<Boolean> stageSet = new ArrayList<Boolean>();
	ArrayList<Boolean> stageWeapons = new ArrayList<Boolean>();
	public static StageController stage_1;
	public static StageController stage_2;
	public Player sponsor;
	boolean stage1set = false;
	boolean stage2set = false;
	boolean stage1weapons = false;
	boolean stage2weapons = false;
	FoeCard foe;
	static public int num_stages;

	public QuestController() {

	}

	public void setupQuest(Game gameEngine, WebSocketSession session) throws IOException {
		String name = getNameFromSession(gameEngine, session);
		System.out.println(name);
		logger.info("{} is setting up quest", name);
		QuestCard currentQuest = (QuestCard) gameEngine.storyDeck.faceUp;
		String QuestString = currentQuest.getName() + ";" + currentQuest.getStages() + ";"
				+ currentQuest.getFoe().getName() + ";" + currentQuest.getStringFile();
		session.sendMessage(new TextMessage("QuestInfo" + QuestString));
		sendToAllSessionsExceptCurrent(gameEngine, session, "QuestBeingSetup");
	}

	public String getNameFromSession(Game gameEngine, WebSocketSession s) {
		String name = "";
		for (int i = 0; i < gameEngine.players.size(); i++) {
			if (gameEngine.players.get(i).id == s.getId()) {
				System.out.println("in if");
				name = gameEngine.players.get(i).getName();
			}
		}
		return name;
	}

	public void sendToAllSessionsExceptCurrent(Game gameEngine, WebSocketSession session, String message)
			throws IOException {
		ArrayList<Player> tempList = new ArrayList<Player>();
		for (int i = 0; i < gameEngine.players.size(); i++) {
			if (gameEngine.players.get(i).id == session.getId()) {

			} else {
				tempList.add(gameEngine.players.get(i));
			}
		}
		for (Player p : tempList) {
			p.session.sendMessage(new TextMessage(message));
		}
	}
}

/*
 * public void backToMain() { log.info("{} quest setup is complete",
 * game.s_deck.faceUp.getName());
 * 
 * FXMLLoader loader = new FXMLLoader();
 * loader.setLocation(this.getClass().getResource("/fxml/ChoosingWeapons.fxml"))
 * ; Parent tableViewParent = loader.load();
 * 
 * ChoosingWeaponsController controller = loader.getController(); Scene
 * tableViewScene = new Scene(tableViewParent); Stage window = (Stage)
 * no_weapons1.getScene().getWindow(); window.setScene(tableViewScene);
 * window.show(); window.setTitle(game.getCurrentParticipant().getName() +
 * "'s Turn!"); inPrep = true;
 * 
 * }
 * 
 * public void startQuest(Model game) throws IOException { QuestCard card =
 * (QuestCard) game.s_deck.faceUp; this.foe = (FoeCard) card.getFoe();
 * QuestController.num_stages = card.getStages();
 * if(game.getActivePlayer().isAI()) launchAI(); }
 * 
 * @FXML void setupComplete(ActionEvent event) {
 * 
 * if (num_stages == 2) { if (stage1done.isArmed()) {
 * stage1done.setVisible(false); foe_s2.setDisable(false);
 * test_s2.setDisable(false); stage1weapons = false; stage2set = true; stage1set
 * = true; stage1PromptW.setVisible(false); removeImageEventHandlers(); } if
 * (stage2done.isArmed()) { stage2PromptW.setVisible(false);
 * stage2done.setVisible(false); finished_button.setVisible(true);
 * removeImageEventHandlers(); } } }
 * 
 * @FXML public void pickWeapons(ActionEvent event) { if (num_stages == 2) { if
 * (yes_weapons1.isArmed()) {
 * stage1PromptW.setText("Please click on cards you want to add");
 * addImageEventHandlers(); stage1weapons = true; no_weapons1.setVisible(false);
 * yes_weapons1.setVisible(false); stage1done.setVisible(true); stage1weapons =
 * true; stage1set = true; } if (no_weapons1.isArmed()) {
 * yes_weapons1.setVisible(false); no_weapons1.setVisible(false);
 * stage1done.setVisible(true); stage1PromptW.setVisible(false); } if
 * (yes_weapons2.isArmed()) { addImageEventHandlers();
 * no_weapons2.setVisible(false); yes_weapons2.setVisible(false);
 * stage2PromptW.setText("Please click on cards you want to add");
 * stage2done.setVisible(true); no_weapons2.setVisible(false);
 * yes_weapons2.setVisible(false); stage2set = false; stage2weapons = true; } if
 * (no_weapons2.isArmed()) { yes_weapons2.setVisible(false);
 * no_weapons2.setVisible(false); stage2done.setVisible(true); } } }
 * 
 * @FXML public void pickTestOrFoe(ActionEvent event) { if (num_stages == 2) {
 * if (test_s1.isArmed()) { } // to do if (foe_s1.isArmed()) {
 * test_s1.setVisible(false); foe_s1.setVisible(false);
 * stage1Prompt.setVisible(true); addImageEventHandlers();
 * stage1Image.setVisible(true); } if (test_s2.isArmed()) { } // to do if
 * (foe_s2.isArmed()) { test_s2.setVisible(false); foe_s2.setVisible(false);
 * stage2Prompt.setVisible(true); stage2Image.setVisible(true);
 * addImageEventHandlers(); } } }
 * 
 * public void renderHand() { for (int i = 0; i <
 * game.getActivePlayer().getHand().size(); i++) {
 * cards.get(i).setImage(game.getActivePlayer().getHand().get(i).getImageFile())
 * ; } }
 * 
 * public void addImageEventHandlers() { for (int i = 0; i < cards.size(); i++)
 * { ImageView card = cards.get(i);
 * card.addEventHandler(MouseEvent.MOUSE_CLICKED, handler); } }
 * 
 * public void removeImageEventHandlers() { for (int i = 0; i < cards.size();
 * i++) { ImageView x = cards.get(i);
 * x.removeEventHandler(MouseEvent.MOUSE_CLICKED, handler); } } /* Checks if the
 * selected view image is a FoeCard. Todo: this can be made more generic
 * 
 * private boolean checkFoeClass(Player player, ImageView card){ FoeCard check =
 * new FoeCard(); Class<? extends Card> selected = game.getSelectedCard(player,
 * card).getClass();
 * 
 * if (!selected.isInstance(check)){ System.out.println("not me!" +
 * selected.toString()); return false; }
 * 
 * 
 * return true; } // TODO: find a way to loop over stage view items;
 * EventHandler<MouseEvent> handler = new EventHandler<MouseEvent>() { public
 * void handle(MouseEvent event) { Player sponsor = game.getActivePlayer();
 * ImageView card = (ImageView) event.getSource(); // log.info("{} selected",
 * card.); int cardIndex = game.getCardIndex(sponsor, card); Alert alert = new
 * Alert(Alert.AlertType.ERROR);
 * 
 * AdventureCard repeatCard = null; for (int k = 0; k <
 * CardList.adventureTypes.size(); k++) {
 * 
 * if (CardList.adventureTypes.get(k).getImageFile().equals(card.getImage())) {
 * repeatCard = (AdventureCard) CardList.adventureTypes.get(k); break;
 * 
 * } }
 * 
 * if (num_stages == 2) { if (stage1weapons == true) { for (int i = 0; i <
 * equip1.size(); i++) { ImageView equip = equip1.get(i); if (equip.getImage()
 * == null) { for (int j = 0; j < stage_1.foe.getWeapons().size(); j++) {
 * 
 * if (stage_1.foe.getWeapons().get(j).equals(repeatCard)) {
 * System.out.println("THIS IS A REPEAT"); stage1PromptW.
 * setText("Please click on cards you want to add - no repeat weapons"); return;
 * } } equip.setImage(card.getImage()); WeaponCard w_card = (WeaponCard)
 * sponsor.getHand().get(cardIndex);
 * game.a_deck.discardPile.add(sponsor.getHand().remove(cardIndex));
 * stage_1.foe.weapons.add((WeaponCard) w_card);
 * card.setImage(CardList.allImage); return;
 * 
 * } } } }
 * 
 * if (stage2weapons == true) {
 * 
 * for (int i = 0; i < equip2.size(); i++) { ImageView equip = equip2.get(i);
 * 
 * if (equip.getImage() == null) {
 * 
 * System.out.println(stage_2.foe.getWeapons().size()); for (int j = 0; j <
 * stage_2.foe.getWeapons().size(); j++) {
 * 
 * if (stage_2.foe.getWeapons().get(j).equals(repeatCard)) {
 * System.out.println("THIS IS A REPEAT"); stage2PromptW.
 * setText("Please click on cards you want to add - no repeat weapons"); return;
 * } } equip.setImage(card.getImage()); for (int i1 = 0; i1 <
 * sponsor.getHand().size(); i1++) { if
 * (card.getImage().equals(sponsor.getHand().get(i1).getImageFile())) {
 * WeaponCard w_card = (WeaponCard) sponsor.getHand().get(cardIndex);
 * game.a_deck.discardPile.add(sponsor.getHand().remove(cardIndex));
 * stage_2.foe.weapons.add((WeaponCard) w_card);
 * card.setImage(CardList.allImage); return; // keep this } } } } }
 * 
 * if (stage1set == false) { if (!checkFoeClass(sponsor, card)) {
 * alert.setTitle("Error");
 * alert.setHeaderText("Error: Please select a Foe Card"); //
 * alert.showAndWait(); } else { removeImageEventHandlers(); stage_1 = new
 * StageController(); stage1Image.setImage(card.getImage());
 * stage1Prompt.setVisible(false); card.setImage(CardList.allImage); stage_1.foe
 * = (FoeCard) sponsor.getHand().get(cardIndex);
 * game.a_deck.discardPile.add(sponsor.getHand().remove(cardIndex));
 * stage1PromptW.setVisible(true); yes_weapons1.setVisible(true);
 * no_weapons1.setVisible(true); log.info("Stage 1: {} has selected {}",
 * sponsor.getName(), stage_1.foe.getName()); return; } } else if (stage2set ==
 * true) { if (!checkFoeClass(sponsor, card)) { alert.setTitle("Error");
 * alert.setHeaderText("Error: Please select a Foe Card"); //
 * alert.showAndWait(); } else { removeImageEventHandlers(); stage_2 = new
 * StageController(); stage2Image.setImage(card.getImage());
 * stage2Prompt.setVisible(false); card.setImage(CardList.allImage); stage_2.foe
 * = (FoeCard) sponsor.getHand().get(cardIndex);
 * game.a_deck.discardPile.add(sponsor.getHand().remove(cardIndex));
 * stage2PromptW.setVisible(true); yes_weapons2.setVisible(true);
 * no_weapons2.setVisible(true); log.info("Stage 2: {} has selected {}",
 * sponsor.getName(), stage_2.foe.getName()); } return; } } };
 * 
 * //makes and populates quest stages //wipes AI lists when finished public void
 * launchAI() throws IOException { //arraylist is just for reuse with bigger
 * quests ArrayList<StageController> questStages = new
 * ArrayList<StageController>(); stage_1 = new StageController(); stage_2 = new
 * StageController(); questStages.add(stage_1); questStages.add(stage_2);
 * 
 * int stages = QuestController.num_stages; for(int i = 0; i <
 * questStages.size(); i++){ aiSetStage(i, questStages.get(i)); }
 * 
 * game.getActivePlayer().getAI().clearLists();
 * 
 * }
 * 
 * //grabs stage cards from AI and populates the StageController objects
 * //handles hand discards private void aiSetStage(int stage, StageController
 * qs) { ArrayList<AdventureCard> stageCards = new ArrayList<AdventureCard>();
 * 
 * stageCards.addAll(game.getActivePlayer().getAI().getStage(stage));
 * 
 * for (AdventureCard c : stageCards) { log.info("{} has added {} to stage {}",
 * game.getActivePlayer().getName(), c.getName(), stage + 1); if (c instanceof
 * FoeCard) { qs.setFoe((FoeCard) c); Utils.discardFromHand(c,
 * game.getActivePlayer()); } } for (AdventureCard c : stageCards) { if (c
 * instanceof WeaponCard) { qs.foe.weapons.add((WeaponCard) c);
 * Utils.discardFromHand(c, game.getActivePlayer()); } } } }
 * 
 */