package GenerateResult;

import autoStow.CallAutoStow;
import importDataProcess.ImportData;
import importDataProcess.PreStowageInfoProcess;
import importDataInfo.*;
import utils.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by leko on 2016/1/22.
 */
public class GenerateAutoStowResult {

    //调用自动配载
    public static List<AutoStowResultInfo> getAutoStowResult(List<GroupInfo> groupInfoList,List<ContainerInfo> containerInfoList, List<ContainerAreaInfo> containerAreaInfoList, List<PreStowageInfo> preStowageInfoList, List<CwpResultInfo> cwpResultInfoList) {
        List<AutoStowResultInfo> autoStowResultInfoList = new ArrayList<AutoStowResultInfo>();

        //处理在场箱信息
        String containerStr = PreStowageInfoProcess.getContainerString(containerInfoList);
        //处理箱区信息
        String containerAreaStr = PreStowageInfoProcess.getContainerareaString(containerAreaInfoList);
        //处理预配信息
        String preStowageStr = PreStowageInfoProcess.getPreStowageString(groupInfoList,preStowageInfoList);
        try {//将自动配载要用的结果写在文件里，让算法去读这个文件
            FileUtil.writeToFile("C:/CwpAutoStowData/Container.txt", containerStr);
            FileUtil.writeToFile("C:/CwpAutoStowData/PreStowage.txt", preStowageStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //处理cwp输出信息
        String cwpResultStr = PreStowageInfoProcess.getCwpResultString(cwpResultInfoList);

        String autoStowStr = null;
        if(containerStr != null && containerAreaStr != null && preStowageStr != null && cwpResultStr != null) {
            //调用自动配载算法
            autoStowStr = CallAutoStow.autoStow(containerStr, containerAreaStr, preStowageStr, cwpResultStr);
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

        List<AutoStowResultInfo> autoStowResultInfos = new ArrayList<>();
        HashMap<String,String[]> stringMap = new HashMap<String, String[]>();
        try{
            String stowResult = autoStowStr;
            //System.out.println(stowResult);
            String[] str = stowResult.split("#");
            System.out.println(str.length);
            for(int i=0; i<str.length; i++) {
                String[] weizi = str[i].split(",")[0].split("%");
                String xianghao = str[i].split(",")[1];
                String[] cangxiangwei = str[i].split(",")[2].split("%");
                String wz = weizi[0] + "." + weizi[1] + "." +weizi[2] + "." +weizi[3];//key,船上的位置
                AutoStowResultInfo autoStowResultInfo = new AutoStowResultInfo();
                autoStowResultInfo.setVesselPosition(wz);
                String[] value = new String[3];//value,0放箱区位置，1放箱号，2放尺寸
                value[0] = cangxiangwei[0] + "." + cangxiangwei[1] + "." +cangxiangwei[2] + "." +cangxiangwei[3];
                autoStowResultInfo.setAreaPosition(value[0]);
                value[1] = xianghao;
                autoStowResultInfo.setUnitID(value[1]);
                if(Integer.valueOf(weizi[1]) % 2 != 0) {//倍为基数
                    value[2] = "20";
                } else {
                    value[2] = "40";
                }
                autoStowResultInfo.setSize(value[2]);
                autoStowResultInfos.add(autoStowResultInfo);
//                System.out.println(wz+"----"+value[0]+"-"+value[1]+"-"+value[2]);
                stringMap.put(wz, value);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        ImportData.autostowresult = stringMap;
        return autoStowResultInfos;
    }

}