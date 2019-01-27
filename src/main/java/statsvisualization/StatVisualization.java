package statsvisualization;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import stats.StatUnitAreaDistance;
import stats.StatUnitInterpolatedDistance;
import stats.StatUnitInvDistance;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;

public class StatVisualization extends Application {
    private static String[] arguments;
    String pathLineOut, pathHistOut, lineMode;

    @Override
    public void start(Stage stage) {
        if (arguments[1].equals("histogram")){
            startHist(stage);

        }
        else if (arguments[1].equals("lineinterp")){
            startLineInterp(stage);

        }
        else if (arguments[1].equals("lineinverse")){
            startLineInv(stage);

        }
        else if (arguments[1].equals("scatter")){
            startScatter2D(stage);

        }

        else if (arguments[1].equals("scatter2dclassified")){
            startScatter2DClassified(stage);
        }

        else if (arguments[1].equals("scatterinterpclassified")){
            startScatterClassifiedInterpolated(stage);
        }

        else if (arguments[1].equals("scatterinvclassified")){
            startScatterClassifiedInv(stage);
        }
    }

    public TreeMap<Double, Integer> readHist(String path){
        TreeMap<Double, Integer> hist = new TreeMap<>(new MyComparator());
        File file = new File(path);
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            int c = 0;
            while ((st = br.readLine()) != null) {
                String[] splited = st.split(" ");
                Double k1 = Double.parseDouble(splited[0]);
                Double k2 = Double.parseDouble(splited[1]);
                Integer bin = Integer.parseInt(splited[2]);
                //System.out.println(k1+" "+k2+" "+bin);
                hist.put(k2-(k2-k1)/2, bin);
                c++;
            }
            System.out.println(hist);
        }
        catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        return hist;
    }

    public void startHist(Stage stage){
        stage.setTitle("Histogram");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Minimal distance");
        final BarChart<String,Number> bc =
                new BarChart<>(xAxis, yAxis);

        bc.setTitle("Histogram");
        String path = arguments[2]; //change

        TreeMap<Double,Integer> hist = readHist(path); //errror!! switch axes
        //not switch actually
        XYChart.Series series1 = new XYChart.Series();

        for (Double x:hist.keySet()) {
            if (hist.get(x)!=0){
                String xx = Double.toString(x);
                series1.getData().add(new XYChart.Data(xx, hist.get(x)));
            }

        }

        Scene scene  = new Scene(bc,800,600);
        bc.getData().addAll(series1);
        stage.setScene(scene);
        stage.show();

    }

    public void startScatter2D(Stage stage){
        String path = arguments[2];
        try{
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<StatUnitAreaDistance> stats = (List<StatUnitAreaDistance>) ois.readObject();
            ois.close();


            stage.setTitle("Classified data");
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Dists");
            yAxis.setLabel("Areas");
            final ScatterChart<Number,Number> scatterChart =
                    new ScatterChart<Number,Number>(xAxis,yAxis);

            scatterChart.setTitle("Areas/Distances");
            XYChart.Series series = new XYChart.Series();
            //series.setName("Counts");
            for (StatUnitAreaDistance su:stats){
                series.getData().add(new XYChart.Data(su.getDist(), su.getArea()));
            }

            Scene scene  = new Scene(scatterChart,800,600);
            scatterChart.getData().add(series);

            stage.setScene(scene);
            stage.show();

        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public void startLineInterp(Stage stage){
        String path = arguments[2];
        try{
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<StatUnitInterpolatedDistance> stats = (List<StatUnitInterpolatedDistance>) ois.readObject();
            ois.close();

            stage.setTitle("Areas");
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Counts");
            yAxis.setLabel("Distances");
            final LineChart<Number,Number> lineChart =
                    new LineChart<Number,Number>(xAxis,yAxis);

            lineChart.setTitle("Distances");
            XYChart.Series series = new XYChart.Series();
            series.setName("Counts");
            int count = 0;
            for (StatUnitInterpolatedDistance su:stats){
                series.getData().add(new XYChart.Data(count, su.getDist()));
                count++;
            }

            Scene scene  = new Scene(lineChart,800,600);
            lineChart.getData().add(series);
            //saveAsPng(lineChart);
            stage.setScene(scene);

            stage.show();

        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public void startLineInv(Stage stage){
        String path = arguments[2];
        try{
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<StatUnitInvDistance> stats = (List<StatUnitInvDistance>) ois.readObject();
            ois.close();

            stage.setTitle("Areas");
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Counts");
            final LineChart<Number,Number> lineChart =
                    new LineChart<Number,Number>(xAxis,yAxis);

            lineChart.setTitle("Areas or Distances");
            XYChart.Series series = new XYChart.Series();
            series.setName("Counts");
            int count = 0;
            for (StatUnitInvDistance su:stats){
                series.getData().add(new XYChart.Data(count, su.getDist()));
                count++;
            }

            Scene scene  = new Scene(lineChart,800,600);
            lineChart.getData().add(series);
            //saveAsPng(lineChart);
            stage.setScene(scene);

            stage.show();

        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public void startScatterClassifiedInv(Stage stage){
        String path = arguments[2];
        String pathC = arguments[3];
        try{
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<StatUnitInvDistance> stats = (List<StatUnitInvDistance>) ois.readObject();
            ois.close();

            fis = new FileInputStream(pathC);
            ois = new ObjectInputStream(fis);
            List<Integer> classes = (List<Integer>) ois.readObject();
            ois.close();


            stage.setTitle("Classified data");
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Counts");
            yAxis.setLabel("Distance");
            final ScatterChart<Number,Number> scatterChart =
                    new ScatterChart<Number,Number>(xAxis,yAxis);

            scatterChart.setTitle("Classified data");
            XYChart.Series series0 = new XYChart.Series();
            XYChart.Series series1 = new XYChart.Series();
            //series.setName("Counts");
            for (int i=0; i < stats.size(); i++){
                if (classes.get(i) == 0)
                    series0.getData().add(new XYChart.Data(i, stats.get(i).getDist()));
                else
                    series1.getData().add(new XYChart.Data(i, stats.get(i).getDist()));
            }

            Scene scene  = new Scene(scatterChart,800,600);
            scatterChart.getData().add(series0);
            scatterChart.getData().add(series1);

            stage.setScene(scene);
            stage.show();

        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public void startScatterClassifiedInterpolated(Stage stage){
        String path = arguments[2];
        String pathC = arguments[3];
        try{
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<StatUnitInterpolatedDistance> stats = (List<StatUnitInterpolatedDistance>) ois.readObject();
            ois.close();

            fis = new FileInputStream(pathC);
            ois = new ObjectInputStream(fis);
            List<Integer> classes = (List<Integer>) ois.readObject();
            ois.close();


            stage.setTitle("Classified data");
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Counts");
            yAxis.setLabel("Distance");
            final ScatterChart<Number,Number> scatterChart =
                    new ScatterChart<Number,Number>(xAxis,yAxis);

            scatterChart.setTitle("Classified data");
            XYChart.Series series0 = new XYChart.Series();
            XYChart.Series series1 = new XYChart.Series();
            //series.setName("Counts");
            for (int i=0; i < stats.size(); i++){
                if (classes.get(i) == 0)
                    series0.getData().add(new XYChart.Data(i, stats.get(i).getDist()));
                else
                    series1.getData().add(new XYChart.Data(i, stats.get(i).getDist()));
            }

            Scene scene  = new Scene(scatterChart,800,600);
            scatterChart.getData().add(series0);
            scatterChart.getData().add(series1);

            stage.setScene(scene);
            stage.show();

        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public void startScatter2DClassified(Stage stage){
        String path = arguments[2];
        String pathC = arguments[3];
        try{
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<StatUnitAreaDistance> stats = (List<StatUnitAreaDistance>) ois.readObject();
            ois.close();

            fis = new FileInputStream(pathC);
            ois = new ObjectInputStream(fis);
            List<Integer> classes = (List<Integer>) ois.readObject();
            ois.close();


            stage.setTitle("Classified data");
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Dist");
            yAxis.setLabel("Areas");
            final ScatterChart<Number,Number> scatterChart =
                    new ScatterChart<Number,Number>(xAxis,yAxis);

            scatterChart.setTitle("Classified data");
            XYChart.Series series0 = new XYChart.Series();
            XYChart.Series series1 = new XYChart.Series();
            //series.setName("Counts");
            for (int i=0; i < stats.size(); i++){
                if (classes.get(i) == 0)
                    series0.getData().add(new XYChart.Data(stats.get(i).getDist(), stats.get(i).getArea()));
                else
                    series1.getData().add(new XYChart.Data(stats.get(i).getDist(), stats.get(i).getArea()));
            }

            Scene scene  = new Scene(scatterChart,800,600);
            scatterChart.getData().add(series0);
            scatterChart.getData().add(series1);

            stage.setScene(scene);
            stage.show();

        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }


    public void saveAsPng(BarChart barChart) {
        WritableImage image = barChart.snapshot(new SnapshotParameters(), null);
        System.out.println(this.pathHistOut);
        File file = new File(this.pathHistOut);
        //System.out.println("1");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);

        } catch (IOException e) {
            System.out.println("2");
            e.printStackTrace();
        }
        //System.out.println("3");
    }

    @FXML
    public void saveAsPng(LineChart lineChart) {
        //System.out.println(lineChart);
        WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);
        System.out.println(this.pathLineOut);
        File file = new File(this.pathLineOut);


        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        arguments = args;
        launch(args);
    }



    class MyComparator implements Comparator<Double> {
        public int compare(Double o1, Double o2) {
            if (o1 > o2) {
                return 1;
            }
            if (o1 < o2) {
                return -1;
            }
            return 0;
        }
    }
}
