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

public class Floor extends Personagem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public Floor(String image, int row, int col) {
        super(image);
        setPosicao(row, col);
        this.bTransponivel = true;
        this.bMortal = false;
    }

    public Floor(String image) {
        super(image);
        this.bMortal = false;
        this.bTransponivel = true;
    }
    
    
}