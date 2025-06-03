/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Auxiliar.Consts;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;

/**
 *
 * @author marks
 */
public class VisualTile {
    private final String imageName;
    private final int x, y;
    private Image cachedImage;

    public VisualTile(String imageName, int x, int y) {
        this.imageName = imageName;
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g, int cameraX, int cameraY, int cellSize) {
        try {
            if (cachedImage == null) {
                String imagePath = new java.io.File(".").getCanonicalPath() + 
                                 Consts.PATH + imageName;
                cachedImage = Toolkit.getDefaultToolkit().getImage(imagePath);
            }

            int screenX = (x - cameraX) * cellSize;
            int screenY = (y - cameraY) * cellSize;
            
            if (screenX > -cellSize && screenY > -cellSize && 
                screenX < Consts.RES * cellSize && screenY < Consts.RES * cellSize) {
                g.drawImage(cachedImage, screenX, screenY, cellSize, cellSize, null);
            }
        } catch (IOException e) {
            System.err.println("Error loading visual tile image: " + e.getMessage());
        }
    }
}