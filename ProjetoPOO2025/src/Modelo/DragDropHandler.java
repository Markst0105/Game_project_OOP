/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author marks
 */
import Controler.Tela;
import java.awt.Point;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;
import javax.swing.*;

public class DragDropHandler extends TransferHandler implements DropTargetListener {
    private final Tela tela;
    
    public DragDropHandler(Tela tela) {
        this.tela = tela;
    }
    
    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            Transferable transferable = dtde.getTransferable();

            if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);

                for (File file : files) {
                    if (file.getName().toLowerCase().endsWith(".zip")) {
                        Point dropPoint = dtde.getLocation();
                        int row = (dropPoint.y - tela.getInsets().top) / Auxiliar.Consts.CELL_SIDE + tela.getCameraLinha();
                        int col = (dropPoint.x - tela.getInsets().left) / Auxiliar.Consts.CELL_SIDE + tela.getCameraColuna();

                        SwingUtilities.invokeLater(() -> {
                            try {
                                List<Personagem> importedChars = CharacterImporter.importFromZip(
                                    file.getAbsolutePath(), row, col);
                                tela.addCharacters(importedChars);
                                tela.repaint();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                        break;
                    }
                }
            }
            dtde.dropComplete(true);
        } catch (Exception ex) {
            dtde.dropComplete(false);
            ex.printStackTrace();
        }
    }

    // Other required DropTargetListener methods (can leave empty)
    @Override public void dragEnter(DropTargetDragEvent dtde) {}
    @Override public void dragOver(DropTargetDragEvent dtde) {}
    @Override public void dropActionChanged(DropTargetDragEvent dtde) {}
    @Override public void dragExit(DropTargetEvent dte) {}
}