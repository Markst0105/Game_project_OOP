package Modelo;

import Auxiliar.Desenho;
import Controler.Tela;
import java.awt.Graphics;
import java.io.Serializable;

public class Fogo extends Personagem implements Serializable{
    private static final long serialVersionUID = 1L;
            
    public Fogo(String sNomeImagePNG, int startRow, int startCol) {
        super(sNomeImagePNG);
        setPosicao(startRow, startCol);
        this.bMortal = true;
    }

    public Fogo(String sNomeImagePNG) {
        super(sNomeImagePNG);
        setPosicao(0, 0);
        this.bMortal = true;
    }
    
    

    @Override
    public void autoDesenho() {
        super.autoDesenho();
        if(!this.moveRight())
            Desenho.acessoATelaDoJogo().removePersonagem(this);
    }
    
}
