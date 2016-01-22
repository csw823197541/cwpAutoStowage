package viewFrame;

import importDataInfo.PreStowageInfo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by leko on 2016/1/21.
 */
public class PrestowageFrame extends JFrame{
    private JPanel panelCenter;
    private JPanel contentPane;
    private JScrollPane scrollPane;
    private JTable tableWQL;

    private JLabel jlFilter;
    private JPanel jpFilter;
    private JTextField jfFilter;
    private JButton btn;

    private List<PreStowageInfo> preStowageInfoList;

    public PrestowageFrame(List<PreStowageInfo> preStowageInfoList) {
        this.preStowageInfoList = preStowageInfoList;
        initComponents();
    }
    private void initComponents(){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);//居中显示
        {
            jlFilter = new JLabel("按舱号查询:");
            jfFilter = new JTextField(10);
            btn = new JButton("查询");

            jpFilter = new JPanel(new FlowLayout());
            jpFilter.add(jlFilter);
            jpFilter.add(jfFilter);
            jpFilter.add(btn);

            this.panelCenter = new JPanel();
            this.panelCenter.setBorder(new TitledBorder(null, "预配信息", TitledBorder.LEADING, TitledBorder.TOP, null, null));

            this.contentPane = new JPanel();
            this.contentPane.setLayout(new BorderLayout(0, 0));
            this.contentPane.add(jpFilter, BorderLayout.NORTH);
            this.contentPane.add(this.panelCenter, BorderLayout.CENTER);
            setContentPane(this.contentPane);
            this.panelCenter.setLayout(new BorderLayout(0, 0));
            {
                this.scrollPane = new JScrollPane();
                this.panelCenter.add(this.scrollPane,BorderLayout.CENTER);
                {
                    this.tableWQL = new JTable();
                    this.scrollPane.setViewportView(this.tableWQL);
                    TableModel tableModel = new TableModel();

                    //增加列名
                    ArrayList<String> colList = new ArrayList<String>(Arrays.asList("舱号","倍号", "层号", "排号","尺寸","属性组","重量等级","MoveOrder"));
                    for (String col : colList) {
                        System.out.println(col);
                        tableModel.addColumn(col);
                    }

                    //增加内容
                    Object[] rowData = new Object[8];
                    for (PreStowageInfo preStowageInfo:preStowageInfoList)
                    {
                        rowData[0] = preStowageInfo.getVHT_ID();
                        rowData[1] = preStowageInfo.getVBY_BAYID();
                        rowData[2] = preStowageInfo.getVTR_TIERNO();
                        rowData[3] = preStowageInfo.getVRW_ROWNO();
                        rowData[4] = preStowageInfo.getSIZE();
                        rowData[5] = preStowageInfo.getGROUP_ID();
                        rowData[6] = preStowageInfo.getWEIGHT();
                        rowData[7] = preStowageInfo.getMOVE_ORDER();
                        //System.out.println(rowData[0]+" "+rowData[1]+" "+rowData[2]+" "+rowData[3]);
                        tableModel.addRow(rowData);
                    }
                    this.tableWQL.setModel(tableModel);


                }
            }

            final TableRowSorter<javax.swing.table.TableModel> sorter = new TableRowSorter<javax.swing.table.TableModel>(
                    tableWQL.getModel());
            tableWQL.setRowSorter(sorter);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String text = jfFilter.getText();
                    if(text.length() == 0){
                        sorter.setRowFilter(null);
                    }else {
                        sorter.setRowFilter(RowFilter.regexFilter("^"+text+"$", 0));//按表格第一例筛选
                    }
                }
            });
        }
    }
}
