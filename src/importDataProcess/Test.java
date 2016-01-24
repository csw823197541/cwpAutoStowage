package importDataProcess;

import GenerateResult.*;
import importDataInfo.*;
import utils.FileUtil;
import viewFrame.*;

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
        String ca = FileUtil.readFileToString(new File("E:/TestData/containerArea.json")).toString();

        List<VoyageInfo> voyageInfoList = VoyageInfoProcess.getVoyageInfo(vo);
//        VoyageFrame voyageFrame = new VoyageFrame(voyageInfoList);
//        voyageFrame.setVisible(true);
        List<VesselStructureInfo> vesselStructureInfoList = VesselStructureInfoProcess.getVesselStructureInfo(sh);
//        VesselStructureFrame vesselStructureFrame = new VesselStructureFrame(vesselStructureInfoList);
//        vesselStructureFrame.setVisible(true);
        List<CraneInfo> craneInfoList = CraneInfoProcess.getCraneInfo(cr);
//        CraneFrame craneFrame = new CraneFrame(craneInfoList);
//        craneFrame.setVisible(true);
        List<ContainerInfo> containerInfoList = ContainerInfoProcess.getContainerInfo(co);
//        ContainerFrame containerFrame = new ContainerFrame(containerInfoList);
//        containerFrame.setVisible(true);
        List<ContainerAreaInfo> containerAreaInfoList = ContainerAreaInfoProcess.getContainerAreaInfo(ca);
//        ContainerAreaFrame containerAreaFrame = new ContainerAreaFrame(containerAreaInfoList);
//        containerAreaFrame.setVisible(true);
        List<GroupInfo> groupInfoList = GenerateGroupResult.getGroupResult(containerInfoList);
//        GroupFrame groupFrame = new GroupFrame( groupInfoList);
//        groupFrame.setVisible(true);

        List<PreStowageInfo> preStowageInfoList = GeneratePreStowageResult.getPrestowageResult(groupInfoList, containerInfoList, vesselStructureInfoList);
//        PreStowageFrame preStowageFrame = new PreStowageFrame(preStowageInfoList);
//        preStowageFrame.setVisible(true);
        List<CwpResultInfo> cwpResultInfoList = GenerateCwpResult.getCwpResult(voyageInfoList, vesselStructureInfoList, craneInfoList, preStowageInfoList);
        List<AutoStowResultInfo> autoStowInfoList = GenerateAutoStowResult.getAutoStowResult(groupInfoList, containerInfoList, containerAreaInfoList, preStowageInfoList, cwpResultInfoList);
        List<MoveInfo> moveInfoList = GenerateMoveInfoResult.getMoveInfoResult(cwpResultInfoList,autoStowInfoList);
        MoveFrame moveFrame = new MoveFrame(moveInfoList);
        moveFrame.setVisible(true);

    }
}
