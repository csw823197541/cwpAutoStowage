package importDataProcess;

import importDataInfo.*;

import java.util.*;

/**
 * Created by leko on 2016/1/22.
 */
public class PreStowageInfoProcess {
    public static String getContainerString(List<ContainerInfo> containerInfoList) {
        //在场箱信息字符串
        String container="";
        List<ContainerInfo> containerInfoList1;
        containerInfoList1 = containerInfoList;
        for (ContainerInfo containerInfo: containerInfoList1)
        {
            if("Y".equals(containerInfo.getIYC_PLANFG())) {
                String temp="";
                temp+=containerInfo.getIYC_CNTRNO()+",";
                temp+=containerInfo.getIYC_CNTR_AREA_ID()+",";
                temp+=containerInfo.getIYC_VOYID()+",";
                temp+=containerInfo.getIYC_CTYPECD()+",";
                temp+=containerInfo.getIYC_CSZ_CSIZECD()+",";
                temp+=containerInfo.getIYC_PORTCD()+",";
                temp+=containerInfo.getIYC_WEIGHT()+",";
                temp+=containerInfo.getIYC_DNGFG()+",";
                temp+=containerInfo.getIYC_REFFG()+",";
                temp+=containerInfo.getIYC_YLOCATION()+",";
                temp+=containerInfo.getIYC_PLANFG()+",";
                temp+=containerInfo.getIYC_RETIME()+"#";
                container+=temp;
            }
        }
//        System.out.println(container);
        return container;
    }

    public static String getContainerareaString(List<ContainerAreaInfo> containerAreaInfoList){
        //箱区信息字符串
        String containerarea="";
        List<ContainerAreaInfo> containerAreaInfoList1 = containerAreaInfoList;
        for (ContainerAreaInfo containerAreaInfo: containerAreaInfoList1)
        {
            String temp="";
            temp+=containerAreaInfo.getASCBOTTOMSPEED().toString()+",";
            temp+=containerAreaInfo.getASCTOPSPEED().toString()+",";
            temp+=containerAreaInfo.getID().toString()+",";
            temp+=containerAreaInfo.getLOCATIONLB()+",";
            temp+=containerAreaInfo.getLOCATIONLH()+",";
            temp+=containerAreaInfo.getLOCATIONRB()+",";
            temp+=containerAreaInfo.getLOCATIONRH()+",";
            temp+=containerAreaInfo.getVBYNUM().toString()+",";
            temp+=containerAreaInfo.getVTRNUM().toString()+",";
            temp+=containerAreaInfo.getVRWNUM().toString()+",";
            temp+=containerAreaInfo.getSCTYPE()+",";
            temp+=containerAreaInfo.getWORKEFFICIENCYB().toString()+",";
            temp+=containerAreaInfo.getDISPATCHEDWORK().toString()+",";
            temp+=containerAreaInfo.getPREDISPATCHEDWORK().toString()+",";
            temp+=containerAreaInfo.getWORKEFFICIENCYT().toString()+"#";
            containerarea+=temp;
        }
//        System.out.println(containerarea);
        return containerarea;
    }

    public static String getPreStowageString(List<GroupInfo> groupInfoList,List<PreStowageData> preStowageDataList){
        //处理属性值
        List<GroupInfo> groupInfoList1 = groupInfoList;
//        HashMap<String, ArrayList<String>> groupmap = new HashMap<String, ArrayList<String>>();
//        for (GroupInfo groupInfo:groupInfoList1){
//            ArrayList<String> groupattri = new ArrayList<String>();
//            groupattri.add(groupInfo.getPort());
//            groupattri.add(groupInfo.getType());
//            groupattri.add(groupInfo.getSize());
//            groupmap.put(groupInfo.getGroupID(),groupattri);
//        }
//        Map<String, String> groupQuery = new HashMap<>();
//        for(GroupInfo groupInfo : groupInfoList1) {
//            String key = groupInfo.getPort() +"."+groupInfo.getSize() +"."+groupInfo.getType();
//            String value = groupInfo.getGroupID();
//            groupQuery.put(key, value);
//        }

        //预配信息字符串
        String prestowage="";
        List<PreStowageData> preStowageInfoList1 = preStowageDataList;
        for (PreStowageData preStowageData:preStowageInfoList1)
        {
            String temp="";
            temp+=preStowageData.getVHT_ID().toString()+",";
            temp+=preStowageData.getVBY_BAYID().toString()+",";
            temp+=preStowageData.getVTR_TIERNO().toString()+",";
            temp+=preStowageData.getVRW_ROWNO().toString()+",";
//            String GroupID = groupQuery.get(preStowageData.getDSTPORT()+"."+preStowageData.getSIZE()+"."+preStowageData.getCTYPECD());
//            System.out.println(GroupID);
            temp+=preStowageData.getSIZE()+",";
            temp+=preStowageData.getDSTPORT()+",";
            temp+=preStowageData.getCTYPECD()+",";
            temp+=preStowageData.getWEIGHT().toString()+",";
            temp+=preStowageData.getMOVE_ORDER().toString()+"#";
            prestowage+=temp;
        }
//        System.out.println(prestowage);
        return prestowage;
    }

    public static String getCwpResultString(List<CwpResultInfo> cwpResultInfoList) {
        //生成cwp输出结果
        String cwpoutput="";
        List<CwpResultInfo> cwpResultInfoList1 = cwpResultInfoList;
        for (CwpResultInfo cwpResultInfo:cwpResultInfoList1)
        {
            String temp="";
            temp+=cwpResultInfo.getCRANEID().toString()+",";
            temp+=cwpResultInfo.getCranesPosition().toString()+",";
            temp+=cwpResultInfo.getHATCHBWID().toString()+",";
            temp+=cwpResultInfo.getHATCHID().toString()+",";
            temp+=cwpResultInfo.getStartMoveID().toString()+",";
            temp+=cwpResultInfo.getMOVECOUNT().toString()+",";
            temp+=cwpResultInfo.getQDC().toString()+",";
            temp+=cwpResultInfo.getVESSELID().toString()+",";
            temp+=cwpResultInfo.getMOVETYPE().toString()+",";
            temp+="L,";
            temp+=cwpResultInfo.getWORKINGENDTIME().toString()+",";
            temp+=cwpResultInfo.getWORKINGSTARTTIME().toString()+"#";
            cwpoutput+=temp;
        }
//        System.out.println(cwpoutput);
        return cwpoutput;
    }
}