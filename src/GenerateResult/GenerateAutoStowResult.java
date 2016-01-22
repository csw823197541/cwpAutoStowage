package GenerateResult;

import importData.PreStowageInfoProcess;
import importDataInfo.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leko on 2016/1/22.
 */
public class GenerateAutoStowResult {

    //调用自动配载
    public static List<AutoStowResultInfo> getAutoStowResult(List<GroupInfo> groupInfoList,List<ContainerInfo> containerInfoList, List<ContainerAreaInfo> containerAreaInfoList, List<PreStowageInfo> preStowageInfoList, List<CwpResultInfo> cwpResultInfoList) {
        List<AutoStowResultInfo> autoStowResultInfoList = new ArrayList<AutoStowResultInfo>();

        //处理在场箱信息
        String containerString = PreStowageInfoProcess.getContainerString(containerInfoList);
        //处理箱区信息
        String containerAreaString = PreStowageInfoProcess.getContainerareaString(containerAreaInfoList);
        //处理预配信息
        String preStowageString = PreStowageInfoProcess.getPreStowageString(groupInfoList,preStowageInfoList);
        //处理cwp输出信息
        String cwpResultString = PreStowageInfoProcess.getCwpResultString(cwpResultInfoList);


        return autoStowResultInfoList;
    }
}