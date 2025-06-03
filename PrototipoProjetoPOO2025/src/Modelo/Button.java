/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author marks
 */
import java.util.List;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Button extends Personagem implements Serializable {
    private List<Door> linkedDoors;
    private boolean isPressed = false;
    private static final long serialVersionUID = 1L;
    
    public Button(String sNomeImagePNG) {
        super(sNomeImagePNG);
        this.linkedDoors = new ArrayList<>();
        this.bTransponivel = true;
        this.bMortal = false;
    }

    public Button(String sNomeImagePNG, int startRow, int startCol) {
        super(sNomeImagePNG);
        this.linkedDoors = new ArrayList<>();
        this.bTransponivel = true; // Player can walk through button
        this.bMortal = false;
        setPosicao(startRow, startCol);
    }
    
    public void addLinkedDoor(Door door) {
        linkedDoors.add(door);
    }
    
    public void activate() {
        if (!isPressed) {
            this.isPressed = true;
            for (Door door : linkedDoors) {
                door.open();
            }
            updateButtonImage();
        }
    }
    
    public void deactivate() {
        if (isPressed) {
            this.isPressed = false;
            for (Door door : linkedDoors) {
                door.close();
            }
            updateButtonImage();
        }
    }
    
    private void updateButtonImage() {
        try {
            String imageName = isPressed ? "button_pressed.png" : "button.png";
            this.iImage = new ImageIcon(new java.io.File(".").getCanonicalPath() + 
                          Auxiliar.Consts.PATH + imageName);
        } catch (IOException e) {
            System.out.println("Error loading button image: " + e.getMessage());
        }
    }
    
    public boolean isPressed() {
        return isPressed;
    }
}