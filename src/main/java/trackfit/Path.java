package trackfit;

import com.umotional.basestructures.Node;

import java.util.ArrayList;
import java.util.List;

public class Path {
    private double distance;
    private List<Node> nodes;
    private Node firstNode;
    private Node lastnode;
    public Path(){
        this.nodes = new ArrayList<Node>();
        this.distance = 0.0;
    }

    public void addNode(Node nd){
        nodes.add(nd);
        //distance+=dist;
    }

    public Node getFirstNode(){
        return nodes.get(nodes.size()-1); //backtracking stuff
    }

    public Node getLastNode(){
        return nodes.get(0); //backtracking stuff
    }


    public int size(){
        return nodes.size();
    }

    public Node getNodeByIndex(int i){
        //return nodes.get(i);
        return nodes.get(nodes.size()-1-i); //waaaaaait
    }

    public Double getDistance(){
        return distance;
    }

    public void setDistance(double dist){
        this.distance = dist;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
