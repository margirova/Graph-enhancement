package graph;

import org.xml.sax.SAXException;
import parser.*;
import com.umotional.basestructures.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import net.sf.javaml.core.kdtree.*;

import javax.xml.parsers.ParserConfigurationException;

public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        String filename = "maps/mapsuperfull";
        OSMParser parser = new OSMParser();
        parser.parse(filename);
        HashMap<Long, MapNode> nds = parser.getNodes();
        HashMap<Long, MapWay> wys = parser.getWays();
        //HashMap<Long, MapRelation> rlts = parser.getRelations();
        System.out.println(nds.size());
        System.out.println(wys.size());
        //System.out.println(rlts.size());
        NodesEdgesConverter nec = new NodesEdgesConverter();
        nec.convert(nds, wys);
        GraphBuilder gb = new GraphBuilder();
        System.out.println("converted");
        //to delete
//        FileOutputStream fos = new FileOutputStream("nodes.ser");
//        ObjectOutputStream oos = new ObjectOutputStream(fos);
//        oos.writeObject(nds);
//        oos.close();
//        fos = new FileOutputStream("ways.ser");
//        oos = new ObjectOutputStream(fos);
//        oos.writeObject(wys);
//        oos.close();



        nds = null;
        wys = null;
        parser = null;
        System.out.println(nec.getGraphnodes().size());
        gb.addNodes(nec.getGraphnodes());
        gb.addEdges(nec.getGraphedges());
        nec = null;
        System.out.println("creating graph");
        Graph<Node, Edge> gr = gb.createGraph();
        System.out.println("done");
        System.out.println(gr.getAllNodes().size());
        gb = null;
        FileOutputStream fos = new FileOutputStream("graphs/graph_oneway.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(gr);
        oos.close();
    }
}
