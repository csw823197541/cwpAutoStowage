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

        return Integer.valueOf(preStowageData.getVBYBAYID()) +  " " + Integer.valueOf(preStowageData.getVRWROWNO()) + " " +Integer.valueOf(preStowageData.getVTRTIERNO())
    }

    public List<PreStowageData> generateMoveOrder(List<PreStowageData> inPreStowageDataList) {

        //将数据放在不同的舱位里
        List<String> VHTIDs = new ArrayList<>()//存放舱位ID
        Map<String, List<PreStowageData>> stringListMap = new HashMap<>()//放在不同的舱位的数据
        for(PreStowageData preStowageData : inPreStowageDataList) {
            if(!VHTIDs.contains(preStowageData.getVHTID())) {
                VHTIDs.add(preStowageData.getVHTID())
            }
        }
        Collections.sort(VHTIDs)
        println "舱位数：" + VHTIDs.size()
        for(String str : VHTIDs) {//
            List<PreStowageData> dataList1 = new ArrayList<>()
            for(PreStowageData preStowageData : inPreStowageDataList) {
                if(str.equals(preStowageData.getVHTID())) {
                    dataList1.add(preStowageData)
                }
            }
            stringListMap.put(str, dataList1)
        }
        int i = 0;
        for(String str : VHTIDs) {//对每个舱进行作业序列和作业工艺的生成
            if(i++ == 1) {
                List<PreStowageData> dataList = stringListMap.get(str)
                this.generateMoveOrderByHatch(dataList)
                break
            }
        }
        return resultList
    }

    private void generateMoveOrderByHatch(List<PreStowageData> hatchPreStowageDataList){
        preProcess(hatchPreStowageDataList);
        processAbove40D()
        above40DMap.clear()
    }
    public void preProcess(List<PreStowageData> inPreStowageDataList){
        //去除过境箱,分列表
        inPreStowageDataList.each {preStowageData ->
            if(preStowageData.getTHROUGHFLAG().equals("N")) {
                if(Integer.valueOf(preStowageData.getVTRTIERNO())>=this.boardSepratorTierNo) {
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
        int seq = 1
        Set<String> keySet = above40DMap.keySet() //得到甲板上40尺卸船的倍.排.层
        List<Integer> bayIdList = this.bayIntegers(keySet) //得到倍位号
        List<Integer> tierList = this.tierIntegers(keySet) //得到所有层号
        Map<Integer,List<Integer>> tierRowQuery = this.rowIntegersMap(keySet)
        List<PreStowageData> preStowageDataAloneList = new ArrayList<>()
        for(int i = tierList.size()-1; i >= 0; i--) {
            int tier = tierList.get(i)
            List<Integer> rowList = tierRowQuery.get(tier)
            println tier+"  40尺卸船的排号有："+rowList
            List<Integer> even = new ArrayList<>()//排号为偶数
            List<Integer> odd = new ArrayList<>()//排号为奇数
            for(Integer rowNum : rowList) {
                if(rowNum%2 == 0) {
                    even.add(rowNum)
                } else {
                    odd.add(rowNum)
                }
            }
            Collections.sort(even)
            Collections.sort(odd)
            int evenRow = 0
            for(int p = even.size()-1; p >= 0;) {//先对偶数排处理,从大到小
                if(p == 0) {//当排到偶数排的最后一个排号时,不进行处理
                    evenRow = even.get(p) //将排号保存起来
                    p = p-1
                    seq--
                } else {
                    if(even.get(p)-2 == even.get(p-1)) { //表示能做双吊具
                        above40DMap.get(bayIdList.get(0)+" "+even.get(p)+" "+tier).setMOVEORDER(seq)
                        above40DMap.get(bayIdList.get(0)+" "+even.get(p)+" "+tier).setWORKFLOW("3")
                        above40DMap.get(bayIdList.get(0)+" "+even.get(p-1)+" "+tier).setMOVEORDER(seq)
                        above40DMap.get(bayIdList.get(0)+" "+even.get(p-1)+" "+tier).setWORKFLOW("3")
                        p = p-2
                    } else { //否则为单吊具
                        preStowageDataAloneList.add(above40DMap.get(bayIdList.get(0)+" "+even.get(p)+" "+tier))
                        seq--
                        p = p-1
                    }
                }
                seq++;
            }
            for(int q = 0; q < odd.size(); ) {//再对奇数排处理，从小到大
                if(evenRow-1 == odd.get(q)) {//如果偶数排最后一个排号是2，奇数排第一个排号是1，则可以一起做双吊具
                    above40DMap.get(bayIdList.get(0)+" "+evenRow+" "+tier).setMOVEORDER(seq)
                    above40DMap.get(bayIdList.get(0)+" "+evenRow+" "+tier).setWORKFLOW("3")
                    above40DMap.get(bayIdList.get(0)+" "+odd.get(0)+" "+tier).setMOVEORDER(seq)
                    above40DMap.get(bayIdList.get(0)+" "+odd.get(0)+" "+tier).setWORKFLOW("3")
                    q++
                }
                else {//否则最后一个偶数排号为单吊具
                    preStowageDataAloneList.add(above40DMap.get(bayIdList.get(0)+" "+evenRow+" "+tier))
                    if(q == odd.size()-1) {//奇数排号最后一个为单吊具
                        preStowageDataAloneList.add(above40DMap.get(bayIdList.get(0)+" "+odd.get(q)+" "+tier))
                        seq--
                        q++
                    } else {
                        if(odd.get(q)+2 == odd.get(q+1)) {
                            above40DMap.get(bayIdList.get(0)+" "+odd.get(q)+" "+tier).setMOVEORDER(seq)
                            above40DMap.get(bayIdList.get(0)+" "+odd.get(q)+" "+tier).setWORKFLOW("3")
                            above40DMap.get(bayIdList.get(0)+" "+odd.get(q+1)+" "+tier).setMOVEORDER(seq)
                            above40DMap.get(bayIdList.get(0)+" "+odd.get(q+1)+" "+tier).setWORKFLOW("3")
                            q = q+2
                        } else { //否则为单吊具
                            preStowageDataAloneList.add(above40DMap.get(bayIdList.get(0)+" "+odd.get(q)+" "+tier))
                            seq--
                            q = q+1
                        }
                    }
                }
                seq++
            }
        }
        //保存结果
        for(Map.Entry<String, PreStowageData> entry : above40DMap) {
            resultList.add(entry.getValue())
        }
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
            int tierInt = key.split(" ")[2].toInteger()
            if(!resultList.contains(tierInt)){
                resultList.add(tierInt)
            }
        }
        Collections.sort(resultList)
        return resultList
    }
    //获得每层排号
    private Map<Integer,List<Integer>> rowIntegersMap(Set<String> keySet){
        Map<Integer,List<Integer>> resultMap = new HashMap<>()
        List<Integer> tierIntList = tierIntegers(keySet)
        tierIntList.each { tierInt->
            List<Integer> rowIntegerList = new ArrayList<>()
            keySet.each {key->
                if(tierInt == Integer.valueOf(key.split(" ")[2])) {
                    int rowInt = Integer.valueOf(key.split(" ")[1])
                    rowIntegerList.add(rowInt)
                }
            }
            resultMap.put(tierInt, rowIntegerList)
        }
        return resultMap
    }
    //获得贝位号
    private List<Integer> bayIntegers(Set<String> keySet){
        List<Integer> resultList = new ArrayList<>();
        keySet.each {key->
            int bayInt = key.split(" ")[0].toInteger()
            if(!resultList.contains(bayInt)){
                resultList.add(bayInt)
            }
        }
        Collections.sort(resultList)
        return resultList
    }
    //判断当前船箱位下面是否有箱子
    private boolean isUnder(String key, Map<String,PreStowageData> map) {
        return map.get(key) != null ? true : false
    }
}
