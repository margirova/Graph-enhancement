package graph;

import com.umotional.basestructures.Node;

public class NodeEntry {
    private Node node;
    private double distance;
    public NodeEntry(Node nd, double dst){
        this.node = nd;
        this.distance = dst;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
