package GenerateResult

import importDataInfo.PreStowageData
import importDataInfo.VesselStructureInfo
import mog.entity.MOSlot
import mog.entity.MOSlotBlock
import mog.entity.MOSlotPosition
import mog.processOrder.POChooser2
import mog.test.MoveOrderPTProcess

/**
 * Created by csw on 2016/8/3 18:00.
 * Explain:
 */
public class GenerateMoveOrder {

    public static List<PreStowageData> generateMoveOrder(List<PreStowageData> preStowageDataList, List<VesselStructureInfo> vesselStructureInfoList) {
        List<PreStowageData> preStowageDataListResult = new ArrayList<>();

        List<PreStowageData> preStowageDataListNew = new ArrayList<>();
        for(PreStowageData preStowageData : preStowageDataList) {
            if("N".equals(preStowageData.getTHROUGHFLAG()) || preStowageData.getTHROUGHFLAG() == null) {
                preStowageDataListNew.add(preStowageData);
            }
        }
        System.out.println("总共有多少个位置：" + preStowageDataListNew.size());
        //将数据放在不同的舱位里
        List<String> VHTIDs = new ArrayList<>();//存放舱位ID
        for(PreStowageData preStowageData : preStowageDataListNew) {
            if(!VHTIDs.contains(preStowageData.getVHTID())) {
                VHTIDs.add(preStowageData.getVHTID());
            }
        }
        Collections.sort(VHTIDs);
        System.out.println( "舱位数：" + VHTIDs.size());

        Map<String, List<PreStowageData>> stringListMap1 = new HashMap<>();//放在不同的舱位的预配数据
        Map<String, List<VesselStructureInfo>> stringListMap2 = new HashMap<>();//放在不同的舱位的船舶结构数据
        for(String str : VHTIDs) {
            List<PreStowageData> dataList1 = new ArrayList<>();
            for(PreStowageData preStowageData : preStowageDataListNew) {
                if(str.equals(preStowageData.getVHTID())) {
                    dataList1.add(preStowageData);
                }
            }
            stringListMap1.put(str, dataList1);
            //不同舱位的船舶结构
            List<VesselStructureInfo> dataList2 = new ArrayList<>();
            for(VesselStructureInfo vesselStructureInfo : vesselStructureInfoList) {
                if(str.equals(vesselStructureInfo.getVHTID())) {
                    dataList2.add(vesselStructureInfo);
                }
            }
            stringListMap2.put(str, dataList2);
        }

        for(String str : VHTIDs) {//逐舱遍历
            List<PreStowageData> preStowageList = stringListMap1.get(str);

            //按装卸船、甲板上下分开
            List<PreStowageData> preStowageListAD = new ArrayList<>();
            List<PreStowageData> preStowageListBD = new ArrayList<>();
            List<PreStowageData> preStowageListAL = new ArrayList<>();
            List<PreStowageData> preStowageListBL = new ArrayList<>();
            for (PreStowageData preStowageData : preStowageList) {
                if("L".equals(preStowageData.getLDULD()) && 50 < Integer.valueOf(preStowageData.getVTRTIERNO())) {
                    preStowageListAL.add(preStowageData);
                }
                if("L".equals(preStowageData.getLDULD()) && 50 > Integer.valueOf(preStowageData.getVTRTIERNO())) {
                    preStowageListBL.add(preStowageData);
                }
                if("D".equals(preStowageData.getLDULD()) && 50 < Integer.valueOf(preStowageData.getVTRTIERNO())) {
                    preStowageListAD.add(preStowageData);
                }
                if("D".equals(preStowageData.getLDULD()) && 50 > Integer.valueOf(preStowageData.getVTRTIERNO())) {
                    preStowageListBD.add(preStowageData);
                }
            }

            //根据船舶结构初始化block
            List<VesselStructureInfo> vesselStructureList = stringListMap2.get(str);
            List<MOSlotPosition> moSlotPositionList = new ArrayList<>();
            for(VesselStructureInfo vesselStructureInfo : vesselStructureList) {
                int bayInt = Integer.valueOf(vesselStructureInfo.getVBYBAYID());
                int rowInt = Integer.valueOf(vesselStructureInfo.getVRWROWNO());
                int tierInt = Integer.valueOf(vesselStructureInfo.getVTRTIERNO());
                moSlotPositionList.add(new MOSlotPosition(bayInt, rowInt, tierInt));
            }
            MOSlotBlock initMOSlotBlockAD = MOSlotBlock.buildEmptyMOSlotBlock(moSlotPositionList);
            MOSlotBlock initMOSlotBlockBD = MOSlotBlock.buildEmptyMOSlotBlock(moSlotPositionList);
            MOSlotBlock initMOSlotBlockBL = MOSlotBlock.buildEmptyMOSlotBlock(moSlotPositionList);
            MOSlotBlock initMOSlotBlockAL = MOSlotBlock.buildEmptyMOSlotBlock(moSlotPositionList);

            //对甲板上卸船的block调用生成作业工艺的方法
            MOSlotBlock moSlotBlockAD = MoveOrderPTProcess.PTChooserProcess(preStowageListAD, initMOSlotBlockAD);
            //对甲板上卸船的block调用编MoveOrder的方法
            POChooser2 poChooser = new POChooser2();
            poChooser.processOrderAD(moSlotBlockAD);

            MOSlotBlock moSlotBlockBD = MoveOrderPTProcess.PTChooserProcess(preStowageListBD, initMOSlotBlockBD);
            poChooser.processOrderAD(moSlotBlockBD);

            MOSlotBlock moSlotBlockBL = MoveOrderPTProcess.PTChooserProcess(preStowageListBL, initMOSlotBlockBL);
            poChooser.processOrderBL(moSlotBlockBL);

            MOSlotBlock moSlotBlockAL = MoveOrderPTProcess.PTChooserProcess(preStowageListAL, initMOSlotBlockAL);
            poChooser.processOrderBL(moSlotBlockAL);

            //完成作业工艺和MoveOrder后,将数据进行保存
            for (PreStowageData preStowageData : preStowageList) {
                int bayInt = Integer.valueOf(preStowageData.getVBYBAYID());
                int rowInt = Integer.valueOf(preStowageData.getVRWROWNO());
                int tierInt = Integer.valueOf(preStowageData.getVTRTIERNO());
                MOSlotPosition moSlotPosition = new MOSlotPosition(bayInt, rowInt, tierInt);
                if("L".equals(preStowageData.getLDULD()) && 50 < Integer.valueOf(preStowageData.getVTRTIERNO())) {
                    MOSlot moSlot = moSlotBlockAL.getMOSlot(moSlotPosition);
                    preStowageData.setWORKFLOW(moSlot.getMoveType());
                    preStowageData.setMOVEORDER(moSlot.getMoveOrderSeq());
                }
                if("L".equals(preStowageData.getLDULD()) && 50 > Integer.valueOf(preStowageData.getVTRTIERNO())) {
                    MOSlot moSlot = moSlotBlockBL.getMOSlot(moSlotPosition);
                    preStowageData.setWORKFLOW(moSlot.getMoveType());
                    preStowageData.setMOVEORDER(moSlot.getMoveOrderSeq());
                }
                if("D".equals(preStowageData.getLDULD()) && 50 < Integer.valueOf(preStowageData.getVTRTIERNO())) {
                    MOSlot moSlot = moSlotBlockAD.getMOSlot(moSlotPosition);
                    System.out.println(moSlotPosition.getBayInt() + "-" + moSlotPosition.getRowInt() + "-" + moSlotPosition.getTierInt())
                    preStowageData.setWORKFLOW(moSlot.getMoveType());
                    preStowageData.setMOVEORDER(moSlot.getMoveOrderSeq());
                }
                if("D".equals(preStowageData.getLDULD()) && 50 > Integer.valueOf(preStowageData.getVTRTIERNO())) {
                    MOSlot moSlot = moSlotBlockBD.getMOSlot(moSlotPosition);
                    preStowageData.setWORKFLOW(moSlot.getMoveType());
                    preStowageData.setMOVEORDER(moSlot.getMoveOrderSeq());
                }
            }
            preStowageDataListResult.addAll(preStowageList);
        }

        return  preStowageDataListResult;
    }
}
