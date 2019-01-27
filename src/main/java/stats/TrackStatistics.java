package stats;

import graph.Track;

import java.io.Serializable;
import java.util.List;

public class TrackStatistics implements Serializable {
    private List<StatUnitAreaDistance> stats;
    private List<Double> pathDists, trackDists;
    private String StatTitle;
    private Track origTrack, graphPath;
    private int nClasses;
    private List<Integer> classified;

    public TrackStatistics(
            Track origTrack,
            Track graphPath,
            List<StatUnitAreaDistance> stats,
            String title
            ) {
        this.stats = stats;
        this.origTrack = origTrack;
        this.graphPath = graphPath;
        this.StatTitle = title;
    }

    public List<StatUnitAreaDistance> getStats() {
        return stats;
    }

    public List<Double> getPathDists() {
        return pathDists;
    }

    public List<Double> getTrackDists() {
        return trackDists;
    }

    public String getStatTitle() {
        return StatTitle;
    }

    public Track getOrigTrack() {
        return origTrack;
    }

    public Track getGraphPath() {
        return graphPath;
    }

    public int getnClasses() {
        return nClasses;
    }

    public List<Integer> getClassified() {
        return classified;
    }

    public void setnClasses(int nClasses) {
        this.nClasses = nClasses;
    }

    public void setClassified(List<Integer> classified) {
        this.classified = classified;
    }
}
