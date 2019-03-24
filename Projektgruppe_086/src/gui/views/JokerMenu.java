package gui.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import game.Game;
import game.GameConstants;
import game.Joker;
import game.Player;
import game.jokers.TroopsJoker;
import game.players.Human;
import gui.GameWindow;
import gui.View;

@SuppressWarnings("serial")
public class JokerMenu extends View{

	private JLabel lblTitle;
    private JComponent[][] jokerConfig;
    private JButton btnStart, btnBack, btnInfo;
    private Game game;
    private List<Player> players;
    private Joker aiJoker;
    

    public JokerMenu(GameWindow gameWindow, Game game) {
        super(gameWindow);
        this.game = game;
        players = game.getPlayers();
        this.aiJoker = new TroopsJoker();
        onInit();
    }

	public void onResize() {

        int offsetY = 25;
        int offsetX = 25;

        lblTitle.setLocation(offsetX, offsetY);
        offsetY += 50;

        int columnWidth = Math.max(300, (getWidth() - 75) / 2);

        // Column 1
        offsetX = (getWidth() - 2*columnWidth - 25) / 2 + (columnWidth - 350) / 2;
        offsetY += 50;

        for(int i = 0; i < GameConstants.MAX_PLAYERS; i++) {
            int tempOffsetX = offsetX;
            for(JComponent c : jokerConfig[i]) {
                c.setLocation(tempOffsetX, offsetY);
                tempOffsetX += c.getWidth() + 10;
                c.setEnabled(i < game.getPlayers().size());
            }

            offsetY += 40;
        }

        // Button bar
        offsetY = this.getHeight() - BUTTON_SIZE.height - 25;
        offsetX = (this.getWidth() - 2*BUTTON_SIZE.width - 25) / 3;
        btnBack.setLocation(offsetX, offsetY);
        btnInfo.setLocation(offsetX + BUTTON_SIZE.width + 25, offsetY);
        btnStart.setLocation(offsetX + BUTTON_SIZE.width + BUTTON_SIZE.width +50, offsetY);
    }

    @Override
    protected void onInit() {
    	if (game == null) return;
        // Title
        lblTitle = createLabel("Joker wählen", 25, true);

        // Player rows:
        // [Number] [Color] [Name] [Human/AI] (Team?)
        Vector<String> jokerTypes = new Vector<>();
        for(Class<?> c : GameConstants.JOKER_TYPES)
            jokerTypes.add(c.getSimpleName());

        String[] playerNames = new String[GameConstants.MAX_PLAYERS];
        for (int i = 0; i<GameConstants.MAX_PLAYERS; i++) {
        	if (i<players.size()) {
        		playerNames[i] = players.get(i).getName();
        	} else {
        		playerNames[i] = "--empty--";
        	}
        }
        
        jokerConfig = new JComponent[GameConstants.MAX_PLAYERS][];
        for(int i = 0; i < GameConstants.MAX_PLAYERS; i++) {
        	
        	JComponent jokerChoice = createLabel("None", 16);
        	try {
        		if (players.get(i) != null) {
            		if (players.get(i).getClass() == Human.class) {
            			jokerChoice = new JComboBox<>(jokerTypes);
            		} else {
            			jokerChoice = createLabel(aiJoker.toString(), 16);
            		}
            	}
        	} catch (IndexOutOfBoundsException e) {
        		
        	}
        	
        	
            jokerConfig[i] = new JComponent[] {
                createLabel(String.format("%d.", i + 1),16),
                createLabel(playerNames[i], 16),
                jokerChoice
            };

            jokerConfig[i][1].setSize(200, 25);
            jokerConfig[i][2].setSize(100, 25);
            jokerConfig[i][0].setSize(25, 25);

            for(JComponent c : jokerConfig[i])
                add(c);
        }

        // Buttons
        btnBack = createButton("Zurück");
        btnStart = createButton("Starten");
        btnInfo = createButton("Info");

        getWindow().setSize(750, 450);
        getWindow().setMinimumSize(new Dimension(750, 450));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource() == btnBack)
            getWindow().setView(new GameMenu(getWindow()));
        else if(actionEvent.getSource() == btnStart) {

            try {
            	for (int i = 0; i<jokerConfig.length;i++) {
            		try {
            			if (jokerConfig[i][2].getClass() == JComboBox.class && players.get(i)!=null) {
                			players.get(i).setJoker(Joker.getInstance(GameConstants.JOKER_TYPES[((JComboBox<String>) jokerConfig[i][2]).getSelectedIndex()]));
                		} else if (players.get(i)!=null) {
                			players.get(i).setJoker(aiJoker);
                		}
            		} catch (IndexOutOfBoundsException e) {
            			
            		}
            		
            	}
            	for (Player p : players) {
            		if (p.getJoker() == null) throw new IllegalArgumentException("Fehler beim Zuweisen der Joker");
            	}
                GameView gameView = new GameView(getWindow(), game);
                game.start(gameView);
                getWindow().setView(gameView);
            } catch(IllegalArgumentException ex) {
                ex.printStackTrace();
                showErrorMessage("Fehler beim Erstellen des Spiels: " + ex.getMessage(), "Interner Fehler");
            }
        } else if (actionEvent.getSource() == btnInfo) {
        	JOptionPane.showMessageDialog(null, "Aus den folgenden drei Jokern kann einer gewählt werden.\nDie Joker können nur einmal benutzt werden.\n\nDiceJoker:\t\tDer Benutzer kann bei einem Mal würfeln nur hohe Zahlen würfeln.\nRevolutionJoker:\t\tDer Benutzer kann eine gegnerische Burg auswählen, welche dann zu seiner eigenen Burg wird. Die gegnerischen Einheiten in der Burg werden auf eine andere Burg verschoben.\nTroopsJoker:\t\tDer Benutzer erhält einmalig beim Verteilen der Truppen eine gewisse Anzahl von Truppen zusätzlich. ", "Information zu den Jokern", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
