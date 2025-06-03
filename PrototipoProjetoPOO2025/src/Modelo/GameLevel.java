/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import auxiliar.Posicao;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author marks
 */
public class GameLevel implements Serializable {
    
    private int levelNumber;
    private ArrayList<Personagem> elements;
    private Posicao heroStartPosition;
    
    
    public GameLevel(int number) {
        this.levelNumber = number;
        this.elements = new ArrayList<>();
    }
    
    public void addElement(Personagem p) {
        elements.add(p);
    }

    public ArrayList<Personagem> getElements() {
        return new ArrayList<>(elements); // Return a copy
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public Posicao getHeroStartPosition() {
        return heroStartPosition;
    }
    
    
}
