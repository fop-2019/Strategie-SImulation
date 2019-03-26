package game.map;

import game.Game;
import game.Player;

import java.awt.*;
import java.util.Optional;

/**
 * Diese Klasse representiert eine Burg.
 * Jede Burg hat Koordinaten auf der Karte und einen Namen.
 * Falls die Burg einen Besitzer hat, hat sie auch eine Anzahl von zugewiesenen Truppen.
 * Die Burg kann weiterhin Teil eines Königreichs sein.
 */
public class Castle {

    private int troopCount;
    private Player owner;
    private Kingdom kingdom;
    private Point location;
    private String name;
    private boolean mainCastle; // changed - SuddenDeath : Luca
    public boolean flag; // changed - Dominance : Sander
    public Player previousOwner; // changed - Deathmatch : Luca
    //public boolean hasGained; // changed - Deathmatch : Luca

    /**
     * Eine neue Burg erstellen
     * @param location die Koordinaten der Burg
     * @param name der Name der Burg
     */
    public Castle(Point location, String name , /* changed - Dominance : Sander*/ boolean  flag) {
        this.location = location;
        this.troopCount = 0;
        this.owner = null;
        this.kingdom = null;
        this.name = name;
        this.mainCastle = false; // changed - SuddenDeath : Luca
        this.flag = flag;  // changed - Dominance : Sander
        this.previousOwner = null; // changed -Deathmatch : Luca
        //this.hasGained = false; // changed - Deathmatch : Luca
        
        
    }

    public Player getOwner() {
        return this.owner;
    }
 // created - SuddenDeath : Luca
    public void setMainCastle(boolean state) {
    	this.mainCastle = state;
    }
 // created - SuddenDeath : Luca
    public boolean getMainCastle() {
    	return this.mainCastle;
    }

    public Kingdom getKingdom() {
        return this.kingdom;
    }

    public int getTroopCount() {
        return this.troopCount;
    }

    /**
     * Truppen von dieser Burg zur angegebenen Burg bewegen.
     * Dies funktioniert nur, wenn die Besitzer übereinstimmen und bei der aktuellen Burg mindestens eine Truppe übrig bleibt
     * @param target
     * @param troops
     */
    public void moveTroops(Castle target, int troops , Player currentPlayer , Game game /*changed*/) {
        // Troops can only be moved to own regions
        if(game.getGoal().gameModeID != 3 && target.owner != this.owner || game.getGoal().gameModeID == 3 &&  /*changed*/ (target.owner == null && currentPlayer.getGainedCastleId3() == true || target.owner != null && target.owner != this.owner) )
            return;

        // At least one unit must remain in the source region
        if(this.troopCount - troops < 1)
            return;

        this.troopCount -= troops;
        target.troopCount += troops;
        
        //changed for deathmatch mode : Luca
        // if target castle is neutral currentPlayer conquers it and gets 5 troops added to his starter castle
        if (game.getGoal().gameModeID == 3) {
	        if(target.getOwner() == null && currentPlayer.getGainedCastleId3() == false) {
	        	

	        	target.setOwner(currentPlayer);
	        	Optional<Castle> main = currentPlayer.getCastles(game).stream().filter(t -> t.getMainCastle()).findFirst();
	        	main.get().addTroops(5);
	        	currentPlayer.hasGained = true;
	        	currentPlayer.setGainedCastleId3(true);
	        	
	        } 
        }

       }
    

    public Point getLocationOnMap() {
        return this.location;
    }

    /**
     * Berechnet die eukldische Distanz zu dem angegebenen Punkt
     * @param dest die Zielkoordinate
     * @return die euklidische Distanz
     */
    public double distance(Point dest) {
        return Math.sqrt(Math.pow(this.location.x - dest.x, 2) + Math.pow(this.location.y - dest.y, 2));
    }

    /**
     * Berechnet die eukldische Distanz zu der angegebenen Burg
     * @param next die Zielburg
     * @return die euklidische Distanz
     * @see #distance(Point)
     */
    public double distance(Castle next) {
        Point otherLocation = next.getLocationOnMap();
        return this.distance(otherLocation);
    }
    // changed
    public void setPreviousOwner(Player player) {
    	this.previousOwner = player;
    }
    public void setOwner(Player player) {
        this.owner = player;
    }

    public void addTroops(int i) {
        if(i <= 0)
            return;

        this.troopCount += i;
    }

    public String getName() {
        return this.name;
    }

    public void removeTroops(int i) {
        this.troopCount = Math.max(0, this.troopCount - i);
        if(this.troopCount == 0)
            this.owner = null;
    }

    /**
     * Gibt den Burg-Typen zurück. Falls die Burg einem Königreich angehört, wird der Typ des Königreichs zurückgegeben, ansonsten 0
     * @return der Burg-Typ für die Anzeige
     */
    public int getType() {
        return this.kingdom == null ? 0 : this.kingdom.getType();
    }

    /**
     * Die Burg einem Königreich zuordnen
     * @param kingdom Ein Königreich oder null
     */
    public void setKingdom(Kingdom kingdom) {
        this.kingdom = kingdom;
        if(kingdom != null)
            kingdom.addCastle(this);
    }
}