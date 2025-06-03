package Modelo;

import Auxiliar.Desenho;
import Controler.Tela;
import java.awt.Graphics;
import java.io.Serializable;

public class FogoR extends Personagem implements Serializable{
    private static final long serialVersionUID = 1L;
    private int moveCounter = 0;
    private static final int MOVE_INTERVAL = 3; // Example: Projectile moves 1 cell every 3 game ticks.

            
    public FogoR(String sNomeImagePNG, int startRow, int startCol) {
        super(sNomeImagePNG);
        setPosicao(startRow, startCol);
        this.bMortal = true;
    }

    public FogoR(String sNomeImagePNG) {
        super(sNomeImagePNG);
        this.bMortal = true;
    }
    
    

    @Override
    public void autoDesenho() {
        super.autoDesenho(); // Draws the Fogo at its current position

        moveCounter++;
        if (moveCounter >= MOVE_INTERVAL) {
            moveCounter = 0; // Reset the counter

            // Fogo is assumed to move right
            if (!this.moveRight()) { // Attempts to move one cell to the right
                // If moveRight() returns false (e.g., hit a boundary), remove the Fogo
                Desenho.acessoATelaDoJogo().removePersonagem(this); //
            }
        }
    }
    
}