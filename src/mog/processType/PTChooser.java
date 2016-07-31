package mog.processType;

import mog.entity.MOSlot;
import mog.entity.MOSlotBlock;

import java.util.List;

/**
 * Created by liuminhang on 16/4/11.
 */
public class PTChooser {

    private MOSlotBlock moSlotBlock;



//    private

    public MOSlotBlock choosePT(){ //选择作业工艺

        //Block层遍历,按层顺序
        for(int i=0;i<moSlotBlock.getTierNoListAsc().size();i++){
            int tierNo = moSlotBlock.getTierNoListAsc().get(i); //获取层号
            //这层按顺序遍历
            List<MOSlot> moSlotList01Bay = moSlotBlock.getMOSlotsByTierOn01Bay(tierNo);
            List<MOSlot> moSlotList03Bay = moSlotBlock.getMOSlotsByTierOn03Bay(tierNo);

            //按序判断作业工艺



        }




        return moSlotBlock;
    }





}
