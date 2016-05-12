package viewFrame;
import javax.swing.*;
import java.awt.*;
import java.util.*;

import importDataInfo.VesselStructureInfo;
import importDataInfo.AutoStowResultInfo;
import importDataProcess.ImportData;

/**
 * Created by ding on 2016/3/13 0013.
 */
public class ContainerImageFrame extends JFrame{
    private JPanel contentPanel;
    private JLabel boardLabel;
    private JScrollPane scrollPane;
    private JPanel forScrolPanel;
    private JLabel title;
    private java.util.List<VesselStructureInfo> vesselStructureInfoList;
    private java.util.List<AutoStowResultInfo> autoStowInfoList;
    private int bayID;

    Map<String,String[]> resultInBay = new HashMap<>();//存储当前倍里的配载结果

    public  ContainerImageFrame(java.util.List<VesselStructureInfo> vesselStructureInfoList,java.util.List<AutoStowResultInfo> autoStowInfoList,int bayID){
       this.vesselStructureInfoList = vesselStructureInfoList;
        this.autoStowInfoList = autoStowInfoList;
        this.bayID = bayID;
        initComponents(bayID);
    }

    public void initComponents(int bayID){
        Container c = getContentPane();
//        c.add(contentPanel);
        this.setTitle("第"+bayID+"倍倍位概图");
        this.setLayout(new BorderLayout());
        this.setSize(1500,900);

        //java.util.List<VesselStructureInfo> vesselStructureInfos = ImportData.vesselStructureInfoList;
        Integer[] rowData = new Integer[3];
        int maxTierUnder = 0;
        int maxTierAbove = 0;
        int maxRowUnder = 0;
        int maxRowAbove = 0;

        Map<String,String[]> autoStowResult = ImportData.autoStowResult;        //自动配载结果
        //统计选定bay的数据：层和拍
        for (VesselStructureInfo vesselStructureInfo : vesselStructureInfoList) {
           // rowData[4] = Integer.valueOf(vesselStructureInfo.getVHTID());      //舱次
            rowData[0] = Integer.valueOf(vesselStructureInfo.getVBYBAYID());     //倍
            rowData[1] = Integer.valueOf(vesselStructureInfo.getVTRTIERNO());    //层
            rowData[2] = Integer.valueOf(vesselStructureInfo.getVRWROWNO());     //排

            if( rowData[0] == bayID){

                //奇数倍
                String vesselLoc = vesselStructureInfo.getVHTID() +"."+ vesselStructureInfo.getVBYBAYID() + "."+vesselStructureInfo.getVTRTIERNO() +"."+ vesselStructureInfo.getVRWROWNO();
                resultInBay.put(vesselStructureInfo.getVTRTIERNO()+"."+vesselStructureInfo.getVRWROWNO(),autoStowResult.get(vesselLoc));//存储配载结果

                // 偶数倍
                int bayPlusOne = 0;
                //这里默认只有13是2倍，57是6倍这种情况。不存在35是4倍这种情况
                if(rowData[0]%4==1){
                     bayPlusOne = rowData[0]+1;
                }else if(rowData[0]%4==3){
                     bayPlusOne = rowData[0]-1;
                }
                String vesselLocInEvenBay = vesselStructureInfo.getVHTID() +"."+ String.format("%02d",bayPlusOne) + "."+vesselStructureInfo.getVTRTIERNO() +"."+ vesselStructureInfo.getVRWROWNO();
                if(autoStowResult.containsKey(vesselLocInEvenBay)){
                    resultInBay.put(vesselStructureInfo.getVTRTIERNO()+"."+vesselStructureInfo.getVRWROWNO(),autoStowResult.get(vesselLocInEvenBay));//将偶数倍大箱子的配载结果记录显示
                }


                //记录甲板上下的最高排数
                if( rowData[1] < 80 &&  rowData[1] >= 0 && maxRowUnder < rowData[2]){
                    maxRowUnder = rowData[2];
                }else if(rowData[1] > 80 && maxRowAbove <rowData[2]){
                    maxRowAbove = rowData[2];
                }
                //记录甲板上下的最高层数
                if( rowData[1] < 80 &&  rowData[1] >= 0 && maxTierUnder < rowData[1]/2){
                    maxTierUnder = rowData[1]/2;
                }else if(rowData[1] > 80 && maxTierAbove < (rowData[1]-80)/2){
                    maxTierAbove = (rowData[1]-80)/2;
                }
            }
        }

        //甲板字样
        boardLabel = new JLabel();
        boardLabel.setFont(new Font("宋体",Font.BOLD,20));
        boardLabel.setHorizontalAlignment(JLabel.RIGHT);
        boardLabel.setText("  甲板");
        //倍位字样
        title = new JLabel();
        title.setFont(new Font("宋体",Font.BOLD,20));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setText("第"+bayID+"倍概图");

        //集装箱panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(2,1,1,10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50,50,50,50));
        contentPanel.setSize(new Dimension(900,900));
        //画集装箱
        DrawContainersInBay drawContainersAbove = new DrawContainersInBay(maxTierAbove,maxRowAbove,true);
        DrawContainersInBay drawContainersUnder = new DrawContainersInBay(maxTierUnder,maxRowUnder,false);
        contentPanel.add(drawContainersAbove);
        contentPanel.add(drawContainersUnder);

        //滚动条panel
        forScrolPanel = new JPanel();
        forScrolPanel.setLayout(new BorderLayout(0,0));
        forScrolPanel.add(boardLabel,BorderLayout.WEST);
        forScrolPanel.add(title,BorderLayout.NORTH);
        forScrolPanel.add(contentPanel,BorderLayout.CENTER);

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(forScrolPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);//滚动条速度？
        this.add(scrollPane);
        this.setLocationRelativeTo(null);

    }
    //画集装箱类
    class DrawContainersInBay extends JPanel{
        public  DrawContainersInBay(int tier,int row,boolean underOrAbove){//underOrAbove用来判断是甲板上下以便于draw排号和层号,true代表甲板上
            this.setLayout(new GridLayout(tier+1,row+1,10,5));

            //生成排数的字符串.偶数放在前面技术放在后面
            StringBuilder stringRes = new StringBuilder("");
            StringBuilder tempStr;
            for(int i = 1;i <= row; i++){
                tempStr = new StringBuilder(String.format("%02d",i));
                if(i % 2 == 0){
                    stringRes = tempStr.append(stringRes);
                }else{
                    stringRes = stringRes.append(tempStr);
                }
            }
            String[] rowNo = new String[row+1];//用于存储排row的号码
            rowNo[0] = "";//第0个排是空白
            for(int i = 1;i <= row; i++){
                rowNo[i] = stringRes.substring(2*(i-1),2*(i-1)+2);//记下排的顺序
            }
            String[] tierNo = new String[tier+1];//用于存储层的号码
            tierNo[tier] = "";//第tier层显示排号
            for(int i = 0;i < tier; i++){
                if(underOrAbove){                //甲板上
                    tierNo[i] = (tier - i)*2+80 + "";
                }else {
                    tierNo[i] =  String.format("%02d",(tier - i)*2);
                }
            }


            //画集装箱,全都用Jbutton表示，层号，排号用透明背景的Jbutton表示，角标要特别注意
            JButton[][] btn = new JButton[tier+1][row+1];
            for(int i=0;i<=tier;i++){
                for(int j=0;j<=row;j++){
                    btn[i][j] = new JButton();
                    btn[i][j].setPreferredSize(new Dimension(100,50));
                    btn[i][j].setFont(new Font("",Font.BOLD,10));

                    if(i== tier && j == 0){//最左下脚的空白位置
                        btn[i][j] = new JButton();
                        btn[i][j].setContentAreaFilled(false);//jbutton透明
                        btn[i][j].setBorderPainted(false);//jbutton透明
                    }else if(i == tier){//显示排号，背景透明
                        btn[i][j] = new JButton(rowNo[j]);
                        btn[i][j].setContentAreaFilled(false);//jbutton透明
                        btn[i][j].setBorderPainted(false);//jbutton透明
                    }else if(j == 0){//显示层号，背景透明
                        btn[i][j] = new JButton();
                        btn[i][j].setText(tierNo[i]);//层数
                        btn[i][j].setContentAreaFilled(false);//jbutton透明
                        btn[i][j].setBorderPainted(false);//jbutton透明
                    }else{//真正用于显示配载情况的Jbuttton
                        String loc = tierNo[i]+"."+rowNo[j];
                        if(resultInBay.containsKey(loc) && null!=resultInBay.get(loc)){
                            if(resultInBay.get(loc)[1].equals("?")){
                                btn[i][j].setText("未配载!");
                            }else {
                                btn[i][j].setFont(new Font("宋体",Font.BOLD,10));
                                btn[i][j].setText("<html>"+resultInBay.get(loc)[1]+"<br>"+resultInBay.get(loc)[0]+"<br>"+"尺寸"+resultInBay.get(loc)[2]+"</html>");
                            }
                        }
                    }
                    add(btn[i][j]);
                }
            }
        }
    }

}
