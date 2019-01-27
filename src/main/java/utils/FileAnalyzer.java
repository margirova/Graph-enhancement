package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class FileAnalyzer {
    private int numberOfFiles;
    private String pathToFolder;
    private List<String> toDelete;
    public FileAnalyzer(String path){
        this.pathToFolder = path;
        this.numberOfFiles = new File(path).list().length;
        this.toDelete = new ArrayList<>();
    }

    public void analyze(){
        int count = 0;
        int countfin = 0;
        int mx = 0;
        for (File f: new File(pathToFolder).listFiles()){
            int num = readFile(f);
            System.out.println(f.getName()+" "+num);
            if (num > mx){
                mx = num;
                countfin = count;
            }
            count++;
        }
        System.out.println(countfin+" "+mx);
    }

    public void fullAnalyze() throws IOException{
        filterFiles();
        replaceFiles(pathToFolder, "tracks_buffered");
        //partiteTracks();
    }

    public void partiteTracks() {
        int limit = 400; //discussable
        int c = 0;
        String newpath = "tracks_regrouped";
        try {
            for (File f : new File(pathToFolder).listFiles()) {
                String fname = f.getName();

                c++;
                int num = readFile(f);
                System.out.println(fname + " " + c+" "+num);
                if (num > limit) {
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    //double temp = num / limit;
                    //int partNum = (int) Math.ceil(temp);
                    int thr = limit;
                    String firstpath = newpath + "/" + fname;
                    System.out.println(firstpath);
                    FileWriter writer = new FileWriter(firstpath, false);
                    for (int i = 0; i < num; i++) {
                        //read original line
                        String st = br.readLine();
                        if (i == thr) {
                            thr = thr + limit;
                            System.out.println(thr+" thres");
                            writer.write(st);
                            writer.close();
                            this.numberOfFiles++;
                            String nextpath = newpath + "/00" + String.valueOf(numberOfFiles) + ".tra";
                            writer = new FileWriter(nextpath, false);
                            //add last line, close file, open new file
                        }
                        else {
                            //add line to curr file
                            writer.write(st+"\n");
                        }
                    }
                    //close last file
                    writer.close();
                } else {
                    //copy to new folder how it is
                    File source = new File(pathToFolder+"/"+fname);
                    File target = new File(newpath+"/"+fname);
                    Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void filterFiles(){
        int c = 0;
        //String newPath = "tracks_regrouped";
        for (File f : new File(pathToFolder).listFiles()) {
            System.out.println(f.getName()+" "+c);
            readFileTime(f);
            c++;
        }

    }


    public double gpsDistanceToMeters(double lat1, double lon1, double lat2, double lon2){
        double rlat1 = lat1/180*Math.PI;
        double rlat2 = lat2/180*Math.PI;
        double rlon1 = lon1/180*Math.PI;
        double rlon2 = lon2/180*Math.PI;
        double R = 6371000.0;
        double dlat = rlat2 - rlat1;
        double dlon = rlon2 - rlon1;
        double a = Math.sin(dlat/2.0) * Math.sin(dlat/2.0) +
                Math.sin(dlon/2.0) * Math.sin(dlon/2.0) * Math.cos(rlat1) * Math.cos(rlat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double res = R*c;
        return res;
    }

    public double gpsDistanceToMeters(int lat1E6, int lon1E6, int lat2E6, int lon2E6){
        double lat1 = (double)lat1E6/1000000;
        double lat2 = (double)lat2E6/1000000;
        double lon1 = (double)lon1E6/1000000;
        double lon2 = (double)lon2E6/1000000;
        double res = gpsDistanceToMeters(lat1, lon1, lat2, lon2);
        return res;
    }



    public int readFile(File file){
        int numberOfLines = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                numberOfLines++;
            }
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return numberOfLines;
    }

    public void readFileTime(File file){
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            st = br.readLine();
            if (st == null) {
                return;
            }
            String[] splited = st.split(",");
            int latold = Integer.parseInt(splited[0]);
            int lonold = Integer.parseInt(splited[1]);
            while ((st = br.readLine()) != null) {
                splited = st.split(",");
                int latnew = Integer.parseInt(splited[0]);
                int lonnew = Integer.parseInt(splited[1]);
                double d = gpsDistanceToMeters(latold, lonold, latnew, lonnew);
                System.out.println(d+" "+file.getName());
                if (d > 250.0){
                    System.out.println("hey");
                    toDelete.add(file.getName());
                    break;
                }
                latold = latnew;
                lonold = lonnew;

            }
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void replaceFiles(String from, String to) throws IOException{
        for (String st:toDelete){
            File f = new File(st);
            File source = new File(from+"/"+f.getName());
            File target = new File(to+"/"+f.getName());
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            //f.delete();
            Files.delete(source.toPath());
        }
    }


    public List<String> getToDelete() {
        return toDelete;
    }
}
