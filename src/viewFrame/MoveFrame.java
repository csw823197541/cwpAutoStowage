package viewFrame;

import importDataInfo.MoveInfo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by leko on 2016/1/22.
 */
public class MoveFrame extends JFrame {
    private JPanel panelCenter;
    private JPanel contentPane;
    private JScrollPane scrollPane;
    private JTable tableWQL;

    private List<MoveInfo> moveInfoList;


    public MoveFrame(List<MoveInfo> moveInfoList) {
        this.moveInfoList = moveInfoList;
        initComponents();
    }

    private void initComponents(){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);//居中显示
        {
            this.panelCenter = new JPanel();
            this.panelCenter.setBorder(new TitledBorder(null, "装船指令", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            this.contentPane = new JPanel();
            this.contentPane.setLayout(new BorderLayout(0, 0));
            this.contentPane.add(this.panelCenter, BorderLayout.CENTER);
            setContentPane(this.contentPane);
            this.panelCenter.setLayout(new BorderLayout(0, 0));
            {
                this.scrollPane = new JScrollPane();
                this.panelCenter.add(this.scrollPane,BorderLayout.CENTER);
                {
                    this.tableWQL = new JTable();
                    this.scrollPane.setViewportView(this.tableWQL);
                    DefaultTableModel tableModel = new DefaultTableModel();

                    //增加列名
                    List<String> colList = MoveInfo.getFiledsInfo();
                    for (String col : colList) {
                        System.out.println(col);
                        tableModel.addColumn(col);
                    }

                    //增加内容
                    System.out.print("生成内容");
                    for (MoveInfo moveInfo:this.moveInfoList){
                        Object[] rowData = new Object[9];
                        rowData[0] = moveInfo.getGkey();
                        rowData[1] = moveInfo.getWORKINGSTARTTIME();
                        rowData[2] = moveInfo.getBatchId();
                        rowData[3] = moveInfo.getMoveId();
                        rowData[4] = moveInfo.getMoveKind();
                        rowData[5] = moveInfo.getUnitId();
                        rowData[6] = moveInfo.getUnitLength();
                        rowData[7] = moveInfo.getExFromPosition();
                        rowData[8] = moveInfo.getExToPosition();
                        tableModel.addRow(rowData);
                    }
                    this.tableWQL.setModel(tableModel);
                }
            }
        }
    }
}
