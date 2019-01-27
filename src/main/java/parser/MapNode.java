package parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapNode implements Serializable {
    //attributes for all classes
    private long id;
    private HashMap<String,String> tags;
    //attributes for nodes only
    private double lat;
    private double lon;
    private boolean referenced;


    public MapNode(long id){
        this.id = id;
        this.tags = new HashMap<String,String>();
        this.referenced = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }


    public HashMap<String, String> getTags() {
        return tags;
    }

    public void addTag(String tagname, String tagval) {
        this.tags.put(tagname, tagval);
    }

    public boolean isReferenced() {
        return referenced;
    }

    public void setReferenced() {
        this.referenced = true;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", tags=" + tags +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
