package GenerateResult;

import importDataProcess.ImportData;
import importDataInfo.AutoStowResultInfo;
import importDataInfo.CwpResultInfo;
import importDataInfo.MoveInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leko on 2016/1/22.
 */
public class GenerateMoveInfoResult {

    public static List<MoveInfo> getMoveInfoResult(List<CwpResultInfo> cwpResultInfoList, List<AutoStowResultInfo> autoStowResultInfoList){
        List<MoveInfo> moveInfoList = new ArrayList<MoveInfo>();

        Map<String,Integer> crane = new HashMap<>();     //桥机的moveID
        Map<String,List<String>> moveRecords = ImportData.moveOrderRecords;   //根据舱和moveorder确定具体位置
        Map<String,String[]> autoStowResult = ImportData.autoStowResult;        //自动配载结果
        List<CwpResultInfo> cwpResultInfoList1 = cwpResultInfoList;            //cwp结果


        for (CwpResultInfo cwpResultInfo: cwpResultInfoList1)
        {
            try {
                String craneID = cwpResultInfo.getCRANEID();                //桥机号
                String hatchID = cwpResultInfo.getHATCHID();                //舱号
                String hatchBwId = cwpResultInfo.getHATCHBWID();//倍位号
                Integer startMoveOrder = cwpResultInfo.getStartMoveID();        //舱内开始的moveorder
                Integer endMoveOrder = cwpResultInfo.getEndMoveID();          //舱内结束的moveorder
                Integer moveCount = cwpResultInfo.getMOVECOUNT();
                Integer startTime = cwpResultInfo.getWORKINGSTARTTIME();
                Integer endTime = cwpResultInfo.getWORKINGENDTIME();
                String LD = cwpResultInfo.getLDULD();
                String moveType = cwpResultInfo.getMOVETYPE();
                Integer singleTime = (endTime-startTime)/cwpResultInfo.getMOVECOUNT();
                for (int i = startMoveOrder; i < startMoveOrder + moveCount; i++)
                {
                    //System.out.println("新生成一条数据");
                    String hatchMoveOrder = hatchID + "." + String.valueOf(i) + "." + moveType;          //舱号连接编号
                    List<String> vesselPosition = moveRecords.get(hatchMoveOrder);
                    for(String str : vesselPosition) {
                        MoveInfo moveInfo = new MoveInfo();
                        moveInfo.setBatchId(craneID);               //批号为桥机号
                        moveInfo.setMoveKind(LD);
                        if (!crane.containsKey(craneID))
                            crane.put(craneID,0);
                        Integer moveID = crane.get(craneID) + i - startMoveOrder + 1;     //桥机的move序列
                        moveInfo.setMoveId(moveID);
                        moveInfo.setGkey(craneID + "@" + moveID.toString());
                        moveInfo.setExToPosition(str);
                        moveInfo.setWORKINGSTARTTIME(startTime + singleTime*(i - startMoveOrder));
                        String areaPosition = autoStowResult.get(str)[0];
                        String unitID = autoStowResult.get(str)[1];
                        String size = autoStowResult.get(str)[2];
                        moveInfo.setExFromPosition(areaPosition);
                        moveInfo.setUnitId(unitID);
                        moveInfo.setUnitLength(size);
                        moveInfoList.add(moveInfo);
                    }
                }
                crane.put(craneID, crane.get(craneID) + moveCount);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return moveInfoList;
    }
}
