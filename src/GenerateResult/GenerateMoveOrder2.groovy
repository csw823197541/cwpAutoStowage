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
    public Map<String,SlotStack2[]> slotStackMap


    GenerateMoveOrder2(List<VesselStructureInfo> vesselStructureInfoList){
        this.vesselStructureInfoList = vesselStructureInfoList
    }

    public List<PreStowageData> generateMoveOrder(List<PreStowageData> preStowageDataList){
        int singleHatchId = 40742;
        println "舱号:" + singleHatchId

        List<PreStowageData> psdl40742 = new ArrayList<PreStowageData>()
        preStowageDataList.each {preStowageData->
            int hatchId = preStowageData.getVHT_ID().toInteger()
            if(hatchId == singleHatchId){
                psdl40742.add(preStowageData)
            }
        }
        println "size:" + psdl40742.size()
        generateMoveOrderInHatch(psdl40742)

    }

    //单个舱内排序
    public List<PreStowageData> generateMoveOrderInHatch(List<PreStowageData> preStowageDataListInHatch){
        //获取该舱的船舶结构,获取最大的排数,最小排数
        int vMaxRowNo = 1,vMinRowNo = 1;
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

        //生成这么多个slotStack表示一组,共分为8组(2小贝*甲板上下*装卸)
        slotStackMap = new HashMap<>()
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
        allPreStowageDataMap = new HashMap<>()
        preStowageDataListInHatch.each { preStowageData->
            allPreStowageDataMap.put(getKey(preStowageData),preStowageData)
        }

        //遍历,填栈
        allPreStowageDataMap.keySet().each {key->
            String[] keyStr = key.split("\\.")
            int bayInt = keyStr[0].toInteger()
            int rowInt = keyStr[1].toInteger()
            int tierInt = keyStr[2].toInteger()
            String dlFlag = allPreStowageDataMap.get(key).getLDULD()
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
                if(tierInt>slotStack.getTopTierNo()){
                    slotStack.setTopTierNo(tierInt)
                }
                if(tierInt<slotStack.getBottomTierNo()){
                    slotStack.setBottomTierNo(tierInt)
                }
            }
        }

        //生成遍历顺序
        //甲板上,从左往右
        List<Integer> rowListAB = new ArrayList<>()

        for(int i = vMaxRowNo%2==0?vMaxRowNo:vMaxRowNo-1;i>=vMinRowNo;i=i-2){//偶数侧
            rowListAB.add(i)
        }
        for(int i = vMinRowNo%2==0?vMinRowNo+1:vMinRowNo;i<=vMaxRowNo;i=i+2){//奇数侧
            rowListAB.add(i)
        }

        println(rowListAB)

        //计算甲板上
        int currentSeq = 0;
        currentSeq = genDschMOByTier(currentSeq,null,rowListAB,slotStackMap.get("1AD"),slotStackMap.get("3AD"))


        return allPreStowageDataMap.values().toList()




    }



    //拼接位置
    public String getKey(PreStowageData preStowageData){
        String key = Integer.valueOf(preStowageData.getVBY_BAYID()) +  "." + Integer.valueOf(preStowageData.getVRW_ROWNO()) + "." +Integer.valueOf(preStowageData.getVTR_TIERNO())
        return key
    }


    //生成卸船队列,无论甲板上甲板下,返回最后的MoveOrderSeq.参数:开始序号,开始位置,排遍历顺序,块数据,小贝slotStacks1,小贝slotStacks3
    private int genDschMOByTier(int startSeq, String startKey, List<Integer> rowSeqList, SlotStack2[] slotStacks1, SlotStack2[] slotStacks3){
        int seq = startSeq;
        boolean flag, flag1, flag2, flag3, flag4;
        flag = true
        while(flag) {
            flag = false
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
                        println key
                        if(allPreStowageDataMap.get(key).getSIZE().startsWith("4")) {
                            if(i+1 > rowSeqList.size()) {
                                allPreStowageDataMap.get(key).setMOVE_ORDER(seq++)
                                allPreStowageDataMap.get(key).setWORKFLOW("1")
                                slotStack.setTopTierNo(topTierNo-2)
                                slotStacks3[curRowNo].setTopTierNo(topTierNo-2)
                                flag1 = true
                            } else {
                                int nextRowNo = rowSeqList.get(i+1)
                                SlotStack2 nextSlotStack = slotStacks1[nextRowNo]
                                int nextTopTierNo = nextSlotStack.getTopTierNo()
                                String nextKey = nextSlotStack.getKey(topTierNo)
                                if(nextKey != null) {
                                    if(allPreStowageDataMap.get(nextKey).getSIZE().startsWith("4")) {
                                        if(topTierNo == nextTopTierNo) {
                                            i++
                                        }
                                    } else {
                                        allPreStowageDataMap.get(key).setMOVE_ORDER(seq++)
                                        allPreStowageDataMap.get(key).setWORKFLOW("1")
                                        slotStack.setTopTierNo(topTierNo-2)
                                        slotStacks3[curRowNo].setTopTierNo(topTierNo-2)
                                        flag1 = true
                                    }
                                } else {
                                    allPreStowageDataMap.get(key).setMOVE_ORDER(seq++)
                                    allPreStowageDataMap.get(key).setWORKFLOW("1")
                                    slotStack.setTopTierNo(topTierNo-2)
                                    slotStacks3[curRowNo].setTopTierNo(topTierNo-2)
                                    flag1 = true
                                }
                            }
                        }
                    }
                }
            }
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
                        println key
                        if(allPreStowageDataMap.get(key).getSIZE().startsWith("4")) {
                            if(i+1 > rowSeqList.size()) {
                                continue
                            } else {
                                int nextRowNo = rowSeqList.get(i+1)
                                SlotStack2 nextSlotStack = slotStacks1[nextRowNo]
                                int nextTopTierNo = nextSlotStack.getTopTierNo()
                                String nextKey = nextSlotStack.getKey(topTierNo)
                                if(nextKey != null) {
                                    if (allPreStowageDataMap.get(nextKey).getSIZE().startsWith("4")) {
                                        if (topTierNo == nextTopTierNo) {
                                            allPreStowageDataMap.get(key).setMOVE_ORDER(seq++)
                                            allPreStowageDataMap.get(key).setWORKFLOW("2")
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
            slotStacks1.each {slotStack->
                int topTierNo = slotStack.getTopTierNo()
                String key = slotStack.getKey(topTierNo)
                println "key "  +topTierNo +"kkkk "+ key
                if(!slotStack.isEmptyOrFull()) {
                    if(!allPreStowageDataMap.get(key).getSIZE().startsWith("4")) {
                        flag = true
                    }
                }
            }
        }




        //1.1处理1*40(顶上的40尺)

        //1.2处理2*40


        //2.1处理1*20(顶上的20尺)

        //2.2处理2*20()

        return startSeq
    }
    //生成装船队列,无论甲板上甲板下,返回最后的MoveOrderSeq.参数:开始序号,开始位置,排遍历顺序,块数据
    private int genLoadMOByTier(int startSeq, String startKey, List<Integer> rowSeqList, SlotStack2[] slotStacks){
        int seq = startSeq;

        return startSeq
    }
}