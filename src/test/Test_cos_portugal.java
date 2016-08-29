package test;

import GenerateResult.GenerateCwpResult;
import GenerateResult.GenerateGroupResult;
import GenerateResult.GeneratePreStowageFromKnowStowage6;
import importDataInfo.*;
import importDataProcess.*;
import utils.FileUtil;
import viewFrame.*;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by csw on 2016/1/21.
 */
public class Test_cos_portugal {
    public static void main(String[] args) {

        String filePath = "COS_PORTUGAL/";

        String vo = FileUtil.readFileToString(new File(filePath + "SHBTOS.CWPJUnitvoy.txt")).toString();

        String sh = FileUtil.readFileToString(new File(filePath + "SHBTOS.CWPJUnitvesselstructure.txt")).toString();

        String cr = FileUtil.readFileToString(new File(filePath + "crane.txt")).toString();

//        String co = FileUtil.readFileToString(new File(filePath + "containers.txt")).toString();
//        String co = FileUtil.readFileToString(new File("toTempData/tempContainer.txt")).toString();

//        String ca = FileUtil.readFileToString(new File(filePath + "area.txt")).toString();

        //航次
        List<VoyageInfo> voyageInfoList = VoyageInfoProcess.getVoyageInfo(vo);
        VoyageFrame voyageFrame = new VoyageFrame(voyageInfoList);
        voyageFrame.setVisible(true);

        //船舶结构
        List<VesselStructureInfo> vesselStructureInfoList = VesselStructureInfoProcess.getVesselStructureInfo(sh);
        ImportData.vesselStructureInfoList = vesselStructureInfoList;
        VesselStructureFrame vesselStructureFrame = new VesselStructureFrame(vesselStructureInfoList);
        vesselStructureFrame.setVisible(true);

//        //测试产生查询倍位绝对坐标的方法
//        Map<String, Double> bayPositionMap = GenerateBayPositionQuery.getBayPositionMap(voyageInfoList, vesselStructureInfoList);


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
        String pr = FileUtil.readFileToString(new File(filePath + "SHBTOS.CWPJUnitperstowage.txt")).toString();
//        String pr = FileUtil.readFileToString(new File("toTempData/tempPreStowage.txt")).toString();

        List<PreStowageData> preStowageDataList = PreStowageDataProcess.getPreStowageInfo(pr);
        //测试根据实配图生成预配图
        List<PreStowageData> resultList = GeneratePreStowageFromKnowStowage6.getPreStowageResult(preStowageDataList);
//        List<PreStowageData> resultList = GenerateMoveOrder.generateMoveOrder(preStowageDataList, vesselStructureInfoList);
        System.out.println(resultList.size());
        PreStowageDataFrame preStowageFrame2 = new PreStowageDataFrame(resultList);
        preStowageFrame2.setVisible(true);

        //调用cwp算法得到结果
        List<CwpResultInfo> cwpResultInfoList = GenerateCwpResult.getCwpResult(voyageInfoList, vesselStructureInfoList, craneInfoList, resultList);

        //对cwp结果进行处理，将连续作业的cwp块放到一起，以及对作业于某个舱所有的桥机进行编顺序，和某桥机作业舱的顺序
        List<CwpResultInfo> cwpResultInfoTransformList =  CwpResultInfoTransform.getTransformResult(cwpResultInfoList);
        CwpResultFrame cwpResultFrame = new CwpResultFrame(cwpResultInfoTransformList, craneInfoList, null);
        cwpResultFrame.setVisible(true);

        //目前现对cwp结果进行处理，得到每一个Move的输出对象，即对现在算法结果进行拆分
//        List<CwpResultMoveInfo> cwpResultInfoToMoveList = CwpResultInfoToMove.getCwpMoveInfoResult(cwpResultInfoList, preStowageDataList);
//        CwpResultMoveInfoFrame cwpResultMoveInfoFrame = new CwpResultMoveInfoFrame(cwpResultInfoToMoveList);
//        cwpResultMoveInfoFrame.setVisible(true);

//        //测试自动配载算法
//        List<AutoStowResultInfo> autoStowInfoList = GenerateAutoStowResult.getAutoStowResult(groupInfoList, containerInfoList, containerAreaInfoList, resultList, cwpResultInfoToMoveList);
//
//        List<MoveInfo> moveInfoList = GenerateMoveInfoResult.getMoveInfoResult(voyageInfoList, resultList, cwpResultInfoToMoveList, autoStowInfoList);
//        MoveFrame moveFrame = new MoveFrame(moveInfoList);
//        moveFrame.setVisible(true);

    }
}
