package Controler;

import Modelo.Personagem;
import Modelo.CannonR;
import Modelo.Hero;
import Modelo.Chaser;
import Auxiliar.Consts;
import Auxiliar.Desenho;
import Modelo.Button;
import Modelo.CharacterExporter;
import Modelo.CharacterImporter;
import Modelo.Door;
import Modelo.DragDropHandler;
import Modelo.FinishPoint;
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
    private final List<Personagem> personagensToAdd = new ArrayList<>();
    private final List<Personagem> personagensToRemove = new ArrayList<>();
    private long gameSessionStartTime;
    private long finalElapsedTimeMillis; // Stores the final time in milliseconds
    

    public Tela() {
      
        Desenho.setCenario(this);
        initComponents();
        this.addKeyListener(this);
        this.setSize(Consts.RES * Consts.CELL_SIDE + getInsets().left + getInsets().right,
                Consts.RES * Consts.CELL_SIDE + getInsets().top + getInsets().bottom);

        faseAtual = new ArrayList<>();
        currentLevel = 1;
        this.gameSessionStartTime = System.currentTimeMillis();
        this.finalElapsedTimeMillis = 0; // Initialize to 0
        
        loadCurrentLevel();
        atualizaCamera();
        new DropTarget(this, new DragDropHandler(this));
              
    }
    
    private void loadCurrentLevel() {
        try {
            gameCompleted = false;
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
    

    public void addPersonagem(Personagem umPersonagem) {
        synchronized (personagensToAdd) { // Synchronize access if multiple threads could call this
            personagensToAdd.add(umPersonagem);
        }
    }

    public void removePersonagem(Personagem umPersonagem) {
        synchronized (personagensToRemove) { // Synchronize access
            personagensToRemove.add(umPersonagem);
        }
    }
    
     // Thread-safe way to get the current characters
    public synchronized List<Personagem> getFaseAtual() {
        return new ArrayList<>(faseAtual); // Returns a copy for thread safety
    }
    
    // Thread-safe way to add characters
    public synchronized void addCharacters(List<Personagem> characters) {
        faseAtual.addAll(characters);
    }
    
    // Thread-safe way to clear/reset
    public synchronized void clearFaseAtual() {
        faseAtual.clear();
    }
    
    private Hero findHero() {
        return (Hero) faseAtual.stream()
                .filter(p -> p instanceof Hero)
                .findFirst()
                .orElse(null);
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
            // Calculate and store the final elapsed time
            this.finalElapsedTimeMillis = System.currentTimeMillis() - this.gameSessionStartTime;
            showGameCompleteMessage();
        }
    }
    
    public void restartLevel() {
        gameCompleted = false;
        cj = new ControleDeJogo(); // Reset game controller if needed

        // Reload the level
        loadCurrentLevel();

        // Force repaint
        repaint();
    }
    
    private void showGameCompleteMessage() {
        // Format the finalElapsedTimeMillis into seconds and milliseconds
        long totalMillis = this.finalElapsedTimeMillis;
        long totalSecondsValue = totalMillis / 1000;      // Total seconds as a whole number
        long millisecondsPart = totalMillis % 1000;   // The milliseconds part

        String timeString = String.format("%d.%03d seconds", totalSecondsValue, millisecondsPart);

        SwingUtilities.invokeLater(() -> { //
            String message = String.format(
                "You've completed all levels!\nFinal Time: %s\nWould you like to play again?",
                timeString // Include the formatted time string
            );
            int option = JOptionPane.showOptionDialog(this, //
                message, // Updated message with time
                "Game Complete", //
                JOptionPane.YES_NO_OPTION, //
                JOptionPane.INFORMATION_MESSAGE, //
                null, //
                new Object[]{"Play Again", "Exit"}, //
                "Play Again"); //

            if (option == JOptionPane.YES_OPTION) { //
                restartGame(); //
            } else {
                System.exit(0); //
            }

            // After the dialog is handled (and if not exiting), request focus back to the main window.
            // This is important for the KeyListener to work.
            if (option == JOptionPane.YES_OPTION) { // Only if the game window is still supposed to be active
                this.requestFocusInWindow(); 
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

    public Graphics getGraphicsBuffer() {
        return g2;
    }

    @Override
    public void paint(Graphics gOld) {
        Graphics g = this.getBufferStrategy().getDrawGraphics();
        g2 = g.create(getInsets().left, getInsets().top, getWidth() - getInsets().right, getHeight() - getInsets().top); //

        // 1. Draw Base Background (e.g., black screen or full map background)
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // 2. Draw Visual Map Tiles (scenery)
        for (VisualTile tile : TiledMapReader.getVisualTiles()) { //
            tile.draw(g2, cameraColuna, cameraLinha, Consts.CELL_SIDE); //
        }

        // 3. Handle Game State Specific Drawing & Logic
        if (cj.isGameOver()) { //
            // Draw GAME OVER screen elements (as you have them)
            // ... (your game over drawing code) ...
            g2.setColor(new Color(0, 0, 0, 180)); //
            g2.fillRect(0, 0, getWidth(), getHeight()); //
            g2.setColor(Color.RED); //
            g2.setFont(new Font("Arial", Font.BOLD, 50)); //
            String gameOverText = "GAME OVER"; //
            int textWidth = g2.getFontMetrics().stringWidth(gameOverText); //
            g2.drawString(gameOverText, (getWidth() - textWidth) / 2, getHeight() / 2); //
            g2.setColor(Color.WHITE); //
            g2.setFont(new Font("Arial", Font.PLAIN, 20)); //
            String restartText = "Press R to restart"; //
            textWidth = g2.getFontMetrics().stringWidth(restartText); //
            g2.drawString(restartText, (getWidth() - textWidth) / 2, getHeight() / 2 + 50); //

        } else if (this.gameCompleted) { //
            drawGameCompleteScreen(g2); // Draws the "CONGRATULATIONS!" screen

        } else {
            // Normal Game Play State:

            // a. Process game logic (should happen once per frame)
            if (!this.faseAtual.isEmpty()) { //
                this.cj.processaTudo(faseAtual); //
            }

            // b. Draw all game elements (characters, items, etc.)
            if (!this.faseAtual.isEmpty()) { //
                this.cj.desenhaTudo(faseAtual); //
            }

            // c. Draw UI elements like lives counter AND the new timer
            drawLivesCounter(g2);       // Your existing method
            drawSessionTimer(g2);       // ***** CALL THE NEW TIMER DRAW METHOD HERE *****
        }

        // 4. Apply Batched Character Additions/Removals
        // (This part is from the first version of Tela.java you provided, ensure it's correctly placed)
        synchronized (faseAtual) { //
            if (!personagensToAdd.isEmpty()) { //
                synchronized (personagensToAdd) { //
                    faseAtual.addAll(personagensToAdd); //
                    personagensToAdd.clear(); //
                }
            }
            if (!personagensToRemove.isEmpty()) { //
                synchronized (personagensToRemove) { //
                    faseAtual.removeAll(personagensToRemove); //
                    personagensToRemove.clear(); //
                }
            }
        }

        // 5. Finalize and show the buffer
        g.dispose(); //
        g2.dispose(); //
        if (!getBufferStrategy().contentsLost()) { //
            getBufferStrategy().show(); //
        }                   
        
    }
    
    private void drawSessionTimer(Graphics g) {
        // gameSessionStartTime is initialized in Tela() constructor and restartGame()
        if (this.gameSessionStartTime == 0) { 
            // Avoid issues if timer hasn't been properly initialized (should not happen with current setup)
            return;
        }

        long currentTimeMillis = System.currentTimeMillis();
        long elapsedTimeMillis = currentTimeMillis - this.gameSessionStartTime;

        long totalElapsedSeconds = elapsedTimeMillis / 1000;      // Total seconds
        long millisecondsPart = elapsedTimeMillis % 1000;       // Milliseconds part for display

        // Format the time string, e.g., "Time: 123.456 s"
        String timeStr = String.format("Time: %d.%03d s", totalElapsedSeconds, millisecondsPart);

        // Set font and color (can be same as lives counter or different)
        g.setColor(Color.WHITE); //
        g.setFont(new Font("Arial", Font.BOLD, 20)); //

        // Determine X position for the timer, next to the lives counter
        int timerX;
        Hero currentHero = findHero(); // Method findHero() already exists
        if (currentHero != null) {
            String livesText = "Lives: " + currentHero.getLives(); //
            java.awt.FontMetrics fm = g.getFontMetrics(); // Get metrics for the current font
            int livesTextWidth = fm.stringWidth(livesText);
            timerX = 20 + livesTextWidth + 30; // Start timer 30px to the right of the lives text (20 is lives' left margin)
        } else {
            timerX = 150; // Fallback X position if hero/lives text isn't available
        }

        int timerY = 30; // Same Y position as the lives counter

        g.drawString(timeStr, timerX, timerY);
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
        g.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2 - 30); 

        // Instructions
        g.setColor(Color.WHITE); //
        g.setFont(new Font("Arial", Font.PLAIN, 20)); 
        text = "You've completed all levels!"; 
        textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2 + 20); 

        // UPDATED INSTRUCTION TEXT:
        text = "Press SPACE to play again"; 
        textWidth = g.getFontMetrics().stringWidth(text); //
        g.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2 + 50); 
        
        // ***** ADDING NAMES START *****
        // Set font and color for the names (you can adjust these)
        g.setFont(new Font("Arial", Font.PLAIN, 14)); // Using a slightly smaller font for the names
        g.setColor(Color.LIGHT_GRAY); // A different color to distinguish from other text

        String name1 = "Mark Seiji Takakura - 15478710";
        String name2 = "Marlon Camargo Mota - 11299828";
        String name3 = "Miguel Filippo Calhabeu - 15480331";

        int leftMargin = 10;    // Pixels from the left edge
        int bottomMargin = 50;  // Pixels from the bottom edge

        java.awt.FontMetrics fm = g.getFontMetrics();
        int fontHeight = fm.getHeight();
        int lineSpacing = 5; // Extra pixels between lines of text

        // Calculate Y positions starting from the bottom
        // The Y coordinate for drawString is the baseline of the text.
        int yPosName3 = getHeight() - bottomMargin;
        int yPosName2 = yPosName3 - (fontHeight + lineSpacing);
        int yPosName1 = yPosName2 - (fontHeight + lineSpacing);

        g.drawString(name3, leftMargin, yPosName3);    // Miguel
        g.drawString(name2, leftMargin, yPosName2);    // Marlon Mota de Camargo
        g.drawString(name1, leftMargin, yPosName1);    // Mark Seiji Takakura - 15478710
        // ***** ADDING NAMES END *****
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
           
        if (gameCompleted) { //
            if (e.getKeyCode() == KeyEvent.VK_SPACE) { // Check for SPACE key
                // Reload the level
                loadCurrentLevel();

                // Force repaint
                repaint();
            }
            return;
        }
        
        if(cj.isGameOver()){
            if (e.getKeyCode() == KeyEvent.VK_R){
                restartLevel();
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
            case KeyEvent.VK_M:
                this.nextLevel();
                break;
        }
        this.atualizaCamera();
        this.setTitle("-> Cell: " + (hero.getPosicao().getColuna()) + ", "
                + (hero.getPosicao().getLinha()));

        //repaint(); /*invoca o paint imediatamente, sem aguardar o refresh*/
    }
    
    private void restartGame() {
        // Reset game state
        gameCompleted = true;
        drawGameCompleteScreen(g2);
        
        cj = new ControleDeJogo(); // Reset game controller if needed

        // Reset to first level
        this.currentLevel = 1;
        
        this.gameSessionStartTime = System.currentTimeMillis();
        this.finalElapsedTimeMillis = 0; // Initialize to 0


        this.requestFocusInWindow();
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
