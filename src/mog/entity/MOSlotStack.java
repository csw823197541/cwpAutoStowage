package mog.entity;

import java.util.*;

/**
 * Created by liuminhang on 16/4/6.
 */
/*
表示一剁箱位
 */
public class MOSlotStack {
    private int topTierNo;
    private int bottomTierNo;
    private Map<Integer,MOContainer> moContainerMap;
    private Map<Integer,Set<MOSlotPosition>> moPositionSetMap;


    MOSlotStack(){
        topTierNo = 0;
        bottomTierNo = 1000;
        moContainerMap = new HashMap<>();
        moPositionSetMap = new HashMap<Integer,Set<MOSlotPosition>>();
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

    //为空或已满
    public boolean isEmptyOrFull(){
        return topTierNo < bottomTierNo;
    }

    public void putMOContainer(int tierNo,MOContainer moContainer){
        moContainerMap.put(tierNo,moContainer);
    }

    public MOContainer getMOContainer(int tierNo){
        return moContainerMap.get(tierNo);
    }

    //栈顶减2
    public void topTierNoDownBy2(){
        setBottomTierNo(topTierNo-2);
    }
    //栈底加2
    public void bottomTierNoUpBy2(){
        setBottomTierNo(bottomTierNo+2);
    }
    //获取顶层
    public MOContainer getTopMOContainer(){
        return moContainerMap.get(topTierNo);
    }
    //获取底层
    public MOContainer getBottomMOContainer(){
        return moContainerMap.get(bottomTierNo);
    }
}
