package importDataProcess

import groovy.json.JsonSlurper
import importDataInfo.ContainerInfo

/**
 * 解析在场箱信息
 * Created by csw on 2016/1/15.
 */
class ContainerInfoProcess {

    //Json字符串解析编码
    public static List<ContainerInfo> getContainerInfo(String jsonStr) {

        boolean isError = false;
        List<ContainerInfo> containerInfoList = new ArrayList<ContainerInfo>();
        try{
            def root = new JsonSlurper().parseText(jsonStr)
            assert root instanceof List//根据读入数据的格式，可以直接把json转换成List
            root.each {container->
                ContainerInfo containerInfo = new ContainerInfo()
                assert container instanceof Map
                containerInfo.IYC_CNTRNO = container.IYCCNTRNO
                containerInfo.IYC_CNTR_AREA_ID = container.IYCCNTRAREAID
                containerInfo.IYC_VOYID = Integer.valueOf(container.IYCEVOYID)
                containerInfo.IYC_CTYPECD = container.IYCCTYPECD
                containerInfo.IYC_CSZ_CSIZECD = container.IYCCSZCSIZECD
                containerInfo.IYC_PORTCD = container.IYCPOTDSTPORT//目的港
                containerInfo.IYC_WEIGHT = Integer.valueOf(container.IYCWEIGHT)
                containerInfo.IYC_DNGFG = container.IYCDNGFG
                containerInfo.IYC_REFFG = container.IYCREFFG
                containerInfo.IYC_YLOCATION = container.IYCYLOCATION
                containerInfo.IYC_PLANFG = container.IYCPLANFG
                containerInfo.IYC_RETIME = container.IYCRETIME
                containerInfoList.add(containerInfo)
            }
        }
        catch (Exception e){
            System.out.println("在场箱信息解析时，发现json数据异常！")
            isError = true;
            e.printStackTrace()
        }
        if(isError) {
            System.out.println("在场箱信息解析失败！")
            return null;
        }else {
            System.out.println("在场箱信息解析成功！")
            return containerInfoList
        }
    }

}
