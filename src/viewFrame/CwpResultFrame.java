package viewFrame;

import importDataInfo.CraneInfo;
import importDataInfo.CwpResultInfo;
import importDataInfo.HatchInfo;
import importDataProcess.CraneInfoProcess;
import importDataProcess.CwpResultInfoProcess;
import importDataProcess.HatchInfoProcess;
import utils.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Created by csw on 2016/3/13.
 */
public class CwpResultFrame extends JFrame{

    private List<CwpResultInfo> cwpResultInfoList;
    private List<CraneInfo> craneInfoList;
    private List<HatchInfo> hatchInfoList;

    public CwpResultFrame(List<CwpResultInfo> cwpResultInfoList, List<CraneInfo> craneInfoList, List<HatchInfo> hatchInfoList) {
        this.cwpResultInfoList = cwpResultInfoList;
        this.craneInfoList = craneInfoList;
        this.hatchInfoList = hatchInfoList;
        initComponents();
    }

    private void initComponents() {
        this.setTitle("cwp计划结果图");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(1350, 750);
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);// 居中显示
        this.getContentPane().setLayout(new BorderLayout());
        CwpResultPanel cwpResultPanel = new CwpResultPanel(cwpResultInfoList, craneInfoList, hatchInfoList);
        this.getContentPane().add(cwpResultPanel, BorderLayout.CENTER);
    }

//    public static void main(String[] args) {
//        String str = FileUtil.readFileToString(new File("toCwpData/cwpResult.txt")).toString();
//        String str1 = FileUtil.readFileToString(new File("toCwpData/crane.txt")).toString();
//        String str2 = FileUtil.readFileToString(new File("toCwpData/hatch.txt")).toString();
//        List<CwpResultInfo> cwpResultInfoList1 = CwpResultInfoProcess.getCwpResultInfo(str);
//        List<CraneInfo> craneInfoList1 = CraneInfoProcess.getCraneInfo(str1);
//        new CwpResultFrame(cwpResultInfoList1, craneInfoList1, null).setVisible(true);
//    }
}
