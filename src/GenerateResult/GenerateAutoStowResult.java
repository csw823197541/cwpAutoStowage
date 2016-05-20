package GenerateResult;

import autoStow.CallAutoStow;
import importDataProcess.ImportData;
import importDataProcess.PreStowageInfoProcess;
import importDataInfo.*;
import utils.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leko on 2016/1/22.
 */
public class GenerateAutoStowResult {

    //调用自动配载
    public static List<AutoStowResultInfo> getAutoStowResult(List<GroupInfo> groupInfoList,List<ContainerInfo> containerInfoList, List<ContainerAreaInfo> containerAreaInfoList, List<PreStowageData> preStowageDataList, List<CwpResultInfo> cwpResultInfoList) {
        List<AutoStowResultInfo> autoStowResultInfoList = new ArrayList<AutoStowResultInfo>();

        //处理在场箱信息
        String containerStr = PreStowageInfoProcess.getContainerString(containerInfoList);
        containerStr = containerStr.substring(0, containerStr.length()-1);

        //处理箱区信息
        String containerAreaStr = PreStowageInfoProcess.getContainerAreaString(containerAreaInfoList);
        containerAreaStr = containerAreaStr.substring(0, containerAreaStr.length()-1);

        //处理预配信息
        String preStowageStr = PreStowageInfoProcess.getPreStowageString(groupInfoList, preStowageDataList);
        preStowageStr = preStowageStr.substring(0, preStowageStr.length()-1);

        try {//将自动配载要用的结果写在文件里，让算法去读这个文件
            FileUtil.writeToFile("toAutoStowData/Container.txt", containerStr);
            FileUtil.writeToFile("toAutoStowData/PreStowage.txt", preStowageStr);
            FileUtil.writeToFile("toAutoStowData/ContainerArea.txt",containerAreaStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //处理cwp输出信息
        String cwpResultStr = PreStowageInfoProcess.getCwpResultString(cwpResultInfoList);
        cwpResultStr = cwpResultStr.substring(0, cwpResultStr.length()-1);

        try {//将自动配载要用的结果写在文件里，让算法去读这个文件
            FileUtil.writeToFile("toAutoStowData/CwpOutput.txt", cwpResultStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String autoStowStr = null;
        if(containerStr != null && containerAreaStr != null && preStowageStr != null && cwpResultStr != null) {
            //调用自动配载算法
            autoStowStr = CallAutoStow.autoStow(containerStr, containerAreaStr, preStowageStr, cwpResultStr);
//            try {
//                Thread.currentThread().sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            System.out.println("自动配载算法返回的结果："+autoStowStr);
            if(autoStowStr != null) {
                autoStowResultInfoList = getAutoStowResult(autoStowStr);
            } else {
                System.out.println("自动配载算法没有返回结果！");
            }
        } else {
            System.out.println("自动配载算法需要的4个参数信息中有空的，不能调用算法！");
        }
        return autoStowResultInfoList;
    }


    public static List<AutoStowResultInfo> getAutoStowResult(String autoStowStr) {

        List<AutoStowResultInfo> autoStowResultInfoList = new ArrayList<>();
        Map<String,String[]> stringMap = new HashMap<String, String[]>();
        try{
            String stowResult = autoStowStr;
            String[] str = stowResult.split("#");
            System.out.println(str.length);
            for(int i=0; i<str.length; i++) {
                String[] weiZi = str[i].split(",")[0].split("%");
                String xiangHao = str[i].split(",")[1];
                String cxw = str[i].split(",")[2];
                String wz = weiZi[0] + "." + weiZi[1] + "." +weiZi[2] + "." +weiZi[3];//key,船上的位置
                AutoStowResultInfo autoStowResultInfo = new AutoStowResultInfo();
                autoStowResultInfo.setVesselPosition(wz);
                String[] value = new String[3];//value,0放箱区位置，1放箱号，2放尺寸
                if(!cxw.equals(" ")) {
                    String[] cangxiangwei = cxw.split("%");
                    value[0] = cangxiangwei[0] + "." + cangxiangwei[1] + "." +cangxiangwei[2] + "." +cangxiangwei[3];
                } else {
                    value[0] = "?";
                }
                if(!xiangHao.equals(" ")) {
                    value[1] = xiangHao;
                } else {
                    value[1] = "?";
                }
                autoStowResultInfo.setAreaPosition(value[0]);
                autoStowResultInfo.setUnitID(value[1]);
                if(Integer.valueOf(weiZi[1]) % 2 != 0) {//倍为基数
                    value[2] = "20";
                } else {
                    value[2] = "40";
                }
                autoStowResultInfo.setSize(value[2]);
                autoStowResultInfoList.add(autoStowResultInfo);
                stringMap.put(wz, value);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        ImportData.autoStowResult = stringMap;
        return autoStowResultInfoList;
    }

}