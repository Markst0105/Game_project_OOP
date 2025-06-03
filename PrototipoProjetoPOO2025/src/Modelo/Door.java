/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author marks
 */
import java.io.IOException;
import java.io.Serializable;
import javax.swing.ImageIcon;

public class Door extends Personagem implements Serializable {
    private boolean isOpen = false;
    private String closedImage;
    private String openImage;
    private int doorId;  // Unique identifier for each door
    private static final long serialVersionUID = 1L;
    
    public Door(String sNomeImagePNG, int doorId) {
        super(sNomeImagePNG);
        this.doorId = doorId;
        this.closedImage = sNomeImagePNG;
        this.openImage = "door_open_" + doorId + ".png"; // Unique open image for each door
        this.bTransponivel = false;
        this.bMortal = false;
    }

    public Door(String sNomeImagePNG, int doorId, int startRow, int startCol) {
        super(sNomeImagePNG);
        this.closedImage = sNomeImagePNG;
        this.doorId = doorId;
        this.openImage = "door_open.png"; 
        this.bTransponivel = false; // Block passage when closed
        this.bMortal = false;
        setPosicao(startRow, startCol);
    }
       
    
    public void open() {
        this.isOpen = true;
        this.bTransponivel = true;
        updateImage();
    }
    
    public void close() {
        this.isOpen = false;
        this.bTransponivel = false;
        updateImage();
    }
    
    private void updateImage() {
        try {
            String imageName = isOpen ? openImage : closedImage;
            this.iImage = new ImageIcon(new java.io.File(".").getCanonicalPath() + 
                          Auxiliar.Consts.PATH + imageName);
        } catch (IOException e) {
            System.out.println("Error loading door image: " + e.getMessage());
        }
    }
    
    public boolean isOpen() {
        return isOpen;
    }
    
    public int getDoorId() {
        return doorId;
    }
}