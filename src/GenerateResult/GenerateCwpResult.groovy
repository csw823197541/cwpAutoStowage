package GenerateResult

import cwp.CallCwpTest
import importDataProcess.CraneInfoProcess
import importDataProcess.CwpResultInfoProcess
import importDataProcess.HatchInfoProcess
import importDataProcess.ImportData
import importDataProcess.WorkMoveInfoProcess
import importDataInfo.BayPositionInfo
import importDataInfo.CraneInfo
import importDataInfo.CwpResultInfo
import importDataInfo.HatchInfo
import importDataInfo.HatchPositionInfo
import importDataInfo.PreStowageInfo
import importDataInfo.VesselStructureInfo
import importDataInfo.VoyageInfo
import importDataInfo.WorkMoveInfo
import importDataInfo.WorkingTimeRange

/**
 * Created by csw on 2016/1/22.
 */
class GenerateCwpResult {

    public
    static List<CwpResultInfo> getCwpResult(List<VoyageInfo> voyageInfoList, List<VesselStructureInfo> vesselStructureInfoList, List<CraneInfo> craneInfoList, List<PreStowageInfo> preStowageInfoList) {

        List<CwpResultInfo> cwpResultInfoList = new ArrayList<>();
        List<HatchPositionInfo> hatchPositionInfoList = getHatchPositionInfo(voyageInfoList, vesselStructureInfoList)
        List<Integer> movecounts = ImportData.movecounts
        List<HatchInfo> hatchInfoList = getHatchInfo(voyageInfoList, hatchPositionInfoList, movecounts);
        List<WorkMoveInfo> workMoveInfoList = getWorkMoveInfo(preStowageInfoList)
        //生成cwp算法要用的3个json串
        String craneJsonStr = CraneInfoProcess.getCraneInfoJsonStr(craneInfoList)
        String hatchJsonStr = HatchInfoProcess.getHatchInfoJsonStr(hatchInfoList)
        String moveJsonStr = WorkMoveInfoProcess.getWorkMoveInfoJsonStr(workMoveInfoList)
        //调用cwp算法
        String cwpResultStr = CallCwpTest.cwp(craneJsonStr, hatchJsonStr, moveJsonStr)
        System.out.println(cwpResultStr);
        cwpResultInfoList = CwpResultInfoProcess.getCwpResultInfo(cwpResultStr)
        return cwpResultInfoList;
    }

    /**
     * 得到船舱的信息，用来生成cwp算法要用的json串
     * @param voyageInfoList
     * @param hatchPositionInfoList
     * @param movecounts
     * @return
     */
    private
    static List<HatchInfo> getHatchInfo(List<VoyageInfo> voyageInfoList, List<HatchPositionInfo> hatchPositionInfoList, List<Integer> movecounts) {
        System.out.println("开始生成船舱信息：");
        List<HatchInfo> hatchInfoList = new ArrayList<>()
        HatchInfo newhatchInfo;
        Date workingstarttime = voyageInfoList.get(0).getVOTPWKSTTM();
        Date workingendtime = voyageInfoList.get(0).getVOTPWKENTM();
        String vesselID = voyageInfoList.get(0).getVESSELID();
        WorkingTimeRange workingTimeRange = new WorkingTimeRange();
        workingTimeRange.setID(null);
        workingTimeRange.setWORKSTARTTIME(workingstarttime);
        workingTimeRange.setWORKENDTIME(workingendtime);
        List<WorkingTimeRange> workingTimeRangeList = new ArrayList<WorkingTimeRange>();
        workingTimeRangeList.add(workingTimeRange);
        Integer i = 0;
        for (HatchPositionInfo hatchPositionInfo : hatchPositionInfoList) {
            newhatchInfo = new HatchInfo();
            newhatchInfo.setHORIZONTALSTARTPOSITION(hatchPositionInfo.getPOSITION());
            newhatchInfo.setID(hatchPositionInfo.getVHT_ID());
            newhatchInfo.setLENGTH(hatchPositionInfo.getLENGTH());
            newhatchInfo.setVESSELID(vesselID);
            newhatchInfo.setMOVECOUNT(movecounts.get(i++));
            newhatchInfo.setNO(hatchPositionInfo.getVHT_ID());
            newhatchInfo.setSEQ(hatchPositionInfo.getVHT_ID());
            newhatchInfo.setWORKINGTIMERANGES(workingTimeRangeList);//工作时间
            System.out.println(newhatchInfo.getHORIZONTALSTARTPOSITION() + " " + newhatchInfo.getID() + " " + newhatchInfo.getMOVECOUNT());
            hatchInfoList.add(newhatchInfo);
        }
        return hatchInfoList;
    }

