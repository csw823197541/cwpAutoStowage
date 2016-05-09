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
            if("Y".equals(containerInfo.getIYCPLANFG())) {
                String temp="";
                temp+=containerInfo.getIYCCNTRNO()+",";
                temp+=containerInfo.getIYCCNTRAREAID()+",";
                temp+=containerInfo.getIYCVOYID()+",";
                temp+=containerInfo.getIYCCTYPECD()+",";
                temp+=containerInfo.getIYCCSZCSIZECD()+",";
                temp+=containerInfo.getIYCPORTCD()+",";
                temp+=containerInfo.getIYCWEIGHT()+",";
                temp+=containerInfo.getIYCDNGFG()+",";
                temp+=containerInfo.getIYCREFFG()+",";
                temp+=containerInfo.getIYCYLOCATION()+",";
                temp+=containerInfo.getIYCPLANFG()+",";
                temp+=containerInfo.getIYCRETIME()+"#";
                container+=temp;
            }
        }
        return container;
    }

    public static String getContainerAreaString(List<ContainerAreaInfo> containerAreaInfoList){
        //箱区信息字符串
        String containerArea="";
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
            containerArea+=temp;
        }
        return containerArea;
    }

    public static String getPreStowageString(List<GroupInfo> groupInfoList,List<PreStowageData> preStowageDataList){

        //预配信息字符串
        String preStowage="";
        List<PreStowageData> preStowageInfoList1 = preStowageDataList;
        for (PreStowageData preStowageData:preStowageInfoList1)
        {
            String temp="";
            temp+=preStowageData.getVHTID().toString()+",";
            temp+=preStowageData.getVBYBAYID().toString()+",";
            temp+=preStowageData.getVTRTIERNO().toString()+",";
            temp+=preStowageData.getVRWROWNO().toString()+",";
            temp+=preStowageData.getSIZE()+",";
            temp+=preStowageData.getDSTPORT()+",";
            temp+=preStowageData.getCTYPECD()+",";
            temp+=preStowageData.getWEIGHT().toString()+",";
            temp+=preStowageData.getMOVEORDER().toString()+"#";
            preStowage+=temp;
        }
        return preStowage;
    }

    public static String getCwpResultString(List<CwpResultInfo> cwpResultInfoList) {
        //生成cwp输出结果
        String cwpOutput="";
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
            temp+=cwpResultInfo.getLDULD()+",";
            temp+=cwpResultInfo.getWORKINGENDTIME().toString()+",";
            temp+=cwpResultInfo.getREALWORKINGSTARTTIME().toString()+"#";
            cwpOutput+=temp;
        }
        return cwpOutput;
    }
}