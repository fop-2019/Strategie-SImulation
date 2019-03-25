package game;

import game.map.Castle;

public interface GameInterface {

    void onAttackStopped();
    void onAttackStarted(Castle source, Castle target, int troopCount);
    void onCastleChosen(Castle castle, Player player);
    void onNextTurn(Player currentPlayer, int troopsGot, boolean human);
    void onGainingCastle(Player currentPlayer , int troopsGot); //created for deathmatch mode : Luca
	void onLosing(Player loosingPlayer , Player Conquerer); //created for deathmatch mode : Luca
	void onGainingKingdom(Player currentPlayer); //created for deathmatch mode : Luca
	void onSurroundingCastle(Player currentPlayer, int i); //created for suddendeath mode : Luca
    void onNewRound(int round);
    void onGameOver(Player winner);
    void onGameStarted(Game game);
    void onConquer(Castle castle, Player player);
    void onUpdate();
    void onAddScore(Player player, int score);
    int[] onRoll(Player player, int dices, boolean fastForward);



}
