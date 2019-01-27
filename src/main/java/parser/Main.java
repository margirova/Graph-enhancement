package parser;

import java.io.IOException;
import java.util.HashMap;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        //String filename = "C:\\Users\\pomdo_000\\Documents\\pj\\map2";
        String filename = "Prag.osm";
        OSMParser parser = new OSMParser();
        parser.parse(filename);
        HashMap<Long, MapNode> nds = parser.getNodes();
        HashMap<Long, MapWay> wys = parser.getWays();
        System.out.println(nds.size());
        System.out.println(wys.size());
    }

}
