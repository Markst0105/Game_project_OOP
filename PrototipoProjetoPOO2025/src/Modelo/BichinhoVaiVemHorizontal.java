package Modelo;

import Auxiliar.Consts;
import Auxiliar.Desenho;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BichinhoVaiVemHorizontal extends Personagem implements Serializable {

    private boolean bRight;
    int iContador;
    private static final long serialVersionUID = 1L;

    public BichinhoVaiVemHorizontal(String sNomeImagePNG, int startRow, int startCol) {
        super(sNomeImagePNG);
        bRight = true;
        setPosicao(startRow, startCol);
        iContador = 0;
        bMortal = true;
    }

    public BichinhoVaiVemHorizontal(String sNomeImagePNG) {
        super(sNomeImagePNG);
        bRight = true;
        iContador = 0;
        bMortal = true;
    }
    
    

    public void autoDesenho() {
        if (iContador == 5) {
            iContador = 0;
            if (bRight) {
                this.setPosicao(pPosicao.getLinha(), pPosicao.getColuna() + 1);
            } else {
                this.setPosicao(pPosicao.getLinha(), pPosicao.getColuna() - 1);
            }

            bRight = !bRight;
        }
        super.autoDesenho();
        iContador++;
    }
}
