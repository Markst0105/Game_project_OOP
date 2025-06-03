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
    
    private ArrayList<GameLevel> levels;
    private int currentLevelIndex;
    private Hero heroState;
    
    public SaveState(ArrayList<GameLevel> levels, int currentLevelIndex, Hero hero) {
        this.levels = levels;
        this.currentLevelIndex = currentLevelIndex;
        this.heroState = hero;
    }
    
    
    
    // Getters
    public ArrayList<GameLevel> getLevels() {
        return levels; 
    }
    public int getCurrentLevelIndex() { 
        return currentLevelIndex; 
    }
    public Hero getHeroState() { 
        return heroState; 
    }
    
}
