
package Modelo;

import Auxiliar.Desenho;
import java.util.Random;

public class BichinhoVaiVemVertical extends Personagem{
    boolean bUp;
    private static final long serialVersionUID = 1L;
    
    public BichinhoVaiVemVertical(String sNomeImagePNG, int startRow, int startCol) {
        super(sNomeImagePNG);
        bUp = true;
        setPosicao(startRow, startCol);
        bMortal = true;
    }

    public BichinhoVaiVemVertical(String sNomeImagePNG) {
        super(sNomeImagePNG);
        bUp = true;
        bMortal = true;
    }
    
    

    public void autoDesenho(){
        if(bUp)
            this.setPosicao(pPosicao.getLinha()-1, pPosicao.getColuna());
        else
            this.setPosicao(pPosicao.getLinha()+1, pPosicao.getColuna());           

        super.autoDesenho();
        bUp = !bUp;
    }  
}
