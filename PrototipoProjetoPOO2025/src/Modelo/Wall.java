/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author marks
 */
import java.io.Serializable;

public class Wall extends Personagem implements Serializable {
    public Wall(String sNomeImagePNG) {
        super(sNomeImagePNG);
        this.bTransponivel = false; // Cannot walk through walls
        this.bMortal = false;      // Walls don't kill you
    }
    
    @Override
    public void autoDesenho() {
        super.autoDesenho();
    }
}