package Modelo;

import Auxiliar.Consts;
import Auxiliar.Desenho;
import Controler.Tela;
import java.awt.Graphics;
import java.io.Serializable;

public class Caveira extends Personagem implements Serializable{
    private int iContaIntervalos;
    private static final long serialVersionUID = 1L;
    private boolean direction = true;
    
    public Caveira(String sNomeImagePNG, boolean direction, int startRow, int startCol) {
        super(sNomeImagePNG);
        this.bTransponivel = false;
        bMortal = true;
        setPosicao(startRow, startCol);
        this.iContaIntervalos = 0;
        this.direction = direction;
    }

    public Caveira(String sNomeImagePNG, boolean direction) {
        super(sNomeImagePNG);
        this.bTransponivel = false;
        bMortal = true;
        this.iContaIntervalos = 0;
        this.direction = direction;
    }
    
    

    public void autoDesenho() {
        super.autoDesenho();

        this.iContaIntervalos++;
        if(this.iContaIntervalos == Consts.TIMER){
            this.iContaIntervalos = 0;
            Fogo f = new Fogo("fire.png");
            if(direction = true){
                f.setPosicao(pPosicao.getLinha(),pPosicao.getColuna()+1);
            }else if(direction = false){
                f.setPosicao(pPosicao.getLinha(),pPosicao.getColuna()-1);
            }
            Desenho.acessoATelaDoJogo().addPersonagem(f);
        }
    }    
}
