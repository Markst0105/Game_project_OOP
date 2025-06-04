package Modelo;

import Auxiliar.Consts;
import Auxiliar.Desenho;
import Controler.ControleDeJogo;
import Controler.Tela;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Hero extends Personagem implements Serializable{
    private int lives;
    private boolean isInvincible = false;
    private long invincibleEndTime = 0;
    private static final long INVINCIBILITY_DURATION = 900; // 0.9 seconds in milliseconds
    private static final long serialVersionUID = 1L;
    
    public Hero(String sNomeImagePNG, int startRow, int startCol) {
        super(sNomeImagePNG);
        this.lives = 3;
        setPosicao(startRow, startCol);
        this.bMortal = false; //doesnt die on first hit       
    }

    public Hero(String sNomeImagePNG) {
        super(sNomeImagePNG);
        this.lives = 3;
        setPosicao(0, 0);
        this.bMortal = false; //doesnt die on first hit
    }
    
    public void startInvincibility() {
        this.isInvincible = true;
        this.invincibleEndTime = System.currentTimeMillis() + INVINCIBILITY_DURATION;
    }
    
    public boolean isInvincible() {
        if (isInvincible && System.currentTimeMillis() > invincibleEndTime) {
            isInvincible = false; // Auto-disable when time expires
        }
        return isInvincible;
    }
    

    public int getLives() {
        return lives;
    }
    
    public void loseLife() {
        if (!isInvincible() && lives > 0) {
            lives--;
            startInvincibility();
        }
    }

    public void setLives(int lives) {
        this.lives = lives;
    }
    
    
    
    public boolean isAlive() {
        return lives > 0;
    }

    public void voltaAUltimaPosicao(){
        this.pPosicao.volta();
    }
    
    
    public boolean setPosicao(int linha, int coluna){
        if(this.pPosicao.setPosicao(linha, coluna)){
            if (!Desenho.acessoATelaDoJogo().ehPosicaoValida(this.getPosicao())) {
                this.voltaAUltimaPosicao();
            }
            return true;
        }
        return false;       
    }

    /*TO-DO: este metodo pode ser interessante a todos os personagens que se movem*/
    private boolean validaPosicao(){
        if (!Desenho.acessoATelaDoJogo().ehPosicaoValida(this.getPosicao())) {
            this.voltaAUltimaPosicao();
            return false;
        }
        return true;       
    }
    
    public boolean moveUp() {
        if(super.moveUp())
            return validaPosicao();
        return false;
    }

    public boolean moveDown() {
        if(super.moveDown())
            return validaPosicao();
        return false;
    }

    public boolean moveRight() {
        if(super.moveRight())
            return validaPosicao();
        return false;
    }

    public boolean moveLeft() {
        if(super.moveLeft())
            return validaPosicao();
        return false;
    }    
    
}
