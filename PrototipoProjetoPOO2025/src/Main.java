import Controler.Tela;
import Modelo.CannonR;
import Modelo.CharacterExporter;
import Modelo.Door;
import Modelo.Hero;
import Modelo.Personagem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
//        List<Personagem> charsToExport = new ArrayList<>();
//        charsToExport.add(new CaveiraR("caveira.png"));
//        CharacterExporter.exportCharacters(charsToExport, "caveiraR.zip");
   
        
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

