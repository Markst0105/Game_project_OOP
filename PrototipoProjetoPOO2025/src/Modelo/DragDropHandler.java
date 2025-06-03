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
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;
import javax.swing.*;

public class DragDropHandler extends TransferHandler implements DropTargetListener {
    private final Tela gameWindow;

    public DragDropHandler(Tela window) {
        this.gameWindow = window;
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            Transferable tr = dtde.getTransferable();
            if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                List<File> files = (List<File>) tr.getTransferData(DataFlavor.javaFileListFlavor);
                
                for (File file : files) {
                    if (file.getName().endsWith(".zip")) {
                        // Convert mouse position to game grid
                        java.awt.Point dropPoint = dtde.getLocation();
                        int col = (dropPoint.x - gameWindow.getInsets().left) / Auxiliar.Consts.CELL_SIDE + gameWindow.getCameraColuna();
                        int row = (dropPoint.y - gameWindow.getInsets().top) / Auxiliar.Consts.CELL_SIDE + gameWindow.getCameraLinha();
                        
                        List<Personagem> newChars = CharacterImporter.importFromZip(file.getPath(), row, col);
                        newChars.forEach(gameWindow::addPersonagem);
                    }
                }
                dtde.dropComplete(true);
                gameWindow.repaint();
            }
        } catch (Exception ex) {
            dtde.rejectDrop();
            System.err.println("Import failed: " + ex.getMessage());
        }
    }

    // Other required DropTargetListener methods (can leave empty)
    @Override public void dragEnter(DropTargetDragEvent dtde) {}
    @Override public void dragOver(DropTargetDragEvent dtde) {}
    @Override public void dropActionChanged(DropTargetDragEvent dtde) {}
    @Override public void dragExit(DropTargetEvent dte) {}
}