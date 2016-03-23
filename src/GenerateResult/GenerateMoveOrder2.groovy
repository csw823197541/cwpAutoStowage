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
    public Map<String,SlotStack[]> slotStackMap


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
        slotStackMap.put("1AD",new SlotStack[vMaxRowNo+1])
        slotStackMap.put("3AD",new SlotStack[vMaxRowNo+1])
        slotStackMap.put("1BD",new SlotStack[vMaxRowNo+1])
        slotStackMap.put("3BD",new SlotStack[vMaxRowNo+1])
        slotStackMap.put("1BL",new SlotStack[vMaxRowNo+1])
        slotStackMap.put("3BL",new SlotStack[vMaxRowNo+1])
        slotStackMap.put("1AL",new SlotStack[vMaxRowNo+1])
        slotStackMap.put("3AL",new SlotStack[vMaxRowNo+1])

        slotStackMap.values().each {slotStacks->
            for(int i = 0;i<slotStacks.length;i++){
                slotStacks[i] = new SlotStack();
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
                SlotStack slotStack = slotStackMap.get(ssKey)[rowInt]
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
                SlotStack slotStack = slotStackMap.get(ssKey)[rowInt]
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

        return null




    }



    //拼接位置
    public String getKey(PreStowageData preStowageData){
        String key = Integer.valueOf(preStowageData.getVBY_BAYID()) +  "." + Integer.valueOf(preStowageData.getVRW_ROWNO()) + "." +Integer.valueOf(preStowageData.getVTR_TIERNO())
        return key
    }


    //生成卸船队列,无论甲板上甲板下,返回最后的MoveOrderSeq.参数:开始序号,开始位置,排遍历顺序,块数据,小贝slotStacks1,小贝slotStacks3
    private int genDschMOByTier(int startSeq,String startKey,List<Integer> rowSeqList,SlotStack[] slotStacks1,SlotStack[] slotStacks3){
        int seq = startSeq;
        //按排号遍历顶层
        for(int i =0;i<rowSeqList.size();i++){
            int curRowNo = rowSeqList.get(i)
            //取出对应slotStack1的顶层
            SlotStack slotStack = slotStacks1[curRowNo]
            if(!slotStack.isEmpty()){
                String key = slotStack.getKey(slotStack.getTopTierNo())
                println "顶层Key"
                println key
            }

        }

        //1.1处理1*40(顶上的40尺)

        //1.2处理2*40


        //2.1处理1*20(顶上的20尺)

        //2.2处理2*20()

        return startSeq
    }
    //生成装船队列,无论甲板上甲板下,返回最后的MoveOrderSeq.参数:开始序号,开始位置,排遍历顺序,块数据
    private int genLoadMOByTier(int startSeq,String startKey,List<Integer> rowSeqList,SlotStack[] slotStacks){
        int seq = startSeq;

        return startSeq
    }
}