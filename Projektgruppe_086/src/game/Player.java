package game;

import game.map.Castle;
import game.map.Kingdom;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Player {

    private final String name;
    private Color color;
    private int points;
    private int remainingTroops;
    private Joker joker;
    private boolean gainedCastleId3; //changed - deathmatch : Luca
    public int flagRounds; // changed - dominance : Sander
    public boolean playerwithflag; // changed - dominance : Sander
    public boolean hasGained; // changed - deathmatch : Luca
    private ArrayList<Kingdom> hasConquered;

    protected Player(String name, Color color) {
        this.name = name;
        this.points = 0;
        this.color = color;
        this.remainingTroops = 0;
        this.gainedCastleId3 = false; // changed - deathmatch : Luca
        this.flagRounds = 0; // changed - dominance : Sander
        this.playerwithflag = false; // changed - dominance : Sander
        this.hasGained = false; // changed - deathmatch : Luca
        this.hasConquered = new ArrayList<Kingdom>();
    }

    public int getRemainingTroops() {
        return this.remainingTroops;
    }

    public static Player createPlayer(Class<?> playerType, String name, Color color) {
        if(!Player.class.isAssignableFrom(playerType))
            throw new IllegalArgumentException("Not a player class");

        try {
            Constructor<?> constructor = playerType.getConstructor(String.class, Color.class);
            return (Player) constructor.newInstance(name, color);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void hasGained(boolean state)  {
    	this.hasGained = state;
    }
    
 // changed - Dominance : Sander
    /**
     * Adds one to the attribut flagRounds
     */
    public void addFlagRound() {
    	this.flagRounds++;
    }
    
    public void setColor(Color c) {
        this.color = c;
    }
    
  //changed
    public boolean getGainedCastleId3( ) {
    	return this.gainedCastleId3;
    }
    //changed
    public void setGainedCastleId3 (boolean state) {
    	this.gainedCastleId3 = state;
    }
    /**
     * 
     * @param kingdom
     * @return true if kingdom is not in hasConquered else false
     */
    public boolean hasConqueredKingdom (Kingdom kingdom) {
    	if (hasConquered.isEmpty()) 
    		return true;
    	
    	else if (hasConquered.contains(kingdom))
    		return false;
    	else
    		return true;
    }
    /**
     * Adds given kingdom to hasConquered.
     * @param kingdom kingdom
     */
    public void addToConquered (Kingdom kingdom) {
    	if (hasConquered.isEmpty() || !hasConquered.contains(kingdom) )
    			hasConquered.add(kingdom);
    }

    public Color getColor() {
        return this.color;
    }

    public String getName() {
        return this.name;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void addTroops(int troops) {
        if(troops < 0)
            return;

        this.remainingTroops += troops;
    }

    public void removeTroops(int troops) {
        if(this.remainingTroops - troops < 0 || troops < 0)
            return;

        this.remainingTroops -= troops;
    }

    public int getNumRegions(Game game) {
        return this.getCastles(game).size();
    }

    public List<Castle> getCastles(Game game) {
        return game.getMap().getCastles().stream().filter(c -> c.getOwner() == this).collect(Collectors.toList());
    }

    public void reset() {
        this.remainingTroops = 0;
        this.points = 0;
    }
    
    public void setJoker(Joker joker) {
    	this.joker = joker;
    }
    
    public Joker getJoker() {
    	return joker;
    }

    /**
     * determin if the joker is being used
     * @param joker2 the joker to be used
     * @return true if player wants to use the joker
     */
	public abstract boolean useJoker(Joker joker2);
}
