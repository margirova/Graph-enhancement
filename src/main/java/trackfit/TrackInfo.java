package trackfit;

import graph.Track;
import stats.StatUnitAreaDistance;

import java.util.List;

public class TrackInfo {
    private Track track;
    private List<StatUnitAreaDistance> stats;
    public TrackInfo(Track track, List<StatUnitAreaDistance> stats){
        this.stats = stats;
        this.track = track;
    }

    public Track getTrack() {
        return track;
    }

    public List<StatUnitAreaDistance> getStats() {
        return stats;
    }
}
