package viewFrame;

import importDataInfo.MoveInfo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

                    //针对特殊的列设置排序器
                    TableRowSorter tableRowSorter = new TableRowSorter(tableModel);

                    tableRowSorter.setComparator(tableWQL.getColumn("gkey").getModelIndex(), new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            //先以@为界分裂
                            String o1s = o1.split("@")[0];
                            String o2s = o2.split("@")[0];
                            String o1d = o1.split("@")[1];
                            String o2d = o2.split("@")[1];
//                            System.out.println(o1s + " " + o1d);
//                            System.out.println(o2s + " " + o2d);
                            if (o1s.equals(o2s)) {//前半截相同,后半截处理为Integer，比对
//                                System.out.println("前半截相同,后半截处理为Integer，比对");
                                int o1i = Integer.valueOf(o1d);
                                int o2i = Integer.valueOf(o2d);
                                return Integer.compare(o1i, o2i);//对比后半截
                            } else {
                                return Collator.getInstance().compare(o1s, o2s);//对比前半截
                            }
                        }
                    });

                    Comparator<Integer> numberComparator = new Comparator<Integer>() {
                        @Override
                        public int compare(Integer o1, Integer o2) {//
                            return o1 - o2;
                        }
                    };

                    tableRowSorter.setComparator(tableWQL.getColumn("moveId").getModelIndex(), numberComparator);
                    tableRowSorter.setComparator(tableWQL.getColumn("WORKINGSTARTTIME").getModelIndex(), numberComparator);

                    tableWQL.setRowSorter(tableRowSorter);
                    //设置排序属性
                    RowSorter.SortKey sortKey = new RowSorter.SortKey(tableWQL.getColumn("gkey").getModelIndex(),SortOrder.ASCENDING);
                    RowSorter.SortKey sortKey1 = new RowSorter.SortKey(tableWQL.getColumn("moveId").getModelIndex(),SortOrder.ASCENDING);
                    RowSorter.SortKey sortKey2 = new RowSorter.SortKey(tableWQL.getColumn("WORKINGSTARTTIME").getModelIndex(),SortOrder.ASCENDING);
                    List<RowSorter.SortKey> sortKeyList = new ArrayList<RowSorter.SortKey>();
                    sortKeyList.add(sortKey);
                    sortKeyList.add(sortKey1);
                    sortKeyList.add(sortKey2);
                    tableRowSorter.setSortKeys(sortKeyList);
                    tableRowSorter.setSortsOnUpdates(true);
                }
            }
        }
    }
}
