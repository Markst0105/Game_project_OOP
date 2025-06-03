package Controler;

import Auxiliar.Desenho;
import Modelo.Bomb;
import Modelo.Button;
import Modelo.CaveiraR;
import Modelo.Chaser;
import Modelo.FinishPoint;
import Modelo.FogoR;
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
                // Check if the character is a bomb and has vanished
                if (p instanceof Bomb && ((Bomb)p).hasVanished()) {
                    continue; // Skip drawing vanished bombs
                }
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
        
        ArrayList<Personagem> copiaDaFase = new ArrayList<>(umaFase);
        for (Personagem p : copiaDaFase) {
            // Skip processing if the character is the hero itself or a vanished bomb
            if (p == hero || (p instanceof Bomb && ((Bomb)p).hasVanished())) {
                continue;
            }

            if (hero.getPosicao().igual(p.getPosicao())) {
                if (p instanceof FinishPoint) {
                    Tela tela = Desenho.acessoATelaDoJogo();
                    tela.nextLevel();
                    return; // Level changes, stop processing current frame
                }
                // Pass the original 'p' from 'umaFase' if needed for modifications,
                // but here 'p' from 'copiaDaFase' is fine as we call methods on it.
                handleCollision(hero, p); 
                
                // If hero died from collision, stop further processing for this frame
                if (gameOver || !hero.isAlive()) {
                    return;
                }
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
        boolean heroTookDamage = false;
        
        if (other instanceof Bomb) {
            Bomb bomb = (Bomb) other;
            if (!bomb.isExploded()) {
                bomb.explode(); // Bomb explodes when touched
                
                // Hero loses a life only if not invincible
                if (!hero.isInvincible()) {
                    hero.loseLife();
                    heroTookDamage = true;
                }
            }
        } else if (other.isbMortal() || 
                   other instanceof CaveiraR || 
                   other instanceof FogoR || 
                   other instanceof Chaser) {
            
            // Hero loses life only if not invincible
            if (!hero.isInvincible()) {
                hero.loseLife();
                heroTookDamage = true;
            }
        }
    }

    /*Retorna true se a posicao p é válida para Hero com relacao a todos os personagens no array*/
    public boolean ehPosicaoValida(ArrayList<Personagem> umaFase, Posicao p) {
        // First check map borders
        if (p.getLinha() < 0 || p.getLinha() >= Auxiliar.Consts.MUNDO_ALTURA ||
            p.getColuna() < 0 || p.getColuna() >= Auxiliar.Consts.MUNDO_LARGURA) {
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
