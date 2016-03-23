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

    public List<Integer> rowListLR = new ArrayList<>()

    int vMaxRowNo,vMinRowNo;

    GenerateMoveOrder2(List<VesselStructureInfo> vesselStructureInfoList){
        this.vesselStructureInfoList = vesselStructureInfoList
    }

    public List<PreStowageData> generateMoveOrder(List<PreStowageData> preStowageDataList){

        vMaxRowNo = 1;
        vMinRowNo = 1;
        vesselStructureInfoList.each { vesselStructureInfo->
            int rowNo = vesselStructureInfo.getVRWROWNO().toInteger()
            if(rowNo> vMaxRowNo){
                vMaxRowNo = rowNo
            }
            if(rowNo<vMinRowNo){//判断是否有00排
                rowNo = vMinRowNo
            }
        }
        println "最大排号:" +  vMaxRowNo + ",最小排号:" + vMinRowNo

        List<String> hatchIdList = new ArrayList<>()//存放舱位ID
        Map<String, List<PreStowageData>> hatchDataMap = new HashMap<>()//放在不同的舱位的数据
        for(PreStowageData preStowageData : preStowageDataList) {
            if(!hatchIdList.contains(preStowageData.getVHT_ID())) {
                hatchIdList.add(preStowageData.getVHT_ID())
            }
        }
        Collections.sort(hatchIdList)
        println "舱位数：" + hatchIdList.size()
        for(String str : hatchIdList) {//
            List<PreStowageData> dataList1 = new ArrayList<>()
            for(PreStowageData preStowageData : preStowageDataList) {
                if(str.equals(preStowageData.getVHT_ID()) && preStowageData.getTHROUGHFLAG().equals("N")) {
                    dataList1.add(preStowageData)
                }
            }
            hatchDataMap.put(str, dataList1)
        }

        List<PreStowageData> resultList = new ArrayList<>()
        for(int i = 1; i < 2; i++) {
            println hatchIdList.get(i)
            resultList.addAll(generateMoveOrderInHatch(hatchDataMap.get(hatchIdList.get(i))))
        }
        return resultList

    }

    //单个舱内排序
    public List<PreStowageData> generateMoveOrderInHatch(List<PreStowageData> preStowageDataListInHatch){
        //获取该舱的船舶结构,获取最大的排数,最小排数


        //生成这么多个slotStack表示一组,共分为8组(2小贝*甲板上下*装卸)
        Map<String,SlotStack2[]> slotStackMap = new HashMap<>()
        slotStackMap.put("1AD",new SlotStack2[vMaxRowNo+1])
        slotStackMap.put("3AD",new SlotStack2[vMaxRowNo+1])
        slotStackMap.put("1BD",new SlotStack2[vMaxRowNo+1])
        slotStackMap.put("3BD",new SlotStack2[vMaxRowNo+1])
        slotStackMap.put("1BL",new SlotStack2[vMaxRowNo+1])
        slotStackMap.put("3BL",new SlotStack2[vMaxRowNo+1])
        slotStackMap.put("1AL",new SlotStack2[vMaxRowNo+1])
        slotStackMap.put("3AL",new SlotStack2[vMaxRowNo+1])

        slotStackMap.values().each {slotStacks->
            for(int i = 0;i<slotStacks.length;i++){
                slotStacks[i] = new SlotStack2();
            }
        }

        //生成整个舱的Map
        Map<String,PreStowageData> allPreStowageDataMapDsch = new HashMap<>()
        Map<String,PreStowageData> allPreStowageDataMapLoad = new HashMap<>()
        preStowageDataListInHatch.each { preStowageData->
            preStowageData.setMOVE_ORDER(0)
            preStowageData.setWORKFLOW("")
            if(preStowageData.getLDULD().equals("D")){
                allPreStowageDataMapDsch.put(getKey(preStowageData),preStowageData)
            }
            if(preStowageData.getLDULD().equals("L")){
                allPreStowageDataMapLoad.put(getKey(preStowageData),preStowageData)
            }

        }

        //遍历,填栈
        [allPreStowageDataMapDsch,allPreStowageDataMapLoad].each { map->
            map.keySet().each {key->
                String[] keyStr = key.split("\\.")
                int bayInt = Integer.valueOf(keyStr[0])
                int rowInt = Integer.valueOf(keyStr[1])
                int tierInt = Integer.valueOf(keyStr[2])

                String dlFlag = map.get(key).getLDULD()
                String abFlag = tierInt >= this.boardSepratorTierNo?"A":"B"
                if(bayInt%4==2){//大贝位,找到两个小贝的指针位置,比较更新
                    String ssKey = "" + "1" + abFlag + dlFlag
                    SlotStack2 slotStack = slotStackMap.get(ssKey)[rowInt]
                    slotStack.putKey(tierInt,key)
                    if(tierInt>slotStack.getTopTierNo()){
                        slotStack.setTopTierNo(tierInt)
                    }
                    if(tierInt<slotStack.getBottomTierNo()){
                        slotStack.setBottomTierNo(tierInt)
                    }
                    ssKey = "" + "3" + abFlag + dlFlag
                    slotStack = slotStackMap.get(ssKey)[rowInt]
                    if(tierInt>slotStack.getTopTierNo()){
                        slotStack.setTopTierNo(tierInt)
                    }
                    if(tierInt<slotStack.getBottomTierNo()){
                        slotStack.setBottomTierNo(tierInt)
                    }
                }
                else {//小贝位,找到对应slotStack
                    String ssKey = "" + bayInt%4 + abFlag + dlFlag  //拼接key
                    SlotStack2 slotStack = slotStackMap.get(ssKey)[rowInt]
                    slotStack.putKey(tierInt,key)
                    if(tierInt > slotStack.getTopTierNo()){
                        slotStack.setTopTierNo(tierInt)
                    }
                    if(tierInt < slotStack.getBottomTierNo()){
                        slotStack.setBottomTierNo(tierInt)
                    }
                }
            }

        }

        //生成遍历顺序
        //甲板上,从左往右
        for(int i = vMaxRowNo%2==0?vMaxRowNo:vMaxRowNo-1;i>=vMinRowNo;i=i-2){//偶数侧
            rowListLR.add(i)
        }
        for(int i = vMinRowNo%2==0?vMinRowNo+1:vMinRowNo;i<=vMaxRowNo;i=i+2){//奇数侧
            rowListLR.add(i)
        }
        println(rowListLR)

        List<Integer> rowListBB = new ArrayList<>(rowListLR)
//        Collections.copy(rowListBB, rowListLR)
        Collections.sort(rowListBB)
        println rowListBB

        //计算甲板上
        int currentSeq = 1;
        currentSeq = genDschMOByTier1(currentSeq, null,rowListLR,slotStackMap.get("1AD"),slotStackMap.get("3AD"), allPreStowageDataMapDsch)
        currentSeq = genDschMOByTier1(currentSeq, null, rowListBB, slotStackMap.get("1BD"), slotStackMap.get("3BD"), allPreStowageDataMapDsch)

        return allPreStowageDataMapDsch.values().toList()

    }

    //拼接位置
    public String getKey(PreStowageData preStowageData){
        String key = Integer.valueOf(preStowageData.getVBY_BAYID()) +  "." + Integer.valueOf(preStowageData.getVRW_ROWNO()) + "." +Integer.valueOf(preStowageData.getVTR_TIERNO())
        return key
    }

    //生成卸船队列,无论甲板上甲板下,返回最后的MoveOrderSeq.参数:开始序号,开始位置,排遍历顺序,块数据,小贝slotStacks1,小贝slotStacks3
    private int genDschMOByTier1(int startSeq, String startKey, List<Integer> rowSeqList, SlotStack2[] slotStacks1, SlotStack2[] slotStacks3, Map<String,PreStowageData> allPreStowageDataMapDsch){
        int seq = startSeq;
        boolean isNoneFlag, size40Flag, size20Flag, flag1, flag2, flag31, flag33, flag4;
        isNoneFlag = true
        while(isNoneFlag) {
            isNoneFlag = false
            size40Flag = true//顶层是否有40尺箱子标记
            while(size40Flag) {
                size40Flag = false
                //1.1处理1*40(顶上的40尺)
                flag1 = true
                while (flag1) {
                    flag1 = false
                    Map<Integer, List<String>> keyMap = new HashMap<>()
                    for(int i = 0; i < rowListLR.size(); i++) {
                        int curRowNo = rowListLR.get(i)
                        SlotStack2 slotStack = slotStacks1[curRowNo]
                        if(!slotStack.isEmptyOrFull()){
                            int topTierNo = slotStack.getTopTierNo()
                            String key = slotStack.getKey(topTierNo)
                            if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("4")) {
                                List<String> keys = new ArrayList<>()
                                if(i+1 > rowListLR.size()) {
//                                    allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq++)
//                                    allPreStowageDataMapDsch.get(key).setWORKFLOW("1")
                                    keys.add(key)
                                    keyMap.put(curRowNo, keys)
                                    slotStack.setTopTierNo(topTierNo-2)
                                    slotStacks3[curRowNo].setTopTierNo(topTierNo-2)
                                    flag1 = true
                                } else {
                                    int nextRowNo = rowListLR.get(i+1)
                                    SlotStack2 nextSlotStack = slotStacks1[nextRowNo]
                                    int nextTopTierNo = nextSlotStack.getTopTierNo()
                                    String nextKey = nextSlotStack.getKey(topTierNo)
                                    if(nextKey != null) {
                                        if(allPreStowageDataMapDsch.get(nextKey).getSIZE().startsWith("4")) {
                                            if(topTierNo == nextTopTierNo) {
                                                i++
                                            }
                                        } else {
//                                            allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq++)
//                                            allPreStowageDataMapDsch.get(key).setWORKFLOW("1")
                                            keys.add(key)
                                            keyMap.put(curRowNo, keys)
                                            slotStack.setTopTierNo(topTierNo-2)
                                            slotStacks3[curRowNo].setTopTierNo(topTierNo-2)
                                            flag1 = true
                                        }
                                    } else {
//                                        allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq++)
//                                        allPreStowageDataMapDsch.get(key).setWORKFLOW("1")
                                        keys.add(key)
                                        keyMap.put(curRowNo, keys)
                                        slotStack.setTopTierNo(topTierNo-2)
                                        slotStacks3[curRowNo].setTopTierNo(topTierNo-2)
                                        flag1 = true
                                    }
                                }
                            }
                        }
                    }
                    //按排号顺序遍历编序
                    for(int i = 0; i < rowSeqList.size(); i++) {
                        int curRowNo = rowSeqList.get(i)
                        if(keyMap.get(curRowNo) != null) {
                            keyMap.get(curRowNo).each {key->
                                allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq)
                                allPreStowageDataMapDsch.get(key).setWORKFLOW("1")
                            }
                            seq++
                        }
                    }
                }
                //1.2处理2*40
                flag2 = true
                while(flag2) {
                    flag2 = false
                    //按排号遍历顶层
                    Map<Integer, List<String>> keyMap = new HashMap<>()
                    for(int i =0;i<rowListLR.size();i++){
                        int curRowNo = rowListLR.get(i)
                        //取出对应slotStack1的顶层
                        SlotStack2 slotStack = slotStacks1[curRowNo]
                        if(!slotStack.isEmptyOrFull()){
                            int topTierNo = slotStack.getTopTierNo()
                            String key = slotStack.getKey(topTierNo)
                            if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("4")) {
                                List<String> keys = new ArrayList<>()
                                if(i+1 > rowListLR.size()) {
                                    continue
                                } else {
                                    int nextRowNo = rowListLR.get(i+1)
                                    SlotStack2 nextSlotStack = slotStacks1[nextRowNo]
                                    int nextTopTierNo = nextSlotStack.getTopTierNo()
                                    String nextKey = nextSlotStack.getKey(topTierNo)
                                    if(nextKey != null) {
                                        if (allPreStowageDataMapDsch.get(nextKey).getSIZE().startsWith("4")) {
                                            if (topTierNo == nextTopTierNo) {
//                                                allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq)
//                                                allPreStowageDataMapDsch.get(key).setWORKFLOW("2")
//                                                allPreStowageDataMapDsch.get(nextKey).setMOVE_ORDER(seq)
//                                                allPreStowageDataMapDsch.get(nextKey).setWORKFLOW("2")
//                                                seq++
                                                keys.add(key)
                                                keys.add(nextKey)
                                                keyMap.put(curRowNo, keys)
                                                slotStack.setTopTierNo(topTierNo - 2)
                                                slotStacks3[curRowNo].setTopTierNo(topTierNo - 2)
                                                nextSlotStack.setTopTierNo(nextTopTierNo - 2)
                                                slotStacks3[nextRowNo].setTopTierNo(nextTopTierNo - 2)
                                                flag2 = true
                                                i++
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //按排号顺序遍历编序
                    for(int i = 0; i < rowSeqList.size(); i++) {
                        int curRowNo = rowSeqList.get(i)
                        if(keyMap.get(curRowNo) != null) {
                            keyMap.get(curRowNo).each {key->
                                allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq)
                                allPreStowageDataMapDsch.get(key).setWORKFLOW("3")
                            }
                            seq++
                        }
                    }
                }
                //遍历是否还有40尺的箱子
                slotStacks1.each {slotStack->
                    int topTierNo = slotStack.getTopTierNo()
                    String key = slotStack.getKey(topTierNo)
                    if(!slotStack.isEmptyOrFull()) {
                        if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("4")) {
                            size40Flag = true
                        }
                    }
                }
            }
            size20Flag = true //顶层是否有20尺箱子标记
            while(size20Flag) {
                size20Flag = false
                //2.1处理1*20(顶上的20尺)
                flag31 = true
                while(flag31) {//01倍20尺单吊具的箱子
                    flag31 = false
                    Map<Integer, List<String>> keyMap = new HashMap<>()
                    for(int i =0;i<rowListLR.size();i++){
                        int curRowNo = rowListLR.get(i)
                        //取出对应slotStack1的顶层
                        SlotStack2 slotStack1 = slotStacks1[curRowNo]
                        if(!slotStack1.isEmptyOrFull()) {
                            List<String> keys = new ArrayList<>()
                            int topTierNo = slotStack1.getTopTierNo()
                            String key = slotStack1.getKey(topTierNo)
                            if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("2")) {
                                SlotStack2 slotStack3 = slotStacks3[curRowNo]
                                String oppositeKey = slotStack3.getKey(topTierNo)
                                if(oppositeKey == null) {
//                                    allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq++)
//                                    allPreStowageDataMapDsch.get(key).setWORKFLOW("1")
                                    keys.add(key)
                                    keyMap.put(curRowNo, keys)
                                    slotStack1.setTopTierNo(topTierNo-2)
                                    flag31 = true
                                }
                            }
                        }
                    }
                    //按排号顺序遍历编序
                    for(int i = 0; i < rowSeqList.size(); i++) {
                        int curRowNo = rowSeqList.get(i)
                        if(keyMap.get(curRowNo) != null) {
                            keyMap.get(curRowNo).each {key->
                                allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq)
                                allPreStowageDataMapDsch.get(key).setWORKFLOW("1")
                            }
                            seq++
                        }
                    }
                }
                flag33 = true;
                while(flag33) {//03倍的20尺单吊具的箱子
                    flag33 = false
                    Map<Integer, List<String>> keyMap = new HashMap<>()
                    for(int i =0;i<rowListLR.size();i++){
                        int curRowNo = rowListLR.get(i)
                        //取出对应slotStack1的顶层
                        SlotStack2 slotStack3 = slotStacks3[curRowNo]
                        if(!slotStack3.isEmptyOrFull()) {
                            List<String> keys = new ArrayList<>()
                            int topTierNo = slotStack3.getTopTierNo()
                            String key = slotStack3.getKey(topTierNo)
                            if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("2")) {
                                SlotStack2 slotStack1 = slotStacks1[curRowNo]
                                String oppositeKey = slotStack1.getKey(topTierNo)
                                if(oppositeKey == null) {
//                                    allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq++)
//                                    allPreStowageDataMapDsch.get(key).setWORKFLOW("1")
                                    keys.add(key)
                                    keyMap.put(curRowNo, keys)
                                    slotStacks3[curRowNo].setTopTierNo(topTierNo-2)
                                    flag33 = true
                                }
                            }
                        }
                    }
                    //按排号顺序遍历编序
                    for(int i = 0; i < rowSeqList.size(); i++) {
                        int curRowNo = rowSeqList.get(i)
                        if(keyMap.get(curRowNo) != null) {
                            keyMap.get(curRowNo).each {key->
                                allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq)
                                allPreStowageDataMapDsch.get(key).setWORKFLOW("1")
                            }
                            seq++
                        }
                    }
                }
                //2.2处理2*20()
                flag4 = true
                while(flag4) {
                    flag4 = false
                    Map<Integer, List<String>> keyMap = new HashMap<>()
                    for(int i = 0; i < rowListLR.size(); i++) {
                        int curRowNo = rowListLR.get(i)
                        SlotStack2 slotStack1 = slotStacks1[curRowNo]
                        if(!slotStack1.isEmptyOrFull()) {
                            List<String> keys = new ArrayList<>()
                            int topTierNo = slotStack1.getTopTierNo()
                            String key = slotStack1.getKey(topTierNo)
                            if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("2")) {
                                SlotStack2 slotStack3 = slotStacks3[curRowNo]
                                String oppositeKey = slotStack3.getKey(topTierNo)
                                if(oppositeKey != null) {
//                                    allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq)
//                                    allPreStowageDataMapDsch.get(key).setWORKFLOW("2")
//                                    allPreStowageDataMapDsch.get(oppositeKey).setMOVE_ORDER(seq)
//                                    allPreStowageDataMapDsch.get(oppositeKey).setWORKFLOW("2")
                                    keys.add(key)
                                    keys.add(oppositeKey)
                                    keyMap.put(curRowNo, keys)
                                    slotStack1.setTopTierNo(topTierNo-2)
                                    slotStack3.setTopTierNo(topTierNo-2)
//                                    seq++
                                    flag4 = true
                                }
                            }
                        }
                    }
                    //按排号顺序遍历编序
                    for(int i = 0; i < rowSeqList.size(); i++) {
                        int curRowNo = rowSeqList.get(i)
                        if(keyMap.get(curRowNo) != null) {
                            keyMap.get(curRowNo).each {key->
                                allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq)
                                allPreStowageDataMapDsch.get(key).setWORKFLOW("2")
                            }
                            seq++
                        }
                    }
                }
                //遍历是否还有20尺的箱子
                slotStacks1.each {slotStack->
                    int topTierNo = slotStack.getTopTierNo()
                    String key = slotStack.getKey(topTierNo)
                    if(!slotStack.isEmptyOrFull()) {
                        if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("2")) {
                            size20Flag = true
                        }
                    }
                }
                slotStacks3.each {slotStack->
                    int topTierNo = slotStack.getTopTierNo()
                    String key = slotStack.getKey(topTierNo)
                    if(!slotStack.isEmptyOrFull()) {
                        if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("2")) {
                            size20Flag = true
                        }
                    }
                }
            }
        }
        return seq
    }


    //生成卸船队列,无论甲板上甲板下,返回最后的MoveOrderSeq.参数:开始序号,开始位置,排遍历顺序,块数据,小贝slotStacks1,小贝slotStacks3
    private int genDschMOByTier(int startSeq, String startKey, List<Integer> rowSeqList, SlotStack2[] slotStacks1, SlotStack2[] slotStacks3, Map<String,PreStowageData> allPreStowageDataMapDsch){
        int seq = startSeq;
        boolean isNoneFlag, size40Flag, size20Flag, flag1, flag2, flag31, flag33, flag4;
        isNoneFlag = true
        while(isNoneFlag) {
            isNoneFlag = false
            size40Flag = true//顶层是否有40尺箱子标记
            while(size40Flag) {
                size40Flag = false
                //1.1处理1*40(顶上的40尺)
                flag1 = true
                while (flag1) {
                    flag1 = false
                    //按排号遍历顶层
                    for(int i =0;i<rowSeqList.size();i++){
                        int curRowNo = rowSeqList.get(i)
                        //取出对应slotStack1的顶层
                        SlotStack2 slotStack = slotStacks1[curRowNo]
                        if(!slotStack.isEmptyOrFull()){
                            int topTierNo = slotStack.getTopTierNo()
                            String key = slotStack.getKey(topTierNo)
                            if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("4")) {
                                if(i+1 > rowSeqList.size()) {
                                    allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq++)
                                    allPreStowageDataMapDsch.get(key).setWORKFLOW("1")
                                    slotStack.setTopTierNo(topTierNo-2)
                                    slotStacks3[curRowNo].setTopTierNo(topTierNo-2)
                                    flag1 = true
                                } else {
                                    int nextRowNo = rowSeqList.get(i+1)
                                    SlotStack2 nextSlotStack = slotStacks1[nextRowNo]
                                    int nextTopTierNo = nextSlotStack.getTopTierNo()
                                    String nextKey = nextSlotStack.getKey(topTierNo)
                                    if(nextKey != null) {
                                        if(allPreStowageDataMapDsch.get(nextKey).getSIZE().startsWith("4")) {
                                            if(topTierNo == nextTopTierNo) {
                                                i++
                                            }
                                        } else {
                                            allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq++)
                                            allPreStowageDataMapDsch.get(key).setWORKFLOW("1")
                                            slotStack.setTopTierNo(topTierNo-2)
                                            slotStacks3[curRowNo].setTopTierNo(topTierNo-2)
                                            flag1 = true
                                        }
                                    } else {
                                        allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq++)
                                        allPreStowageDataMapDsch.get(key).setWORKFLOW("1")
                                        slotStack.setTopTierNo(topTierNo-2)
                                        slotStacks3[curRowNo].setTopTierNo(topTierNo-2)
                                        flag1 = true
                                    }
                                }
                            }
                        }
                    }
                }
                //1.2处理2*40
                flag2 = true
                while(flag2) {
                    flag2 = false
                    //按排号遍历顶层
                    for(int i =0;i<rowSeqList.size();i++){
                        int curRowNo = rowSeqList.get(i)
                        //取出对应slotStack1的顶层
                        SlotStack2 slotStack = slotStacks1[curRowNo]
                        if(!slotStack.isEmptyOrFull()){
                            int topTierNo = slotStack.getTopTierNo()
                            String key = slotStack.getKey(topTierNo)
                            if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("4")) {
                                if(i+1 > rowSeqList.size()) {
                                    continue
                                } else {
                                    int nextRowNo = rowSeqList.get(i+1)
                                    SlotStack2 nextSlotStack = slotStacks1[nextRowNo]
                                    int nextTopTierNo = nextSlotStack.getTopTierNo()
                                    String nextKey = nextSlotStack.getKey(topTierNo)
                                    if(nextKey != null) {
                                        if (allPreStowageDataMapDsch.get(nextKey).getSIZE().startsWith("4")) {
                                            if (topTierNo == nextTopTierNo) {
                                                allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq)
                                                allPreStowageDataMapDsch.get(key).setWORKFLOW("2")
                                                allPreStowageDataMapDsch.get(nextKey).setMOVE_ORDER(seq)
                                                allPreStowageDataMapDsch.get(nextKey).setWORKFLOW("2")
                                                seq++
                                                slotStack.setTopTierNo(topTierNo - 2)
                                                slotStacks3[curRowNo].setTopTierNo(topTierNo - 2)
                                                nextSlotStack.setTopTierNo(nextTopTierNo - 2)
                                                slotStacks3[nextRowNo].setTopTierNo(nextTopTierNo - 2)
                                                flag2 = true
                                                i++
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //遍历是否还有40尺的箱子
                slotStacks1.each {slotStack->
                    int topTierNo = slotStack.getTopTierNo()
                    String key = slotStack.getKey(topTierNo)
                    if(!slotStack.isEmptyOrFull()) {
                        if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("4")) {
                            size40Flag = true
                        }
                    }
                }
            }
            size20Flag = true //顶层是否有20尺箱子标记
            while(size20Flag) {
                size20Flag = false
                //2.1处理1*20(顶上的20尺)
                flag31 = true
                while(flag31) {//01倍20尺单吊具的箱子
                    flag31 = false
                    for(int i =0;i<rowSeqList.size();i++){
                        int curRowNo = rowSeqList.get(i)
                        //取出对应slotStack1的顶层
                        SlotStack2 slotStack1 = slotStacks1[curRowNo]
                        if(!slotStack1.isEmptyOrFull()) {
                            int topTierNo = slotStack1.getTopTierNo()
                            String key = slotStack1.getKey(topTierNo)
                            if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("2")) {
                                SlotStack2 slotStack3 = slotStacks3[curRowNo]
                                String oppositeKey = slotStack3.getKey(topTierNo)
                                if(oppositeKey == null) {
                                    allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq++)
                                    allPreStowageDataMapDsch.get(key).setWORKFLOW("1")
                                    slotStack1.setTopTierNo(topTierNo-2)
                                    flag31 = true
                                }
                            }
                        }
                    }
                }
                flag33 = true;
                while(flag33) {//03倍的20尺单吊具的箱子
                    flag33 = false
                    for(int i =0;i<rowSeqList.size();i++){
                        int curRowNo = rowSeqList.get(i)
                        //取出对应slotStack1的顶层
                        SlotStack2 slotStack3 = slotStacks3[curRowNo]
                        if(!slotStack3.isEmptyOrFull()) {
                            int topTierNo = slotStack3.getTopTierNo()
                            String key = slotStack3.getKey(topTierNo)
                            if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("2")) {
                                SlotStack2 slotStack1 = slotStacks1[curRowNo]
                                String oppositeKey = slotStack1.getKey(topTierNo)
                                if(oppositeKey == null) {
                                    allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq++)
                                    allPreStowageDataMapDsch.get(key).setWORKFLOW("1")
                                    slotStacks3[curRowNo].setTopTierNo(topTierNo-2)
                                    flag33 = true
                                }
                            }
                        }
                    }
                }
                //2.2处理2*20()
                flag4 = true
                while(flag4) {
                    flag4 = false
                    for(int i = 0; i < rowSeqList.size(); i++) {
                        int curRowNo = rowSeqList.get(i)
                        SlotStack2 slotStack1 = slotStacks1[curRowNo]
                        if(!slotStack1.isEmptyOrFull()) {
                            int topTierNo = slotStack1.getTopTierNo()
                            String key = slotStack1.getKey(topTierNo)
                            if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("2")) {
                                SlotStack2 slotStack3 = slotStacks3[curRowNo]
                                String oppositeKey = slotStack3.getKey(topTierNo)
                                if(oppositeKey != null) {
                                    allPreStowageDataMapDsch.get(key).setMOVE_ORDER(seq)
                                    allPreStowageDataMapDsch.get(key).setWORKFLOW("2")
                                    allPreStowageDataMapDsch.get(oppositeKey).setMOVE_ORDER(seq)
                                    allPreStowageDataMapDsch.get(oppositeKey).setWORKFLOW("2")
                                    slotStack1.setTopTierNo(topTierNo-2)
                                    slotStack3.setTopTierNo(topTierNo-2)
                                    seq++
                                    flag4 = true
                                }
                            }
                        }
                    }
                }
                //遍历是否还有20尺的箱子
                slotStacks1.each {slotStack->
                    int topTierNo = slotStack.getTopTierNo()
                    String key = slotStack.getKey(topTierNo)
                    if(!slotStack.isEmptyOrFull()) {
                        if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("2")) {
                            size20Flag = true
                        }
                    }
                }
                slotStacks3.each {slotStack->
                    int topTierNo = slotStack.getTopTierNo()
                    String key = slotStack.getKey(topTierNo)
                    if(!slotStack.isEmptyOrFull()) {
                        if(allPreStowageDataMapDsch.get(key).getSIZE().startsWith("2")) {
                            size20Flag = true
                        }
                    }
                }
            }
        }
        return seq
    }
    //生成装船队列,无论甲板上甲板下,返回最后的MoveOrderSeq.参数:开始序号,开始位置,排遍历顺序,块数据
    private int genLoadMOByTier(int startSeq, String startKey, List<Integer> rowSeqList, SlotStack2[] slotStacks){
        int seq = startSeq;

        return startSeq
    }
}