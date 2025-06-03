package Controler;

import Modelo.Personagem;
import Modelo.Caveira;
import Modelo.Hero;
import Modelo.Chaser;
import Modelo.BichinhoVaiVemHorizontal;
import Auxiliar.Consts;
import Auxiliar.Desenho;
import Modelo.BichinhoVaiVemVertical;
import Modelo.Button;
import Modelo.CharacterExporter;
import Modelo.CharacterImporter;
import Modelo.Door;
import Modelo.DragDropHandler;
import Modelo.FinishPoint;
import Modelo.ZigueZague;
import Modelo.GameLevel;
import Modelo.LevelLoader;
import Modelo.LevelManager;
import Modelo.SaveState;
import Modelo.TiledMapReader;
import Modelo.VisualTile;
import Modelo.Wall;
import auxiliar.Posicao;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

// the problem is in the class, it has to be a paiting so that the objects can spawn on it
// make respawn be in the same level

public class Tela extends javax.swing.JFrame implements KeyListener {

    private Hero hero;
    private final ArrayList<Personagem> faseAtual;
    private ControleDeJogo cj = new ControleDeJogo();
    private Graphics g2;
    private int cameraLinha = 0;
    private int cameraColuna = 0;
    private ArrayList<GameLevel> levels;
    private int currentLevel;
    private ArrayList<GameLevel> allLevels; // Store all levels
    private int currentLevelIndex = 0; // Track current level
    private boolean gameCompleted = false;
    
    

    public Tela() {
        
        
        
        Desenho.setCenario(this);
        initComponents();
        this.addKeyListener(this);
        this.setSize(Consts.RES * Consts.CELL_SIDE + getInsets().left + getInsets().right,
                Consts.RES * Consts.CELL_SIDE + getInsets().top + getInsets().bottom);

        faseAtual = new ArrayList<>();
        currentLevel = 1;
        loadCurrentLevel();
        atualizaCamera();
        new DropTarget(this, new DragDropHandler(this));
              
    }
    
