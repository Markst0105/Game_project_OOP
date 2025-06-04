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
    
    public static void loadLevel(int levelNumber, ArrayList<Personagem> faseAtual) throws IOException {
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
                        faseAtual.add(new Floor("stone.png", y, x));
                    } else if (tileId == 2) { // Muro2 tile
                        faseAtual.add(new Wall("quartz1.png", y, x));
                    } else if(tileId == 3){
                        faseAtual.add(new Floor("grass.png", y, x));
                    } else if(tileId == 4){
                        faseAtual.add(new Wall("oakPlank.png", y, x));
                    }
                }
            }
        }
    }
    
    private static void addLevelCharacters(int levelNumber, ArrayList<Personagem> faseAtual) {
        // Add level-specific characters
        switch(levelNumber) {
            case 1:
                Hero hero = new Hero("Robbo.png");
                hero.setPosicao(1, 1);

                FinishPoint finish = new FinishPoint("finishFlag.png");
                finish.setPosicao(38, 18);
                
                faseAtual.add(new Chaser("Chaser.png", 10, 10));

                faseAtual.add(hero);
                faseAtual.add(finish);

                break;
            case 2:
                Hero hero2 = new Hero("Robbo.png");
                hero2.setPosicao(1, 1);

                FinishPoint finish2 = new FinishPoint("finishFlag.png");
                finish2.setPosicao(38, 1);
                CannonR Caveira1 = new CannonR("cannonR.png");
                Caveira1.setPosicao(11, 1);
                CannonR Caveira2 = new CannonR("cannonR.png");
                Caveira2.setPosicao(10, 1);
                CannonL Caveira3 = new CannonL("cannonL.png");
                Caveira3.setPosicao(18, 18);
                CannonL Caveira4 = new CannonL("cannonL.png");
                Caveira4.setPosicao(19, 18);
                CannonL Caveira5 = new CannonL("cannonL.png", 50, 30, 18);          
                CannonL Caveira6 = new CannonL("cannonL.png", 40, 37, 18);              
                CannonR Caveira7 = new CannonR("cannonR.png", 50, 38, 2);

                faseAtual.add(hero2);
                faseAtual.add(finish2);
                faseAtual.add(Caveira1);
                faseAtual.add(Caveira2);
                faseAtual.add(Caveira3);
                faseAtual.add(Caveira4);
                faseAtual.add(Caveira5);
                faseAtual.add(Caveira6);
                faseAtual.add(Caveira7);
                
                break;
            case 3:
                // Hero e final
                Hero hero3 = new Hero("Robbo.png");
                hero3.setPosicao(1, 1);

                FinishPoint finish3 = new FinishPoint("finishFlag.png");
                finish3.setPosicao(18, 46);  

                // Portas
                Door door1 = new Door("trapdoor.png", 1, 1, 9);  
                Door door2 = new Door("trapdoor.png", 2, 7, 5);  
                Door door3 = new Door("trapdoor.png", 3, 13, 11); 
                Door door4 = new Door("trapdoor.png", 4, 14, 11); 
                Door door5 = new Door("trapdoor.png", 5, 1, 28);  
                Door door6 = new Door("trapdoor.png", 6, 2, 28);  

                // Botões
                Button Button1 = new Button("button.png");
                Button1.setPosicao(5, 1);  
                Button1.addLinkedDoor(door1);

                Button Button2 = new Button("button.png");
                Button2.setPosicao(3, 12);  
                Button2.addLinkedDoor(door2);

                Button Button3 = new Button("button.png");
                Button3.setPosicao(15, 3);  
                Button3.addLinkedDoor(door3);
                Button3.addLinkedDoor(door4);

                Button Button4 = new Button("button.png");
                Button4.setPosicao(18, 31);  
                Button4.addLinkedDoor(door5);
                Button4.addLinkedDoor(door6);

                // Adiciona todos os elementos ao fase
                faseAtual.add(hero3);
                faseAtual.add(finish3);

                faseAtual.add(door1);
                faseAtual.add(door2);
                faseAtual.add(door3);
                faseAtual.add(door4);
                faseAtual.add(door5);
                faseAtual.add(door6);

                faseAtual.add(Button1);
                faseAtual.add(Button2);
                faseAtual.add(Button3);
                faseAtual.add(Button4);
                break;
                
            case 4:
                Hero hero4 = new Hero("Robbo.png");
                hero4.setPosicao(1, 1); // Exemplo de posição
    
                
                FinishPoint finish4 = new FinishPoint("finishFlag.png");
                finish4.setPosicao(39, 18);

                // Criar 10 Bombs
                Bomb Bomb1 = new Bomb("tnt.png");
                Bomb1.setPosicao(10, 7);

                Bomb Bomb2 = new Bomb("tnt.png");
                Bomb2.setPosicao(0, 18);

                Bomb Bomb3 = new Bomb("tnt.png");
                Bomb3.setPosicao(4, 2);

                Bomb Bomb4 = new Bomb("tnt.png");
                Bomb4.setPosicao(20, 1);

                Bomb Bomb5 = new Bomb("tnt.png");
                Bomb5.setPosicao(16, 18);

                Bomb Bomb6 = new Bomb("tnt.png");
                Bomb6.setPosicao(27, 15);

                Bomb Bomb7 = new Bomb("tnt.png");
                Bomb7.setPosicao(29, 10);

                Bomb Bomb9 = new Bomb("tnt.png");
                Bomb9.setPosicao(35, 10);

                Bomb Bomb10 = new Bomb("tnt.png");
                Bomb10.setPosicao(39, 0);
                
                Chaser chaser2 = new Chaser("Chaser.png", 3, 2);
                
                faseAtual.add(hero4);
                faseAtual.add(finish4);
                faseAtual.add(chaser2);

                faseAtual.add(Bomb1);
                faseAtual.add(Bomb2);
                faseAtual.add(Bomb3);
                faseAtual.add(Bomb4);
                faseAtual.add(Bomb5);
                faseAtual.add(Bomb6);
                faseAtual.add(Bomb7);
                faseAtual.add(Bomb9);
                faseAtual.add(Bomb10);
                break;
                
            case 5:
                faseAtual.add(new Hero("Robbo.png", 1, 1));
                
                faseAtual.add(new FinishPoint("finishFlag.png", 38, 28));

                faseAtual.add(new Bomb("tnt.png", 2, 8));
                faseAtual.add(new Bomb("tnt.png", 7, 11));
                faseAtual.add(new Bomb("tnt.png", 8, 11));
                faseAtual.add(new Bomb("tnt.png", 9, 11));
                faseAtual.add(new Bomb("tnt.png", 7, 12));
                faseAtual.add(new Bomb("tnt.png", 8, 12));
                faseAtual.add(new Bomb("tnt.png", 9, 12));
                faseAtual.add(new Bomb("tnt.png", 9, 37));
                faseAtual.add(new Bomb("tnt.png", 28, 38));
                faseAtual.add(new Bomb("tnt.png", 27, 38));
                faseAtual.add(new Bomb("tnt.png", 28, 36));
                faseAtual.add(new Bomb("tnt.png", 27, 34));
                faseAtual.add(new Bomb("tnt.png", 28, 30));
                faseAtual.add(new Bomb("tnt.png", 27, 26));
                faseAtual.add(new Bomb("tnt.png", 29, 25));
                faseAtual.add(new Bomb("tnt.png", 28, 23));
                faseAtual.add(new Bomb("tnt.png", 30, 22));
                faseAtual.add(new Bomb("tnt.png", 31, 23));
                faseAtual.add(new Bomb("tnt.png", 31, 26));
                faseAtual.add(new Bomb("tnt.png", 32, 24));
                faseAtual.add(new Bomb("tnt.png", 32, 27));
                faseAtual.add(new Bomb("tnt.png", 33, 21));
                faseAtual.add(new Bomb("tnt.png", 34, 24));
                faseAtual.add(new Bomb("tnt.png", 35, 21));
                faseAtual.add(new Bomb("tnt.png", 35, 26));
                faseAtual.add(new Bomb("tnt.png", 36, 25));
                faseAtual.add(new Bomb("tnt.png", 36, 27));
                faseAtual.add(new Bomb("tnt.png", 37, 23));
                faseAtual.add(new Bomb("tnt.png", 38, 21));
                faseAtual.add(new Bomb("tnt.png", 38, 26));


                faseAtual.add(new CannonR("cannonR.png", 22, 1, 2));
                faseAtual.add(new CannonL("cannonL.png", 22, 7, 8));
                faseAtual.add(new CannonL("cannonL.png", 45, 1, 18));
                faseAtual.add(new CannonL("cannonL.png", 45, 18, 28));
                faseAtual.add(new CannonR("cannonR.png", 20, 17, 20));
                faseAtual.add(new CannonR("cannonR.png", 20, 12, 20));
                faseAtual.add(new CannonL("cannonL.png", 20, 10, 28));
                faseAtual.add(new CannonR("cannonR.png", 20, 8, 20));
                faseAtual.add(new CannonL("cannonL.png", 20, 5, 28));
                faseAtual.add(new CannonR("cannonR.png", 20, 3, 20));
                faseAtual.add(new CannonR("cannonR.png", 20, 2, 20));
                faseAtual.add(new CannonR("cannonR.png", 22, 15, 30));
                faseAtual.add(new CannonL("cannonL.png", 22, 19, 38));
                faseAtual.add(new CannonR("cannonR.png", 22, 21, 21));
                faseAtual.add(new CannonR("cannonR.png", 22, 26, 21));
                faseAtual.add(new CannonL("cannonL.png", 20, 29, 28));

                Door door7 = new Door("trapdoor.png", 7, 8, 9);
                Door door8 = new Door("trapdoor.png", 8, 9, 13);
                Door door9 = new Door("trapdoor.png", 9, 1, 29);
                Door door10 = new Door("trapdoor.png", 10, 2, 29);
                Door door11 = new Door("trapdoor.png", 11, 14, 38);

                // Buttons
                Button Button5 = new Button("button.png");
                Button5.setPosicao(1, 8);
                Button5.addLinkedDoor(door7);

                Button Button6 = new Button("button.png");
                Button6.setPosicao(4, 14);
                Button6.addLinkedDoor(door8);

                Button Button7 = new Button("button.png");
                Button7.setPosicao(1, 20);
                Button7.addLinkedDoor(door9);
                Button7.addLinkedDoor(door10);

                Button Button8 = new Button("button.png");
                Button8.setPosicao(11, 33);
                Button8.addLinkedDoor(door11);

                faseAtual.add(door7);
                faseAtual.add(door8);
                faseAtual.add(door9);
                faseAtual.add(door10);
                faseAtual.add(door11);

                faseAtual.add(Button5);
                faseAtual.add(Button6);
                faseAtual.add(Button7);
                faseAtual.add(Button8);
            // Add more levels as needed
        }
    }
    
    
}