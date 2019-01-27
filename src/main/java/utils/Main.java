package utils;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException{
        String path = "tracked_Prague_05-2015_01-2016";
        //String path = "tracks_regrouped";
        FileAnalyzer fa = new FileAnalyzer(path);
        fa.fullAnalyze();
        //System.out.println(fa.getToDelete());
        System.out.println(fa.getToDelete().size());
        //fa.replaceFiles();
        //fa.partiteTracks();
    }
}
