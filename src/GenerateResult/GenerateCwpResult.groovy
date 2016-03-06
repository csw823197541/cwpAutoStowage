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
import utils.FileUtil
import viewFrame.VesselStructureFrame

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
        FileUtil.writeToFile("E:/hatch.txt", hatchJsonStr)
        String moveJsonStr = WorkMoveInfoProcess.getWorkMoveInfoJsonStr(workMoveInfoList)
        //调用cwp算法
        if(craneJsonStr != null && hatchJsonStr != null && moveJsonStr != null) {
            String cwpResultStr = CallCwpTest.cwp(craneJsonStr, hatchJsonStr, moveJsonStr)
            System.out.println("cwp算法返回的json字符串:" + cwpResultStr);
            if(cwpResultStr != null){
                cwpResultInfoList = CwpResultInfoProcess.getCwpResultInfo(cwpResultStr)
            } else {
                System.out.println("cwp算法没有返回结果！")
            }
        } else {
            System.out.println("cwp算法需要的3个参数信息中有空的，不能调用算法！")
        }
        return cwpResultInfoList;
    }

    /**
     * 得到船舱的信息，用来生成cwp算法要用的json串
     * @param voyageInfoList
     * @param hatchPositionInfoList
     * @param movecounts
     * @return
     */
    public
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
//            System.out.println(newhatchInfo.getHORIZONTALSTARTPOSITION() + " " + newhatchInfo.getID() + " " + newhatchInfo.getMOVECOUNT());
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
    public
    static List<HatchPositionInfo> getHatchPositionInfo(List<VoyageInfo> voyageInfoList, List<VesselStructureInfo> vesselStructureInfoList) {

        Set<String> hatchs = new HashSet<String>();          //舱集合
        Set<String> bays = new HashSet<String>();           //倍集合

        List<HatchPositionInfo> hatchPositionInfoList = new ArrayList<>();
        List<BayPositionInfo> bayPositionInfoList = new ArrayList<BayPositionInfo>();

        HatchPositionInfo newhatchPositionInfo;
        BayPositionInfo newbayPositionInfo;
        Integer startposition = voyageInfoList.get(0).getSTARTPOSITION();

        //计算舱开始相对于船头位置、倍位中心相对于船头位置
        List<String> VHTIDs = new ArrayList<>();
        List<String> VBYBAYIDs = new ArrayList<>();
        for (VesselStructureInfo vesselStructureInfo : vesselStructureInfoList) {
            if(!VHTIDs.contains(vesselStructureInfo.getVHTID()))
                VHTIDs.add(vesselStructureInfo.getVHTID())
            if(!VBYBAYIDs.contains(vesselStructureInfo.getVBYBAYID()))
                VBYBAYIDs.add(vesselStructureInfo.getVBYBAYID())
        }//统计倍舱位数和倍位数
//        println VHTIDs.size() +"-----"+VBYBAYIDs.size()
        Map<String, Integer> VHTPOSITIONs = new HashMap<>();
        int i = 0;
        for(String vhtiDs : VHTIDs) {
            VHTPOSITIONs.put(vhtiDs, i*16)//Todo假设舱间距为2米，这个数据码头还没回复我是否合理
            i++
        }
        Map<String, Double> VBYPOSIYIONs = new HashMap<>();
        i = 1;
        Double d = 3.5
        Double pre = 0.0
        for(String vBYBAYIDs : VBYBAYIDs) {
            if(i == 1) {
                d = 3.5
            }
            else if(i%2 != 0) {
                d = 9
            } else {
                d = 7
            }
            pre = pre + d;
            VBYPOSIYIONs.put(vBYBAYIDs, pre)
            i++
        }
        //结束

        for (VesselStructureInfo vesselStructureInfo : vesselStructureInfoList) {
            String VHTID = vesselStructureInfo.getVHTID().toString();
            vesselStructureInfo.setVHTPOSITION(VHTPOSITIONs.get(VHTID))//将舱开始相对于船头位置赋值
            Integer Length = vesselStructureInfo.getLENGTH();
            Integer VHTPOSITION = vesselStructureInfo.getVHTPOSITION()
            String VBYBAYID = vesselStructureInfo.getVBYBAYID().toString();
            vesselStructureInfo.setVBYPOSITION(VBYPOSIYIONs.get(VBYBAYID))//将倍位中心相对于船头位置赋值
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
        //为了查看船舶结构两个坐标是否正确，
        VesselStructureFrame vesselStructureFrame = new VesselStructureFrame(vesselStructureInfoList);
        vesselStructureFrame.setVisible(true);
        //结束
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
//            System.out.println(newworkmoveInfo.getCWPWORKMOVENUM() + " " + preStowageInfo.getVBY_BAYID() + " " + newworkmoveInfo.getHORIZONTALPOSITION() + " " + newworkmoveInfo.getHATCH());
            workMoveInfoList.add(newworkmoveInfo);
        }
        return workMoveInfoList;
    }
}
