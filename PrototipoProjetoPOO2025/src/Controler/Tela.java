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
import Modelo.CharacterImporter;
import Modelo.Door;
import Modelo.DragDropHandler;
import Modelo.FinishPoint;
import Modelo.ZigueZague;
import Modelo.GameLevel;
import Modelo.LevelLoader;
import Modelo.SaveState;
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

public class Tela extends javax.swing.JFrame implements KeyListener {

    private Hero hero;
    private ArrayList<Personagem> faseAtual;
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

        // Initialize collections
        faseAtual = new ArrayList<>();
        allLevels = new ArrayList<>();
        levels = new ArrayList<>();

        // Create game elements
        createMapBorders();
        initializeDefaultLevels();
        loadLevel(0);

        // Start game
        atualizaCamera();
        new DropTarget(this, new DragDropHandler(this));
              
    }
    
    private void loadAllLevels() {
        levels = new ArrayList<>(); // Create new list instead of clearing
        int levelCount = 1;

        while (true) {
            try {
                GameLevel level = LevelLoader.loadLevel(levelCount);
                levels.add(level);
                levelCount++;
            } catch (IOException e) {
                break;
            }
        }

        if (levels.isEmpty()) {
            initializeDefaultLevels();
        }
       
    }
    
    private void initializeDefaultLevels() {
        // Create default levels if file loading fails
        GameLevel level1 = new GameLevel(1);
    
        // Create door and button
        Door door = new Door("Grade.png", 1); 
        door.setPosicao(10, 5);
        

        Button button = new Button("Botao.png"); 
        button.addLinkedDoor(door);
        button.setPosicao(5, 5);

        // Add elements to level
        Hero hero = new Hero("Robbo.png");
        hero.setPosicao(2, 7);
               
        level1.addElement(hero);
        level1.addElement(door);
        level1.addElement(button);
        

        // Add other elements...
        level1.addElement(new BichinhoVaiVemHorizontal("roboPink.png", 3, 3));
        level1.addElement(new FinishPoint("finish.png", 15, 15));

        levels.add(level1);
        
        // Add more default levels as needed...
        
        
        GameLevel level2 = new GameLevel(2);
        
        
        
        level2.addElement(new Hero("robbo.png", 1, 1));
        level2.addElement(new Chaser("Chaser.png", 20, 3));
        Door door2 = new Door("Grade.png", 2, 14, 14); 
        Door door3 = new Door("Grade.png", 3, 14, 15);
        Door door4 = new Door("Grade.png", 4, 14, 16);
        Door door5 = new Door("Grade.png", 5, 15, 16);
        Door door6 = new Door("Grade.png", 6, 16, 16);
        Door door7 = new Door("Grade.png", 7, 16, 15);
        Door door8 = new Door("Grade.png", 8, 16, 14);
        Door door9 = new Door("Grade.png", 9, 15, 14);
               

        Button button2 = new Button("Botao.png"); 
        button2.addLinkedDoor(door2);
        button2.addLinkedDoor(door3);
        button2.addLinkedDoor(door4);
        button2.addLinkedDoor(door5);
        button2.addLinkedDoor(door6);
        button2.addLinkedDoor(door7);
        button2.addLinkedDoor(door8);
        button2.addLinkedDoor(door9);
        
        button2.setPosicao(10, 10);
        
        level2.addElement(door2);
        level2.addElement(door3);
        level2.addElement(door4);
        level2.addElement(door5);
        level2.addElement(door6);
        level2.addElement(door7);
        level2.addElement(door8);
        level2.addElement(door9);
        
        level2.addElement(button2);
        
        level2.addElement(new FinishPoint("finish.png", 15, 15));
        
        levels.add(level2);
        
    }
    
    private void loadLevel(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < levels.size()) {
            faseAtual.clear();
            faseAtual.addAll(levels.get(levelIndex).getElements());
            currentLevel = levelIndex;
            hero = (Hero)faseAtual.get(0); // Update hero reference
            atualizaCamera();
        }
    }

    
    private void saveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("savegame.dat"))) {

            // Create a deep copy of hero to avoid saving references
            Hero savedHero = new Hero("Robbo.png");
            savedHero.setPosicao(hero.getPosicao().getLinha(), hero.getPosicao().getColuna());
            savedHero.setLives(hero.getLives());

            SaveState state = new SaveState(levels, currentLevelIndex, savedHero);
            out.writeObject(state);
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
        }
    }
    
    private void loadGame() {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("savegame.dat"))) {

            SaveState state = (SaveState) in.readObject();
            this.levels = state.getLevels();
            this.currentLevelIndex = state.getCurrentLevelIndex();

            // Load the level
            loadLevel(currentLevelIndex);

            // Restore hero state
            Hero loadedHero = state.getHeroState();
            this.hero.setPosicao(loadedHero.getPosicao().getLinha(), loadedHero.getPosicao().getColuna());
            this.hero.setLives(loadedHero.getLives());

            System.out.println("Game loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load game: " + e.getMessage());
        }
    }
    
    private void createMapBorders() {
        // Top and bottom walls
        for (int col = 0; col < Consts.MUNDO_LARGURA; col++) {
            // Top wall
            Wall topWall = new Wall("bricks.png");
            topWall.setPosicao(0, col);
            this.addPersonagem(topWall);

            // Bottom wall
            Wall bottomWall = new Wall("bricks.png");
            bottomWall.setPosicao(Consts.MUNDO_ALTURA - 1, col);
            this.addPersonagem(bottomWall);
        }

        // Left and right walls (skip corners since we already did them)
        for (int row = 1; row < Consts.MUNDO_ALTURA - 1; row++) {
            // Left wall
            Wall leftWall = new Wall("wall.png");
            leftWall.setPosicao(row, 0);
            this.addPersonagem(leftWall);

            // Right wall
            Wall rightWall = new Wall("wall.png");
            rightWall.setPosicao(row, Consts.MUNDO_LARGURA - 1);
            this.addPersonagem(rightWall);
        }
    }
    
    public void nextLevel() {
        if (gameCompleted) {
            return; // Don't do anything if game is already complete
        }
        
        if (currentLevel + 1 < levels.size()) {
            loadLevel(currentLevel + 1);
        } else {
            gameCompleted = true;
            showGameCompleteMessage(); // This will handle restart/exit
        }
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
                            imageName = "Muro.png";
                        } else {
                            imageName = "blackTile.png";
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
        Hero hero = (Hero) faseAtual.get(0);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Lives: " + hero.getLives(), 20, 30);
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
        // Reset all game states
        gameCompleted = false;
        cj = new ControleDeJogo();

        // Reload all levels
        levels.clear();
        loadAllLevels();

        // Reset to first level
        loadLevel(0);

        // Reset hero
        hero = (Hero) faseAtual.get(0);
        atualizaCamera();

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
