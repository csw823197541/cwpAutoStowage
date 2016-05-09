package GenerateResult;

import importDataInfo.PreStowageData;
import importDataInfo.VesselStructureInfo
import mog.entity.MOContainer
import mog.entity.MOSlotBlock
import mog.entity.MOSlotPosition;

import java.util.List;

/**
 * Created by liuminhang on 16/4/13.
 */
public class GenerateMoveOrderN {
    public List<VesselStructureInfo> vesselStructureInfoList;
    public List<PreStowageData> preStowageDataList;


    public void generateMoveOrder(){


    }
    public void generateMoveOderInHatch(List<PreStowageData> preStowageDataListInHatch){
        //获取该舱的船舶结构,获取最大的排数,最小排数
        int vMaxRowNo = 1;
        int vMinRowNo = 1;
        vesselStructureInfoList.each { vesselStructureInfo->
            int rowNo = vesselStructureInfo.getVRWROWNO().toInteger()
            if(rowNo> vMaxRowNo){
                vMaxRowNo = rowNo
            }
            if(rowNo<vMinRowNo){//判断是否有00排
                rowNo = vMinRowNo
            }
        }
        println "最大排号:" +  vMaxRowNo + ",最小排号:" + vMinRowNo
        //构造Block数据
        MOSlotBlock moSlotBlock = MOSlotBlock.buildEmptyMOSlotBlock(vMinRowNo,vMaxRowNo);
        //填充
        preStowageDataListInHatch.each {preStowageData->
            int bayInt = Integer.valueOf(preStowageData.getVBYBAYID())
            int rowInt = Integer.valueOf(preStowageData.getVRWROWNO())
            int tierInt = Integer.valueOf(preStowageData.getVTRTIERNO())
            MOSlotPosition moSlotPosition = new MOSlotPosition(bayInt,rowInt,tierInt);
            MOContainer moContainer = new MOContainer();
            moContainer.type = preStowageData.SIZE
            moSlotBlock.putMOContainer(moSlotPosition,moContainer)

        }

    }
}
