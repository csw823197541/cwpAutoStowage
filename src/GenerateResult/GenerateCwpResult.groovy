package GenerateResult

import cwp.CallCwpTest
import importDataInfo.PreStowageData
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

import java.text.DecimalFormat

/**
 * Created by csw on 2016/1/22.
 */
class GenerateCwpResult {

    public static DecimalFormat df = new DecimalFormat("#.00");

    public
    static List<CwpResultInfo> getCwpResult(List<VoyageInfo> voyageInfoList, List<VesselStructureInfo> vesselStructureInfoList, List<CraneInfo> craneInfoList, List<PreStowageData> preStowageDataList) {

        List<CwpResultInfo> cwpResultInfoList = new ArrayList<>();
        List<HatchPositionInfo> hatchPositionInfoList = getHatchPositionInfo(voyageInfoList, vesselStructureInfoList)
        //根据实配图得到编好作业序列和作业工艺的预配信息
        List<PreStowageData> preStowageResult = GeneratePreStowageFromKnowStowage.getPreStowageResult(preStowageDataList)
        //计算movecounts和对预配图编属性组
        List<PreStowageData> preStowageDatas = GenerateMoveCountAndGroupId.getMoveCountAndGroupId(preStowageResult)
        List<Integer> movecounts = ImportData.movecounts
        List<HatchInfo> hatchInfoList = getHatchInfo(voyageInfoList, hatchPositionInfoList, movecounts);
        List<WorkMoveInfo> workMoveInfoList = getWorkMoveInfo(preStowageResult)
        //生成cwp算法要用的3个json串
        String craneJsonStr = CraneInfoProcess.getCraneInfoJsonStr(craneInfoList)
        String hatchJsonStr = HatchInfoProcess.getHatchInfoJsonStr(hatchInfoList)
        String moveJsonStr = WorkMoveInfoProcess.getWorkMoveInfoJsonStr(workMoveInfoList)
        try{
            FileUtil.writeToFile("E:/toCwpData/hatch.txt", hatchJsonStr)
            FileUtil.writeToFile("E:/toCwpData/crane.txt", craneJsonStr)
            FileUtil.writeToFile("E:/toCwpData/moves.txt", moveJsonStr)
        } catch (Exception e) {
            e.printStackTrace()
        }
        //调用cwp算法
        if(craneJsonStr != null && hatchJsonStr != null && moveJsonStr != null) {
            String cwpResultStr = null
//            String cwpResultStr = CallCwpTest.cwp(craneJsonStr, hatchJsonStr, moveJsonStr)
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
        Map<String, Double> VHTPOSITIONs = new HashMap<>();
        int i = 0;
        for(String vhtiDs : VHTIDs) {
            VHTPOSITIONs.put(vhtiDs, i*49.28)//Todo假设舱间距为2米，这个数据码头还没回复我是否合理
            i++
        }
        Map<String, Double> VBYPOSIYIONs = new HashMap<>();
        i = 1;
        Double d = 11.5
        Double pre = 0.0
        for(String vBYBAYIDs : VBYBAYIDs) {
            if(i == 1) {
                d = 11.5
            }
            else if(i%2 != 0) {
                d = 26.28
            } else {
                d = 23
            }
            pre = pre + d;
            VBYPOSIYIONs.put(vBYBAYIDs, Double.valueOf(df.format(pre)))
            i++
        }
        //结束

        for (VesselStructureInfo vesselStructureInfo : vesselStructureInfoList) {
            String VHTID = vesselStructureInfo.getVHTID().toString();
            vesselStructureInfo.setVHTPOSITION(VHTPOSITIONs.get(VHTID))//将舱开始相对于船头位置赋值
            Integer Length = vesselStructureInfo.getLENGTH();
            Double VHTPOSITION = vesselStructureInfo.getVHTPOSITION()
            String VBYBAYID = vesselStructureInfo.getVBYBAYID().toString();
            vesselStructureInfo.setVBYPOSITION(VBYPOSIYIONs.get(VBYBAYID))//将倍位中心相对于船头位置赋值
            Double VBYPOSIYION = vesselStructureInfo.getVBYPOSITION();

            if (!hatchs.contains(VHTID)) {
                newhatchPositionInfo = new HatchPositionInfo();
                newhatchPositionInfo.setVHT_ID(VHTID);
                newhatchPositionInfo.setLENGTH(Length);
                Double position = startposition + VHTPOSITION;
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
//        VesselStructureFrame vesselStructureFrame = new VesselStructureFrame(vesselStructureInfoList);
//        vesselStructureFrame.setVisible(true);
        //结束
        ImportData.bayPositionInfoList = bayPositionInfoList;
        return hatchPositionInfoList;
    }

    /**
     * 得到舱内作业关信息，用来生成cwp算法要用的json串
     * @param preStowageInfoList
     * @return
     */
    private static List<WorkMoveInfo> getWorkMoveInfo(List<PreStowageData> preStowageDataList) {

        List<WorkMoveInfo> workMoveInfoList = new ArrayList<WorkMoveInfo>();
        List<BayPositionInfo> bayPositionInfoList = ImportData.bayPositionInfoList//倍位中心绝对位置
        Map<String, Double> bayPositionQuery = new HashMap<>()
        for(BayPositionInfo bayPositionInfo : bayPositionInfoList) {
            bayPositionQuery.put(bayPositionInfo.getVBY_BAYID().replace("0", ""), bayPositionInfo.getVBY_POSITION())
        }
        for(int i = 0; i < bayPositionInfoList.size(); i = i+2) {//将每个倍的中心位置保存起来，生成作业关的时候查找
            String s1 = Integer.valueOf(bayPositionInfoList.get(i).getVBY_BAYID().replace("0", ""))+1+""//大倍号
            Double d = (bayPositionInfoList.get(i).getVBY_POSITION()+bayPositionInfoList.get(i+1).getVBY_POSITION())/2
            println s1 + "----" + d
            bayPositionQuery.put(s1, d)
        }
        System.out.println("开始生成舱内作业关信息：");
        //将数据放在不同的舱位里
        List<String> VHTIDs = new ArrayList<>()//存放舱位ID
        Map<String, List<PreStowageData>> stringListMap = new HashMap<>()//放在不同的舱位的数据
        for(PreStowageData preStowageData : preStowageDataList) {
            if(!VHTIDs.contains(preStowageData.getVHT_ID())) {
                VHTIDs.add(preStowageData.getVHT_ID())
            }
        }
        Collections.sort(VHTIDs)
        println "舱位数：" + VHTIDs.size()
        for(String str : VHTIDs) {//
            List<PreStowageData> dataList1 = new ArrayList<>()
            for(PreStowageData preStowageData : preStowageDataList) {
                if(str.equals(preStowageData.getVHT_ID())) {
                    dataList1.add(preStowageData)
                }
            }
            stringListMap.put(str, dataList1)
        }
        for(String str : VHTIDs) {//逐舱生成舱内作业关信息
            List<PreStowageData> dataList = stringListMap.get(str)
            List<Integer> orders = new ArrayList<>()//每个舱的作业序列
            for(PreStowageData preStowageData1 : dataList) {
                if(!orders.contains(preStowageData1.getMOVE_ORDER())) {
                    orders.add(preStowageData1.getMOVE_ORDER())
                }
            }
            for(Integer order : orders) {//按舱内的序列来生成舱内作业关信息
                WorkMoveInfo workMoveInfo = new WorkMoveInfo()
                List<PreStowageData> moveDataList = new ArrayList<>()
                for(PreStowageData preStowageData2 : dataList) {//将同一序列的数据保存下来
                    if(order == preStowageData2.getMOVE_ORDER()) {
                        moveDataList.add(preStowageData2)
                    }
                }
                if(moveDataList.size() == 2) {//双箱吊或者双吊具
                    if(moveDataList.get(0).getVRW_ROWNO().equals(
                            moveDataList.get(1).getVRW_ROWNO())) {//排号相同，为双箱吊
                        workMoveInfo.setCWPWORKMOVENUM(order)
                        //甲板上/下
                        Integer tier = Integer.valueOf(moveDataList.get(0).getVTR_TIERNO());
                        String deck = tier >= 82 ? "H" : "D"
                        workMoveInfo.setDECK(deck)
                        workMoveInfo.setGLOBALPRIORITY(2)
                        workMoveInfo.setHATCH(moveDataList.get(0).getVHT_ID())
                        workMoveInfo.setMOVETYPE(moveDataList.get(0).getWORKFLOW())
                        workMoveInfo.setLD(moveDataList.get(0).getLDULD())
                        //倍位中心的绝对位置
                        Double d = (bayPositionQuery.get(moveDataList.get(0).getVBY_BAYID())+bayPositionQuery.get(moveDataList.get(1).getVBY_BAYID()))/2
                        workMoveInfo.setHORIZONTALPOSITION(d)
                    }
                    if(moveDataList.get(0).getVBY_BAYID().equals(
                            moveDataList.get(1).getVBY_BAYID())) {//倍位号相同，为双吊具
                        workMoveInfo.setCWPWORKMOVENUM(order)
                        //甲板上/下
                        Integer tier = Integer.valueOf(moveDataList.get(0).getVTR_TIERNO());
                        String deck = tier >= 82 ? "H" : "D"
                        workMoveInfo.setDECK(deck)
                        workMoveInfo.setGLOBALPRIORITY(2)
                        workMoveInfo.setHATCH(moveDataList.get(0).getVHT_ID())
                        workMoveInfo.setMOVETYPE(moveDataList.get(0).getWORKFLOW())
                        workMoveInfo.setLD(moveDataList.get(0).getLDULD())
                        //倍位中心的绝对位置
                        Double d = bayPositionQuery.get(moveDataList.get(0).getVBY_BAYID())
                        workMoveInfo.setHORIZONTALPOSITION(d)
                    }
                } else {//单吊具
                    workMoveInfo.setCWPWORKMOVENUM(order)
                    //甲板上/下
                    Integer tier = Integer.valueOf(moveDataList.get(0).getVTR_TIERNO());
                    String deck = tier >= 82 ? "H" : "D"
                    workMoveInfo.setDECK(deck)
                    workMoveInfo.setGLOBALPRIORITY(2)
                    workMoveInfo.setHATCH(moveDataList.get(0).getVHT_ID())
                    workMoveInfo.setMOVETYPE(moveDataList.get(0).getWORKFLOW())
                    workMoveInfo.setLD(moveDataList.get(0).getLDULD())
                    //倍位中心的绝对位置
                    Double d = bayPositionQuery.get(moveDataList.get(0).getVBY_BAYID())
                    workMoveInfo.setHORIZONTALPOSITION(d)
                }
                workMoveInfoList.add(workMoveInfo)
            }
        }
        return workMoveInfoList;
    }
}
