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
import java.util.List;

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
        // First find the hero in the list
        Hero hero = null;
        for (Personagem p : umaFase) {
            if (p instanceof Hero) {
                hero = (Hero) p;
                break;
            }
        }
        
        // Create a copy of the list to avoid concurrent modification
        List<Personagem> copy;
        synchronized(umaFase) {
            copy = new ArrayList<>(umaFase);
        }

        // Process the copy instead of the original
        for (Personagem p : copy) {
            if (p instanceof Hero) {
                hero = (Hero) p;
                break;
            }
        }
        
        if (hero == null || gameOver || !hero.isAlive()) {
            return;
        }   
        
        boolean heroOnButton = false;
        
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

        // Check all collisions
        for (int i = 0; i < umaFase.size(); i++) {
            Personagem p = umaFase.get(i);
            
            if (p != hero && hero.getPosicao().igual(p.getPosicao())) {
                if (p instanceof FinishPoint) {
                    // Level completed!
                    Tela tela = Desenho.acessoATelaDoJogo();
                    tela.nextLevel();
                    return;
                }
                handleCollision(hero, p);
            }
        }
        
        // Process chasers' movement
        for (Personagem p : umaFase) {
            if (p instanceof Chaser) {
                Chaser chaser = (Chaser) p;
                chaser.computeDirection(hero.getPosicao());
                chaser.autoDesenho();
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
