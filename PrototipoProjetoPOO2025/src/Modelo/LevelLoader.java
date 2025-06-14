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
import Auxiliar.Posicao;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;  //https://github.com/stleary/JSON-java?tab=readme-ov-file
import org.json.JSONObject;

public class LevelLoader {
    private static final String LEVELS_DIR = "/levels/";
    
    public static GameLevel loadLevelFromJSON(String filePath) throws IOException {
        // Read the entire file content
        
        // Get the file path relative to your project
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Level file not found: " + file.getAbsolutePath());
        }
        
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        
        
        
        JSONObject json = new JSONObject(content.toString());
        GameLevel level = new GameLevel(json.getInt("levelNumber"));
        
        JSONArray elements = json.getJSONArray("elements");
        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            String type = element.getString("type");
            JSONObject pos = element.getJSONObject("position");
            Posicao position = new Posicao(pos.getInt("row"), pos.getInt("col"));
            
            Personagem character = createCharacter(type, element.getString("image"), position);
            level.addElement(character);
        }
        
        return level;
    }
    
    public static GameLevel loadLevel(int levelNumber) throws IOException {
        String levelPath = LEVELS_DIR + "level" + levelNumber + ".json";
        return loadLevelFromJSON(levelPath);
    }
    
    private static Personagem createCharacter(String type, String image, Posicao position) {
        switch (type) {
            case "Hero":
                Hero hero = new Hero(image);
                hero.setPosicao(position.getLinha(), position.getColuna());
                return hero;
            case "CaveiraR":
                CannonR skull = new CannonR(image);
                skull.setPosicao(position.getLinha(), position.getColuna());
                return skull;
            case "CaveiraL":
                CannonL skull2 = new CannonL(image);
                skull2.setPosicao(position.getLinha(), position.getColuna());
                return skull2;
            case "Chaser":
                Chaser chaser = new Chaser(image);
                chaser.setPosicao(position.getLinha(), position.getColuna());
                return chaser;
            case "FinishPoint":
                FinishPoint finish = new FinishPoint(image);
                finish.setPosicao(position.getLinha(), position.getColuna());
                return finish;                    
            default:
                throw new IllegalArgumentException("Unknown character type: " + type);
        }
    }
}