package test;

import GenerateResult.GenerateCwpResult;
import GenerateResult.GenerateGroupResult;
import GenerateResult.GenerateMoveOrder;
import GenerateResult.GeneratePreStowageFromKnowStowage6;
import importDataInfo.*;
import importDataProcess.*;
import utils.FileUtil;
import viewFrame.CraneFrame;
import viewFrame.*;
import viewFrame.PreStowageDataFrame;
import viewFrame.VoyageFrame;

import java.io.File;
import java.util.List;

/**
 * Created by csw on 2016/1/21.
 */
public class Test30 {

    public static String fileResult = "";

    public static void main(String[] args) {

        String filePath = "5.25data\\15791（P）\\";

        File file = new File(filePath);
        File[] tempList = file.listFiles();
        System.out.println("该目录下对象个数：" + tempList.length);
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                System.out.println("文件：" + tempList[i]);
            }
            if (tempList[i].isDirectory()) {
                filePath = tempList[i] + "\\Json\\";
                fileResult = tempList[i] + "";
                System.out.println("文件夹：" + filePath);
                String vo = FileUtil.readFileToString(new File(filePath + "SHBTOS.CWPJUnitvoy.txt")).toString();
                String sh = FileUtil.readFileToString(new File(filePath + "SHBTOS.CWPJUnitvesselstructure.txt")).toString();
                String cr = FileUtil.readFileToString(new File("5.25data/13445（P）/HAMBURG BRI/Json/crane1.txt")).toString();
                String str = Test30.fileResult;
                str = str.split("\\\\")[1] + "\\" +str.split("\\\\")[2];
                str = "C:\\Users\\csw\\Desktop\\30\\" + str;
                System.out.println(str);
//        //航次
                List<VoyageInfo> voyageInfoList = VoyageInfoProcess.getVoyageInfo(vo);
//        VoyageFrame voyageFrame = new VoyageFrame(voyageInfoList);
//        voyageFrame.setVisible(true);

                //船舶结构
                List<VesselStructureInfo> vesselStructureInfoList = VesselStructureInfoProcess.getVesselStructureInfo(sh);
                ImportData.vesselStructureInfoList = vesselStructureInfoList;
//        VesselStructureFrame vesselStructureFrame = new VesselStructureFrame(vesselStructureInfoList);
//        vesselStructureFrame.setVisible(true);

//        //桥机
                List<CraneInfo> craneInfoList = CraneInfoProcess.getCraneInfo(cr);
//        CraneFrame craneFrame = new CraneFrame(craneInfoList);
//        craneFrame.setVisible(true);

                //实配图
                String pr = FileUtil.readFileToString(new File(filePath + "SHBTOS.CWPJUnitperstowage.txt")).toString();
                List<PreStowageData> preStowageDataList = PreStowageDataProcess.getPreStowageInfo(pr);
//        PreStowageDataFrame preStowageFrame1 = new PreStowageDataFrame(preStowageDataList);
//        preStowageFrame1.setVisible(true);

        //测试根据实配图生成预配图
//      List<PreStowageData> resultList = GeneratePreStowageFromKnowStowage6.getPreStowageResult(preStowageDataList);
        List<PreStowageData> resultList = GenerateMoveOrder.generateMoveOrder(preStowageDataList, vesselStructureInfoList);
      PreStowageDataFrame preStowageFrame2 = new PreStowageDataFrame(resultList);
      preStowageFrame2.setVisible(true);

                //测试两个绝对位置
//        GenerateCwpResult.getHatchPositionInfo(voyageInfoList, vesselStructureInfoList);

                //调用cwp算法得到结果
        List<CwpResultInfo> cwpResultInfoList = GenerateCwpResult.getCwpResult(voyageInfoList, vesselStructureInfoList, craneInfoList, resultList);
//      CwpResultFrame cwpResultFrame = new CwpResultFrame(cwpResultInfoList, craneInfoList, null);
//      cwpResultFrame.setVisible(true);

        //对cwp结果进行处理，将连续作业的cwp块放到一起，以及对作业于某个舱所有的桥机进行编顺序，和某桥机作业舱的顺序
        List<CwpResultInfo> cwpResultInfoTransformList = CwpResultInfoTransform.getTransformResult(cwpResultInfoList);
        CwpResultFrame cwpResultFrame = new CwpResultFrame(cwpResultInfoTransformList, craneInfoList, null);
        cwpResultFrame.setVisible(true);
            }
        }


    }
}
