package game.map;

import base.*;
import game.GameConstants;
import gui.Resources;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse representiert das Spielfeld. Sie beinhaltet das Hintergrundbild,
 * welches mit Perlin noise erzeugt wurde, eine Liste mit Königreichen und alle
 * Burgen und deren Verbindungen als Graphen.
 *
 * Die Karte wird in mehreren Schritten generiert, siehe dazu
 * {@link #generateRandomMap(int, int, int, int, int)}
 */
public class GameMap {

	private BufferedImage backgroundImage;
	private Graph<Castle> castleGraph;
	private List<Kingdom> kingdoms;

	// Map Generation
	private double[][] noiseValues;
	static private int width, height, scale;

	public static int getMapWidth() {
		return width;
	}

	public static int getMapHeight() {
		return height;
	}

	public static int getMapScale() {
		return scale;
	}

	/**
	 * Erzeugt eine neue leere Karte. Der Konstruktor sollte niemals direkt
	 * aufgerufen werden. Um eine neue Karte zu erstellen, muss
	 * {@link #generateRandomMap(int, int, int, int, int)} verwendet werden
	 * 
	 * @param width  die Breite der Karte
	 * @param height die Höhe der Karte
	 * @param scale  der Skalierungsfaktor
	 */
	private GameMap(int width, int height, int scale) {
		this.castleGraph = new Graph<>();
		GameMap.width = width;
		GameMap.height = height;
		GameMap.scale = scale;
	}

	/**
	 * Wandelt einen Noise-Wert in eine Farbe um. Die Methode kann nach belieben
	 * angepasst werden
	 * 
	 * @param value der Perlin-Noise-Wert
	 * @return die resultierende Farbe
	 */
	private Color doubleToColor(double value) {
		if (value <= 0.40)
			return GameConstants.COLOR_WATER;
		else if (value <= 0.5)
			return GameConstants.COLOR_SAND;
		else if (value <= 0.7)
			return GameConstants.COLOR_GRASS;
		else if (value <= 0.8)
			return GameConstants.COLOR_STONE;
		else
			return GameConstants.COLOR_SNOW;
	}

	/**
	 * Hier wird das Hintergrund-Bild mittels Perlin-Noise erzeugt. Siehe auch:
	 * {@link PerlinNoise}
	 */
	private void generateBackground() {
		PerlinNoise perlinNoise = new PerlinNoise(width, height, scale);
		Dimension realSize = perlinNoise.getRealSize();

		noiseValues = new double[realSize.width][realSize.height];
		backgroundImage = new BufferedImage(realSize.width, realSize.height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < realSize.width; x++) {
			for (int y = 0; y < realSize.height; y++) {
				double noiseValue = perlinNoise.getNoise(x, y);
				noiseValues[x][y] = noiseValue;
				backgroundImage.setRGB(x, y, doubleToColor(noiseValue).getRGB());
			}
		}
	}

	/**
	 * Hier werden die Burgen erzeugt. Dabei wir die Karte in Felder unterteilt,
	 * sodass auf jedes Fals maximal eine Burg kommt. Sollte auf einem Feld keine
	 * Position für eine Burg existieren (z.B. aufgrund von Wasser oder angrenzenden
	 * Burgen), wird dieses übersprungen. Dadurch kann es vorkommen, dass nicht alle
	 * Burgen generiert werden
	 * 
	 * @param castleCount die maximale Anzahl der zu generierenden Burgen
	 */
	private void generateCastles(int castleCount) {
		double square = Math.ceil(Math.sqrt(castleCount));
		double length = width + height;

		int tilesX = (int) Math.max(1, (width / length + 0.5) * square) + 5;
		int tilesY = (int) Math.max(1, (height / length + 0.5) * square) + 5;
		int tileW = (width * scale / tilesX);
		int tileH = (height * scale / tilesY);

		if (tilesX * tilesY < castleCount) {
			throw new IllegalArgumentException(String.format("CALCULATION Error: tilesX=%d * tilesY=%d < castles=%d",
					tilesX, tilesY, castleCount));
		}

		// Add possible tiles
		List<Point> possibleFields = new ArrayList<>(tilesX * tilesY);
		for (int x = 0; x < tilesX - 1; x++) {
			for (int y = 0; y < tilesY - 1; y++) {
				possibleFields.add(new Point(x, y));
			}
		}

		// Generate castles
		List<String> possibleNames = generateCastleNames();
		int castlesGenerated = 0;
		while (possibleFields.size() > 0 && castlesGenerated < castleCount) {
			Point randomField = possibleFields.remove((int) (Math.random() * possibleFields.size()));
			int x0 = (int) ((randomField.x + 0.5) * tileW);
			int y0 = (int) ((randomField.y + 0.5) * tileH);

			for (int x = (int) (0.5 * tileW); x >= 0; x--) {
				boolean positionFound = false;
				for (int y = (int) (0.5 * tileH); y >= 0; y--) {
					int x_mid = (int) (x0 + x + 0.5 * tileW);
					int y_mid = (int) (y0 + y + 0.5 * tileH);
					if (noiseValues[x_mid][y_mid] >= 0.6) {
						String name = possibleNames.isEmpty() ? "Burg " + (castlesGenerated + 1)
								: possibleNames.get((int) (Math.random() * possibleNames.size()));
						Castle newCastle = new Castle(new Point(x0 + x, y0 + y), name);
						boolean doesIntersect = false;

						for (Castle r : castleGraph.getAllValues()) {
							if (r.distance(newCastle) < Math.max(tileW, tileH)) {
								doesIntersect = true;
								break;
							}
						}

						if (!doesIntersect) {
							possibleNames.remove(name);
							castleGraph.addNode(newCastle);
							castlesGenerated++;
							positionFound = true;
							break;
						}
					}
				}

				if (positionFound)
					break;
			}
		}
	}

	/**
	 * Hier werden die Kanten erzeugt. Dazu werden zunächst alle Burgen durch eine
	 * Linie verbunden und anschließend jede Burg mit allen anderen in einem
	 * bestimmten Radius nochmals verbunden
	 */
	/**
	 * Hier werden die Kanten erzeugt. Dazu werden zunächst alle Burgen durch eine
	 * Linie verbunden und anschließend jede Burg mit allen anderen in einem
	 * bestimmten Radius nochmals verbunden
	 */
	private void generateEdges() {
		// radius for edges
		int tile = 0;
		// radius if there are less than 20 castles
		if (castleGraph.getNodes().size() < 20) {
//			System.out.println("less than 20");
			tile = (width * scale + height * scale) / 8;
		}
		// radius if there are between 20 and 45 castles
		else if (castleGraph.getNodes().size() >= 20 && castleGraph.getNodes().size() <= 45) {
//			System.out.println("between 20 and 45");
			tile = (width * scale + height * scale) / 10;
		}
		// radius if there are more than 45 castles
		else if (castleGraph.getNodes().size() > 45) {
//			System.out.println("more than 45");
			tile = (width * scale + height * scale) / 12;
		}

		// calls connecting function for each castle
		for (int i = 0; i < castleGraph.getNodes().size(); i++) {
			connectToCloseCastles(tile, castleGraph.getNodes().get(i));
		}

		// checks all castles with 0 Edges and connects them to nearest castle
		for (int i = 0; i < castleGraph.getNodes().size(); i++) {
			Node<Castle> currentCastle = castleGraph.getNodes().get(i);

			if (castleGraph.getEdges(currentCastle).size() == 0) {
				Node<Castle> closestCastle = castleGraph.getNodes().get(0);

				if (currentCastle.getValue().equals(closestCastle.getValue())) {
					closestCastle = castleGraph.getNodes().get(1);
				}

				for (int j = 0; j < castleGraph.getNodes().size(); j++) {
					Node<Castle> cI = castleGraph.getNodes().get(j);

					if (currentCastle.getValue().distance(cI.getValue()) <= currentCastle.getValue()
							.distance(closestCastle.getValue()) && !currentCastle.getValue().equals(cI.getValue())) {
						closestCastle = cI;
					}

				}
				castleGraph.addEdge(currentCastle, closestCastle);
			}
		}

		// checks all castles with 1 edge and connects them to nearest castle that is
		// further away then 1 tile
		for (int j = 0; j < castleGraph.getNodes().size(); j++) {
			Node<Castle> currentCastle = castleGraph.getNodes().get(j);

			if (castleGraph.getEdges(currentCastle).size() == 1) {
				Node<Castle> closestCastle = castleGraph.getNodes().get(0);

				if (currentCastle.getValue().equals(closestCastle.getValue())) {
					closestCastle = castleGraph.getNodes().get(1);
				}

				for (int k = 0; k < castleGraph.getNodes().size(); k++) {
					Node<Castle> cI = castleGraph.getNodes().get(k);
					// filter out all nodes that are not cI - fList needs to be empty so we know
					// there isn't an exisiting edge to that castle already
					Object[] fList = castleGraph.getEdges(currentCastle).stream()
							.filter(t -> t.getOtherNode(currentCastle).equals(cI)).toArray();

					if (currentCastle.getValue().distance(cI.getValue()) <= currentCastle.getValue()
							.distance(closestCastle.getValue()) && !currentCastle.getValue().equals(cI.getValue())
							&& currentCastle.getValue().distance(cI.getValue()) > tile && fList.length == 0) {
						closestCastle = cI;
					}
				}
				castleGraph.addEdge(currentCastle, closestCastle);
			}
		}
	}

	/**
	 * Connects all castle to castle c that are within radius r
	 * 
	 * @param r radius
	 * @param c castle
	 */
	private void connectToCloseCastles(int r, Node<Castle> c) {

		for (int i = 0; i < castleGraph.getNodes().size(); i++) {
			Node<Castle> cI = castleGraph.getNodes().get(i);

			if (cI.getValue().equals(c.getValue())) {
				continue;
			}

			else {

				if (c.getValue().distance(cI.getValue()) <= r) {

					if (castleGraph.getEdge(c, cI) != null || castleGraph.getEdge(cI, c) != null) {
						continue;
					}

					else {
						castleGraph.addEdge(c, cI);
					}
				}
			}
		}
	}

	/**
	 * Hier werden die Burgen in Königreiche unterteilt. Dazu wird der
	 * {@link Clustering} Algorithmus aufgerufen.
	 * 
	 * @param kingdomCount die Anzahl der zu generierenden Königreiche
	 */
	private void generateKingdoms(int kingdomCount) {
		if (kingdomCount > 0 && kingdomCount < castleGraph.getAllValues().size()) {
			Clustering clustering = new Clustering(castleGraph.getAllValues(), kingdomCount);
			kingdoms = clustering.getPointsClusters();
		} else {
			kingdoms = new ArrayList<>();
		}
	}

	/**
	 * Eine neue Spielfeldkarte generieren. Dazu werden folgende Schritte
	 * abgearbeitet: 1. Das Hintergrundbild generieren 2. Burgen generieren 3.
	 * Kanten hinzufügen 4. Burgen in Köngireiche unterteilen
	 * 
	 * @param width        die Breite des Spielfelds
	 * @param height       die Höhe des Spielfelds
	 * @param scale        die Skalierung
	 * @param castleCount  die maximale Anzahl an Burgen
	 * @param kingdomCount die Anzahl der Königreiche
	 * @return eine neue GameMap-Instanz
	 */
	public static GameMap generateRandomMap(int width, int height, int scale, int castleCount, int kingdomCount) {

		width = Math.max(width, 15);
		height = Math.max(height, 10);

		if (scale <= 0 || castleCount <= 0)
			throw new IllegalArgumentException();

		System.out.println(String.format("Generating new map, castles=%d, width=%d, height=%d, kingdoms=%d",
				castleCount, width*scale, height*scale, kingdomCount));
		GameMap gameMap = new GameMap(width, height, scale);
		gameMap.generateBackground();
		gameMap.generateCastles(castleCount);
		gameMap.generateEdges();
		gameMap.generateKingdoms(kingdomCount);

		if (!gameMap.getGraph().allNodesConnected()) {
			System.out.println("Fehler bei der Verifikation: Es sind nicht alle Knoten miteinander verbunden!");
			return null;
		}

		return gameMap;
	}

	/**
	 * Generiert eine Liste von Zufallsnamen für Burgen. Dabei wird ein Prefix
	 * (Schloss, Burg oder Festung) an einen vorhandenen Namen aus den Resourcen
	 * angefügt. Siehe auch: {@link Resources#getcastleNames()}
	 * 
	 * @return eine Liste mit Zufallsnamen
	 */
	private List<String> generateCastleNames() {
		String[] prefixes = { "Schloss", "Burg", "Festung" };
		List<String> names = Resources.getInstance().getCastleNames();
		List<String> nameList = new ArrayList<>(names.size());

		for (String name : names) {
			String prefix = prefixes[(int) (Math.random() * prefixes.length)];
			nameList.add(prefix + " " + name);
		}

		return nameList;
	}

	public int getWidth() {
		return backgroundImage.getWidth();
	}

	public int getHeight() {
		return backgroundImage.getHeight();
	}

	public BufferedImage getBackgroundImage() {
		return this.backgroundImage;
	}

	public Dimension getSize() {
		return new Dimension(this.getWidth(), this.getHeight());
	}

	public List<Castle> getCastles() {
		return castleGraph.getAllValues();
	}

	public Graph<Castle> getGraph() {
		return this.castleGraph;
	}

	public List<Edge<Castle>> getEdges() {
		return this.castleGraph.getEdges();
	}

	public List<Kingdom> getKingdoms() {
		return this.kingdoms;
	}
}