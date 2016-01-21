package importData

import groovy.json.JsonSlurper
import importDataInfo.VoyageInfo

import java.text.SimpleDateFormat

/**
 * Created by csw on 2016/1/16.
 */
class VoyageInfoProcess {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    //Json字符串解析编码
    public static List<VoyageInfo> getVoyageInfo(String jsonStr) {

        boolean isError = false;
        List<VoyageInfo> voyageInfoList = new ArrayList<VoyageInfo>();
        try{
            def root = new JsonSlurper().parseText(jsonStr)

            assert root instanceof List//根据读入数据的格式，可以直接把json转换成List

            root.each { voyage ->
                VoyageInfo voyageInfo = new VoyageInfo()
                assert voyage instanceof Map
                voyageInfo.VOTVOYID = voyage.VOTVOYID
                voyageInfo.VESSELID = voyage.VESSELID
                if(voyage.VOTPWKSTTM != null)
                    voyageInfo.VOTPWKSTTM = sdf.parse(voyage.VOTPWKSTTM)
                if(voyage.VOTPWKENTM != null)
                    voyageInfo.VOTPWKENTM = sdf.parse(voyage.VOTPWKENTM)
                voyageInfo.STARTPOSITION = voyage.STARTPOSITION
                voyageInfo.ENDPOSITION = voyage.ENDPOSITION
                voyageInfoList.add(voyageInfo)
            }
        }catch (Exception e){
            System.out.println("航次数据解析时，发现json数据异常！")
            isError = true;
            e.printStackTrace()
        }
        if(isError) {
            System.out.println("航次数据解析失败！")
            return null;
        }else {
            System.out.println("航次数据解析成功！")
            return voyageInfoList
        }
    }
}
