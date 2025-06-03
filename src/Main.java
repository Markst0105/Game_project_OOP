import Controler.Tela;
import Modelo.Caveira;
import Modelo.CharacterExporter;
import Modelo.Door;
import Modelo.Hero;
import Modelo.Personagem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        
   
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Tela tTela = new Tela();
                tTela.setVisible(true);
                tTela.createBufferStrategy(2);
                tTela.go();
            }
        });
    }
}

