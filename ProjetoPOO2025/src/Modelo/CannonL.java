/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Auxiliar.Consts;
import Auxiliar.Desenho;
import java.io.Serializable;

/**
 *
 * @author marks
 */
public class CannonL extends Personagem implements Serializable {
    private int iContaIntervalos;
    private static final long serialVersionUID = 2L;
    private int fireRate; // Adjust this value as needed
    
    public CannonL(String sNomeImagePNG, int fireRate, int startRow, int startCol) {
        super(sNomeImagePNG);
        this.fireRate = fireRate;
        this.bTransponivel = false; // CaveiraL is not transponivel
        bMortal = true; // CaveiraL is mortal to the hero on contact
        setPosicao(startRow, startCol);
        this.iContaIntervalos = 0;
    }

    public CannonL(String sNomeImagePNG) {
        super(sNomeImagePNG);
        this.fireRate = 25;
        this.bTransponivel = false; // CaveiraL is not transponivel
        bMortal = true; // CaveiraL is mortal to the hero on contact
        // pPosicao is (1,1) by default from Personagem constructor
        this.iContaIntervalos = 0;
    }

    @Override
    public void autoDesenho() {
        super.autoDesenho();

        this.iContaIntervalos++;
        if (this.iContaIntervalos >= fireRate) { // Use >= for robustness, though == Consts.TIMER is common
            this.iContaIntervalos = 0;
            
            int fogoLinha = pPosicao.getLinha();
            int fogoColuna = pPosicao.getColuna() - 1; // Spawn to the left

            // Create the left-moving FogoL projectile
            // The FogoL constructor will attempt to set its position.
            // If Posicao.setPosicao has been fixed (see step 3), this should work.
            FogoL f = new FogoL("fireCharge.png", fogoLinha, fogoColuna); 
            
            Desenho.acessoATelaDoJogo().addPersonagem(f);
        }
    }     
}
