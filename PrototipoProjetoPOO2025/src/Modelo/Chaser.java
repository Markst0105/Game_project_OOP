/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Auxiliar.Consts;
import Auxiliar.Desenho;
import auxiliar.Posicao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

public class Chaser extends Personagem implements Serializable {
    private int moveCounter = 0;
    private static final int SPEED_DELAY_INTERVAL = 18; // Adjust as needed

    private enum IntendedMove { UP, DOWN, LEFT, RIGHT, NONE }
    private IntendedMove nextMove = IntendedMove.NONE;

    // PathNode inner class (or defined separately)
    private static class PathNode implements Comparable<PathNode> {
        Posicao position;
        PathNode parent;
        int gCost;
        int hCost;
        int fCost;

        public PathNode(Posicao position, PathNode parent, int gCost, int hCost) {
            this.position = position;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
        }

        @Override
        public int compareTo(PathNode other) {
            if (this.fCost != other.fCost) {
                return Integer.compare(this.fCost, other.fCost);
            }
            return Integer.compare(this.hCost, other.hCost);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PathNode pathNode = (PathNode) o;
            // Relies on Posicao having a proper equals method
            return Objects.equals(position, pathNode.position);
        }

        @Override
        public int hashCode() {
            // Relies on Posicao having a proper hashCode method
            return Objects.hash(position);
        }
    }


    public Chaser(String sNomeImagePNG, int startRow, int startCol) {
        super(sNomeImagePNG);
        this.setPosicao(startRow, startCol);
        this.bTransponivel = true;
        this.bMortal = true; //
    }

    public Chaser(String sNomeImagePNG) {
        super(sNomeImagePNG);
        this.bTransponivel = true;
        this.bMortal = true; //
    }

    /**
     * Uses A* pathfinding to determine the next move towards the hero.
     * Updates this.nextMove.
     */
    public void computeDirection(Posicao heroPos) { // Removed 'fase' as ehPosicaoValida from Tela will use its faseAtual
        this.nextMove = IntendedMove.NONE;
        Posicao startPos = this.getPosicao();

        if (startPos.igual(heroPos)) {
            return; // Already at hero
        }

        PriorityQueue<PathNode> openList = new PriorityQueue<>();
        HashSet<Posicao> closedList = new HashSet<>(); // Store Posicao to avoid re-processing cells

        // Heuristic: Manhattan distance
        int initialHCost = Math.abs(startPos.getLinha() - heroPos.getLinha()) + Math.abs(startPos.getColuna() - heroPos.getColuna());
        openList.add(new PathNode(startPos, null, 0, initialHCost));

        PathNode targetNode = null;

        while (!openList.isEmpty()) {
            PathNode currentNode = openList.poll();

            if (closedList.contains(currentNode.position)) {
                continue;
            }
            closedList.add(currentNode.position);

            if (currentNode.position.igual(heroPos)) {
                targetNode = currentNode; // Path found
                break;
            }

            // Explore neighbors (Up, Down, Left, Right)
            int[] dLinha = {-1, 1, 0, 0}; // Changes in linha for Up, Down
            int[] dColuna = {0, 0, -1, 1}; // Changes in coluna for Left, Right

            for (int i = 0; i < 4; i++) {
                Posicao neighborPos = new Posicao(currentNode.position.getLinha() + dLinha[i], 
                                                  currentNode.position.getColuna() + dColuna[i]);

                // Check if neighbor is valid to move into using the game's validation
                // Desenho.acessoATelaDoJogo().ehPosicaoValida() checks boundaries and non-transponible objects
                if (Desenho.acessoATelaDoJogo().ehPosicaoValida(neighborPos) && !closedList.contains(neighborPos)) {
                    int gCost = currentNode.gCost + 1; // Assuming cost of 1 per step
                    int hCost = Math.abs(neighborPos.getLinha() - heroPos.getLinha()) + 
                                Math.abs(neighborPos.getColuna() - heroPos.getColuna());
                    
                    PathNode existingNodeInOpen = null;
                    for(PathNode nodeInOpen : openList) { // Check if neighbor is in openList and if new path is better
                        if(nodeInOpen.position.igual(neighborPos)) {
                            existingNodeInOpen = nodeInOpen;
                            break;
                        }
                    }

                    if (existingNodeInOpen == null || gCost < existingNodeInOpen.gCost) {
                        if(existingNodeInOpen != null) {
                            openList.remove(existingNodeInOpen); // Remove old one to re-add with better cost
                        }
                        openList.add(new PathNode(neighborPos, currentNode, gCost, hCost));
                    }
                }
            }
        }

        // Reconstruct path and set nextMove
        if (targetNode != null) {
            List<Posicao> path = new ArrayList<>();
            PathNode step = targetNode;
            while (step != null) {
                path.add(step.position);
                step = step.parent;
            }
            Collections.reverse(path); // Path from start to target

            if (path.size() > 1) { // Path includes start node, so need at least 2 nodes for a move
                Posicao nextStepPos = path.get(1); // The cell to move into
                if (nextStepPos.getLinha() < startPos.getLinha()) this.nextMove = IntendedMove.UP;
                else if (nextStepPos.getLinha() > startPos.getLinha()) this.nextMove = IntendedMove.DOWN;
                else if (nextStepPos.getColuna() < startPos.getColuna()) this.nextMove = IntendedMove.LEFT;
                else if (nextStepPos.getColuna() > startPos.getColuna()) this.nextMove = IntendedMove.RIGHT;
            }
        }
        // If no path found (targetNode is null), nextMove remains IntendedMove.NONE
    }

