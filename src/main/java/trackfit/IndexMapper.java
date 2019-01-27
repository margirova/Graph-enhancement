package trackfit;

import stats.StatUnitAreaDistance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IndexMapper {
    public IndexMapper(){

    }

    public void addUnits(HashMap<StatUnitAreaDistance, List<Integer>> hm,
                         List<StatUnitAreaDistance> bigArray,
                         List<StatUnitAreaDistance> smallArray){
        int initSize = bigArray.size();
        for (int i=0; i<smallArray.size(); i++){
            StatUnitAreaDistance unit = smallArray.get(i);
            if (hm.containsKey(unit)){
                List<Integer> l = hm.get(unit);
                l.add(initSize+i);
            }
            else{
                List<Integer> l = new ArrayList<>();
                l.add(initSize+i);
                hm.put(unit, l);
            }

            bigArray.add(unit);
        }
    }
}
