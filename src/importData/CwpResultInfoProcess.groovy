package importData

import groovy.json.JsonSlurper
import importDataInfo.CwpResultInfo

/**
 * Created by csw on 2016/1/16.
 */
class CwpResultInfoProcess {

    //Json字符串解析编码
    public static List<CwpResultInfo> getCwpResultInfo(String jsonStr) {

        boolean isError = false;
        List<CwpResultInfo> cwpResultInfoList = new ArrayList<CwpResultInfo>();
        try{
            def root = new JsonSlurper().parseText(jsonStr)

            assert root instanceof List//根据读入数据的格式，可以直接把json转换成List

            root.each { cwpResult ->
                CwpResultInfo cwpResultInfo = new CwpResultInfo()
                assert cwpResult instanceof Map
                cwpResultInfo.CRANEID = cwpResult.CRANEID
                cwpResultInfo.HATCHBWID = cwpResult.HATCHBWID
                cwpResultInfo.HATCHID = cwpResult.HATCHID
                cwpResultInfo.MOVECOUNT = cwpResult.MOVECOUNT
                cwpResultInfo.QDC = cwpResult.QDC
                cwpResultInfo.StartMoveID = cwpResult.StartMoveID
                cwpResultInfo.VESSELID = cwpResult.VESSELID
                cwpResultInfo.WORKINGENDTIME = cwpResult.WORKINGENDTIME
                cwpResultInfo.WORKINGSTARTTIME = cwpResult.WORKINGSTARTTIME
                cwpResultInfo.setEndMoveID(cwpResultInfo.getMOVECOUNT() + cwpResultInfo.getStartMoveID())
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
