package GenerateResult

import importDataInfo.HatchInfo
import importDataInfo.PreStowageData

/**
 * Created by csw on 2016/3/25.
 */
class GenerateModelValue {

    public static String getMoZhi(List<HatchInfo> hatchInfoList, int craneSize) {

        String resultStr = ""
        int moveCountAll = 0
        for(HatchInfo hatchInfo : hatchInfoList) {
            moveCountAll += hatchInfo.getMOVECOUNT()
        }
        int meanMoveCount = moveCountAll/craneSize
        println "平均值:" + meanMoveCount
        Map<Integer, List<HatchInfo>> stringListMap = new HashMap<>()
        List<Integer> remainCountList = new ArrayList<>()
        List<HatchInfo> hatchList = new ArrayList<>()
        int p = 0
        int groupNum = 0
        int count = 0
        int toNextGroupCount = 0
        int remainCount =0
        List<String> resultList = new ArrayList<>()
        HatchInfo hatchInfo = null
        for(int i = 0; i < hatchInfoList.size(); i++) {
            int ct = hatchInfoList.get(i).getMOVECOUNT()
            count += ct
            hatchList.add(hatchInfoList.get(i))
            if(count + toNextGroupCount >= meanMoveCount) {
                toNextGroupCount = count + toNextGroupCount - meanMoveCount
                remainCount = ct - toNextGroupCount
                List<HatchInfo> hatchSs = new ArrayList<>()
                if(hatchInfo != null) {
                    hatchSs.add(hatchInfo)
                }
                if(toNextGroupCount == 0) {//这个分组的总量刚好为平均数
                    for(int j = 0; j < hatchList.size(); j++) {
                        hatchSs.add(hatchList.get(j))
                    }
                    hatchInfo = null
                } else {//分组总量大于平均数，则将最后一个舱分开，留一部分给下一个分组
                    for(int j = 0; j < hatchList.size()-1; j++) {
                        hatchSs.add(hatchList.get(j))
                    }
                    hatchInfo = hatchList.get(hatchList.size()-1)//拆分的舱
                    remainCountList.add(remainCount)
                }
                stringListMap.put(groupNum++, hatchSs)
                if(groupNum == craneSize) {
                    hatchSs.add(hatchInfo)
                    stringListMap.put(groupNum-1, hatchSs)
                    remainCountList.removeElement(remainCount)
                }
                println "分组"+ groupNum +"有"+hatchSs.size()+"个舱,分组的第一个舱做"+remainCountList+"个move结束";
                p = i;
                count = 0
                hatchList.clear()
            }
        }
        //将剩余的moveCount数划分给最后一个分组
        for(p = p+1; p < hatchInfoList.size(); p++) {
            stringListMap.get(groupNum-1).add(hatchInfoList.get(p))
        }
        //给每个舱设置model值
        int maxGroupSize = 0
        for(Map.Entry<Integer, List<HatchInfo>> entry : stringListMap) {
            if(entry.value.size() > maxGroupSize) {//得到分组数目最大的值
                maxGroupSize = entry.value.size()
            }
        }
        for(Map.Entry<Integer, List<HatchInfo>> entry : stringListMap) {
            List<HatchInfo> hatchInfoS = entry.value
            int model = maxGroupSize
            hatchInfoS.each {hatch->
                if(hatch.getMOVECOUNT() == 0) {
                    resultStr += "0#"
                } else {
                    resultStr += (model--)*1000+"#"
                }
            }
        }
        println resultStr
        return resultStr
    }
}
