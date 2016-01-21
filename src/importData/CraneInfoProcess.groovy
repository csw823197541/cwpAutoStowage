package importData

import groovy.json.JsonSlurper
import importDataInfo.CraneInfo
import importDataInfo.WorkingTimeRange

import java.text.SimpleDateFormat


/**
 * Created by csw on 2016/1/16.
 */
class CraneInfoProcess {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    //Json字符串解析编码
    public static List<CraneInfo> getCraneInfo(String jsonStr) {

        boolean isError = false;
        List<CraneInfo> craneInfoList = new ArrayList<CraneInfo>();
        try{
            def root = new JsonSlurper().parseText(jsonStr)

            assert root instanceof List//根据读入数据的格式，可以直接把json转换成List

            root.each { crane ->
                CraneInfo craneInfo = new CraneInfo()
                assert crane instanceof Map
                craneInfo.CURRENTPOSITION = crane.CURRENTPOSITION
                craneInfo.DISCHARGEEFFICIENCY20 = crane.DISCHARGEEFFICIENCY20
                craneInfo.DISCHARGEEFFICIENCY40 = crane.DISCHARGEEFFICIENCY40
                craneInfo.DISCHARGEEFFICIENCYTWIN = crane.DISCHARGEEFFICIENCYTWIN
                craneInfo.ID = crane.ID
                craneInfo.LOADINGEFFICIENCY20 = crane.LOADINGEFFICIENCY20
                craneInfo.LOADINGEFFICIENCY40 = crane.LOADINGEFFICIENCY40
                craneInfo.LOADINGEFFICIENCYTWIN = crane.LOADINGEFFICIENCYTWIN
                craneInfo.MOVINGRANGEFROM = crane.MOVINGRANGEFROM
                craneInfo.MOVINGRANGETO = crane.MOVINGRANGETO
                craneInfo.NAME = crane.NAME
                craneInfo.SAFESPAN = crane.SAFESPAN
                craneInfo.SEQ = crane.SEQ
                craneInfo.SPEED = crane.SPEED
                craneInfo.WIDTH = crane.WIDTH

                def workingTime = crane.WORKINGTIMERANGES//解析工作时间区间
                assert workingTime instanceof List
                List<WorkingTimeRange> workingTimeRangeList = new ArrayList<WorkingTimeRange>()
                workingTime.each {time->
                    WorkingTimeRange workingTimeRange = new WorkingTimeRange()
                    assert time instanceof Map
                    workingTimeRange.ID = time.ID
                    if(time.WORKSTARTTIME)
                        workingTimeRange.WORKSTARTTIME = sdf.parse(new String(time.WORKSTARTTIME))
                    if(time.WORKENDTIME)
                        workingTimeRange.WORKENDTIME = sdf.parse(new String(time.WORKENDTIME))
                    workingTimeRangeList.add(workingTimeRange)
                }
                craneInfo.WORKINGTIMERANGES = workingTimeRangeList

                craneInfoList.add(craneInfo)
            }
        }catch (Exception e){
            System.out.println("桥机数据解析时，发现json数据异常！")
            isError = true;
            e.printStackTrace()
        }
        if(isError) {
            System.out.println("桥机数据解析失败！")
            return null;
        }else {
            System.out.println("桥机数据解析成功！")
            return craneInfoList
        }
    }

}
