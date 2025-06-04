import Controler.Tela;
import Modelo.*;
import Modelo.CharacterExporter;

import Modelo.Personagem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
//        List<Personagem> charsToExport = new ArrayList<>();
//        charsToExport.add(new CannonR("CanhaoD.png"));
//        CharacterExporter.exportCharacters(charsToExport, "canhaoD.zip");
            
        // only used to create a zip file containing one character
        
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

