package viewFrame;


import importDataInfo.ContainerInfo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by leko on 2016/1/19.
 */
public class ContainerFrame extends JFrame{
    private JPanel panelCenter;
    private JPanel contentPane;
    private JScrollPane scrollPane;
    private JTable tableWQL;

    private List<ContainerInfo> containerInfoList;

    public ContainerFrame(List<ContainerInfo> containerInfoList) {
        this.containerInfoList = containerInfoList;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);//居中显示
        {
            this.panelCenter = new JPanel();
            this.panelCenter.setBorder(new TitledBorder(null, "在场箱", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            this.contentPane = new JPanel();
            this.contentPane.setLayout(new BorderLayout(0, 0));
            this.contentPane.add(this.panelCenter, BorderLayout.CENTER);
            setContentPane(this.contentPane);
            this.panelCenter.setLayout(new BorderLayout(0, 0));
            {
                {
                    this.scrollPane = new JScrollPane();
                    this.panelCenter.add(this.scrollPane, BorderLayout.CENTER);
                    {
                        this.tableWQL = new JTable();
                        this.scrollPane.setViewportView(this.tableWQL);
                        DefaultTableModel tableModel = new DefaultTableModel();

                        //增加列名
                        ArrayList<String> colList = new ArrayList<String>(Arrays.asList("航次","箱号","箱区号","场箱位",  "箱型", "尺寸", "目的港", "箱重","是否危险品","是否冷藏","是否可配载","倒箱时间"));
                        for (String col : colList) {
                            System.out.println(col);
                            tableModel.addColumn(col);
                        }

                        //增加内容
                        List<ContainerInfo> containerInfoList = this.containerInfoList;
                        System.out.print("生成内容");
                        for (ContainerInfo containerInfo : containerInfoList) {
                            Object[] rowData = new Object[12];
                            rowData[0] = containerInfo.getIYC_VOYID();
                            rowData[1] = containerInfo.getIYC_CNTRNO();
                            rowData[2] = containerInfo.getIYC_CNTR_AREA_ID();
                            rowData[3] = containerInfo.getIYC_YLOCATION();
                            rowData[4] = containerInfo.getIYC_CTYPECD();
                            rowData[5] = containerInfo.getIYC_CSZ_CSIZECD();
                            rowData[6] = containerInfo.getIYC_PORTCD();
                            rowData[7] = containerInfo.getIYC_WEIGHT();
                            rowData[8] = containerInfo.getIYC_DNGFG();
                            rowData[9] = containerInfo.getIYC_REFFG();
                            rowData[10] = containerInfo.getIYC_PLANFG();
                            rowData[11] = containerInfo.getIYC_RETIME();
                            tableModel.addRow(rowData);
                        }
                        this.tableWQL.setModel(tableModel);
                    }
                }
            }
        }
    }
}
