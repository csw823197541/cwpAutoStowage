package GenerateResult;

import importData.ImportData;
import importDataInfo.AutoStowResultInfo;
import importDataInfo.CwpResultInfo;
import importDataInfo.MoveInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by leko on 2016/1/22.
 */
public class GenerateMoveInfoResult {
    public static List<MoveInfo> getMoveInfoResult(List<CwpResultInfo> cwpResultInfoList, List<AutoStowResultInfo> autoStowResultInfoList){
        List<MoveInfo> moveInfoList = new ArrayList<MoveInfo>();

        HashMap<String,Integer> crane = new HashMap<String, Integer>();     //桥机的moveID
        HashMap<String,String> moverecords = ImportData.moveorderrecords;   //根据舱和moveorder确定具体位置
        HashMap<String,String[]> autostowresult = ImportData.autostowresult;        //自动配载结果
        List<CwpResultInfo> cwpResultInfoList1 = cwpResultInfoList;             //cwp结果

        MoveInfo moveInfo;
        for (CwpResultInfo cwpResultInfo: cwpResultInfoList1)
        {
            try {
                String craneID = cwpResultInfo.getCRANEID();                //桥机号
                String hatchID = cwpResultInfo.getHATCHID();                //舱号
                Integer startmoveorder = cwpResultInfo.getStartMoveID();        //舱内开始的moveorder
                //System.out.println("start"+startmoveorder.toString());
                Integer endmoveorder = cwpResultInfo.getEndMoveID();          //舱内结束的moveorder
                //System.out.println("end"+endmoveorder.toString());
                Integer starttime = cwpResultInfo.getWORKINGSTARTTIME();
                Integer endtime = cwpResultInfo.getWORKINGENDTIME();
                Integer singletime = (endtime-starttime)/cwpResultInfo.getMOVECOUNT();
                for (int i=startmoveorder;i<endmoveorder;i++)
                {
                    //System.out.println("新生成一条数据");
                    moveInfo = new MoveInfo();
                    moveInfo.setBatchId(craneID);               //批号为桥机号
                    //System.out.println("桥机号"+ moveInfo.getBatchId());
                    moveInfo.setMoveKind("Load");
                    if (!crane.containsKey(craneID)) crane.put(craneID,0);
                    Integer moveID = crane.get(craneID)+i-startmoveorder+1;     //桥机的move序列
                    moveInfo.setMoveId(moveID);
                    moveInfo.setGkey(craneID+"@"+moveID.toString());
                    String hatchmoveorder = hatchID+"."+String.valueOf(i);          //舱号连接编号
                    //System.out.println("moveorder:"+hatchmoveorder);
                    String vesselpositon = moverecords.get(hatchmoveorder);
                    moveInfo.setExToPosition(vesselpositon);
                    moveInfo.setWORKINGSTARTTIME(starttime+singletime*(i-startmoveorder));
                    //String areaposition = autostowresult.get(vesselpositon)[0];
                    //String unitID = autostowresult.get(vesselpositon)[1];
                    //String size = autostowresult.get(vesselpositon)[2];
                    //moveInfo.setExFromPosition(areaposition);
                    //moveInfo.setUnitId(unitID);
                    //moveInfo.setUnitLength(size);
                    moveInfoList.add(moveInfo);

                }
                crane.put(craneID, crane.get(craneID) + cwpResultInfo.getMOVECOUNT());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return moveInfoList;
    }
}
