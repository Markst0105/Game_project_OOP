package auxiliar;

import java.io.Serializable;
import java.util.Objects;

public class Posicao implements Serializable {
    private int linha;
    private int coluna;
    
    private int linhaAnterior;
    private int colunaAnterior;

    public Posicao(int linha, int coluna) {
        this.setPosicao(linha, coluna);
    }

    public boolean setPosicao(int linha, int coluna) {
        // Prevent moving into walls (border positions)
        if (linha < 0 || linha >= Auxiliar.Consts.MUNDO_ALTURA) {
            return false; // Row is out of bounds
        }
        if (coluna < 0 || coluna >= Auxiliar.Consts.MUNDO_LARGURA) {
            return false; // Column is out of bounds
        }

        // If checks pass, update position
        this.linhaAnterior = this.linha;
        this.colunaAnterior = this.coluna;
        this.linha = linha;
        this.coluna = coluna;

        return true;
    }

    public int getLinha() {
        return linha;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Posicao pos = (Posicao) o;
        return linha == pos.linha && coluna == pos.coluna;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(linha, coluna);
    }

    public boolean volta() {
        return this.setPosicao(linhaAnterior, colunaAnterior);
    }

    public int getColuna() {
        return coluna;
    }

    public boolean igual(Posicao posicao) {
        return (linha == posicao.getLinha() && coluna == posicao.getColuna());
    }

    public boolean copia(Posicao posicao) {
        return this.setPosicao(posicao.getLinha(), posicao.getColuna());
    }

    public boolean moveUp() {
        return this.setPosicao(this.getLinha() - 1, this.getColuna());
    }

    public boolean moveDown() {
        return this.setPosicao(this.getLinha() + 1, this.getColuna());
    }

    public boolean moveRight() {
        return this.setPosicao(this.getLinha(), this.getColuna() + 1);
    }

    public boolean moveLeft() {
        return this.setPosicao(this.getLinha(), this.getColuna() - 1);
    }
}
