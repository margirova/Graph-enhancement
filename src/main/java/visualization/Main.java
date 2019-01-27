package visualization;

import processing.core.PApplet;
import statsvisualization.StatVisualization;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException{
        String name = "002085";
        String filetrack = "tracked_Prague_05-2015_01-2016/"+name+".tra";
//        String filetrack = "tracks_regrouped/"+name+".tra";
        String outpicture = "for_report/"+name+".jpeg";
//        String outtrack = "outputtracks/output"+name+".tra";
//        String graphpath = "graphs/graph_oneway.ser";
        //PApplet.main(new String[] { Visualization.class.getName(), "fit", outpicture, filetrack, outtrack});
        PApplet.main(new String[] { Visualization.class.getName(), "track", outpicture, filetrack});
        //PApplet.main(new String[] { Visualization.class.getName(), "all", outpicture, filetrack, outtrack, graphpath});

//        String pathStat = "stats/"+name+".ser";
//        StatVisualization.main(new String[] {StatVisualization.class.getName(), "line", pathStat});
    }
}
