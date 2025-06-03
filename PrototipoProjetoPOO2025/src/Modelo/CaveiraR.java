package Modelo;

import Auxiliar.Consts;
import Auxiliar.Desenho;
import Controler.Tela;
import java.awt.Graphics;
import java.io.Serializable;

public class CaveiraR extends Personagem implements Serializable{
    private int iContaIntervalos;
    private static final long serialVersionUID = 1L;
    private static final int FIRE_INTERVAL = 20; // Adjust this value as needed
    
    public CaveiraR(String sNomeImagePNG, int startRow, int startCol) {
        super(sNomeImagePNG);
        this.bTransponivel = false;
        bMortal = true;
        setPosicao(startRow, startCol);
        this.iContaIntervalos = 0;
    }

    public CaveiraR(String sNomeImagePNG) {
        super(sNomeImagePNG);
        this.bTransponivel = false;
        bMortal = true;
        this.iContaIntervalos = 0;
    }
    
    

    public void autoDesenho() {
        super.autoDesenho();

        this.iContaIntervalos++;
        if(this.iContaIntervalos == FIRE_INTERVAL){
            this.iContaIntervalos = 0;
            FogoR f = new FogoR("fire.png");
            f.setPosicao(pPosicao.getLinha(),pPosicao.getColuna()+1);
            Desenho.acessoATelaDoJogo().addPersonagem(f);
        }
    }    
}