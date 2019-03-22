package game.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import game.Game;
import gui.views.GameMenu;

import java.awt.*;

/**
 * Diese Klasse teilt Burgen in Königreiche auf
 */
public class Clustering {

	private Random random;
	private final List<Castle> allCastles;
	private final int kingdomCount;

	/**
	 * Ein neues Clustering-Objekt erzeugen.
	 * 
	 * @param castles      Die Liste von Burgen, die aufgeteilt werden sollen
	 * @param kingdomCount Die Anzahl von Königreichen die generiert werden sollen
	 */
	public Clustering(List<Castle> castles, int kingdomCount) {
		if (kingdomCount < 2)
			throw new IllegalArgumentException("Ungültige Anzahl an Königreichen");

		this.random = new Random();
		this.kingdomCount = kingdomCount;
		this.allCastles = Collections.unmodifiableList(castles);
	}

	/**
	 * Gibt eine Liste von Königreichen zurück. Jedes Königreich sollte dabei einen
	 * Index im Bereich 0-5 bekommen, damit die Burg richtig angezeigt werden kann.
	 * Siehe auch {@link Kingdom#getType()}
	 */
	public List<Kingdom> getPointsClusters() {
		int h, w, s;
		List<Kingdom> currentKingdoms = new ArrayList<Kingdom>();
		List<Kingdom> previousKingdoms = new ArrayList<Kingdom>();
		h = GameMap.getMapHeight();
		w = GameMap.getMapWidth();
		s = GameMap.getMapScale();
//		System.out.println(h + " " + w);
		int closestKingdom = -1;
		double smallestDist = (int) Math.floor(Math.sqrt((w * w * s * s) + (h * h * s * s)));
		Point[] centers = new Point[kingdomCount];

		// generating first random centers
		for (int i = 0; i < kingdomCount; i++) {
			centers[i] = new Point(0, 0);
			centers[i].x = (int) Math.floor(Math.random() * (w * s + 1));
			centers[i].y = (int) Math.floor(Math.random() * (h * s + 1));
//			System.out.println("Centre " + i + " at: x: " + centers[i].x + " y: " + centers[i].y);
		}

		// generating first empty kingdoms
		for (int i = 0; i < kingdomCount; i++) {
			currentKingdoms.add(new Kingdom(i));
		}
		
		while (currentKingdoms != previousKingdoms) {
			
			previousKingdoms = currentKingdoms;
			currentKingdoms.removeAll(previousKingdoms);
			for (int i = 0; i < kingdomCount; i++) {
				currentKingdoms.add(new Kingdom(i));
			}
			// assigning castles to closest kingdoms
			for (int i = 0; i < allCastles.size(); i++) {
				for (int j = 0; j < kingdomCount; j++) {
					if (allCastles.get(i).distance(centers[j]) < smallestDist) {
						smallestDist = allCastles.get(i).distance(centers[j]);
						closestKingdom = j;
					}
				}
				allCastles.get(i).setKingdom(currentKingdoms.get(closestKingdom));
				closestKingdom = -1;
				smallestDist = (int) Math.floor(Math.sqrt((w * w * s * s) + (h * h * s * s)));
			}

			// generating new centers
			for (int i = 0; i < kingdomCount; i++) {
				double newX = 0;
				double newY = 0;
				for (int j = 0; j < currentKingdoms.get(i).getCastles().size(); j++) {
					newX = +currentKingdoms.get(i).getCastles().get(j).getLocationOnMap().getX();
					newY = +currentKingdoms.get(i).getCastles().get(j).getLocationOnMap().getY();
				}
				centers[i].setLocation((int) Math.floor(newX / currentKingdoms.get(i).getCastles().size()),
						(int) Math.floor(newY / currentKingdoms.get(i).getCastles().size()));
			}

		}
		
		return currentKingdoms;
	}
}
