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

public class FinishPoint extends Personagem implements Serializable {
    public FinishPoint(String sNomeImagePNG) {
        super(sNomeImagePNG);
        this.bTransponivel = true; // Player can walk through it
        this.bMortal = false;     // Doesn't kill the player
    }
    
    public FinishPoint(String sNomeImagePNG, int startRow, int startCol){
        super(sNomeImagePNG);
        this.bMortal = false;
        this.bTransponivel = true;
        setPosicao(startRow, startCol);
    }
}