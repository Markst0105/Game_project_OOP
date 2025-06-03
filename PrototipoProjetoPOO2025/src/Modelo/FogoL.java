/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Auxiliar.Desenho;
import java.io.Serializable;

/**
 *
 * @author marks
 */
public class FogoL extends Personagem implements Serializable {
    private static final long serialVersionUID = 2L; 
    private int moveCounter = 0;
    private static final int MOVE_INTERVAL = 4; // Example: Projectile moves 1 cell every 4 game ticks.
    
    public FogoL(String sNomeImagePNG) {
        super(sNomeImagePNG);
        // pPosicao is initialized in the superclass Personagem constructor (e.g., to (1,1))
        this.bMortal = true; // Projectile is deadly
    }

    public FogoL(String sNomeImagePNG, int startRow, int startCol) {
        super(sNomeImagePNG);
        this.setPosicao(startRow, startCol); // Set its starting position
        this.bMortal = true; // Projectile is deadly
    }

    @Override
    public void autoDesenho() {
        super.autoDesenho(); // Draws the FogoL at its current position

        moveCounter++;
        if (moveCounter >= MOVE_INTERVAL) {
            moveCounter = 0; // Reset the counter

            // FogoL moves left
            if (!this.moveLeft()) { // Attempts to move one cell to the left
                // If moveLeft() returns false, remove the FogoL
                Desenho.acessoATelaDoJogo().removePersonagem(this);
            }
        }
    }
}