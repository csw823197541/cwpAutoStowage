package GenerateResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuminhang on 16/3/22.
 */
public class SlotStack2 {

    
    private int topTierNo;
    private int bottomTierNo;
    private Map<Integer,String> keys;


    SlotStack2(){
        topTierNo = 0;
        bottomTierNo = 1000;
        keys = new HashMap<>();
    }

    public int getTopTierNo() {
        return topTierNo;
    }

    public void setTopTierNo(int topTierNo) {
        this.topTierNo = topTierNo;
    }

    public int getBottomTierNo() {
        return bottomTierNo;
    }

    public void setBottomTierNo(int bottomTierNo) {
        this.bottomTierNo = bottomTierNo;
    }

    public boolean isEmpty(){
        return topTierNo < bottomTierNo;
    }

    public void putKey(int tierNo,String key){
        keys.put(tierNo,key);
    }

    public String getKey(int tierNo){
        return keys.get(tierNo);
    }
}
