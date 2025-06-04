package Modelo;

import Auxiliar.Consts;
import Auxiliar.Desenho;
import Controler.Tela;
import java.awt.Graphics;
import java.io.Serializable;

public class CannonR extends Personagem implements Serializable{
    private int iContaIntervalos;
    private static final long serialVersionUID = 1L;
    private int fireRate; // Adjust this value as needed
    
    public CannonR(String sNomeImagePNG, int fireRate, int startRow, int startCol) {
        super(sNomeImagePNG);
        this.bTransponivel = false;
        this.fireRate = fireRate;
        bMortal = true;
        setPosicao(startRow, startCol);
        this.iContaIntervalos = 0;
    }

    public CannonR(String sNomeImagePNG) {
        super(sNomeImagePNG);
        this.fireRate = 25;
        this.bTransponivel = false;
        bMortal = true;
        this.iContaIntervalos = 0;
    }
    
    

    public void autoDesenho() {
        super.autoDesenho();

        this.iContaIntervalos++;
        if(this.iContaIntervalos == fireRate){
            this.iContaIntervalos = 0;
            FogoR f = new FogoR("FireR.png");
            f.setPosicao(pPosicao.getLinha(),pPosicao.getColuna()+1);
            Desenho.acessoATelaDoJogo().addPersonagem(f);
        }
    }    
}