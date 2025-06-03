package Controler;

import Auxiliar.Desenho;
import Modelo.Button;
import Modelo.Caveira;
import Modelo.Chaser;
import Modelo.FinishPoint;
import Modelo.Fogo;
import Modelo.Personagem;
import Modelo.Hero;
import auxiliar.Posicao;
import java.util.ArrayList;

public class ControleDeJogo {
    private boolean gameOver = false;

    public boolean isGameOver() {
        return gameOver;
    }
       
    
    public void desenhaTudo(ArrayList<Personagem> e) {
        // Draw all elements except hero first
        Hero hero = null;
        for (Personagem p : e) {
            if (p instanceof Hero) {
                hero = (Hero) p;
            } else {
                p.autoDesenho();
            }
        }

        // Then draw hero on top of everything
        if (hero != null) {
            hero.autoDesenho();
        }
    }
    
    public void processaTudo(ArrayList<Personagem> umaFase) {
        Hero hero = (Hero) umaFase.get(0);
        boolean heroOnButton = false;
        if (gameOver || !hero.isAlive()) {
            return;
        }
        
        // Check all buttons
        for (Personagem p : umaFase) {
            if (p instanceof Button) {
                Button button = (Button) p;
                if (button.getPosicao().igual(hero.getPosicao())) {
                    button.activate();
                    heroOnButton = true;
                }
            }
        }
        
//        boolean heroOnButton = false;
//    
//        // Check button presses
//        for (Personagem p : umaFase) {
//            if (p instanceof Button) {
//                if (hero.getPosicao().igual(p.getPosicao())) {      //if player leaves button door closes
//                    ((Button) p).activate();
//                    heroOnButton = true;
//                } else {
//                    ((Button) p).deactivate();
//                }
//            }
//        }
        
        for (int i = 1; i < umaFase.size(); i++) {
            Personagem p = umaFase.get(i);

            if (hero.getPosicao().igual(p.getPosicao())) {
                if (p instanceof FinishPoint) {
                    // Level completed!
                    Tela tela = Desenho.acessoATelaDoJogo();
                    tela.nextLevel();
                    return;
                }
                handleCollision(hero, p);
            }
        }
        
        for (int i = 1; i < umaFase.size(); i++) {
            Personagem p = umaFase.get(i);
            
            // Check for collision with hero
            if (hero.getPosicao().igual(p.getPosicao())) {
                handleCollision(hero, p);
            }
        }
        
        // Process chasers' movement
        for (int i = 1; i < umaFase.size(); i++) {
            Personagem p = umaFase.get(i);
            if (p instanceof Chaser) {
                ((Chaser) p).computeDirection(hero.getPosicao());
            }
        }
    }
    
    private void handleCollision(Hero hero, Personagem other) {
        // Check if the other object is dangerous
        if (hero.isInvincible()) {
            return;
        }
        
        if (other.isbMortal() || 
            other instanceof Caveira || 
            other instanceof Fogo || 
            other instanceof Chaser) {
            
            hero.loseLife();
            
            if (!hero.isAlive()) {               
                gameOver = true;
            }
        }
    }

    /*Retorna true se a posicao p é válida para Hero com relacao a todos os personagens no array*/
    public boolean ehPosicaoValida(ArrayList<Personagem> umaFase, Posicao p) {
        // First check map borders
        if (p.getLinha() <= 0 || p.getLinha() >= Auxiliar.Consts.MUNDO_ALTURA - 1 ||
            p.getColuna() <= 0 || p.getColuna() >= Auxiliar.Consts.MUNDO_LARGURA - 1) {
            return false;
        }

        // Then check other objects
        for (Personagem personagem : umaFase) {
            if (!personagem.isbTransponivel() && personagem.getPosicao().igual(p)) {
                return false;
            }
        }
        return true;
    }
}
