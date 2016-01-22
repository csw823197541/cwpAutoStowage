package importData;
import GenerateResult.GenerateCwpResult;
import GenerateResult.GenerateGroupResult;
import GenerateResult.GeneratePreStowageResult;
import importDataInfo.*;
import utils.FileUtil;
import viewFrame.ContainerAreaFrame;
import viewFrame.ContainerFrame;

import java.io.File;
import java.util.List;

/**
 * Created by csw on 2016/1/21.
 */
public class Test {
    public static void main(String[] args) {

        String vo = FileUtil.readFileToString(new File("E:/TestData/Voyage.json")).toString();
        String sh = FileUtil.readFileToString(new File("E:/TestData/shipstructure.json")).toString();
        String cr = FileUtil.readFileToString(new File("E:/TestData/crane.json")).toString();
        String co = FileUtil.readFileToString(new File("E:/TestData/container.json")).toString();
//        String c = FileUtil.readFileToString(new File("E:/TestData/crane.json")).toString();

        List<VoyageInfo> voyageInfoList = VoyageInfoProcess.getVoyageInfo(vo);
        List<VesselStructureInfo> vesselStructureInfoList = VesselStructureInfoProcess.getVesselStructureInfo(sh);
        List<CraneInfo> craneInfoList = CraneInfoProcess.getCraneInfo(cr);
        List<ContainerInfo> containerInfoList = ContainerInfoProcess.getContainerInfo(co);
        List<GroupInfo> groupInfoList = GenerateGroupResult.getGroupResult(containerInfoList);
        List<PreStowageInfo> preStowageInfoList = GeneratePreStowageResult.getPrestowageResult(groupInfoList, containerInfoList, vesselStructureInfoList);
        List<CwpResultInfo> cwpResultInfoList = GenerateCwpResult.getCwpResult(voyageInfoList, vesselStructureInfoList, craneInfoList, preStowageInfoList);

    }
}
