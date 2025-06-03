/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.io.Serializable;
import Modelo.GameLevel;
import Modelo.Hero;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author marks
 */
public class SaveState implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int currentLevel;
    private Hero heroState;
    private int heroLives;
    
    public SaveState(int currentLevel, Hero hero) {
        this.currentLevel = currentLevel;
        this.heroState = new Hero("robbo.png", 
                                hero.getPosicao().getLinha(), 
                                hero.getPosicao().getColuna());
        this.heroLives = hero.getLives();
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    public Hero getHeroState() {
        return heroState;
    }
    
    public int getHeroLives() {
        return heroLives;
    }
    
}
