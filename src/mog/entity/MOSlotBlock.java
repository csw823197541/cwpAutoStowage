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

    private List<Integer> rowListLR;


    private MOSlotBlock(){
        bay01 = new HashMap<>();
        bay03 = new HashMap<>();
        rowListLR = new ArrayList<>();
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
            moSlotBlock.rowListLR.add(i);
        }
        for(int i = vMinRowNo%2==0?vMinRowNo+1:vMinRowNo;i<=vMaxRowNo;i=i+2){//奇数侧
            moSlotBlock.rowListLR.add(i);
        }
        return moSlotBlock;
    }


    //待实现的函数
    //按位置填入箱
    public void putMOContainer(){

    }
    //按位置读取箱
    public MOContainer getMOContainer(){

        return null;
    }
    //获取


}
