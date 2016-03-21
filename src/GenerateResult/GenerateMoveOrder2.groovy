package GenerateResult

import importDataInfo.PreStowageData
import importDataInfo.VesselStructureInfo

/**
 * Created by liuminhang on 16/3/21.
 */
class GenerateMoveOrder2 {

    public List<VesselStructureInfo> vesselStructureInfoList;
    public List<PreStowageData> preStowageDataList;

    public int boardSepratorTierNo = 50;

    public Map<String,PreStowageData> allPreStowageDataMap

    GenerateMoveOrder2(){

    }

    public List<PreStowageData> generateMoveOrder(List<PreStowageData> preStowageDataList){

    }

    //单个舱内排序
    public List<PreStowageData> generateMoveOrderInHatch(List<PreStowageData> preStowageDataList){
        //声明8个指针数组
        Map<String,int[]> tpMap = new HashMap<>()

        //生成整个舱的Map
        allPreStowageDataMap = new HashMap<>()
        preStowageDataList.each { preStowageData->
            allPreStowageDataMap.put(getKey(preStowageData),preStowageData)
        }

        //获取排号,生成栈指针数组
        List<Integer> rowIntegerList = getRowIntegers(allPreStowageDataMap.keySet())
        //获取最大排号
        final int maxRowNo = rowIntegerList.get(rowIntegerList.size()-1)
        //初始化栈指针数组

        tpMap.put("1AD",new int[maxRowNo+1])
        tpMap.put("3AD",new int[maxRowNo+1])
        tpMap.put("1BD",new int[maxRowNo+1])
        tpMap.put("3BD",new int[maxRowNo+1])
        tpMap.put("1BL",new int[maxRowNo+1])
        tpMap.put("3BL",new int[maxRowNo+1])
        tpMap.put("1AL",new int[maxRowNo+1])
        tpMap.put("3AL",new int[maxRowNo+1])
        //获取每排最大指针
        allPreStowageDataMap.keySet().each {key->
            String[] keyStr = key.split(".")
            int bayInt = keyStr[0].toInteger()
            int rowInt = keyStr[1].toInteger()
            int tierInt = keyStr[2].toInteger()
            String dlFlag = allPreStowageDataMap.get(key).getLDULD()
            String abFlag = tierInt >= this.boardSepratorTierNo?"A":"B"
            if(bayInt%4==2){//大贝位,找到两个小贝的指针位置,比较更新
                String tpKey = "" + "1" + abFlag + dlFlag
                int[] tpArray = tpMap.get(tpKey)
                if(tpArray[rowInt]==null||tierInt>tpArray[rowInt]){
                    tpArray[rowInt] = tierInt
                }
                tpKey = "" + "3" + abFlag + dlFlag
                tpArray = tpMap.get(tpKey)
                if(tpArray[rowInt]==null||tierInt>tpArray[rowInt]){
                    tpArray[rowInt] = tierInt
                }
            }
            else {//小贝位,找到对应的指针位置,比较更新
                String tpKey = "" + bayInt%4 + abFlag + dlFlag  //拼接key
                int[] tpArray = tpMap.get(tpKey)
                if(tpArray[rowInt]==null||tierInt>tpArray[rowInt]){
                    tpArray[rowInt] = tierInt
                }
            }
        }

        //按栈遍历,排序




    }



    //拼接位置
    public String getKey(PreStowageData preStowageData){
        String key = Integer.valueOf(preStowageData.getVBY_BAYID()) +  "." + Integer.valueOf(preStowageData.getVRW_ROWNO()) + "." +Integer.valueOf(preStowageData.getVTR_TIERNO())
        return key
    }

    //获得排号
    private List<Integer> getRowIntegers(Set<String> keySet){
        List<Integer> resultList = new ArrayList<>();
        keySet.each {key->
            int rowInt = key.split(".")[1].toInteger()
            if(!resultList.contains(rowInt)){
                resultList.add(rowInt)
            }
        }
        Collections.sort(resultList)
        return resultList
    }
    //获得每排最大层号
    private Map<Integer,Integer> getTierMaxIntegersMap(Set<String> keySet){
        Map<Integer,Integer> resultMap = new HashMap<>()
        List<Integer> rowIntList = getRowIntegers((keySet))
        rowIntList.each { rowInt->
            int maxTierNum = 0
            keySet.each {key->
                if(rowInt==key.split(".")[1].toInteger()){
                    int tierInt = key.split(".")[2].toInteger()
                    maxTierNum = maxTierNum > tierInt ? maxTierNum : tierInt
                }
            }
            resultMap.put(rowInt,maxTierNum)
        }
        return resultMap
    }


}