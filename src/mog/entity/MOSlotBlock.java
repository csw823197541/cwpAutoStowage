package mog.entity;

import java.util.*;

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



    private List<Integer> tierNoListAsc;       //层号列表,递增



    private List<MOSlotPosition> slotPositions; //位置列表,包含空舱位

    private int vMaxRowNo = -1,vMinRowNo = 9999;


    private MOSlotBlock(){
        bay01 = new HashMap<>();
        bay03 = new HashMap<>();
        rowSeqList = new ArrayList<>();
        tierNoListAsc = new ArrayList<>();
    }

//    static public MOSlotBlock buildEmptyMOSlotBlock(int vMinRowNo,int vMaxRowNo){
//        MOSlotBlock moSlotBlock = new MOSlotBlock();
//        for(int i=vMinRowNo;i<=vMaxRowNo;i++){
//            moSlotBlock.bay01.put(i,new MOSlotStack());
//            moSlotBlock.bay03.put(i,new MOSlotStack());
//        }
//
//        //生成遍历顺序
//        //甲板上,从左往右
//        for(int i = vMaxRowNo%2==0?vMaxRowNo:vMaxRowNo-1;i>=vMinRowNo;i=i-2){//偶数侧
//            moSlotBlock.rowSeqList.add(i);
//        }
//        for(int i = vMinRowNo%2==0?vMinRowNo+1:vMinRowNo;i<=vMaxRowNo;i=i+2){//奇数侧
//            moSlotBlock.rowSeqList.add(i);
//        }
//        return moSlotBlock;
//    }

    //按照船舶结构构造空的Block
    static public MOSlotBlock buildEmptyMOSlotBlock(List<MOSlotPosition> moSlotPositionList){
        MOSlotBlock moSlotBlock = new MOSlotBlock();
        moSlotBlock.slotPositions = moSlotPositionList;

        for(int i=0;i<moSlotPositionList.size();i++){
            MOSlotPosition moSlotPosition = moSlotPositionList.get(i);
            MOSlot moSlot = new MOSlot(moSlotPosition);
            //初始化空的MOSlot
            if(moSlotPosition.getBayInt()%4==1){ //第一个小贝
                moSlotBlock.bay01.get(moSlotPosition.getRowInt()).putMOSlot(moSlotPosition.getTierInt(),moSlot);
            }
            if(moSlotPosition.getBayInt()%4==3){ //第二个小贝
                moSlotBlock.bay03.get(moSlotPosition.getRowInt()).putMOSlot(moSlotPosition.getTierInt(),moSlot);
            }
            if(moSlotPosition.getBayInt()%4==2){ //第一和二个小贝
                moSlotBlock.bay01.get(moSlotPosition.getRowInt()).putMOSlot(moSlotPosition.getTierInt(),moSlot);
                moSlotBlock.bay03.get(moSlotPosition.getRowInt()).putMOSlot(moSlotPosition.getTierInt(),moSlot);
            }
            //处理最大排,最小排
            if(moSlotPosition.getRowInt()>moSlotBlock.vMaxRowNo){
                moSlotBlock.vMaxRowNo = moSlotPosition.getTierInt();
            }
            if(moSlotPosition.getRowInt()<moSlotBlock.vMinRowNo){
                moSlotBlock.vMinRowNo = moSlotPosition.getRowInt();
            }
            //添加层号
            if(!moSlotBlock.tierNoListAsc.contains(moSlotPosition.getTierInt())){
                moSlotBlock.tierNoListAsc.add(moSlotPosition.getTierInt());
            }
        }

        //生成遍历顺序
        //甲板上,从左往右
        for(int i = moSlotBlock.vMaxRowNo%2==0?moSlotBlock.vMaxRowNo:moSlotBlock.vMaxRowNo-1;i>=moSlotBlock.vMinRowNo;i=i-2){//偶数侧
            moSlotBlock.rowSeqList.add(i);
        }
        for(int i = moSlotBlock.vMinRowNo%2==0?moSlotBlock.vMinRowNo+1:moSlotBlock.vMinRowNo;i<=moSlotBlock.vMaxRowNo;i=i+2){//奇数侧
            moSlotBlock.rowSeqList.add(i);
        }
        //对层号进行排序
        Collections.sort(moSlotBlock.tierNoListAsc);

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
        if(moSlotPosition.getBayInt()%4==1){ //第一个小贝
            bay01.get(moSlotPosition.getRowInt()).getMOSlot(moSlotPosition.getTierInt()).setMoContainer(moContainer);
        }
        if(moSlotPosition.getBayInt()%4==3){ //第二个小贝
            bay03.get(moSlotPosition.getRowInt()).getMOSlot(moSlotPosition.getTierInt()).setMoContainer(moContainer);
        }
        if(moSlotPosition.getBayInt()%4==2){ //第一和二个小贝
            bay01.get(moSlotPosition.getRowInt()).getMOSlot(moSlotPosition.getTierInt()).setMoContainer(moContainer);
            bay03.get(moSlotPosition.getRowInt()).getMOSlot(moSlotPosition.getTierInt()).setMoContainer(moContainer);
        }

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

    //获取TierNo List,获取层号,递增
    public List<Integer> getTierNoListAsc() {
        return tierNoListAsc;
    }

    //获取舱位列表
    public List<MOSlotPosition> getSlotPositions() {
        return slotPositions;
    }

    //按排顺序获取某层的MOSlot,返回两个列表组成的数组,分别是第一小贝和第三小贝的List<MOSlot>
    public List<MOSlot> getMOSlotsByTierOn01Bay(int tierNo){
        List<MOSlot> moSlotList = new ArrayList<>();
        for(int i=0;i<rowSeqList.size();i++){
            moSlotList.add(bay01.get(rowSeqList.get(i)).getMOSlot(tierNo));
        }
        return moSlotList;
    }
    public List<MOSlot> getMOSlotsByTierOn03Bay(int tierNo){
        List<MOSlot> moSlotList = new ArrayList<>();
        for(int i=0;i<rowSeqList.size();i++){
            moSlotList.add(bay03.get(rowSeqList.get(i)).getMOSlot(tierNo));
        }
        return moSlotList;
    }

    //获取当前位置的箱的顶面高度




}
