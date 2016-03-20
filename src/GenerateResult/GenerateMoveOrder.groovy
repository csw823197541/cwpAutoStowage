package GenerateResult

import importDataInfo.PreStowageData

/**
 * Created by liuminhang on 16/3/20.
 */
class GenerateMoveOrder {

    public int boardSepratorTierNo = 50;

    public List<PreStowageData> resultList = new ArrayList<>();

    public Map<String,PreStowageData> above40DMap = new HashMap<>();
    public Map<String,PreStowageData> above20DMap = new HashMap<>();
    public Map<String,PreStowageData> below40DMap = new HashMap<>();
    public Map<String,PreStowageData> below20DMap = new HashMap<>();

    public Map<String,PreStowageData> below20LMap = new HashMap<>();
    public Map<String,PreStowageData> below40LMap = new HashMap<>();
    public Map<String,PreStowageData> above20LMap = new HashMap<>();
    public Map<String,PreStowageData> above40LMap = new HashMap<>();




    GenerateMoveOrder(){

    }
    public String getKey(PreStowageData preStowageData){
        String key = Integer.valueOf(preStowageData.getVBY_BAYID()) +  "." + Integer.valueOf(preStowageData.getVRW_ROWNO()) + "." +Integer.valueOf(preStowageData.getVTR_TIERNO())
        return key
    }
    public List<PreStowageData> generateMoveOrderByHatch(List<PreStowageData> inPreStowageDataList){
        preProcess(inPreStowageDataList);



    }
    public void preProcess(List<PreStowageData> inPreStowageDataList){
        //去除过境箱,分列表
        inPreStowageDataList.each {preStowageData ->
            if(preStowageData.getTHROUGHFLAG().equals("N")) {
                if(Integer.valueOf(preStowageData.getVTR_TIERNO())>=this.boardSepratorTierNo) {
                    //甲板上
                    if(preStowageData.getSIZE().startsWith("4")&&preStowageData.getLDULD().equals("D")){
                        above40DMap.put(getKey(preStowageData),preStowageData)
                    }
                    if(preStowageData.getSIZE().startsWith("2")&&preStowageData.getLDULD().equals("D")){
                        above20DMap.put(getKey(preStowageData),preStowageData)
                    }
                    if(preStowageData.getSIZE().startsWith("4")&&preStowageData.getLDULD().equals("L")){
                        above40LMap.put(getKey(preStowageData),preStowageData)
                    }
                    if(preStowageData.getSIZE().startsWith("2")&&preStowageData.getLDULD().equals("L")){
                        above20LMap.put(getKey(preStowageData),preStowageData)
                    }
                }
                else {
                    //甲板下
                    if(preStowageData.getSIZE().startsWith("4")&&preStowageData.getLDULD().equals("D")){
                        below40DMap.put(getKey(preStowageData),preStowageData)
                    }
                    if(preStowageData.getSIZE().startsWith("2")&&preStowageData.getLDULD().equals("D")){
                        below20DMap.put(getKey(preStowageData),preStowageData)
                    }
                    if(preStowageData.getSIZE().startsWith("4")&&preStowageData.getLDULD().equals("L")){
                        below40LMap.put(getKey(preStowageData),preStowageData)
                    }
                    if(preStowageData.getSIZE().startsWith("2")&&preStowageData.getLDULD().equals("L")){
                        below20LMap.put(getKey(preStowageData),preStowageData)
                    }
                }

            }
        }
    }
    //卸船
    public List<PreStowageData> processAbove40D(){

    }
    public List<PreStowageData> processAbove20D(){}
    public List<PreStowageData> processBelow40D(){}
    public List<PreStowageData> processBelow20D(){}

    //装船
    public List<PreStowageData> processBelow20L(){}
    public List<PreStowageData> processBelow40L(){}
    public List<PreStowageData> processAbove20L(){}
    public List<PreStowageData> processAbove40L(){}



    //获得层号
    private List<Integer> tierIntegers(Set<String> keySet){
        List<Integer> resultList = new ArrayList<>();
        keySet.each {key->
            int tierInt = key.split(".")[-1].toInteger()
            if(!resultList.contains(tierInt)){
                resultList.add(tierInt)
            }
        }
        return Collections.sort(resultList)
    }
    //获得每层排号
    private Map<Integer,List<Integer>> rowIntegersMap(Set<String> keySet){
        Map<Integer,List<Integer>> resultMap = new HashMap<>()
        List<Integer> tierIntList = tierIntegers(keySet)
        tierIntList.each { tierInt->
            List<Integer> rowIntegerList = new ArrayList<>()
            resultMap.put(tierInt,rowIntegerList)
        }
        keySet.each {key->
            int rowInt = key.split(".")[1].toInteger()
            int tierInt = key.split(".")[2].toInteger()
            resultMap.get(tierInt).add(rowInt)
        }

        return resultMap
    }
    //获得贝位号
    private List<Integer> bayIntegers(Set<String> keySet){
        List<Integer> resultList = new ArrayList<>();
        keySet.each {key->
            int bayInt = key.split(".")[-1].toInteger()
            if(!resultList.contains(bayInt)){
                resultList.add(bayInt)
            }
        }
        return Collections.sort(resultList)
    }
}
