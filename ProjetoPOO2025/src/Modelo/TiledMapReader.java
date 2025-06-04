/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author marks
 */
import Modelo.Wall;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TiledMapReader {
    private static final List<VisualTile> visualTiles = new ArrayList<>();
    private static final Map<Integer, String> tileIdToImageMap = new HashMap<>();
    private static JSONObject mapJson;

    public static void loadMap(String filePath, ArrayList<Personagem> gameObjects) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        mapJson = new JSONObject(content);
        loadTilesetMappings();
        JSONArray layers = mapJson.getJSONArray("layers");

        for (int i = 0; i < layers.length(); i++) {
            JSONObject layer = layers.getJSONObject(i);
            if ("tilelayer".equals(layer.getString("type"))) {
                processLayer(layer, gameObjects);
            }
        }
    }

    private static void loadTilesetMappings() {
        JSONArray tilesets = mapJson.getJSONArray("tilesets");
        for (int i = 0; i < tilesets.length(); i++) {
            JSONObject tileset = tilesets.getJSONObject(i);
            int firstGid = tileset.getInt("firstgid");
            String imagePath = tileset.getString("image");
            String imageName = imagePath.substring(imagePath.lastIndexOf('/') + 1);
            tileIdToImageMap.put(firstGid, imageName);
        }
    }

    private static void processLayer(JSONObject layer, ArrayList<Personagem> gameObjects) {
        String layerName = layer.getString("name");
        JSONArray data = layer.getJSONArray("data");
        int width = layer.getInt("width");
        int height = layer.getInt("height");

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int tileId = data.getInt(y * width + x);
                if (tileId != 0) {
                    String imageName = getImageForTileId(tileId);
                    if (layerName.equalsIgnoreCase("collision")) {
                        gameObjects.add(createGameObject(imageName, y, x));
                    } else {
                        visualTiles.add(new VisualTile(imageName, x, y));
                    }
                }
            }
        }
    }

    private static String getImageForTileId(int tileId) {
        // Find the highest firstGid that's <= tileId
        int bestMatch = 0;
        for (Integer gid : tileIdToImageMap.keySet()) {
            if (gid <= tileId && gid > bestMatch) {
                bestMatch = gid;
            }
        }
        return tileIdToImageMap.get(bestMatch);
    }

    private static Personagem createGameObject(String imageName, int x, int y) {
        // Customize this based on your game's needs
        if (imageName.contains("wall") || imageName.contains("muro")) {
            return new Wall(imageName, x, y);
        }
        // Add other object types as needed
        return new Floor(imageName, x, y); // Default fallback
    }

    public static List<VisualTile> getVisualTiles() {
        return visualTiles;
    }

    public static void clearVisualTiles() {
        visualTiles.clear();
        tileIdToImageMap.clear();
    }
}