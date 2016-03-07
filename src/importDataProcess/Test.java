package importDataProcess;

import GenerateResult.GenerateCwpResult;
import GenerateResult.GenerateMoveCountAndGroupId;
import GenerateResult.GeneratePreStowageFromKnowStowage;
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

        String vo = FileUtil.readFileToString(new File("E:/NewTestData/SHBTOS.CWPJUnitvoy.json")).toString();
        String sh = FileUtil.readFileToString(new File("E:/NewTestData/SHBTOS.CWPJUnitvesselstructure.json")).toString();
        String cr = FileUtil.readFileToString(new File("E:/NewTestData/SHBTOS.CWPJUnitqcInfo.json")).toString();
        String co = FileUtil.readFileToString(new File("E:/NewTestData/SHBTOS.CWPJUnitcontainers.json")).toString();
        String ca = FileUtil.readFileToString(new File("E:/NewTestData/SHBTOS.CWPJUnitarea.json")).toString();

//        //航次
        List<VoyageInfo> voyageInfoList = VoyageInfoProcess.getVoyageInfo(vo);
//        VoyageFrame voyageFrame = new VoyageFrame(voyageInfoList);
//        voyageFrame.setVisible(true);
        //船舶结构
        List<VesselStructureInfo> vesselStructureInfoList = VesselStructureInfoProcess.getVesselStructureInfo(sh);
//        VesselStructureFrame vesselStructureFrame = new VesselStructureFrame(vesselStructureInfoList);
//        vesselStructureFrame.setVisible(true);
//        //桥机
        List<CraneInfo> craneInfoList = CraneInfoProcess.getCraneInfo(cr);
        CraneFrame craneFrame = new CraneFrame(craneInfoList);
        craneFrame.setVisible(true);
//        //在场箱
//        List<ContainerInfo> containerInfoList = ContainerInfoProcess.getContainerInfo(co);
//        ContainerFrame containerFrame = new ContainerFrame(containerInfoList);
//        containerFrame.setVisible(true);
//        //箱区
//        List<ContainerAreaInfo> containerAreaInfoList = ContainerAreaInfoProcess.getContainerAreaInfo(ca);
//        ContainerAreaFrame containerAreaFrame = new ContainerAreaFrame(containerAreaInfoList);
//        containerAreaFrame.setVisible(true);
//        //属性组
//        List<GroupInfo> groupInfoList = GenerateGroupResult.getGroupResult(containerInfoList);
//        GroupFrame groupFrame = new GroupFrame( groupInfoList);
//        groupFrame.setVisible(true);
        //实配图
        String pr = FileUtil.readFileToString(new File("E:/NewTestData/preStowage.json")).toString();
        List<PreStowageData> preStowageDataList = PreStowageDataProcess.getPreStowageInfo(pr);
//        PreStowageDataFrame preStowageFrame1 = new PreStowageDataFrame(preStowageInfoList1);
//        preStowageFrame1.setVisible(true);
        //测试生成预配图
        List<PreStowageData> resultList = GeneratePreStowageFromKnowStowage.getPreStowageResult(preStowageDataList);
//        System.out.println(resultList.size());
//        PreStowageDataFrame preStowageFrame1 = new PreStowageDataFrame(resultList);
//        preStowageFrame1.setVisible(true);
        GenerateCwpResult.getHatchPositionInfo(voyageInfoList, vesselStructureInfoList);
//        List<PreStowageData> resultList2 = GenerateMoveCountAndGroupId.getMoveCountAndGroupId(resultList);
//        PreStowageDataFrame preStowageFrame2 = new PreStowageDataFrame(resultList2);
//        preStowageFrame2.setVisible(true);
        //调用cwp算法得到结果
        List<CwpResultInfo> cwpResultInfoList = GenerateCwpResult.getCwpResult(voyageInfoList, vesselStructureInfoList, craneInfoList, preStowageDataList);
//        List<AutoStowResultInfo> autoStowInfoList = GenerateAutoStowResult.getAutoStowResult(groupInfoList, containerInfoList, containerAreaInfoList, preStowageInfoList, cwpResultInfoList);
//        List<MoveInfo> moveInfoList = GenerateMoveInfoResult.getMoveInfoResult(cwpResultInfoList,autoStowInfoList);
//        MoveFrame moveFrame = new MoveFrame(moveInfoList);
//        moveFrame.setVisible(true);

    }
}
