/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author marks
 */
import Modelo.*;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.json.JSONArray;

public class LevelManager {
    private static final String LEVELS_DIR = "src/levels/";
    private static final String MAPS_DIR = LEVELS_DIR + "maps/";
    private static final String LEVEL_INFO_FILE = LEVELS_DIR + "level_info.json";
    private static int totalLevels = 5;
    
    static {
        // Initialize total levels count
        try {
            String content = new String(Files.readAllBytes(Paths.get(LEVEL_INFO_FILE)));
            JSONObject allLevels = new JSONObject(content);
            totalLevels = allLevels.length();
        } catch (IOException e) {
            System.err.println("Error loading level info: " + e.getMessage());
            totalLevels = 3; // Fallback value
        }
    }
    
    public static int getTotalLevels() {
        return totalLevels;
    }
    
    public static void loadLevel(int levelNumber, List<Personagem> faseAtual) throws IOException {
        JSONObject levelInfo = getLevelInfo(levelNumber);
        String mapFile = levelInfo.getString("map");
        
        // Load the Tiled map
        loadTiledMap(MAPS_DIR + mapFile, faseAtual);
        
        // Add level-specific characters
        addLevelCharacters(levelNumber, faseAtual);
    }
    
    private static JSONObject getLevelInfo(int levelNumber) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(LEVEL_INFO_FILE)));
        JSONObject allLevels = new JSONObject(content);

        // Check if level exists
        String levelKey = "level_" + levelNumber;
        if (!allLevels.has(levelKey)) {
            throw new IOException("Level " + levelNumber + " not found in level info");
    }
    
    return allLevels.getJSONObject(levelKey);
    }
    
    private static void loadTiledMap(String mapPath, List<Personagem> faseAtual) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(mapPath)));
        JSONObject mapJson = new JSONObject(content);
        
        JSONArray layers = mapJson.getJSONArray("layers");
        for (int i = 0; i < layers.length(); i++) {
            JSONObject layer = layers.getJSONObject(i);
            if ("tilelayer".equals(layer.getString("type"))) {
                loadTileLayer(layer, faseAtual);
            }
        }
    }
    
    private static void loadTileLayer(JSONObject layer, List<Personagem> faseAtual) {
        JSONArray data = layer.getJSONArray("data");
        int width = layer.getInt("width");
        String layerName = layer.getString("name");

        for (int y = 0; y < layer.getInt("height"); y++) {
            for (int x = 0; x < width; x++) {
                int tileId = data.getInt(y * width + x);
                if (tileId != 0) {
                    // Determine which tileset the tile belongs to
                    // In your JSON, firstgid 1 is Tela-Fase1, firstgid 2 is Muro2
                    if (tileId == 1) { // Tela-Fase1 tile
                        faseAtual.add(new Floor("Tela-Fase1.png", x, y));
                    } else if (tileId == 2) { // Muro2 tile
                        faseAtual.add(new Wall("Muro2.png", x, y));
                    } else if(tileId == 3){
                        faseAtual.add(new Wall("Muro.png", x, y));
                    } else if(tileId == 4){
                        faseAtual.add(new Wall("Muro.png", x, y));
                    }
                }
            }
        }
    }
    
    private static void addLevelCharacters(int levelNumber, List<Personagem> faseAtual) {
        // Add level-specific characters
        switch(levelNumber) {
            case 1:
                Hero levelHero = new Hero("Robbo.png", 2, 2);
                faseAtual.add(levelHero);
                Door door2 = new Door("Grade.png", 2, 5, 12);
                Button button2 = new Button("Botao.png", 4, 13);
                button2.addLinkedDoor(door2);
                faseAtual.add(door2);
                faseAtual.add(button2);
                
                Chaser chaser = new Chaser("Chaser.png", 8, 8);;
//                faseAtual.add(chaser);
                faseAtual.add(new FinishPoint("finish.png", 10, 10));
                break;
            case 2:
                faseAtual.add(new Hero("Robbo.png", 1, 1));
//                Door door2 = new Door("Grade.png", 2, 10, 5);
//                Button button2 = new Button("Botao.png", 5, 5);
//                button2.addLinkedDoor(door2);
                
                break;
            // Add more levels as needed
        }
    }
    
    private void initializeDefaultLevels() {
        // Create default levels if file loading fails
        GameLevel level1 = new GameLevel(1);
    
        // Create door and button
        Door door = new Door("Grade.png", 1); 
        door.setPosicao(10, 5);
        

        Button button = new Button("Botao.png"); 
        button.addLinkedDoor(door);
        button.setPosicao(5, 5);

        // Add elements to level
        Hero hero = new Hero("Robbo.png");
        hero.setPosicao(2, 7);
               
        level1.addElement(hero);
        level1.addElement(door);
        level1.addElement(button);
        

        // Add other elements...
        level1.addElement(new BichinhoVaiVemHorizontal("roboPink.png", 3, 3));
        level1.addElement(new FinishPoint("finish.png", 15, 15));

//        levels.add(level1);
        
        // Add more default levels as needed...
        
        
        GameLevel level2 = new GameLevel(2);
        
        
        
        level2.addElement(new Hero("robbo.png", 1, 1));
        level2.addElement(new Chaser("Chaser.png", 20, 3));
        Door door2 = new Door("Grade.png", 2, 14, 14); 
        Door door3 = new Door("Grade.png", 3, 14, 15);
        Door door4 = new Door("Grade.png", 4, 14, 16);
        Door door5 = new Door("Grade.png", 5, 15, 16);
        Door door6 = new Door("Grade.png", 6, 16, 16);
        Door door7 = new Door("Grade.png", 7, 16, 15);
        Door door8 = new Door("Grade.png", 8, 16, 14);
        Door door9 = new Door("Grade.png", 9, 15, 14);
               

        Button button2 = new Button("Botao.png"); 
        button2.addLinkedDoor(door2);
        button2.addLinkedDoor(door3);
        button2.addLinkedDoor(door4);
        button2.addLinkedDoor(door5);
        button2.addLinkedDoor(door6);
        button2.addLinkedDoor(door7);
        button2.addLinkedDoor(door8);
        button2.addLinkedDoor(door9);
        
        button2.setPosicao(10, 10);
        
        level2.addElement(door2);
        level2.addElement(door3);
        level2.addElement(door4);
        level2.addElement(door5);
        level2.addElement(door6);
        level2.addElement(door7);
        level2.addElement(door8);
        level2.addElement(door9);
        
        level2.addElement(button2);
        
        level2.addElement(new FinishPoint("finish.png", 15, 15));
        
//        levels.add(level2);
        
    }
}