    /**
     * 得到舱位置信息，生成船舱信息时要用到，顺便将倍位置信息保存在ImportData全局里面
     * @param voyageInfoList
     * @param vesselStructureInfoList
     * @return
     */
    private
    static List<HatchPositionInfo> getHatchPositionInfo(List<VoyageInfo> voyageInfoList, List<VesselStructureInfo> vesselStructureInfoList) {

        Set<String> hatchs = new HashSet<String>();          //舱集合
        Set<String> bays = new HashSet<String>();           //倍集合

        List<HatchPositionInfo> hatchPositionInfoList = new ArrayList<>();
        List<BayPositionInfo> bayPositionInfoList = new ArrayList<BayPositionInfo>();

        HatchPositionInfo newhatchPositionInfo;
        BayPositionInfo newbayPositionInfo;
        Integer startposition = voyageInfoList.get(0).getSTARTPOSITION();
        for (VesselStructureInfo vesselStructureInfo : vesselStructureInfoList) {
            String VHTID = vesselStructureInfo.getVHTID().toString();
            Integer Length = vesselStructureInfo.getLENGTH();
            Integer VHTPOSITION = vesselStructureInfo.getVHTPOISITION();
            String VBYBAYID = vesselStructureInfo.getVBYBAYID().toString();
            Integer VBYPOSIYION = vesselStructureInfo.getVBYPOSITION();
            if (!hatchs.contains(VHTID)) {
                newhatchPositionInfo = new HatchPositionInfo();
                newhatchPositionInfo.setVHT_ID(VHTID);
                newhatchPositionInfo.setLENGTH(Length);
                Integer position = startposition + VHTPOSITION;
                newhatchPositionInfo.setPOSITION(position);
                hatchs.add(VHTID);
                hatchPositionInfoList.add(newhatchPositionInfo);
            }
            if (!bays.contains(VBYBAYID)) {
                newbayPositionInfo = new BayPositionInfo();
                newbayPositionInfo.setVHT_ID(VHTID);
                newbayPositionInfo.setVBY_BAYID(VBYBAYID);
                newbayPositionInfo.setVBY_POSITION(startposition + VBYPOSIYION);
                bays.add(VBYBAYID);
                bayPositionInfoList.add(newbayPositionInfo);
            }
        }
        ImportData.bayPositionInfoList = bayPositionInfoList;
        return hatchPositionInfoList;
    }

    /**
     * 得到舱内作业关信息，用来生成cwp算法要用的json串
     * @param preStowageInfoList
     * @return
     */
    private static List<WorkMoveInfo> getWorkMoveInfo(List<PreStowageInfo> preStowageInfoList) {

        List<WorkMoveInfo> workMoveInfoList = new ArrayList<WorkMoveInfo>();
        HashMap<String, Integer> baypos = new HashMap<String, Integer>();

        WorkMoveInfo newworkmoveInfo;
        for (BayPositionInfo bayPositionInfo : ImportData.bayPositionInfoList) {
            Integer BAYID = Integer.valueOf(bayPositionInfo.getVBY_BAYID());
            baypos.put(bayPositionInfo.getVBY_BAYID(), bayPositionInfo.getVBY_POSITION());
            //算平均值在这里省略了........因为默认一个舱长48.......
            if (BAYID % 4 == 1) {
                BAYID++;
                baypos.put(BAYID.toString(), bayPositionInfo.getVBY_POSITION() + 12);
            }
        }
        System.out.println("开始生成舱内作业关信息：");
        for (PreStowageInfo preStowageInfo : preStowageInfoList) {
            newworkmoveInfo = new WorkMoveInfo();
            newworkmoveInfo.setCWPWORKMOVENUM(preStowageInfo.getMOVE_ORDER());
            //甲板上/下
            Integer BAYID = Integer.valueOf(preStowageInfo.getVBY_BAYID());
            if (BAYID > 80) {
                newworkmoveInfo.setDECK("H");
            } else {
                newworkmoveInfo.setDECK("D");
            }
            newworkmoveInfo.setGLOBALPRIORITY(2);
            newworkmoveInfo.setHATCH(preStowageInfo.getVHT_ID().toString());
            //倍位中心的绝对位置
            newworkmoveInfo.setHORIZONTALPOSITION(baypos.get(BAYID.toString()));
            newworkmoveInfo.setLD("L");
            newworkmoveInfo.setMOVETYPE(preStowageInfo.getSIZE().toString());

            //调试信息
            System.out.println(newworkmoveInfo.getCWPWORKMOVENUM() + " " + preStowageInfo.getVBY_BAYID() + " " + newworkmoveInfo.getHORIZONTALPOSITION() + " " + newworkmoveInfo.getHATCH());
            workMoveInfoList.add(newworkmoveInfo);
        }
        return workMoveInfoList;
    }
}
