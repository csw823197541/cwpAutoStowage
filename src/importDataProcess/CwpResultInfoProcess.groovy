package importDataProcess

import groovy.json.JsonSlurper
import importDataInfo.CwpResultInfo
import importDataInfo.VoyageInfo

import java.text.DecimalFormat

/**
 * Created by csw on 2016/1/16.
 */
class CwpResultInfoProcess {

    public static DecimalFormat df = new DecimalFormat("#.00");

    //Json字符串解析编码
    public static List<CwpResultInfo> getCwpResultInfo(String jsonStr, List<VoyageInfo> voyageInfoList) {

        boolean isError = false;
        List<CwpResultInfo> cwpResultInfoList = new ArrayList<CwpResultInfo>();

        try{
            Date voyageStartTime = voyageInfoList.get(0).getVOTPWKSTTM();
            long stLong = voyageStartTime.getTime();

            def root = new JsonSlurper().parseText(jsonStr)

            assert root instanceof List//根据读入数据的格式，可以直接把json转换成List

            root.each { cwpResult ->
                CwpResultInfo cwpResultInfo = new CwpResultInfo()
                assert cwpResult instanceof Map
                cwpResultInfo.CRANEID = cwpResult.CRANEID
                cwpResultInfo.CranesPosition = Double.valueOf(df.format(cwpResult.CranesPosition))
                cwpResultInfo.HATCHBWID = cwpResult.HATCHBWID
                cwpResultInfo.HATCHID = cwpResult.HATCHID
                cwpResultInfo.MOVECOUNT = cwpResult.MOVECOUNT
                cwpResultInfo.QDC = cwpResult.QDC
                cwpResultInfo.StartMoveID = cwpResult.StartMoveID
                cwpResultInfo.VESSELID = cwpResult.VESSELD
                cwpResultInfo.WORKINGENDTIME = cwpResult.WORKINGENDTIME
                cwpResultInfo.REALWORKINGSTARTTIME = cwpResult.REALWORKINGSTARTTIME
                cwpResultInfo.WORKINGSTARTTIME = cwpResult.WORKINGSTARTTIME
                cwpResultInfo.endMoveID = cwpResult.EndMoveID
                cwpResultInfo.MOVETYPE = cwpResult.MOVETYPE
                cwpResultInfo.LDULD = cwpResult.mLD

                cwpResultInfo.workingStartTime = new Date(stLong + cwpResult.REALWORKINGSTARTTIME.intValue()*1000)
                cwpResultInfo.workingEndTime = new Date(stLong + cwpResult.WORKINGENDTIME*1000)
                cwpResultInfo.craneWorkStartTime = new Date(stLong + cwpResult.WORKINGSTARTTIME*1000)
                cwpResultInfoList.add(cwpResultInfo)
            }
        }catch (Exception e){
            System.out.println("cwp返回结果数据解析时，发现json数据异常！")
            isError = true;
            e.printStackTrace()
        }
        if(isError) {
            System.out.println("cwp返回结果数据解析失败！")
            return null;
        }else {
            System.out.println("cwp返回结果数据解析成功！")
            return cwpResultInfoList
        }
    }
}
