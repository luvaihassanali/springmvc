package com.luvai.model;

import java.util.ArrayList;

import com.luvai.model.AdventureCards.AllyCard;
import com.luvai.model.AdventureCards.AmourCard;
import com.luvai.model.AdventureCards.FoeCard;
import com.luvai.model.AdventureCards.TestCard;
import com.luvai.model.AdventureCards.WeaponCard;
import com.luvai.model.StoryCards.EventCard;
import com.luvai.model.StoryCards.QuestCard;
import com.luvai.model.StoryCards.TournamentCard;

public class CardList {

	public ArrayList<Card> adventureTypes;
	public ArrayList<Card> allTypes;
	public ArrayList<Card> exclusionList;

	public CardList() {

		adventureTypes = new ArrayList<Card>();
		adventureTypes.add(Horse);
		adventureTypes.add(Sword);
		adventureTypes.add(Dagger);
		adventureTypes.add(Lance);
		adventureTypes.add(Battleax);
		adventureTypes.add(Excalibur);
		adventureTypes.add(RobberKnight);
		adventureTypes.add(Boar);
		adventureTypes.add(Thieves);
		adventureTypes.add(Saxons);
		adventureTypes.add(SaxonKnight);
		adventureTypes.add(EvilKnight);
		adventureTypes.add(GreenKnight);
		adventureTypes.add(BlackKnight);
		adventureTypes.add(Dragon);
		adventureTypes.add(Giant);
		adventureTypes.add(Mordred);
		adventureTypes.add(SirGawain);
		adventureTypes.add(SirPellinore);
		adventureTypes.add(SirPercival);
		adventureTypes.add(SirTristan);
		adventureTypes.add(KingArthur);
		adventureTypes.add(QueenGuinevere);
		adventureTypes.add(Merlin);
		adventureTypes.add(QueenIseult);
		adventureTypes.add(SirLancelot);
		adventureTypes.add(SirGalahad);
		adventureTypes.add(Amour);
		adventureTypes.add(BeastTest);
		adventureTypes.add(TemptationTest);
		adventureTypes.add(ValorTest);
		adventureTypes.add(MorganTest);

		exclusionList = new ArrayList<Card>();
		exclusionList.add(Quest6);
		exclusionList.add(Event8);
		exclusionList.add(Event1);
		exclusionList.add(Quest5);

		allTypes = new ArrayList<Card>();
		allTypes.add(Horse);
		allTypes.add(Sword);
		allTypes.add(Dagger);
		allTypes.add(Lance);
		allTypes.add(Battleax);
		allTypes.add(Excalibur);
		allTypes.add(RobberKnight);
		allTypes.add(Boar);
		allTypes.add(Thieves);
		allTypes.add(Saxons);
		allTypes.add(SaxonKnight);
		allTypes.add(EvilKnight);
		allTypes.add(GreenKnight);
		allTypes.add(BlackKnight);
		allTypes.add(Dragon);
		allTypes.add(Giant);
		allTypes.add(Mordred);
		allTypes.add(SirGawain);
		allTypes.add(SirPellinore);
		allTypes.add(SirPercival);
		allTypes.add(SirTristan);
		allTypes.add(KingArthur);
		allTypes.add(QueenGuinevere);
		allTypes.add(Merlin);
		allTypes.add(QueenIseult);
		allTypes.add(SirLancelot);
		allTypes.add(SirGalahad);
		allTypes.add(Amour);
		allTypes.add(BeastTest);
		allTypes.add(TemptationTest);
		allTypes.add(ValorTest);
		allTypes.add(MorganTest);
		allTypes.add(Quest1);
		allTypes.add(Quest2);
		allTypes.add(Quest3);
		allTypes.add(Quest4);
		allTypes.add(Quest5);
		allTypes.add(Quest6);
		allTypes.add(Quest7);
		allTypes.add(Quest8);
		allTypes.add(Quest9);
		allTypes.add(Quest10);
		allTypes.add(Tournament1);
		allTypes.add(Tournament2);
		allTypes.add(Tournament3);
		allTypes.add(Tournament4);
		allTypes.add(Event1);
		allTypes.add(Event2);
		allTypes.add(Event3);
		allTypes.add(Event5);
		allTypes.add(Event6);
		allTypes.add(Event7);
		allTypes.add(Event8);
	}

