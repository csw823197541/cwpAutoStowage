package GenerateResult;

import importData.ImportData;
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
        HashMap<String, ArrayList<String>> groupmap = new HashMap<String, ArrayList<String>>();

        Set<String> cportSet = new HashSet<String>();   //包含港口类型
        Set<String> ctypeSet = new HashSet<String>();  //包含箱型类型
        Set<String> csizeSet = new HashSet<String>();   //包含尺寸类型

        System.out.println("开始生成分组属性");
        for (ContainerInfo containerInfo : containerInfoList) {
            String port = containerInfo.getIYC_PORTCD();
            cportSet.add(port);                                      //统计港口类型
            String type = containerInfo.getIYC_CTYPECD();
            ctypeSet.add(type);                                      //统计箱型类型
            String size = containerInfo.getIYC_CSZ_CSIZECD();
            csizeSet.add(size);                                      //统计尺寸类型
        }

        Integer groupnum=1;
        GroupInfo groupInfo;
        for (String port: cportSet){
            for (String type:ctypeSet){
                for (String size: csizeSet) {
                    //向hashmap增加数据
                    ArrayList<String> groupattri = new ArrayList<String>();
                    groupattri.add(port);
                    groupattri.add(type);
                    groupattri.add(size);
                    String groupID = "G" + groupnum.toString();
                    groupmap.put(groupID, groupattri);
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
        ImportData.groupmap = groupmap;
        return groupInfoList;
    }
}