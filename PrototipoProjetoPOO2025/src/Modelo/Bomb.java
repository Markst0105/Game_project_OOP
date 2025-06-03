/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author marks
 */
import Auxiliar.Consts;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.ImageIcon;

public class Bomb extends Personagem implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String EXPLOSION_IMAGE_NAME = "explosao.png"; // Image for the explosion
    private static final long EXPLOSION_DURATION_MS = 1000; // How long the explosion image stays (0.5 seconds)

    private boolean exploded;
    private long explosionEndTime;
    private boolean vanished; // True if the bomb has finished exploding and should not be drawn

    public Bomb(String sNomeImagePNG, int startRow, int startCol) {
        super(sNomeImagePNG);
        this.setPosicao(startRow, startCol);
        this.bMortal = true;       // Touching the bomb is initially dangerous
        this.bTransponivel = true; // Can't walk over the unexploded bomb
        this.exploded = false;
        this.vanished = false;
    }

    public Bomb(String sNomeImagePNG) {
        super(sNomeImagePNG);
        this.bMortal = true;       // Touching the bomb is initially dangerous
        this.bTransponivel = true; // Can't walk over the unexploded bomb
        this.exploded = false;
        this.vanished = false;
    }
    
    

    /**
     * Triggers the bomb's explosion.
     * Changes the image to an explosion, sets a timer for it to vanish,
     * and makes it non-mortal and transponible.
     */
    public void explode() {
        if (!exploded) {
            this.exploded = true;
            this.explosionEndTime = System.currentTimeMillis() + EXPLOSION_DURATION_MS;
            this.bMortal = false;       // The explosion itself (after the initial event) isn't repeatedly mortal
            this.bTransponivel = true;  // Hero can pass through the explosion graphic

            // Change image to explosion
            try {
                iImage = new ImageIcon(new java.io.File(".").getCanonicalPath() + Consts.PATH + EXPLOSION_IMAGE_NAME);
                Image img = iImage.getImage();
                BufferedImage bi = new BufferedImage(Consts.CELL_SIDE, Consts.CELL_SIDE, BufferedImage.TYPE_INT_ARGB);
                Graphics g = bi.createGraphics();
                g.drawImage(img, 0, 0, Consts.CELL_SIDE, Consts.CELL_SIDE, null);
                iImage = new ImageIcon(bi);
            } catch (IOException ex) {
                System.err.println("Error loading explosion image: " + ex.getMessage());
            }
        }
    }

    public boolean isExploded() {
        return exploded;
    }
    

    public boolean hasVanished() {
        return vanished;
    }

    @Override
    public void autoDesenho() {
        if (vanished) {
            return; // If vanished, do nothing (don't draw)
        }

        if (exploded && System.currentTimeMillis() > explosionEndTime) {
            vanished = true; // Mark as vanished after explosion duration
            return;
        }

        super.autoDesenho(); // Draw the current image (bomb or explosion)
    }
}