/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Auxiliar.Consts;
import Auxiliar.Desenho;
import auxiliar.Posicao;
import java.io.Serializable;

public class Chaser extends Personagem implements Serializable {
    private boolean iDirectionV;
    private boolean iDirectionH;
    private static final long serialVersionUID = 1L;
    private int moveCounter = 0;
    private int speedDelay; // Higher = slower (default: moves every 3 frames)
    
    public Chaser(String sNomeImagePNG, int startRow, int startCol) {
        super(sNomeImagePNG);
        iDirectionV = true;
        iDirectionH = true;
        setPosicao(startRow, startCol);
        this.bMortal = true;
        this.bTransponivel = true;
        this.speedDelay = 3;
    }

    public Chaser(String sNomeImagePNG) {
        super(sNomeImagePNG);
        iDirectionV = true;
        iDirectionH = true;
        this.bMortal = true;
        this.speedDelay = 3;
        this.bTransponivel = true;
    }
    
    
    public int getSpeed() {
        return speedDelay;
    }

    public void computeDirection(Posicao heroPos) {
        if (heroPos.getColuna() < this.getPosicao().getColuna()) {
            iDirectionH = true;
        } else if (heroPos.getColuna() > this.getPosicao().getColuna()) {
            iDirectionH = false;
        }
        if (heroPos.getLinha() < this.getPosicao().getLinha()) {
            iDirectionV = true;
        } else if (heroPos.getLinha() > this.getPosicao().getLinha()) {
            iDirectionV = false;
        }
    }

    public void autoDesenho() {
        moveCounter++;
        if (moveCounter >= speedDelay) {
            moveCounter = 0;
            
            // Only move when counter reaches speedDelay
            if (iDirectionH) {
                this.moveLeft();
            } else {
                this.moveRight();
            }
            if (iDirectionV) {
                this.moveUp();
            } else {
                this.moveDown();
            }
        }
        
        // Always draw the chaser (even when not moving)
        super.autoDesenho();
    }
}
