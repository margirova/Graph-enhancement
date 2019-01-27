package parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapWay implements Serializable {
    //attributes for all classes
    private long id;
    private HashMap<String,String> tags;
    //attributes for ways only
    private List<Long> nodesreferences;
    private boolean oneway;


    public MapWay(long id){
        this.id = id;
        this.nodesreferences = new ArrayList<Long>();
        this.tags = new HashMap<String, String>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HashMap<String, String> getTags() {
        return tags;
    }

    public void addTag(String tagname, String tagval) {
        this.tags.put(tagname, tagval);
    }

    public List<Long> getNodesreferences() {
        return nodesreferences;
    }

    public void addNodereference(long nodeid) {
        this.nodesreferences.add(nodeid);
    }

    public boolean isOneway() {
        return oneway;
    }

    public void setOneway() {
        this.oneway = true;
    }

    public void unsetOneway() {

        this.oneway = false;

    }
}
