package GenerateResult;

import autoStow.CallAutoStow;
import importDataProcess.ImportData;
import importDataProcess.PreStowageInfoProcess;
import importDataInfo.*;

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
        //处理cwp输出信息
        String cwpResultStr = PreStowageInfoProcess.getCwpResultString(cwpResultInfoList);

        String autoStowStr = null;
        //调用c++
        autoStowStr = CallAutoStow.autoStow(containerStr, containerAreaStr, preStowageStr, cwpResultStr);
        System.out.println("自动配载算法返回的结果："+autoStowStr);
        autoStowResultInfoList = getAutoStowResult(autoStowStr);
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
                System.out.println(wz+"----"+value[0]+"-"+value[1]+"-"+value[2]);
                stringMap.put(wz, value);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        ImportData.autostowresult = stringMap;
        return autoStowResultInfos;
    }

}