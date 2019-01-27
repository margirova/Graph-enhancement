package parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class OSMParser extends DefaultHandler {
    private HashMap<Long, MapNode> nodes;
    private HashMap<Long, MapWay> ways;
//    private HashMap<Long, MapRelation> relations;
    private MapNode currentnode;
    private MapWay currentway;
    private boolean highwaytagmet;

    public OSMParser() {
        super();
    }

    public void parse(String source) throws SAXException, IOException {
        File f = new File(source);
        nodes = new HashMap<Long, MapNode>();
        ways = new HashMap<Long, MapWay>();
//        relations = new HashMap<Long, MapRelation>();
        highwaytagmet = false;
        InputSource inpt = new InputSource(new FileReader(f));
        XMLReader xr = XMLReaderFactory.createXMLReader();
        xr.setContentHandler(this);
        xr.setErrorHandler(this);
        xr.parse(inpt);
    }

    @Override
    public void endElement(String s, String localName, String element) throws SAXException {
        super.endElement(s, localName, element); //?
        if (element.equalsIgnoreCase("node")) {
            //if (highwaytagmet){
            nodes.put(currentnode.getId(), currentnode);
            highwaytagmet = false;
            //}
            currentnode = null;

        }
        else if (element.equalsIgnoreCase("way")) {
            if (highwaytagmet){
                //add refs from temps
                transferNodes();
                ways.put(currentway.getId(), currentway);
                highwaytagmet = false;
            }
            currentway = null;
        }
    }

    @Override
    public void startElement(String s, String localName, String element, Attributes attributes) throws SAXException {
        super.startElement(s, localName, element, attributes);

        //tag node
        if (element.equalsIgnoreCase("node")) {
            long id = Long.parseLong(attributes.getValue("id"));
            currentnode = new MapNode(id);
            currentnode.setLat(Double.parseDouble(attributes.getValue("lat")));
            currentnode.setLon(Double.parseDouble(attributes.getValue("lon")));

        }
        //tag way
        else if (element.equalsIgnoreCase("way")) {
            //System.out.println("ways!");
            long id = Long.parseLong(attributes.getValue("id"));
            currentway = new MapWay(id);
        }
        //tag nd
        else if(localName.equals("nd")) {
            long ref = Long.parseLong(attributes.getValue("ref"));
            currentway.addNodereference(ref);
        }
        //tag tag
        else if (element.equalsIgnoreCase("tag")) {
            if (attributes.getValue("k").equalsIgnoreCase("highway")){
                highwaytagmet = true;
            }
//            if ((attributes.getValue("k").equalsIgnoreCase("oneway")) &&
//                    (attributes.getValue("v").equalsIgnoreCase("yes")) &&
//                    (currentway != null)){
//                currentway.setOneway();
//            }
            if ((attributes.getValue("k").equalsIgnoreCase("oneway")) &&
                        ((attributes.getValue("v").equalsIgnoreCase("yes")) ||
                        (attributes.getValue("v").equalsIgnoreCase("true"))||
                        (attributes.getValue("v").equalsIgnoreCase("1"))) &&
                    (currentway != null)){
                    currentway.setOneway();
            }
            if ((attributes.getValue("k").equalsIgnoreCase("oneway:bicycle")) &&
                ((attributes.getValue("v").equalsIgnoreCase("yes")) ||
                (attributes.getValue("v").equalsIgnoreCase("true"))||
                (attributes.getValue("v").equalsIgnoreCase("1"))) &&
                (currentway != null)){
                currentway.unsetOneway();
            }

        }
    }

    //////!!!!!!!!!
    public void transferNodes(){
        if (currentway != null){
            for (Long ref:currentway.getNodesreferences()){
                if (nodes.containsKey(ref)){
                    nodes.get(ref).setReferenced();
                }
            }
        }
    }


    public HashMap<Long, MapNode> getNodes() {
        return nodes;
    }


    public HashMap<Long, MapWay> getWays() {
        return ways;
    }

//    public HashMap<Long, MapRelation> getRelations() {
//        return relations;
//    }
}




//if ((attributes.getValue("k").equalsIgnoreCase("oneway")) &&
//        ((attributes.getValue("v").equalsIgnoreCase("yes")) ||
//        (attributes.getValue("v").equalsIgnoreCase("true"))||
//        (attributes.getValue("v").equalsIgnoreCase("1"))) &&
//        (currentway != null)){
//        currentway.setOneway();
//        }
//        if ((attributes.getValue("k").equalsIgnoreCase("oneway:bicycle")) &&
//        ((attributes.getValue("v").equalsIgnoreCase("yes")) ||
//        (attributes.getValue("v").equalsIgnoreCase("true"))||
//        (attributes.getValue("v").equalsIgnoreCase("1"))) &&
//        (currentway != null)){
//        currentway.unsetOneway();
//        }