	public static String allImage = "/resources/images/all.png";
	public static String back = "'/resources/images/card-back.png'";
	public static String horseImage = "/resources/images/W Horse.jpg";
	public static String swordImage = "/resources/images/W Sword.jpg";
	public static String daggerImage = "/resources/images/W Dagger.jpg";
	public static String lanceImage = "/resources/images/W Lance.jpg";
	public static String battleAxImage = "/resources/images/W Battle-ax.jpg";
	public static String excaliburImage = "/resources/images/W Excalibur.jpg";
	public static String robberKnightImage = "/resources/images/F Robber Knight.jpg";
	public static String saxonsImage = "/resources/images/F Saxons.jpg";
	public static String boarImage = "/resources/images/F Boar.jpg";
	public static String thievesImage = "/resources/images/F Thieves.jpg";
	public static String greenKnightImage = "/resources/images/F Green Knight.jpg";
	public static String blackKnightImage = "/resources/images/F Black Knight.jpg";
	public static String evilKnightImage = "/resources/images/F Evil Knight.jpg";
	public static String saxonKnightImage = "/resources/images/F Saxon Knight.jpg";
	public static String dragonImage = "/resources/images/F Dragon.jpg";
	public static String giantImage = "/resources/images/F Giant.jpg";
	public static String mordredImage = "/resources/images/F Mordred.jpg";
	public static String sirGImage = "/resources/images/A Sir Gawain.jpg";
	public static String sirPeImage = "/resources/images/A King Pellinore.jpg";
	public static String sirPImage = "/resources/images/A Sir Percival.jpg";
	public static String sirTImage = "/resources/images/A Sir Tristan.jpg";
	public static String sirLImage = "/resources/images/A Sir Lancelot.jpg";
	public static String sirGaImage = "/resources/images/A Sir Galahad.jpg";
	public static String queenGImage = "/resources/images/A Queen Guinevere.jpg";
	public static String queenIImage = "/resources/images/A Queen Iseult.jpg";
	public static String arthurImage = "/resources/images/A King Arthur.jpg";
	public static String merlinImage = "/resources/images/A Merlin.jpg";
	public static String amourImage = "/resources/images/Amour.jpg";
	public static String chivalrousDeedImage = "/resources/images/E Chivalrous Deed.jpg";
	public static String courtCamelotImage = "/resources/images/E Court Called Camelot.jpg";
	public static String callToArmsImage = "/resources/images/E King's Call to Arms.jpg";
	public static String recognitionImage = "/resources/images/E King's Recognition.jpg";
	public static String plagueImage = "/resources/images/E Plague.jpg";
	public static String poxImage = "/resources/images/E Pox.jpg";
	public static String prosperityImage = "/resources/images/E Prosperity Throughout the Realm.jpg";
	public static String queensFavorImage = "/resources/images/E Queen's Favor.jpg";
	public static String testMorganImage = "/resources/images/T Test of Morgan Le Fey.jpg";
	public static String testTempImage = "/resources/images/T Test of Temptation.jpg";
	public static String testBeastImage = "/resources/images/T Test of the Questing Beast.jpg";
	public static String testValorImage = "/resources/images/T Test of Valor.jpg";
	public static String arthurQuestImage = "/resources/images/Q Arthur.jpg";
	public static String beastQuestImage = "/resources/images/Q Beast.jpg";
	public static String boarQuestImage = "/resources/images/Q Boar.jpg";
	public static String dragonQuestImage = "/resources/images/Q Dragon.jpg";
	public static String forestQuestImage = "/resources/images/Q Forest.jpg";
	public static String grailQuestImage = "/resources/images/Q Grail.jpg";
	public static String gkQuestImage = "/resources/images/Q Green.jpg";
	public static String honorQuestImage = "/resources/images/Q Honor.jpg";
	public static String maidenQuestImage = "/resources/images/Q Maiden.jpg";
	public static String saxonQuestImage = "/resources/images/Q Saxon.jpg";
	public static String squireImage = "/resources/images/R Squire.jpg";
	public static String knightImage = "/resources/images/R Knight.jpg";
	public static String cKnightImage = "/resources/images/R Champion Knight.jpg";
	public static String camelotImage = "/resources/images/T1.jpg";
	public static String orkneyImage = "/resources/images/T2.jpg";
	public static String tintagelImage = "/resources/images/T3.jpg";
	public static String yorkImage = "/resources/images/T4.jpg";
	public static String CTRImage = "/resources/images/RoundTableKnight.png";
	public static WeaponCard Horse = new WeaponCard("Horse", horseImage, 10);
	public static WeaponCard Sword = new WeaponCard("Sword", swordImage, 10);
	public static WeaponCard Dagger = new WeaponCard("Dagger", daggerImage, 5);
	public static WeaponCard Lance = new WeaponCard("Lance", lanceImage, 20);
	public static WeaponCard Battleax = new WeaponCard("Battle-ax", battleAxImage, 15);
	public static WeaponCard Excalibur = new WeaponCard("Excalibur", excaliburImage, 30);
	public static FoeCard RobberKnight = new FoeCard("Robber Knight", robberKnightImage, 15, 0, null);
	public static FoeCard Saxons = new FoeCard("Saxons", saxonsImage, 10, 20, CardList.Quest5);
	public static FoeCard Boar = new FoeCard("Boar", boarImage, 5, 15, CardList.Quest6);
	public static FoeCard Thieves = new FoeCard("Thieves", thievesImage, 5, 0, null);
	public static FoeCard GreenKnight = new FoeCard("Green Knight", greenKnightImage, 25, 40, CardList.Quest10);
	public static FoeCard BlackKnight = new FoeCard("Black Knight", blackKnightImage, 25, 35, CardList.Quest8);
	public static FoeCard EvilKnight = new FoeCard("Evil Knight", evilKnightImage, 20, 30, CardList.Quest1);
	public static FoeCard SaxonKnight = new FoeCard("Saxon Knight", saxonKnightImage, 15, 25, CardList.Quest5);
	public static FoeCard Dragon = new FoeCard("Dragon", dragonImage, 50, 70, CardList.Quest7);
	public static FoeCard Giant = new FoeCard("Giant", giantImage, 40, 0, null);
	public static FoeCard Mordred = new FoeCard("Mordred", mordredImage, 30, 0, null); // special
	public static FoeCard All = new FoeCard("All", allImage, 0, 0, null); // todo: work on implementation of all foes
	public static AllyCard SirGawain = new AllyCard("Sir Gawain", sirGImage, 10, 20, 0, 0);
	public static AllyCard SirPellinore = new AllyCard("King Pellinore", sirPeImage, 10, 0, 0, 4); // card says king, //
																									// rules say sir
	public static AllyCard SirPercival = new AllyCard("Sir Percival", sirPImage, 5, 20, 0, 0);
	public static AllyCard SirTristan = new AllyCard("Sir Tristan", sirTImage, 10, 20, 0, 0);
	public static AllyCard KingArthur = new AllyCard("King Arthur", arthurImage, 10, 0, 2, 0);
	public static AllyCard QueenGuinevere = new AllyCard("Queen Guinevere", queenGImage, 0, 0, 3, 0);
	public static AllyCard Merlin = new AllyCard("Merlin", merlinImage, 0, 0, 0, 0); // special
	public static AllyCard QueenIseult = new AllyCard("Queen Iseult", queenIImage, 0, 0, 2, 4);
	public static AllyCard SirLancelot = new AllyCard("Sir Lancelot", sirLImage, 15, 25, 0, 0);
	public static AllyCard SirGalahad = new AllyCard("Sir Galahad", sirGImage, 15, 0, 0, 0);
	public static AmourCard Amour = new AmourCard("Amour", amourImage, 10, 1);
	public static TestCard BeastTest = new TestCard("Test of the Questing Beast", testBeastImage, 4);
	public static TestCard TemptationTest = new TestCard("Test of Temptation", testTempImage, 0);
	public static TestCard ValorTest = new TestCard("Test of Valor", testValorImage, 0);
	public static TestCard MorganTest = new TestCard("Test of Morgan Le Fey", testMorganImage, 3);
	public static QuestCard Quest1 = new QuestCard("Journey through the Enchanted Forest", forestQuestImage, null, 3);
	public static QuestCard Quest2 = new QuestCard("Search for the Questing Beast", beastQuestImage, null, null, 4);
	public static QuestCard Quest3 = new QuestCard("Vanquish King Arthur's Enemies", arthurQuestImage, null, null, 3);
	public static QuestCard Quest4 = new QuestCard("Defend the Queen's Honor", honorQuestImage, All, null, 4);
	public static QuestCard Quest5 = new QuestCard("Repel the Saxon Raiders", saxonQuestImage, Saxons, SaxonKnight, 2);
	public static QuestCard Quest6 = new QuestCard("Boar Hunt", boarQuestImage, Boar, null, 2);
	public static QuestCard Quest7 = new QuestCard("Slay the Dragon", dragonQuestImage, Dragon, null, 3);
	public static QuestCard Quest8 = new QuestCard("Rescue the Fair Maiden", maidenQuestImage, BlackKnight, null, 3);
	public static QuestCard Quest9 = new QuestCard("Search for the Holy Grail", grailQuestImage, All, null, 5);
	public static QuestCard Quest10 = new QuestCard("Test of the Green Knight", gkQuestImage, GreenKnight, null, 4);
	public static TournamentCard Tournament1 = new TournamentCard("At Camelot", camelotImage, 3);
	public static TournamentCard Tournament2 = new TournamentCard("At Ornkey", orkneyImage, 2);
	public static TournamentCard Tournament3 = new TournamentCard("At Tintagel", tintagelImage, 1);
	public static TournamentCard Tournament4 = new TournamentCard("At York", yorkImage, 0);
	public static EventCard Event1 = new EventCard("Chivalrous Deed", chivalrousDeedImage,
			"Player(s) with both lowerst rank and least amount of shields, receives 3 shields");
	public static EventCard Event2 = new EventCard("Court Called to Camelot", courtCamelotImage,
			"All Allies in play must be discarded");
	public static EventCard Event3 = new EventCard("Queen's Favor", queensFavorImage,
			"The lowest ranked player(s) immediately receieves 2 Adventure Cards");
	public static EventCard Event4 = new EventCard("Pox", poxImage,
			"All players except the player drawing this card lose 1 shield");
	public static EventCard Event5 = new EventCard("Plague", plagueImage, "Drawer loses 2 shields if possible");
	public static EventCard Event6 = new EventCard("King's Call to Arms", callToArmsImage,
			"The highest ranked player(s) must place 1 weapon in the discard pile. If unable to do so, 2 Foe Cards must be discarded");
	public static EventCard Event7 = new EventCard("King's Recognition", recognitionImage,
			"The next player(s) to complete a Quest will receive 2 extra shields");
	public static EventCard Event8 = new EventCard("Prosperity Throughout the Realm", prosperityImage,
			"All players may immediately draw 2 Adventure Cards");
	public static RankCard Squire = new RankCard("Squire", squireImage, 5);
	public static RankCard Knight = new RankCard("Knight", knightImage, 10);
	public static RankCard ChampionKnight = new RankCard("Champion Knight", cKnightImage, 20);
	public static RankCard RoundTableKnight = new RankCard("Champion Knight of The Round Table", CTRImage, 100);
}
