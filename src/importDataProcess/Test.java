package importDataProcess;
import GenerateResult.*;
import importDataInfo.*;
import utils.FileUtil;
import viewFrame.MoveFrame;

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
//        String c = FileUtil.readFileToString(new File("E:/TestData/crane.json")).toString();

        List<VoyageInfo> voyageInfoList = VoyageInfoProcess.getVoyageInfo(vo);
        List<VesselStructureInfo> vesselStructureInfoList = VesselStructureInfoProcess.getVesselStructureInfo(sh);
        List<CraneInfo> craneInfoList = CraneInfoProcess.getCraneInfo(cr);
        List<ContainerInfo> containerInfoList = ContainerInfoProcess.getContainerInfo(co);
        List<ContainerAreaInfo> containerAreaInfoList = ContainerAreaInfoProcess.getContainerAreaInfo(ca);
        List<GroupInfo> groupInfoList = GenerateGroupResult.getGroupResult(containerInfoList);
//        GroupFrame groupFrame = new GroupFrame( groupInfoList);
//        groupFrame.setVisible(true);

        List<PreStowageInfo> preStowageInfoList = GeneratePreStowageResult.getPrestowageResult(groupInfoList, containerInfoList, vesselStructureInfoList);
        List<CwpResultInfo> cwpResultInfoList = GenerateCwpResult.getCwpResult(voyageInfoList, vesselStructureInfoList, craneInfoList, preStowageInfoList);
        List<AutoStowResultInfo> autostowInfoList = GenerateAutoStowResult.getAutoStowResult(groupInfoList,containerInfoList,containerAreaInfoList,preStowageInfoList,cwpResultInfoList);
        //List<AutoStowResultInfo> autostowInfoList = new ArrayList<AutoStowResultInfo>();
        List<MoveInfo> moveInfoList = GenerateMoveInfoResult.getMoveInfoResult(cwpResultInfoList,autostowInfoList);
        MoveFrame moveFrame = new MoveFrame(moveInfoList);
        moveFrame.setVisible(true);

    }
}
