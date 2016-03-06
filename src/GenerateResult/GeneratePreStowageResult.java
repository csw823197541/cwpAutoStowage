package GenerateResult;

import importDataProcess.ImportData;
import importDataInfo.ContainerInfo;
import importDataInfo.GroupInfo;
import importDataInfo.PreStowageInfo;
import importDataInfo.VesselStructureInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by leko on 2016/1/21.
 */
public class GeneratePreStowageResult {

    private static ArrayList<Integer[]>  allocation = new ArrayList<Integer[]>();                   //人工分配每个舱的箱子数量
    private static String[] tier={"02","04","06","08","10","12","14","16","18","20","82","84","86","88","90","92","94","96","98"};  //层号
    private static Integer[] moveorderchange ={5,3,4,0,3,-3,2,-6,1,-9};                     //甲板上的顺序改变
    private static Integer[] moveorderchange2={0,-5,-4,-4,-3,-3,-2,-2,-1,-1,0};             //不满排的顺序改变
    private static Integer ROWnum = 10;
    private static List<Integer> movecounts =new ArrayList<Integer>(Arrays.asList(160,0,120,120,0,0,240,260,160,140,0,150,0,134,6));  //每个舱总数
    private static HashMap<String,String> records = new HashMap<String, String>();          //根据舱和moveorder确定位置