    @Override
    public void autoDesenho() {
        super.autoDesenho(); // Draw the chaser

        moveCounter++;
        if (moveCounter < SPEED_DELAY_INTERVAL) {
            return; 
        }
        moveCounter = 0; 

        if (this.nextMove == IntendedMove.NONE) {
            // computeDirection might have found no path or decided not to move
            // or chaser is already on hero.
            // Optionally, if nextMove is NONE because pathfinding failed, you could
            // revert to a simpler direct move attempt here as a fallback,
            // but that might try to go through walls again if not careful.
            // For now, if pathfinding says NONE, it does nothing.
            return; 
        }

        Posicao currentPos = this.getPosicao();
        Posicao potentialNextPos = new Posicao(currentPos.getLinha(), currentPos.getColuna());
        boolean boundaryCheckOK = false;

        switch (this.nextMove) {
            case UP:    boundaryCheckOK = potentialNextPos.moveUp();    break;
            case DOWN:  boundaryCheckOK = potentialNextPos.moveDown();  break;
            case LEFT:  boundaryCheckOK = potentialNextPos.moveLeft();  break;
            case RIGHT: boundaryCheckOK = potentialNextPos.moveRight(); break;
            case NONE: 
                return; 
        }

        // The A* should ideally only give a nextMove if it's valid.
        // However, the game state could change between computeDirection and autoDesenho (e.g. another entity moves).
        // So, a final validation before moving is still good.
        if (boundaryCheckOK) {
            if (Desenho.acessoATelaDoJogo().ehPosicaoValida(potentialNextPos)) {
                this.pPosicao.setPosicao(potentialNextPos.getLinha(), potentialNextPos.getColuna());
            } else {
                // The path chosen by A* was valid when calculated, but something blocked it
                // (or ehPosicaoValida is stricter). Chaser doesn't move.
                // This might mean A* should be re-run more frequently or ehPosicaoValida refined.
            }
        }
        // If boundaryCheckOK was false, it means the move determined by A* was somehow out of bounds,
        // which shouldn't happen if ehPosicaoValida was used correctly by A* neighbors scan.
        // This indicates a potential discrepancy between Posicao.moveX() boundary logic and ehPosicaoValida boundary logic.
        // Ensure Posicao.setPosicao uses "<0" and ">=MAX" for boundaries.
    }
}
