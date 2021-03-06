package GenerateResult

import cwp.CallCwpTest
import importDataInfo.*
import importDataProcess.*
import utils.FileUtil

import java.text.DecimalFormat

/**
 * Created by csw on 2016/1/22.
 */
class GenerateCwpResult1 {

    public static DecimalFormat df = new DecimalFormat("#.00");

    public
    static List<CwpResultInfo> getCwpResult(List<VoyageInfo> voyageInfoList, List<VesselStructureInfo> vesselStructureInfoList, List<CraneInfo> craneInfoList, List<PreStowageData> preStowageDataList) {

        List<CwpResultInfo> cwpResultInfoList = new ArrayList<>();
        List<HatchPositionInfo> hatchPositionInfoList = getHatchPositionInfo(voyageInfoList, vesselStructureInfoList)
        //根据实配图得到编好作业序列和作业工艺的预配信息
//        List<PreStowageData> preStowageResult = GeneratePreStowageFromKnowStowage2.getPreStowageResult(preStowageDataList)
        //计算movecounts和对预配图编属性组
        List<PreStowageData> preStowageDatas = GenerateMoveCountAndGroupId.getMoveCountAndGroupId(preStowageDataList)
        Map<String, Integer> moveCountQuery = ImportData.moveCountQuery
        List<HatchInfo> hatchInfoList = getHatchInfo(voyageInfoList, hatchPositionInfoList, moveCountQuery);

        //计算每个舱的moZhi


        List<WorkMoveInfo> workMoveInfoList = getWorkMoveInfo(preStowageDataList)
        //生成cwp算法要用的3个json串
        String craneJsonStr = CraneInfoProcess.getCraneInfoJsonStr(craneInfoList)
        String hatchJsonStr = HatchInfoProcess.getHatchInfoJsonStr(hatchInfoList)
        String moveJsonStr = WorkMoveInfoProcess.getWorkMoveInfoJsonStr(workMoveInfoList)
        try{
            FileUtil.writeToFile("toCwpData/hatch.txt", hatchJsonStr)
            FileUtil.writeToFile("toCwpData/crane.txt", craneJsonStr)
            FileUtil.writeToFile("toCwpData/moves.txt", moveJsonStr)
        } catch (Exception e) {
            e.printStackTrace()
        }
        //调用cwp算法
        if(craneJsonStr != null && hatchJsonStr != null && moveJsonStr != null) {
            String cwpResultStr = null
            int craneSize = 4  //桥机数
            String moZhiStr = GenerateModelValue.getMoZhi(hatchInfoList, craneSize) //得到各个舱的模值
            cwpResultStr = CallCwpTest.cwp(craneJsonStr, hatchJsonStr, moveJsonStr, ""+craneSize, moZhiStr, "0.2") //后三个参数分别代表桥机数量、moveCount数量模值、效率缩小
            System.out.println("cwp算法返回的json字符串:" + cwpResultStr);
            if(cwpResultStr != null){
                try{
                    FileUtil.writeToFile("toCwpData/cwpResult.txt", cwpResultStr)
                }catch (Exception e) {
                    e.printStackTrace()
                }
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
    static List<HatchInfo> getHatchInfo(List<VoyageInfo> voyageInfoList, List<HatchPositionInfo> hatchPositionInfoList, Map<String, Integer> moveCountQuery) {
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
            newhatchInfo.setID(hatchPositionInfo.getVHTID());
            newhatchInfo.setLENGTH(hatchPositionInfo.getLENGTH());
            newhatchInfo.setVESSELID(vesselID);
            int count = moveCountQuery.get(hatchPositionInfo.getVHTID()) != null ? moveCountQuery.get(hatchPositionInfo.getVHTID()) : 0;
            newhatchInfo.setMOVECOUNT(count);
            newhatchInfo.setNO(hatchPositionInfo.getVHTID());
            newhatchInfo.setSEQ(hatchPositionInfo.getVHTID());
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
        int length = vesselStructureInfoList.get(0).getLENGTH()
        Double cjj = 3.28//舱间距3.28英尺
        for(String vhtiDs : VHTIDs) {
            VHTPOSITIONs.put(vhtiDs, Double.valueOf(df.format(i*(length + cjj))))//Todo假设舱间距为2米，这个数据码头还没回复我是否合理
            i++
        }
        Map<String, Double> VBYPOSIYIONs = new HashMap<>();
        i = 1;
        Double d = length/4
        Double pre = 0.0
        for(String vBYBAYIDs : VBYBAYIDs) {
            if(i == 1) {
//                d = 11.5
                d = length/4
            }
            else if(i%2 != 0) {
//                d = 26.28
                d = length/2 + cjj
            } else {
//                d = 23
                d = length/2
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
                newhatchPositionInfo.setVHTID(VHTID);
                newhatchPositionInfo.setLENGTH(Length);
                Double position = startposition + VHTPOSITION;
                newhatchPositionInfo.setPOSITION(position);
                hatchs.add(VHTID);
                hatchPositionInfoList.add(newhatchPositionInfo);
            }
            if (!bays.contains(VBYBAYID)) {
                newbayPositionInfo = new BayPositionInfo();
                newbayPositionInfo.setVHTID(VHTID);
                newbayPositionInfo.setVBYBAYID(VBYBAYID);
                newbayPositionInfo.setVBYPOSITION(startposition + VBYPOSIYION);
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
        Map<String, Double> bayPositionQuery = new HashMap<>()//存放每个的绝对中心位置,以便生成作业关信息是查找
        for(BayPositionInfo bayPositionInfo : bayPositionInfoList) {//每个舱有两个小倍，这个已经通过计算保存过
            String bayStr = bayPositionInfo.getVBYBAYID().startsWith("0") ? bayPositionInfo.getVBYBAYID().replace("0", "") : bayPositionInfo.getVBYBAYID()
            bayPositionQuery.put(bayStr, bayPositionInfo.getVBYPOSITION())
//            println bayPositionInfo.getVBY_BAYID().replace("0", "") + "----" + bayPositionInfo.getVBY_POSITION()
        }
        for(int i = 0; i < bayPositionInfoList.size(); i = i+2) {//计算每个舱的大倍，大倍的位置就是两个小倍的位置除以2
            String bayStr = bayPositionInfoList.get(i).getVBYBAYID().startsWith("0") ? bayPositionInfoList.get(i).getVBYBAYID().replace("0", "") : bayPositionInfoList.get(i).getVBYBAYID()
            String s1 = Integer.valueOf(bayStr)+1+""//大倍号
            Double d = (bayPositionInfoList.get(i).getVBYPOSITION()+bayPositionInfoList.get(i+1).getVBYPOSITION())/2
//            println s1 + "----" + d
            bayPositionQuery.put(s1, d)
        }
        System.out.println("开始生成舱内作业关信息：");
        //将数据放在不同的舱位里
        List<String> VHTIDs = new ArrayList<>()//存放舱位ID
        Map<String, List<PreStowageData>> stringListMap = new HashMap<>()//放在不同的舱位的数据
        for(PreStowageData preStowageData : preStowageDataList) {
            if(!VHTIDs.contains(preStowageData.getVHTID())) {
                VHTIDs.add(preStowageData.getVHTID())
            }
        }
        Collections.sort(VHTIDs)
        println "舱位数：" + VHTIDs.size()
        for(String str : VHTIDs) {//将数据存放在不同舱位里
            List<PreStowageData> dataList1 = new ArrayList<>()
            for(PreStowageData preStowageData : preStowageDataList) {
                if(str.equals(preStowageData.getVHTID())) {
                    dataList1.add(preStowageData)
                }
            }
            stringListMap.put(str, dataList1)
        }
        for(String str : VHTIDs) {//逐舱生成舱内作业关信息
            List<PreStowageData> dataList = stringListMap.get(str)
            List<Integer> orders = new ArrayList<>()//每个舱的作业序列
            for(PreStowageData preStowageData1 : dataList) {
                if(!orders.contains(preStowageData1.getMOVEORDER())) {
                    orders.add(preStowageData1.getMOVEORDER())
                }
            }
            for(Integer order : orders) {//按舱内的序列来生成舱内作业关信息
                WorkMoveInfo workMoveInfo = new WorkMoveInfo()
                List<PreStowageData> moveDataList = new ArrayList<>()
                for(PreStowageData preStowageData2 : dataList) {//将同一序列的数据保存下来
                    if(order == preStowageData2.getMOVEORDER()) {
                        moveDataList.add(preStowageData2)
                    }
                }
//                println "是否取到相同作业序列:"+str+"-"+order+"-"+moveDataList.size()
                if(moveDataList.size() == 2) {//作业序列相同,可能是双箱吊或者双吊具
                    if(moveDataList.get(0).getVRWROWNO().equals(
                            moveDataList.get(1).getVRWROWNO())) {//排号相同，为双箱吊
                        workMoveInfo.setCWPWORKMOVENUM(order)
                        //甲板上/下
                        Integer tier = Integer.valueOf(moveDataList.get(0).getVTRTIERNO());
                        String deck = tier >= 50 ? "H" : "D"
                        workMoveInfo.setDECK(deck)
                        workMoveInfo.setGLOBALPRIORITY(2)
                        workMoveInfo.setHATCH(moveDataList.get(0).getVHTID())
                        workMoveInfo.setMOVETYPE(moveDataList.get(0).getWORKFLOW())
                        workMoveInfo.setLD(moveDataList.get(0).getLDULD())
                        //倍位中心的绝对位置
                        String bayStr0 = moveDataList.get(0).getVBYBAYID()//去掉倍为号前面的0
                        bayStr0 = bayStr0.startsWith("0") ? bayStr0.replace("0", "") : bayStr0
                        String bayStr1 = moveDataList.get(1).getVBYBAYID()//
                        bayStr1 = bayStr1.startsWith("0") ? bayStr1.replace("0", "") : bayStr1
                        Double d = (bayPositionQuery.get(bayStr0)+bayPositionQuery.get(bayStr1))/2
                        workMoveInfo.setHORIZONTALPOSITION(d)
                    }
                    if(moveDataList.get(0).getVBYBAYID().equals(
                            moveDataList.get(1).getVBYBAYID())) {//倍位号相同，为双吊具
                        workMoveInfo.setCWPWORKMOVENUM(order)
                        //甲板上/下
                        Integer tier = Integer.valueOf(moveDataList.get(0).getVTRTIERNO());
                        String deck = tier >= 50 ? "H" : "D"
                        workMoveInfo.setDECK(deck)
                        workMoveInfo.setGLOBALPRIORITY(2)
                        workMoveInfo.setHATCH(moveDataList.get(0).getVHTID())
                        workMoveInfo.setMOVETYPE(moveDataList.get(0).getWORKFLOW())
                        workMoveInfo.setLD(moveDataList.get(0).getLDULD())
                        //倍位中心的绝对位置
                        String bayStr = moveDataList.get(0).getVBYBAYID()//
                        bayStr = bayStr.startsWith("0") ? bayStr.replace("0", "") : bayStr
                        Double d = bayPositionQuery.get(bayStr)
                        workMoveInfo.setHORIZONTALPOSITION(d)
                    }
                } else {//单吊具
                    workMoveInfo.setCWPWORKMOVENUM(order)
                    //甲板上/下
                    Integer tier = Integer.valueOf(moveDataList.get(0).getVTRTIERNO());
                    String deck = tier >= 50 ? "H" : "D"
                    workMoveInfo.setDECK(deck)
                    workMoveInfo.setGLOBALPRIORITY(2)
                    workMoveInfo.setHATCH(moveDataList.get(0).getVHTID())
                    workMoveInfo.setMOVETYPE(moveDataList.get(0).getWORKFLOW())
                    workMoveInfo.setLD(moveDataList.get(0).getLDULD())
                    //倍位中心的绝对位置
                    String bayStr0 = moveDataList.get(0).getVBYBAYID()//去掉倍为号前面的0
                    bayStr0 = bayStr0.startsWith("0") ? bayStr0.replace("0", "") : bayStr0
                    Double d = bayPositionQuery.get(bayStr0)
                    workMoveInfo.setHORIZONTALPOSITION(d)
                }
                workMoveInfoList.add(workMoveInfo)
            }
        }
        return workMoveInfoList;
    }
}
