package GenerateResult;

import importDataInfo.ContainerInfo;
import importDataInfo.GroupInfo;

import java.util.*;

/**
 * Created by leko on 2016/1/21.
 */
public class GenerateGroupResult {

    //生成属性组
    public static List<GroupInfo> getGroupResult(List<ContainerInfo> containerInfoList){

        List<GroupInfo> groupInfoList = new ArrayList<GroupInfo>();

        Set<String> cportSet = new HashSet<String>();   //包含港口类型
        Set<String> ctypeSet = new HashSet<String>();  //包含箱型类型
        Set<String> csizeSet = new HashSet<String>();   //包含尺寸类型

        System.out.println("开始生成分组属性");
        for (ContainerInfo containerInfo : containerInfoList) {
            String port = containerInfo.getIYCPORTCD();
            cportSet.add(port);                                      //统计港口类型
            String type = containerInfo.getIYCCTYPECD();
            ctypeSet.add(type);                                      //统计箱型类型
            String size = containerInfo.getIYCCSZCSIZECD();
            csizeSet.add(size);                                      //统计尺寸类型
        }

        Integer groupnum=1;
        GroupInfo groupInfo;
        for (String port: cportSet){
            for (String type:ctypeSet){
                for (String size: csizeSet) {
                    String groupID = "G" + groupnum.toString();
                    groupnum++;
                    //向groupInfoList增加数据
                    groupInfo = new GroupInfo();
                    groupInfo.setGroupID(groupID);
                    groupInfo.setPort(port);
                    groupInfo.setSize(size);
                    groupInfo.setType(type);
                    groupInfoList.add(groupInfo);
                }
            }
        }

        return groupInfoList;
    }
}
