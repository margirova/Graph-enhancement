package classifier;

import stats.StatUnitInterpolatedDistance;
import stats.StatUnitInvDistance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Statistic1DClassifier {
    private List<StatUnitInvDistance> ultimateInv;
    private List<StatUnitInterpolatedDistance> ultimateInterp;

    private double meanInterpolated, meanInverse, stdInterpolated, stdInverse;
    private double madInterpolated, madInverse, medianInterpolated, medianInverse;
    private double p25Interpolated, p25Inverse, p75Interpolated, p75Inverse;

    private double thrInterpolated, thrInverse;

    public Statistic1DClassifier(List<StatUnitInvDistance> ultimateInv,
                                 List<StatUnitInterpolatedDistance> ultimateInterp){
        System.out.println(ultimateInterp.size()+" "+ultimateInv.size());
        this.ultimateInterp = ultimateInterp;
        this.ultimateInv = ultimateInv;

        this.meanInterpolated = meanInterp(ultimateInterp);
        this.meanInverse = meanInv(ultimateInv);

        System.out.println(this.meanInterpolated+" "+this.meanInverse);

        this.stdInterpolated = stdInterp(ultimateInterp, this.meanInterpolated);
        this.stdInverse = stdInv(ultimateInv, this.meanInverse);

        this.medianInterpolated = medianInterp(ultimateInterp);
        this.medianInverse = medianInv(ultimateInv);
        System.out.println(this.medianInterpolated+" "+this.medianInverse);

        this.madInterpolated = madInterp(ultimateInterp, medianInterpolated);
        this.madInverse = madInv(ultimateInv, medianInverse);

        System.out.println(this.madInterpolated+" "+this.madInverse);

        this.p25Interpolated = percentileInterp(ultimateInterp, 25);
        this.p75Interpolated = percentileInterp(ultimateInterp, 75);

        this.p25Inverse = percentileInv(ultimateInv, 25);
        this.p75Inverse = percentileInv(ultimateInv, 75);

        double iqrInterpolated = p75Interpolated-p25Interpolated;
        thrInterpolated = p75Interpolated + (iqrInterpolated * 1.5);

        double iqrInverse = p75Inverse-p25Inverse;
        thrInverse = p75Inverse + (iqrInverse * 1.5);

    }

    public double meanInterp(List<StatUnitInterpolatedDistance> array){
        double sum = 0.0;
        for (StatUnitInterpolatedDistance su:array){
            sum += su.getDist();
        }
        sum = sum / array.size();
        return sum;
    }

    public double meanInv(List<StatUnitInvDistance> array){
        double sum = 0.0;
        for (StatUnitInvDistance su:array){
            sum += su.getDist();
        }
        sum = sum / array.size();
        return sum;
    }

    public double stdInterp(List<StatUnitInterpolatedDistance> array, double m){
        double sum = 0.0;
        for (StatUnitInterpolatedDistance su:array){
            sum += (su.getDist() - m)*(su.getDist() - m);
        }
        sum = Math.sqrt(sum / (array.size()-1));
        return sum;
    }

    public double stdInv(List<StatUnitInvDistance> array, double m){
        double sum = 0.0;
        for (StatUnitInvDistance su:array){
            sum += (su.getDist() - m)*(su.getDist() - m);
        }
        sum = Math.sqrt(sum / (array.size()-1));
        return sum;
    }

    public double medianInterp(List<StatUnitInterpolatedDistance> array){
        double[] numArray = new double[array.size()];
        for (int i = 0; i < numArray.length; i++){
            numArray[i] = array.get(i).getDist();
        }

        double median = median(numArray);
        return median;
    }

    public double median(double[] array){
        Arrays.sort(array);
        double median;
        if (array.length % 2 == 0)
            median = ((double)array[array.length/2] + (double)array[array.length/2 - 1])/2;
        else
            median = (double) array[array.length/2];

        return median;
    }

    public double medianInv(List<StatUnitInvDistance> array){
        double[] numArray = new double[array.size()];
        for (int i = 0; i < numArray.length; i++){
            numArray[i] = array.get(i).getDist();
        }

        double median = median(numArray);
        return median;
    }

    public double madInterp(List<StatUnitInterpolatedDistance> array, double median){
        double[] numArray = new double[array.size()];
        for (int i = 0; i < numArray.length; i++){
            numArray[i] = Math.abs(median - array.get(i).getDist());
        }
        return median(numArray);
    }

    public double madInv(List<StatUnitInvDistance> array, double median){
        double[] numArray = new double[array.size()];
        for (int i = 0; i < numArray.length; i++){
            numArray[i] = Math.abs(median - array.get(i).getDist());
        }
        return median(numArray);
    }

    public List<Integer> zScoreClassifyInv(List<StatUnitInvDistance> array){
        List<Integer> classified = new ArrayList<>();
        double thr = 3.0;
        for (StatUnitInvDistance su:array){
            double value = (su.getDist() - this.meanInverse)/this.stdInverse;
            if (value > thr){
                classified.add(1);
            }
            else{
                classified.add(0);
            }
        }
        return classified;
    }

    public List<Integer> zScoreClassifyInterp(List<StatUnitInterpolatedDistance> array){
        List<Integer> classified = new ArrayList<>();
        double thr = 3.0;
        for (StatUnitInterpolatedDistance su:array){
            double value = (su.getDist() - this.meanInterpolated)/this.stdInterpolated;
            if (value > thr){
                classified.add(1);
            }
            else{
                classified.add(0);
            }
        }
        return classified;
    }

    public List<Integer> zScoreRobustClassifyInterp(List<StatUnitInterpolatedDistance> array){
        List<Integer> classified = new ArrayList<>();
        double thr = 3.9; //??
        double coeff = 0.6745;
        for (StatUnitInterpolatedDistance su:array){
            double value = coeff*(su.getDist() - this.medianInterpolated)/this.madInterpolated;
            if (value > thr){
                classified.add(1);
            }
            else{
                classified.add(0);
            }
        }
        return classified;
    }

    public List<Integer> zScoreRobustClassifyInv(List<StatUnitInvDistance> array){
        List<Integer> classified = new ArrayList<>();
        double thr = 3.5;
        double coeff = 0.6745;
        for (StatUnitInvDistance su:array){
            double value = coeff*(su.getDist() - this.medianInverse)/this.madInverse;
            if (value > thr){
                classified.add(1);
            }
            else{
                classified.add(0);
            }
        }
        return classified;
    }

    public List<Integer> iqrClassifyInterp(List<StatUnitInterpolatedDistance> array){
        List<Integer> classified = new ArrayList<>();
        for (StatUnitInterpolatedDistance su:array){
            if (su.getDist() > this.thrInterpolated){
                classified.add(1);
            }
            else{
                classified.add(0);
            }
        }
        return classified;
    }

    public List<Integer> iqrClassifyInv(List<StatUnitInvDistance> array){
        List<Integer> classified = new ArrayList<>();
        for (StatUnitInvDistance su:array){
            if (su.getDist() > this.thrInverse){
                classified.add(1);
            }
            else{
                classified.add(0);
            }
        }
        return classified;
    }

    public double percentileInterp(List<StatUnitInterpolatedDistance> array, int percent){
        double[] numArray = new double[array.size()];
        for (int i = 0; i < numArray.length; i++){
            numArray[i] = array.get(i).getDist();
        }

        double percentile = percentile(numArray, percent);
        return percentile;
    }

    public double percentileInv(List<StatUnitInvDistance> array, int percent){
        double[] numArray = new double[array.size()];
        for (int i = 0; i < numArray.length; i++){
            numArray[i] = array.get(i).getDist();
        }

        double percentile = percentile(numArray, percent);
        return percentile;
    }

    public double percentile(double[] array, int percent){
        Arrays.sort(array);

        int index = (int) Math.ceil(((double) percent / 100.0) * (double) array.length);
        //System.out.println("attention "+array.length+ " "+index);
        return array[index - 1];
    }

//    public List<Integer> ultimateClassificationInterp(List<List<StatUnitInterpolatedDistance>> classifications){
//
//    }

}
