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
public class Test1 {
    public static void main(String[] args) {

        String vo = FileUtil.readFileToString(new File("NewTestData1/SHBTOS.CWPJUnitvoy.txt")).toString();
        String sh = FileUtil.readFileToString(new File("NewTestData1/SHBTOS.CWPJUnitvesselstructure.txt")).toString();
//        String cr = FileUtil.readFileToString(new File("NewTestData/SHBTOS.CWPJUnitqcInfo.json")).toString();
        String cr = FileUtil.readFileToString(new File("NewTestData1/crane1.txt")).toString();
        String co = FileUtil.readFileToString(new File("NewTestData1/SHBTOS.CWPJUnitcontainers.txt")).toString();
        String ca = FileUtil.readFileToString(new File("NewTestData1/SHBTOS.CWPJUnitarea.txt")).toString();

//        //航次
        List<VoyageInfo> voyageInfoList = VoyageInfoProcess.getVoyageInfo(vo);
//        VoyageFrame voyageFrame = new VoyageFrame(voyageInfoList);
//        voyageFrame.setVisible(true);
        //船舶结构
        List<VesselStructureInfo> vesselStructureInfoList = VesselStructureInfoProcess.getVesselStructureInfo(sh);
//        VesselStructureFrame vesselStructureFrame = new VesselStructureFrame(vesselStructureInfoList);
//        vesselStructureFrame.setVisible(true);
        ImportData.vesselStructureInfoList = vesselStructureInfoList;
//        //桥机
        List<CraneInfo> craneInfoList = CraneInfoProcess.getCraneInfo(cr);
//        CraneFrame craneFrame = new CraneFrame(craneInfoList);
//        craneFrame.setVisible(true);
//        //在场箱
        List<ContainerInfo> containerInfoList = ContainerInfoProcess.getContainerInfo(co);
//        ContainerFrame containerFrame = new ContainerFrame(containerInfoList);
//        containerFrame.setVisible(true);
        //箱区
//        List<ContainerAreaInfo> containerAreaInfoList = ContainerAreaInfoProcess.getContainerAreaInfo(ca);
//        ContainerAreaFrame containerAreaFrame = new ContainerAreaFrame(containerAreaInfoList);
//        containerAreaFrame.setVisible(true);
//        //属性组
//        List<GroupInfo> groupInfoList = GenerateGroupResult.getGroupResult(containerInfoList);
//        GroupFrame groupFrame = new GroupFrame( groupInfoList);
//        groupFrame.setVisible(true);
        //实配图
        String pr = FileUtil.readFileToString(new File("NewTestData1/SHBTOS.CWPJUnitperstowage.txt")).toString();
        List<PreStowageData> preStowageDataList = PreStowageDataProcess.getPreStowageInfo(pr);
//        PreStowageDataFrame preStowageFrame1 = new PreStowageDataFrame(preStowageDataList);
//        preStowageFrame1.setVisible(true);
        //测试根据实配图生成预配图
        List<PreStowageData> resultList = GeneratePreStowageFromKnowStowage6.getPreStowageResult(preStowageDataList);
//        System.out.println(resultList.size());
        PreStowageDataFrame preStowageFrame2 = new PreStowageDataFrame(resultList);
        preStowageFrame2.setVisible(true);
        //测试两个绝对位置
//        GenerateCwpResult.getHatchPositionInfo(voyageInfoList, vesselStructureInfoList);
        //测试生成属性组和moveCount
//        List<PreStowageData> resultList2 = GenerateMoveCountAndGroupId.getMoveCountAndGroupId(resultList);
//        PreStowageDataFrame preStowageFrame2 = new PreStowageDataFrame(resultList2);
//        preStowageFrame2.setVisible(true);
        //调用cwp算法得到结果
        List<CwpResultInfo> cwpResultInfoList = GenerateCwpResult1.getCwpResult(voyageInfoList, vesselStructureInfoList, craneInfoList, resultList);
        CwpResultFrame cwpResultFrame = new CwpResultFrame(cwpResultInfoList, craneInfoList, null);
        cwpResultFrame.setVisible(true);
        //测试自动配载算法
//        String cwpResultStr = FileUtil.readFileToString(new File("E:/NewTestData/cwpBlock.txt")).toString();
//        List<CwpResultInfo> cwpResultInfoList = CwpResultInfoProcess.getCwpResultInfo(cwpResultStr);
//        List<AutoStowResultInfo> autoStowInfoList = GenerateAutoStowResult.getAutoStowResult(groupInfoList, containerInfoList, containerAreaInfoList, resultList, cwpResultInfoList);
//        List<MoveInfo> moveInfoList = GenerateMoveInfoResult.getMoveInfoResult(cwpResultInfoList,autoStowInfoList);
//        MoveFrame moveFrame = new MoveFrame(moveInfoList);
//        moveFrame.setVisible(true);

    }
}