    private void loadCurrentLevel() {
        try {
            TiledMapReader.clearVisualTiles(); // Clear previous visual tiles
            faseAtual.clear();
            LevelManager.loadLevel(currentLevel, faseAtual);
            hero = findHero();

            atualizaCamera();
            repaint();
        } catch (Exception e) {
            System.err.println("Error loading level " + currentLevel + ": " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to load level " + currentLevel + "\n" + e.getMessage(),
                "Level Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    public synchronized void addCharacters(List<Personagem> characters) {
        faseAtual.addAll(characters);
    }

    public synchronized List<Personagem> getFaseAtualCopy() {
        return new ArrayList<>(faseAtual);
    }

    public synchronized void clearFaseAtual() {
        faseAtual.clear();
    }
    
    private Hero findHero() {
        return (Hero) faseAtual.stream()
                .filter(p -> p instanceof Hero)
                .findFirst()
                .orElse(null);
    }
    
    private void loadLevel(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < levels.size()) {
            faseAtual.clear();
            faseAtual.addAll(levels.get(levelIndex).getElements());
            currentLevel = levelIndex;
            hero = findHero();
            if (hero == null) {
                throw new RuntimeException("Hero not found in level");
            }
            atualizaCamera();
        }
    }

    
    private void saveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(
            new FileOutputStream("savegame.dat"))) {

            // Save current level and hero state
            SaveState state = new SaveState(currentLevel, hero);
            out.writeObject(state);

            System.out.println("Game saved successfully for level " + currentLevel);
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to save game progress",
                "Save Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadGame() {
        try (ObjectInputStream in = new ObjectInputStream(
            new FileInputStream("savegame.dat"))) {

            SaveState state = (SaveState) in.readObject();

            // Load the saved level
            this.currentLevel = state.getCurrentLevel();
            loadCurrentLevel();

            // Restore hero state
            Hero savedHero = state.getHeroState();
            this.hero.setPosicao(savedHero.getPosicao().getLinha(), 
                               savedHero.getPosicao().getColuna());
            this.hero.setLives(state.getHeroLives());

            System.out.println("Game loaded successfully for level " + currentLevel);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load game: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to load saved game\nStarting new game",
                "Load Error", 
                JOptionPane.ERROR_MESSAGE);
            restartGame(); // Fallback to new game
        }
    }
    
    public void nextLevel() {
        if (currentLevel < LevelManager.getTotalLevels()) {
            currentLevel++; // Increment to next level
            loadCurrentLevel();
        } else {
            showGameCompleteMessage();
        }
    }
    
    public void restartLevel() {
        gameCompleted = false;
        currentLevel = 1;
        loadCurrentLevel();
    }
    
    private void showLevelCompleteMessage() {
        // You can implement a proper message display
        System.out.println("Level completed! Loading next level...");
        
    }
    
    private void showGameCompleteMessage() {
        SwingUtilities.invokeLater(() -> {
            int option = JOptionPane.showOptionDialog(this,
                "You've completed all levels!\nWould you like to play again?",
                "Game Complete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{"Play Again", "Exit"},
                "Play Again");

            if (option == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                System.exit(0);
            }
        });
    }
    
    private void loadScenarioMap() {
        try {
            // Clear existing walls/scenario
            faseAtual.removeIf(p -> p instanceof Wall);
            
            // Load new map
            TiledMapReader.loadMap("levels/Mapa1.json", faseAtual);
        } catch (IOException e) {
            System.err.println("Failed to load map: " + e.getMessage());
            // Fallback to default walls if needed
        }
    }
    
    // Call this when initializing or changing levels
    public void initLevel() {
        loadScenarioMap();
        // Add your characters/objects here
    }
    

    public int getCameraLinha() {
        return cameraLinha;
    }

    public int getCameraColuna() {
        return cameraColuna;
    }

    public boolean ehPosicaoValida(Posicao p) {
        return cj.ehPosicaoValida(this.faseAtual, p);
    }

    public void addPersonagem(Personagem umPersonagem) {
        faseAtual.add(umPersonagem);
    }

    public void removePersonagem(Personagem umPersonagem) {
        faseAtual.remove(umPersonagem);
    }

    public Graphics getGraphicsBuffer() {
        return g2;
    }

    @Override
    public void paint(Graphics gOld) {
        Graphics g = this.getBufferStrategy().getDrawGraphics();
        /*Criamos um contexto gráfico*/
        g2 = g.create(getInsets().left, getInsets().top, getWidth() - getInsets().right, getHeight() - getInsets().top);
        /**
         * ***********Desenha cenário de fundo*************
         */
        
        for (VisualTile tile : TiledMapReader.getVisualTiles()) {
            tile.draw(g2, cameraColuna, cameraLinha, Consts.CELL_SIDE);
        }

        // 2. Then draw game objects
        if (!this.faseAtual.isEmpty()) {
            this.cj.desenhaTudo(faseAtual);
            this.cj.processaTudo(faseAtual);
        }
        
        for (int i = 0; i < Consts.RES; i++) {
            for (int j = 0; j < Consts.RES; j++) {
                int mapaLinha = cameraLinha + i;
                int mapaColuna = cameraColuna + j;

                if (mapaLinha < Consts.MUNDO_ALTURA && mapaColuna < Consts.MUNDO_LARGURA) {
                    try {
                        Image newImage = Toolkit.getDefaultToolkit().getImage(
                                new java.io.File(".").getCanonicalPath() + Consts.PATH + "blackTile.png");
                        g2.drawImage(newImage,
                                j * Consts.CELL_SIDE, i * Consts.CELL_SIDE,
                                Consts.CELL_SIDE, Consts.CELL_SIDE, null);
                    } catch (IOException ex) {
                        Logger.getLogger(Tela.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        if (gameCompleted) {
            drawGameCompleteScreen(g2);
        } else {
            // Draw normal game
            if (!this.faseAtual.isEmpty()) {
                this.cj.desenhaTudo(faseAtual);
                this.cj.processaTudo(faseAtual);
            }
            drawLivesCounter(g2);
        }
        
        for (int i = 0; i < Consts.RES; i++) {
            for (int j = 0; j < Consts.RES; j++) {
                int mapaLinha = cameraLinha + i;
                int mapaColuna = cameraColuna + j;

                if (mapaLinha < Consts.MUNDO_ALTURA && mapaColuna < Consts.MUNDO_LARGURA) {
                    try {
                        String imageName;
                        // Check if this is a border position
                        if (mapaLinha == 0 || mapaLinha == Consts.MUNDO_ALTURA - 1 ||
                            mapaColuna == 0 || mapaColuna == Consts.MUNDO_LARGURA - 1) {
                            imageName = "Muro2.png";
                        } else {
                            imageName = "Tela-Fase1.png";
                        }

                        Image newImage = Toolkit.getDefaultToolkit().getImage(
                            new java.io.File(".").getCanonicalPath() + Consts.PATH + imageName);
                        g2.drawImage(newImage,
                            j * Consts.CELL_SIDE, i * Consts.CELL_SIDE,
                            Consts.CELL_SIDE, Consts.CELL_SIDE, null);
                    } catch (IOException ex) {
                        Logger.getLogger(Tela.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        
        
        /* Check game over state */
        if (cj.isGameOver()) {
            // Draw semi-transparent overlay
            g2.setColor(new Color(0, 0, 0, 180)); // Semi-transparent black
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Draw game over text
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            String gameOverText = "GAME OVER";
            int textWidth = g2.getFontMetrics().stringWidth(gameOverText);
            g2.drawString(gameOverText, (getWidth() - textWidth)/2, getHeight()/2);

            // Draw restart instruction
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            String restartText = "Press R to restart";
            textWidth = g2.getFontMetrics().stringWidth(restartText);
            g2.drawString(restartText, (getWidth() - textWidth)/2, getHeight()/2 + 50);
        } else {
            /* Draw game elements if not game over */
            if (!this.faseAtual.isEmpty()) {
                this.cj.desenhaTudo(faseAtual);
                this.cj.processaTudo(faseAtual);
            }
            drawLivesCounter(g2);
        }

        g.dispose();
        g2.dispose();
        if (!getBufferStrategy().contentsLost()) {
            getBufferStrategy().show();
        }               
        
    }
    
    private void drawLivesCounter(Graphics g) {
        Hero hero = findHero();
        if (hero != null) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Lives: " + hero.getLives(), 20, 30);
        }
    }
    
    private void drawGameCompleteScreen(Graphics g) {
        // Dark background
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Game complete text
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        String text = "CONGRATULATIONS!";
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (getWidth() - textWidth)/2, getHeight()/2 - 30);

        // Instructions
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        text = "You've completed all levels!";
        textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (getWidth() - textWidth)/2, getHeight()/2 + 20);

        text = "Press R to play again or ESC to exit";
        textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (getWidth() - textWidth)/2, getHeight()/2 + 50);
    }
    

    private void atualizaCamera() {
        int linha = hero.getPosicao().getLinha();
        int coluna = hero.getPosicao().getColuna();

        cameraLinha = Math.max(0, Math.min(linha - Consts.RES / 2, Consts.MUNDO_ALTURA - Consts.RES));
        cameraColuna = Math.max(0, Math.min(coluna - Consts.RES / 2, Consts.MUNDO_LARGURA - Consts.RES));
    }

    public void go() {
        TimerTask task = new TimerTask() {
            public void run() {
                repaint();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, Consts.PERIOD);
    }

    public void keyPressed(KeyEvent e) {
        
        if(cj.isGameOver()){
            if (e.getKeyCode() == KeyEvent.VK_R){
                restartGame();
            }
            return;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_S:
                saveGame();
                break;
            case KeyEvent.VK_L:
                loadGame();
                break;
            case KeyEvent.VK_UP:
                hero.moveUp();
                break;
            case KeyEvent.VK_DOWN:
                hero.moveDown();
                break;
            case KeyEvent.VK_LEFT:
                hero.moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                hero.moveRight();
                break;
        }
        this.atualizaCamera();
        this.setTitle("-> Cell: " + (hero.getPosicao().getColuna()) + ", "
                + (hero.getPosicao().getLinha()));

        //repaint(); /*invoca o paint imediatamente, sem aguardar o refresh*/
    }
    
    private void restartGame() {
        // Reset game state
        gameCompleted = false;
        cj = new ControleDeJogo(); // Reset game controller if needed

        // Reset to first level
        currentLevel = 1;

        // Reload the level
        loadCurrentLevel();

        // Force repaint
        repaint();
    }

    


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("POO2023-1 - Skooter");
        setAlwaysOnTop(true);
        setAutoRequestFocus(false);
        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 561, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }
}
