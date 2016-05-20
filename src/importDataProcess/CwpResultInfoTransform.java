package importDataProcess;

import importDataInfo.CwpResultInfo;
import importDataInfo.VoyageInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by csw on 2016/5/19.
 */
public class CwpResultInfoTransform {


    public static List<CwpResultInfo> getTransformResult(List<VoyageInfo> voyageInfoList, List<CwpResultInfo> cwpResultInfoList) {
        List<CwpResultInfo> resultInfoList = new ArrayList<>();

        //对时间进行转换
        Date startDate = voyageInfoList.get(0).getVOTPWKSTTM();
        long startDateMillis = startDate.getTime();//将开始时间转化成秒
        for(CwpResultInfo cwpResultInfo : cwpResultInfoList) {
            cwpResultInfo.setWorkingStartTime(new Date(cwpResultInfo.getWORKINGSTARTTIME()*1000 + startDateMillis));
            cwpResultInfo.setWorkingEndTime(new Date(cwpResultInfo.getWORKINGENDTIME()*1000 + startDateMillis));
            resultInfoList.add(cwpResultInfo);
        }
        return resultInfoList;
    }
}
