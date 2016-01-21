package importData

import groovy.json.JsonSlurper
import importDataInfo.ContainerAreaInfo

/**
 * Created by csw on 2016/1/17.
 */
class ContainerAreaInfoProcess {

    //Json字符串解析编码
    public static List<ContainerAreaInfo> getContainerAreaInfo(String jsonStr) {

        boolean isError = false;
        List<ContainerAreaInfo> containerAreaInfoList = new ArrayList<ContainerAreaInfo>();
        try{
            def root = new JsonSlurper().parseText(jsonStr)

            assert root instanceof List//根据读入数据的格式，可以直接把json转换成List

            root.each { containerArea ->
                ContainerAreaInfo containerAreaInfo = new ContainerAreaInfo()
                assert containerArea instanceof Map
                containerAreaInfo.ASCBOTTOMSPEED = containerArea.ASCBOTTOMSPEED
                containerAreaInfo.ASCTOPSPEED = containerArea.ASCTOPSPEED
                containerAreaInfo.ID = containerArea.ID
                containerAreaInfo.LOCATIONLB = containerArea.LOCATIONLB
                containerAreaInfo.LOCATIONLH = containerArea.LOCATIONLH
                containerAreaInfo.LOCATIONRB = containerArea.LOCATIONRB
                containerAreaInfo.LOCATIONRH = containerArea.LOCATIONRH
                containerAreaInfo.VBYNUM = containerArea.VBYNUM
                containerAreaInfo.VRWNUM = containerArea.VRWNUM
                containerAreaInfo.VTRNUM = containerArea.VTRNUM
                containerAreaInfo.SCTYPE = containerArea.SCTYPE
                containerAreaInfo.WORKEFFICIENCYB = containerArea.WORKEFFICIENCYB
                containerAreaInfo.WORKEFFICIENCYT = containerArea.WORKEFFICIENCYT

                containerAreaInfoList.add(containerAreaInfo)
            }
        }catch (Exception e){
            System.out.println("堆场各箱区数据解析时，发现json数据异常！")
            isError = true;
            e.printStackTrace()
        }
        if(isError) {
            System.out.println("堆场各箱区数据解析失败！")
            return null;
        }else {
            System.out.println("堆场各箱区数据解析成功！")
            return containerAreaInfoList
        }
    }
}
