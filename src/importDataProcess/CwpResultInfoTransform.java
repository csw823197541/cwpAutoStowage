package importDataProcess;

import importDataInfo.CwpResultInfo;

import java.util.*;

/**
 * Created by csw on 2016/5/19.
 */
public class CwpResultInfoTransform {

    public static List<CwpResultInfo> getTransformResult(List<CwpResultInfo> cwpResultInfoList) {
        List<CwpResultInfo> resultInfoList = new ArrayList<>();

        List<String> hatchList = new ArrayList<>();

        //先统计有多少个舱，和多少个桥机
        for(CwpResultInfo cwpResultInfo : cwpResultInfoList) {
            if(!hatchList.contains(cwpResultInfo.getHATCHID())) {
                hatchList.add(cwpResultInfo.getHATCHID());
            }
        }
        //排序
        Collections.sort(hatchList);

        //用Map存储每个舱的作业块
        Map<String, List<CwpResultInfo>> hatchListMap = new HashMap<>();
        //初始化hatchListMap
        for(String hatchId : hatchList) {
            hatchListMap.put(hatchId, new ArrayList<CwpResultInfo>());
        }
        //先按舱将数据放在Map里
        for(CwpResultInfo cwpResultInfo : cwpResultInfoList) {
            String hatchId = cwpResultInfo.getHATCHID();
            hatchListMap.get(hatchId).add(cwpResultInfo);
        }

        //用Map存储每个舱、同一个桥机连续作业时间的作业块
        Map<String, List<CwpResultInfo>> hatchCraneListMap = new HashMap<>();
        //按舱将数据取出来，然后按桥机将连续时间的作业块进行组合，形成一个大的作业块
        for(String hatchId : hatchList) {
            List<CwpResultInfo> cwpResultInfoList1 = hatchListMap.get(hatchId);
            //统计该舱有多少部桥机作业
            List<String> craneList = new ArrayList<>();
            for(CwpResultInfo cwpResultInfo : cwpResultInfoList1) {
                String craneId = cwpResultInfo.getCRANEID();
                if(!craneList.contains(craneId)) {
                    craneList.add(craneId);
                }
            }
            //按桥机和连续作业时间分块
            String lastCraneId = null;
            int lastStartTime = 0;
            CwpResultInfo lastCwpResultInfo = new CwpResultInfo();
            int totalMoveCount = 0;
            int workTimeMinute = 0;
            int craneSeq = 1;
            for(CwpResultInfo cwpResultInfo : cwpResultInfoList1) {
                String craneId = cwpResultInfo.getCRANEID();
                int startTime = cwpResultInfo.getWORKINGSTARTTIME();
                int endTime = cwpResultInfo.getWORKINGENDTIME();
                int moveCount = cwpResultInfo.getMOVECOUNT();
                if(craneId.equals(lastCraneId)) {
                    if(lastStartTime == startTime || lastStartTime == startTime+1) {
                        totalMoveCount += moveCount;
                        workTimeMinute += endTime - startTime;
                    } else {
                        lastCwpResultInfo.setMOVECOUNT(totalMoveCount);
                        lastCwpResultInfo.setCraneSeq(craneSeq++);
                        lastCwpResultInfo.setWorkTimeMinute(workTimeMinute);
                        resultInfoList.add(lastCwpResultInfo);
                        totalMoveCount = 0;
                        workTimeMinute = 0;
                    }
                } else {//桥机号不相同
                    if(lastCwpResultInfo != null) {
                        lastCwpResultInfo.setMOVECOUNT(totalMoveCount);
                        lastCwpResultInfo.setCraneSeq(craneSeq++);
                        lastCwpResultInfo.setWorkTimeMinute(workTimeMinute);
                        resultInfoList.add(lastCwpResultInfo);
                        totalMoveCount = 0;
                        workTimeMinute = 0;
                    }
                    totalMoveCount += moveCount;
                    workTimeMinute += endTime - startTime;
                }
                lastCraneId = craneId;
                lastStartTime = endTime;
                lastCwpResultInfo = cwpResultInfo;
            }
        }
        return resultInfoList;
    }
}
