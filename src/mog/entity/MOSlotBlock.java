package mog.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuminhang on 16/4/6.
 */
/*
表示整个箱块,例如某舱甲板上所有箱
 */
public class MOSlotBlock {

    private Map <Integer,MOSlotStack> bay01;    //01小贝,即第一个小贝
    private Map <Integer,MOSlotStack> bay03;    //03小贝,即第二个小贝

    private List<Integer> rowSeqList;           //排号顺序,默认偶数侧在前


    private MOSlotBlock(){
        bay01 = new HashMap<>();
        bay03 = new HashMap<>();
        rowSeqList = new ArrayList<>();
    }

    static public MOSlotBlock buildEmptyMOSlotBlock(int vMinRowNo,int vMaxRowNo){
        MOSlotBlock moSlotBlock = new MOSlotBlock();
        for(int i=vMinRowNo;i<=vMaxRowNo;i++){
            moSlotBlock.bay01.put(i,new MOSlotStack());
            moSlotBlock.bay03.put(i,new MOSlotStack());
        }

        //生成遍历顺序
        //甲板上,从左往右
        for(int i = vMaxRowNo%2==0?vMaxRowNo:vMaxRowNo-1;i>=vMinRowNo;i=i-2){//偶数侧
            moSlotBlock.rowSeqList.add(i);
        }
        for(int i = vMinRowNo%2==0?vMinRowNo+1:vMinRowNo;i<=vMaxRowNo;i=i+2){//奇数侧
            moSlotBlock.rowSeqList.add(i);
        }
        return moSlotBlock;
    }

    //反转排遍历顺序



    //按位置填入Slot
    public void putMOSlot(MOSlotPosition moSlotPosition,MOSlot moSlot){
        if(moSlotPosition.getBayInt()%4==1){ //第一个小贝
            bay01.get(moSlotPosition.getRowInt()).putMOSlot(moSlotPosition.getTierInt(),moSlot);
        }
        if(moSlotPosition.getBayInt()%4==3){ //第二个小贝
            bay03.get(moSlotPosition.getRowInt()).putMOSlot(moSlotPosition.getTierInt(),moSlot);
        }
        if(moSlotPosition.getBayInt()%4==2){ //第一和二个小贝
            bay01.get(moSlotPosition.getRowInt()).putMOSlot(moSlotPosition.getTierInt(),moSlot);
            bay03.get(moSlotPosition.getRowInt()).putMOSlot(moSlotPosition.getTierInt(),moSlot);
        }
    }


    //按位置填入箱
    public void putMOContainer(MOSlotPosition moSlotPosition,MOContainer moContainer){
        MOSlot moSlot = new MOSlot();
        moSlot.setMoContainer(moContainer);
        putMOSlot(moSlotPosition,moSlot);

    }
    //按位置读取Slot
    public MOSlot getMOSlot(MOSlotPosition moSlotPosition){
        MOSlot moSlot = null;
        if(moSlotPosition.getBayInt()%4==1){ //第一个小贝
            moSlot = bay01.get(moSlotPosition.getRowInt()).getMOSlot(moSlotPosition.getTierInt());
        }
        if(moSlotPosition.getBayInt()%4==3){ //第二个小贝
            moSlot = bay03.get(moSlotPosition.getRowInt()).getMOSlot(moSlotPosition.getTierInt());
        }
        if(moSlotPosition.getBayInt()%4==2){ //大贝,取第一个小贝
            moSlot = bay01.get(moSlotPosition.getRowInt()).getMOSlot(moSlotPosition.getTierInt());
        }
        return moSlot;
    }
    //按位置读取箱
    public MOContainer getMOContainer(MOSlotPosition moSlotPosition){
        MOSlot moSlot = getMOSlot(moSlotPosition);
        if(moSlot!=null){
            return moSlot.getMoContainer();
        }
        else {
            return null;
        }
    }
    //获取当前位置的箱的顶面高度




}