    //生成预配图（包括MoveOrder）
    public static List<PreStowageInfo> getPrestowageResult(List<GroupInfo> groupInfoList , List<ContainerInfo> containerInfoList, List<VesselStructureInfo> vesselStructureInfoList){
        List<PreStowageInfo> preStowageInfoList = new ArrayList<PreStowageInfo>();

        //接收参数
        List<GroupInfo> groupInfoList1 = groupInfoList;
        List<ContainerInfo> containerInfoList1 = containerInfoList;
        List<VesselStructureInfo> vesselStructureInfoList1 = vesselStructureInfoList;


        //处理属性组信息
        HashMap<String, ArrayList<String>> groupmap = new HashMap<String, ArrayList<String>>();
        for (GroupInfo groupInfo:groupInfoList1){
            ArrayList<String> groupattri = new ArrayList<String>();
            groupattri.add(groupInfo.getPort());
            groupattri.add(groupInfo.getType());
            groupattri.add(groupInfo.getSize());
            groupmap.put(groupInfo.getGroupID(),groupattri);
        }

        //初始化
        records.clear();
        allocation.clear();

        //人工生成预配图
        System.out.println("开始生成预配信息");
        {
            Integer[] a1={1,100,60};                        //第1个舱有100个大箱60个小箱
            Integer[] a2={3,40,80};
            Integer[] a3={4,60,60};
            Integer[] a4={7,140,100};
            Integer[] a5={8,100,160};
            Integer[] a6={9,100,60};
            Integer[] a7={10,60,80};
            Integer[] a8={12,70,80};
            Integer[] a9={14,68,66};
            Integer[] a10={15,6,0};
            allocation.add(a1);
            allocation.add(a2);
            allocation.add(a3);
            allocation.add(a4);
            allocation.add(a5);
            allocation.add(a6);
            allocation.add(a7);
            allocation.add(a8);
            allocation.add(a9);
            allocation.add(a10);
        }

        //生成预配图
        PreStowageInfo newPrestowageInfo;
        for (Integer[] allo:allocation){
            Integer VHTID = allo[0];            //舱号
            Integer cnt=1;
            //奇数倍
            Integer TIERnum1 = allo[2]/2/ROWnum;  //小箱占的层号
            if (allo[2]/2%ROWnum!=0)
            {
                TIERnum1++;
            }
            for (Integer i = (VHTID-1)*4+1;i<(VHTID*4);i+=2)
            {
                Integer VBYBAYID = i;           //倍号
                for (Integer j=0;j<TIERnum1;j++)
                {
                    String VTRTIERNO = tier[j];       //层号
                    {
                        for (Integer k=1;k<=10;k++)
                        {
                            if (j*ROWnum+k>allo[2]/2){
                                System.out.println("break,"+j+" "+ROWnum+" "+allo[2]/2);
                                break;
                            }
                            String VRWROWNO = k.toString();      //排号
                            String SIZE = "20";         //尺寸20
                            String GROUPID="G1";      //属性组
                            Integer WEIGHT=25;         //重量
                            Integer MOVEORDER=cnt++;   //移动顺序
                            if (Integer.valueOf(VTRTIERNO)>=82){
                                MOVEORDER = MOVEORDER + moveorderchange[k-1];
                            }
                            newPrestowageInfo = new PreStowageInfo();
                            newPrestowageInfo.setVHT_ID(VHTID.toString());
                            newPrestowageInfo.setVBY_BAYID(VBYBAYID.toString());
                            newPrestowageInfo.setVTR_TIERNO(VTRTIERNO);
                            newPrestowageInfo.setVRW_ROWNO(VRWROWNO);
                            newPrestowageInfo.setSIZE(SIZE);
                            newPrestowageInfo.setGROUP_ID(GROUPID);
                            newPrestowageInfo.setWEIGHT(WEIGHT);
                            newPrestowageInfo.setMOVE_ORDER(MOVEORDER);
                            String a=VHTID.toString()+"."+MOVEORDER.toString();
                            String b=VHTID.toString()+"."+VBYBAYID.toString()+"."+VTRTIERNO.toString()+"."+VRWROWNO.toString();
                            records.put(a,b);
                            preStowageInfoList.add(newPrestowageInfo);
                        }
                    }
                }
            }
            //偶数倍
            Integer VBYBAYID = 4*VHTID-2;       //倍号
            Integer TIERnum = (allo[1]+allo[2]/2)/ROWnum;  //小箱+大箱占的层号
            if ((allo[1]+allo[2]/2)%ROWnum!=0)
            {
                TIERnum++;
            }
            //小箱排的不正好
            if ((allo[2]/2) % 10!=0){
                Integer breaktier = allo[2]/2/10;
                String VTRTIERNO = tier[breaktier];      //层号
                Integer breakk=allo[2]/2%10+1;
                for (Integer k=breakk;k<=10;k++)
                {
                    if (breaktier*ROWnum+k>allo[1]+allo[2]/2){
                        break;
                    }
                    String VRWROWNO = k.toString();      //排号
                    String SIZE = "40";         //尺寸20
                    String GROUPID="G2";      //属性组
                    Integer WEIGHT=25;         //重量
                    Integer MOVEORDER=cnt++;   //移动顺序
                    if (Integer.valueOf(VTRTIERNO)>=82){
                        MOVEORDER = MOVEORDER + moveorderchange[k-1];
                    }
                    //System.out.println(VHTID+" "+VBYBAYID+" "+VTRTIERNO+" "+VRWROWNO+" "+SIZE+" "+GROUPID+" "+WEIGHT+ " "+MOVEORDER);
                    newPrestowageInfo = new PreStowageInfo();
                    newPrestowageInfo.setVHT_ID(VHTID.toString());
                    newPrestowageInfo.setVBY_BAYID(VBYBAYID.toString());
                    newPrestowageInfo.setVTR_TIERNO(VTRTIERNO);
                    newPrestowageInfo.setVRW_ROWNO(VRWROWNO);
                    newPrestowageInfo.setSIZE(SIZE);
                    newPrestowageInfo.setGROUP_ID(GROUPID);
                    newPrestowageInfo.setWEIGHT(WEIGHT);
                    newPrestowageInfo.setMOVE_ORDER(MOVEORDER);
                    String a=VHTID.toString()+"."+MOVEORDER.toString();
                    String b=VHTID.toString()+"."+VBYBAYID.toString()+"."+VTRTIERNO.toString()+"."+VRWROWNO.toString();
                    records.put(a,b);
                    preStowageInfoList.add(newPrestowageInfo);
                }
            }
            for (Integer j=TIERnum1;j<TIERnum;j++)
            {
                //System.out.println("放置大箱");
                String VTRTIERNO = tier[j];       //层号
                {
                    for (Integer k=1;k<=10;k++)
                    {
                        if (j*ROWnum+k>(allo[1]+allo[2]/2)){
                            break;
                        }
                        String VRWROWNO = k.toString();      //排号
                        String SIZE = "40";         //尺寸40
                        String GROUPID="G2";      //属性组
                        Integer WEIGHT=25;         //重量
                        Integer MOVEORDER=cnt++;   //移动顺序
                        if (Integer.valueOf(VTRTIERNO)>=82){
                            MOVEORDER = MOVEORDER + moveorderchange[k-1];
                            if (j==TIERnum-1){
                                Integer last = (allo[1]+allo[2]/2)%10;
                                MOVEORDER = MOVEORDER +moveorderchange2[last];
                            }
                        }
                        //System.out.println(VHTID+" "+VBYBAYID+" "+VTRTIERNO+" "+VRWROWNO+" "+SIZE+" "+GROUPID+" "+WEIGHT+ " "+MOVEORDER);
                        newPrestowageInfo = new PreStowageInfo();
                        newPrestowageInfo.setVHT_ID(VHTID.toString());
                        newPrestowageInfo.setVBY_BAYID(VBYBAYID.toString());
                        newPrestowageInfo.setVTR_TIERNO(VTRTIERNO);
                        newPrestowageInfo.setVRW_ROWNO(VRWROWNO);
                        newPrestowageInfo.setSIZE(SIZE);
                        newPrestowageInfo.setGROUP_ID(GROUPID);
                        newPrestowageInfo.setWEIGHT(WEIGHT);
                        newPrestowageInfo.setMOVE_ORDER(MOVEORDER);
                        String a=VHTID.toString()+"."+MOVEORDER.toString();
                        String b=VHTID.toString()+"."+VBYBAYID.toString()+"."+VTRTIERNO.toString()+"."+VRWROWNO.toString();
                        records.put(a,b);
                        preStowageInfoList.add(newPrestowageInfo);
                    }
                }
            }
        }
        ImportData.movecounts = movecounts;
        ImportData.moveorderrecords = records;
        return preStowageInfoList;
    }
}
