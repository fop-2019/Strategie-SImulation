# Strategie-SImulation


Änderung für gamemodes in folgenden Klassen: (Sollten eigtl. alle kommentiert und entsprechend ersichtlich sein oder unter einer if-condition mit gameModeID = 1 oder 2 oder 3 sein)

Game
GameConstants
GameInterface
Goal
Player
Game.goals
Castle
Resources
MapPanel
GameView


Für Tests der 1.2 allNodesConnected wurden Test angelegt:
GraphConnectionTest


GameMap.generateEdges wurde geupdated
 - anderen Radius für Maps mit über 80 Castlen (Große Map mit vier Spielern)
 - Radius von Maps mit > 40 Burgen nun auf > 40 und <= 80 gesetzt
 
 
 Änderungen für Clustering:
  gameMap getters für width height etc. gelöscht
  Funktion getSizeInfo in Clustering erstellt
  gameMap.generateKingdoms - Aufruf von getSizeInfo mit entsprechenden Klassenattributen der GameMap hinzugefügt
  Klassenattribute width, height, scale in Clustering erstellt, welche für getPointsClusters statt den static abrufen von GameMap gettern benutzt werden.
