package GenerateResult

import importDataInfo.PreStowageData
import importDataProcess.ImportData

/**
 * Created by csw on 2016/3/7.
 */
class GenerateMoveCountAndGroupId {

    public static List<PreStowageData> getMoveCountAndGroupId(List<PreStowageData> preStowageDataList) {
        try{
            List<PreStowageData> resultList = new ArrayList<>()
            List<Integer> moveCounts = new ArrayList<>()
            Map<String, Integer> moveCountQuery = new HashMap<>()

            Map<String, String> groupQuery = new HashMap<>()  //统计预配信息里的属性组，将属性组信息保存
            Set<String> cportSet = new HashSet<String>();   //包含港口类型
            Set<String> ctypeSet = new HashSet<String>();  //包含箱型类型
            Set<String> csizeSet = new HashSet<String>();   //包含尺寸类型

            //将数据放在不同的舱位里
            List<String> VHTIDs = new ArrayList<>()//存放舱位ID
            Map<String, List<PreStowageData>> stringListMap = new HashMap<>()//放在不同的舱位的数据
            for(PreStowageData preStowageData : preStowageDataList) {
                if(!VHTIDs.contains(preStowageData.getVHT_ID())) {
                    VHTIDs.add(preStowageData.getVHT_ID())
                }
                String port = preStowageData.getDSTPORT()
                cportSet.add(port);                                      //统计港口类型
                String type = preStowageData.getCTYPECD()
                ctypeSet.add(type);                                      //统计箱型类型
                String size = preStowageData.getSIZE()
                csizeSet.add(size);                                      //统计尺寸类型
            }
            Collections.sort(VHTIDs)
            println "舱位数：" + VHTIDs.size()
            for(String str : VHTIDs) {//
                List<PreStowageData> dataList1 = new ArrayList<>()
                for(PreStowageData preStowageData : preStowageDataList) {
                    if(str.equals(preStowageData.getVHT_ID())) {
                        dataList1.add(preStowageData)
                    }
                }
                stringListMap.put(str, dataList1)
            }
            int t = 0
            for(String str : VHTIDs) {
                List<PreStowageData> dataList = stringListMap.get(str)
                List<Integer> orders = new ArrayList<>()
                for(PreStowageData preStowageData1 : dataList) {
                    if(!orders.contains(preStowageData1.getMOVE_ORDER())) {
                        orders.add(preStowageData1.getMOVE_ORDER())
                    }
                }
                println "舱id:"+str+"-moveCount数："+ orders.size()
                t += orders.size()
                moveCountQuery.put(str, orders.size())
            }
            println "总movecount数：" + t
            ImportData.moveCountQuery = moveCountQuery

            //先生成属性组
//            int groupNum=1;
////            println cportSet.size() +"----"+ctypeSet.size()+"----"+csizeSet.size()
//            for (String port: cportSet){
//                for (String type:ctypeSet){
//                    for (String size: csizeSet) {
//                        String groupID = "G" + groupNum;
//                        String key = port+"."+type+"."+size
//    //                    println key +"----"+groupID
//                        groupQuery.put(key, groupID)
//                        groupNum++;
//                    }
//                }
//            }
            //根据在场箱的属性组生成可查找的Map形式

            //将预配信息里的属性组赋值
//            for(PreStowageData preStowageData : preStowageDataList) {
//                String key = preStowageData.getDSTPORT() +"."+preStowageData.getCTYPECD() +"."+preStowageData.getSIZE()
//                preStowageData.setGROUP_ID(groupQuery.get(key))
//            }
            return preStowageDataList
        }catch (Exception e) {
            e.printStackTrace()
        }
    }
}
