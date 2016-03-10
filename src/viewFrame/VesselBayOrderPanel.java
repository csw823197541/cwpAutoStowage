package viewFrame;

import importDataInfo.PreStowageData;
import importDataInfo.VesselStructureInfo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by liuminhang on 16/3/10.
 */
public class VesselBayOrderPanel extends JPanel {
    private List<PreStowageData> preStowageDataList;
    public int size_one_row = 4;//每行显示贝位
    public int size_one_colume = 2;//列数
    public int rect_length = 20;//边长
    public static int start_x = 300,start_y = 560;
    public int size_width = 512;
    public int size_height = 600;
    public Font font = new Font("宋体",Font.BOLD,10);
    public Font font2 = new Font("宋体",Font.PLAIN,6);

    public VesselBayOrderPanel(List<PreStowageData> inPreStowageDataList){
        preStowageDataList = inPreStowageDataList;
        initComponents();
    }
    private void initComponents(){
        setSize(size_width, size_height);
        setOpaque(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paintComponents(g);
        Graphics2D g2d = (Graphics2D)g;

        //取出贝位
        for (int i = 0;i<preStowageDataList.size();i++){
            PreStowageData preStowageData = preStowageDataList.get(i);
            int x = 0,y = 0;
            int bayInt = Integer.valueOf(preStowageData.getVBY_BAYID());
            int rowInt = Integer.valueOf(preStowageData.getVRW_ROWNO());
            int tierInt = Integer.valueOf(preStowageData.getVTR_TIERNO());

            if(bayInt>0){
//            if(bayInt==3){
                if(rowInt == 1){
                    System.out.println("AAAAAAA:"+ rowInt + "," + tierInt);
                }

                x = start_x;
                y = start_y ;
                System.out.println("Draw Start Pos:"+ x + "," + y);
                g2d.drawOval(x,y,4,4);


                if(rowInt%2==1){//奇数
                    x = (rowInt/2)*rect_length + x;
                }
                else{
                    x = x - (rowInt/2)*rect_length;
                }
                if(tierInt>=82){
                    y = y - ((tierInt-80)/2)*rect_length-280;
                }
                else {
                    y = y - (tierInt/2)*rect_length;
                }

                g2d.drawRect(x,y,rect_length,rect_length);
                String tag2 = preStowageData.getMOVE_ORDER().toString();
                g2d.setFont(font);
                g2d.setPaint(Color.red);
                g2d.drawString(tag2,x,y+8);

                String tag1 = "" + rowInt+"," + tierInt;
                g2d.setFont(font2);
                g2d.setPaint(Color.black);
                g2d.drawString(tag1,x,y+18);

                System.out.println("Slot:"+ rowInt + "," + tierInt +"   Draw:[" + x + "," + y + "]");

            }




        }

    }
}
