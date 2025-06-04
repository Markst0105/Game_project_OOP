/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author marks
 */
import Auxiliar.Posicao; 
import java.util.Objects;

class PathNode implements Comparable<PathNode> {
    Posicao position;
    PathNode parent;
    int gCost; // Cost from start to this node
    int hCost; // Heuristic cost from this node to target
    int fCost; // gCost + hCost

    public PathNode(Posicao position, PathNode parent, int gCost, int hCost) {
        this.position = position;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
    }

    @Override
    public int compareTo(PathNode other) {
        // Primarily compare by fCost, then by hCost as a tie-breaker
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
        return Objects.equals(position, pathNode.position); 
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }
}