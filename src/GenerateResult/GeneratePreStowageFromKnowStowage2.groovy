package GenerateResult

import importDataInfo.PreStowageData

/**
 * 版本1.2， 先将卸的箱子的序列排好，再考虑装的箱子
 * 当小倍位里有2个以上的箱子要做单箱吊的时候，则该舱重新排序列
 * Created by csw on 2016/3/8.
 */
class GeneratePreStowageFromKnowStowage2 {

    /**
     * 根据实配图生成预配图，生成作业序列和作业工艺的方法
     * @param preStowageDataList
     * @return
     */
    public static List<PreStowageData> getPreStowageResult(List<PreStowageData> preStowageDataList){
        try{

        List<PreStowageData> resultList = new ArrayList<>()
        //去掉过境的箱子
        List<PreStowageData> preStowageDataListNew = new ArrayList<>()
        for(PreStowageData preStowageData : preStowageDataList) {
            if(preStowageData.getTHROUGHFLAG().equals("N")) {
                preStowageDataListNew.add(preStowageData)
            }
        }
        println "总共有多少个位置：" + preStowageDataListNew.size()
        //将数据放在不同的舱位里
        List<String> VHTIDs = new ArrayList<>()//存放舱位ID
        Map<String, List<PreStowageData>> stringListMap = new HashMap<>()//放在不同的舱位的数据
        for(PreStowageData preStowageData : preStowageDataListNew) {
            if(!VHTIDs.contains(preStowageData.getVHT_ID())) {
                VHTIDs.add(preStowageData.getVHT_ID())
            }
        }
        Collections.sort(VHTIDs)
        println "舱位数：" + VHTIDs.size()
        for(String str : VHTIDs) {//
            List<PreStowageData> dataList1 = new ArrayList<>()
            for(PreStowageData preStowageData : preStowageDataListNew) {
                if(str.equals(preStowageData.getVHT_ID())) {
                    dataList1.add(preStowageData)
                }
            }
            stringListMap.put(str, dataList1)
        }
        int i = 0;
        for(String str : VHTIDs) {//对每个舱进行作业序列和作业工艺的生成
            if(i++ >= 0) {
                int seq = 1
                List<PreStowageData> dataList = stringListMap.get(str)//当前舱的船箱位数据
                println "舱位"+str+"有"+dataList.size()+"个船箱位"
                List<Integer> VBY_BAYIDs = new ArrayList<>()//倍位
                List<Integer> VTR_TIERNOs = new ArrayList<>()//层
                List<String> VBY_BAYIDss = new ArrayList<>()//倍位
                List<String> VTR_TIERNOss = new ArrayList<>()//层
                List<Integer> VRW_ROWNO_D_20 = new ArrayList<>()//排
                List<Integer> VRW_ROWNO_D_40 = new ArrayList<>()//排
                List<Integer> VRW_ROWNO_L_20 = new ArrayList<>()//排
                List<Integer> VRW_ROWNO_L_40 = new ArrayList<>()//排
                for(PreStowageData preStowageData : dataList) {
                    if(!VBY_BAYIDss.contains(preStowageData.getVBY_BAYID())) {
                        VBY_BAYIDss.add(preStowageData.getVBY_BAYID())
                        VBY_BAYIDs.add(Integer.valueOf(preStowageData.getVBY_BAYID()))
                    }//统计倍位数
                    if(!VTR_TIERNOss.contains(preStowageData.getVTR_TIERNO())) {
                        VTR_TIERNOss.add(preStowageData.getVTR_TIERNO())
                        VTR_TIERNOs.add(Integer.valueOf(preStowageData.getVTR_TIERNO()))
                    }//统计层数
                }//
                Collections.sort(VBY_BAYIDs)
                Collections.sort(VTR_TIERNOs)
                println "倍位数：" + VBY_BAYIDs.size() + VBY_BAYIDs + "  层数:" + VTR_TIERNOs.size() + VTR_TIERNOs
                Map<String, PreStowageData> stringListMap_D_20 = new HashMap<>()
                Map<String, PreStowageData> stringListMap_D_40 = new HashMap<>()
                Map<String, PreStowageData> stringListMap_L_20 = new HashMap<>()
                Map<String, PreStowageData> stringListMap_L_40 = new HashMap<>()

                List<Integer> smallBay = new ArrayList<>()
                List<Integer> largeBay = new ArrayList<>()
                if(VBY_BAYIDs.size() == 1) {//舱内只有一个倍，一般为40或45尺的箱子
                    largeBay.add(VBY_BAYIDs.get(0))
                    List<PreStowageData> resultReturnD = this.workBayDischarge(seq, dataList, smallBay, largeBay, VTR_TIERNOs, VRW_ROWNO_D_20, stringListMap_D_20, VRW_ROWNO_L_20,  stringListMap_L_20,
                            VRW_ROWNO_D_40, stringListMap_D_40, VRW_ROWNO_L_40, stringListMap_L_40, resultList)
                    int seqL = 1//取出卸完箱子后的序列
                    for(PreStowageData preStowageData1: resultReturnD) {
                        if(preStowageData1.getMOVE_ORDER() > seqL)
                            seqL = preStowageData1.getMOVE_ORDER()
                    }//取得最大值，给装船当作开始序列
                    List<PreStowageData> resultReturnL = this.workBayLoad(seqL, dataList, smallBay, largeBay, VTR_TIERNOs, VRW_ROWNO_D_20, stringListMap_D_20, VRW_ROWNO_L_20,  stringListMap_L_20,
                            VRW_ROWNO_D_40, stringListMap_D_40, VRW_ROWNO_L_40, stringListMap_L_40, resultList)
                }
                if(VBY_BAYIDs.size() == 2) {//舱内有两个倍，一般全是20尺的箱子
                    smallBay.add(VBY_BAYIDs.get(0))
                    smallBay.add(VBY_BAYIDs.get(1))
                    List<PreStowageData> result = new ArrayList<>()
                    List<PreStowageData> resultReturnD = this.workBayDischarge(seq, dataList, smallBay, largeBay, VTR_TIERNOs, VRW_ROWNO_D_20, stringListMap_D_20, VRW_ROWNO_L_20,  stringListMap_L_20,
                            VRW_ROWNO_D_40, stringListMap_D_40, VRW_ROWNO_L_40, stringListMap_L_40, resultList)
                    int seqL = 1//取出卸完箱子后的序列
                    for(PreStowageData preStowageData1: resultReturnD) {
                        if(preStowageData1.getMOVE_ORDER() > seqL)
                            seqL = preStowageData1.getMOVE_ORDER()
                    }//取得最大值，给装船当作开始序列
                    List<PreStowageData> resultReturnL = this.workBayLoad(seqL, dataList, smallBay, largeBay, VTR_TIERNOs, VRW_ROWNO_D_20, stringListMap_D_20, VRW_ROWNO_L_20,  stringListMap_L_20,
                            VRW_ROWNO_D_40, stringListMap_D_40, VRW_ROWNO_L_40, stringListMap_L_40, resultList)
                    for(PreStowageData preStowageData2 : resultReturnD) {
                        result.add(preStowageData2)
                    }
                    for(PreStowageData preStowageData3 : resultReturnL) {
                        result.add(preStowageData3)
                    }
                    //统计结果中20尺、作业工艺为1的箱子的个数
                    int num = 0
                    for(PreStowageData preStowageData1 : result) {
                        if("20".equals(preStowageData1.getSIZE()) && "1".equals(preStowageData1.getWORKFLOW())) {
                            num++
                        }
                    }
                    if(num > 2) {//当小倍位里有2个以上的箱子要做单箱吊的时候，则按新规则重新排序列：先小倍，从左到右，后大倍
                        seq = 1
                        this.reWork(seq, dataList, smallBay, largeBay, VTR_TIERNOs, VRW_ROWNO_D_20, stringListMap_D_20, VRW_ROWNO_L_20,  stringListMap_L_20,
                                VRW_ROWNO_D_40, stringListMap_D_40, VRW_ROWNO_L_40, stringListMap_L_40, resultList)
                    }
                }
                if(VBY_BAYIDs.size() == 3) {//有3个倍位的处理方法
                    smallBay.add(VBY_BAYIDs.get(0))
                    smallBay.add(VBY_BAYIDs.get(2))
                    largeBay.add(VBY_BAYIDs.get(1))
                    List<PreStowageData> result = new ArrayList<>()
                    List<PreStowageData> resultReturnD = this.workBayDischarge(seq, dataList, smallBay, largeBay, VTR_TIERNOs, VRW_ROWNO_D_20, stringListMap_D_20, VRW_ROWNO_L_20,  stringListMap_L_20,
                            VRW_ROWNO_D_40, stringListMap_D_40, VRW_ROWNO_L_40, stringListMap_L_40, resultList)
                    int seqL = 1//取出卸完箱子后的序列
                    for(PreStowageData preStowageData1: resultReturnD) {
                        if(preStowageData1.getMOVE_ORDER() > seqL)
                            seqL = preStowageData1.getMOVE_ORDER()
                    }//取得最大值，给装船当作开始序列
                    List<PreStowageData> resultReturnL = this.workBayLoad(seqL, dataList, smallBay, largeBay, VTR_TIERNOs, VRW_ROWNO_D_20, stringListMap_D_20, VRW_ROWNO_L_20,  stringListMap_L_20,
                            VRW_ROWNO_D_40, stringListMap_D_40, VRW_ROWNO_L_40, stringListMap_L_40, resultList)
                    for(PreStowageData preStowageData2 : resultReturnD) {
                        result.add(preStowageData2)
                    }
                    for(PreStowageData preStowageData3 : resultReturnL) {
                        result.add(preStowageData3)
                    }
                    //统计结果中20尺、作业工艺为1的箱子的个数
                    int num = 0
                    for(PreStowageData preStowageData1 : result) {
                        if("20".equals(preStowageData1.getSIZE()) && "1".equals(preStowageData1.getWORKFLOW())) {
                            num++
                        }
                    }
                    if(num > 2) {//当小倍位里有2个以上的箱子要做单箱吊的时候，按新规则重新排序列：先小倍，从左到右，后大倍
                        seq = 1
                        this.reWork(seq, dataList, smallBay, largeBay, VTR_TIERNOs, VRW_ROWNO_D_20, stringListMap_D_20, VRW_ROWNO_L_20,  stringListMap_L_20,
                                VRW_ROWNO_D_40, stringListMap_D_40, VRW_ROWNO_L_40, stringListMap_L_40, resultList)
                    }
                }
            }
        }
            return resultList
        }catch (Exception e) {
            e.printStackTrace()
        }

    }

    public static void reWork(int seq, List<PreStowageData> dataList, List<Integer> smallBay, List<Integer> largeBay, List<Integer> VTR_TIERNOs,
                                         List<Integer> VRW_ROWNO_D_20, Map<String, PreStowageData> stringListMap_D_20,
                                         List<Integer> VRW_ROWNO_L_20, Map<String, PreStowageData> stringListMap_L_20,
                                         List<Integer> VRW_ROWNO_D_40, Map<String, PreStowageData> stringListMap_D_40,
                                         List<Integer> VRW_ROWNO_L_40, Map<String, PreStowageData> stringListMap_L_40,
                                         List<PreStowageData> resultList) {
        println "去除需要重排的记录前有"+resultList.size()+"个船箱位"
        for(PreStowageData preStowageData : dataList) {//去除resultList里面已经编好序列的数据
            resultList.remove(preStowageData)
        }
        println "去除需要重排的记录后有"+resultList.size()+"个船箱位"
        List<PreStowageData> preStowageDataList01 = new ArrayList<>()
        List<PreStowageData> preStowageDataList03 = new ArrayList<>()
        List<PreStowageData> preStowageDataList02 = new ArrayList<>()
        for(PreStowageData preStowageData : dataList) {//将数据按倍位分别保存
            if(smallBay.get(0) == Integer.valueOf(preStowageData.getVBY_BAYID())) {
                preStowageDataList01.add(preStowageData)
            }
            if(smallBay.get(1) == Integer.valueOf(preStowageData.getVBY_BAYID())) {
                preStowageDataList03.add(preStowageData)
            }
            if(largeBay.size() > 0) {
                if(largeBay.get(0) == Integer.valueOf(preStowageData.getVBY_BAYID())) {
                    preStowageDataList02.add(preStowageData)
                }
            }
        }

        List<Integer> tiers = new ArrayList<>()//层
        List<String> VTR_TIERNOss = new ArrayList<>()//层
        //02倍，卸船开始
        if(preStowageDataList02.size() > 0) {
            for(PreStowageData preStowageData : preStowageDataList02) {
                if(!VTR_TIERNOss.contains(preStowageData.getVTR_TIERNO())) {
                    VTR_TIERNOss.add(preStowageData.getVTR_TIERNO())
                    tiers.add(Integer.valueOf(preStowageData.getVTR_TIERNO()))
                }//统计层数
            }
            Collections.sort(tiers)
            for(int i = tiers.size()-1; i >= 0; i-- ) {//开始逐层卸箱子，从上往下
                for(PreStowageData preStowageData : preStowageDataList02) {
                    if(tiers.get(i) == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                            "D".equals(preStowageData.getLDULD()) && ("40".equals(preStowageData.getSIZE()) || "45".equals(preStowageData.getSIZE()))) {//将同一层、卸船的船箱位取出
                        VRW_ROWNO_D_40.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                        String key = Integer.valueOf(preStowageData.getVBY_BAYID()) + "." +Integer.valueOf(preStowageData.getVTR_TIERNO()) + "." + Integer.valueOf(preStowageData.getVRW_ROWNO())
                        stringListMap_D_40.put(key, preStowageData)
                    }
                }
                if(tiers.get(i) < 50) {
                    //卸40或45尺的箱子，甲板下由中央向两边
                    Collections.sort(VRW_ROWNO_D_40)
                    println tiers.get(i)+"  40尺卸船的排号有："+VRW_ROWNO_D_40
                    for(Integer row : VRW_ROWNO_D_40) {
                        stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+row).setMOVE_ORDER(seq)
                        stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+row).setWORKFLOW(1+"")
                        seq++
                    }
                }
                if(tiers.get(i) > 50) {
                    //卸40尺的箱子
                    println tiers.get(i)+"  40尺卸船的排号有："+VRW_ROWNO_D_40
                    List<Integer> even = new ArrayList<>()//排号为偶数
                    List<Integer> odd = new ArrayList<>()//排号为奇数
                    for(Integer rowNum : VRW_ROWNO_D_40) {
                        if(rowNum%2 == 0) {
                            even.add(rowNum)
                        } else {
                            odd.add(rowNum)
                        }
                    }
                    Collections.sort(even)
                    Collections.sort(odd)
                    for(int p = even.size()-1; p >= 0;) {//先对偶数排处理,从大到小
                        if(p == 0) {
                            stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+even.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+even.get(p)).setWORKFLOW(1+"")
                            p = p-1
                        } else {
                            if(even.get(p)-2 == even.get(p-1)) {
                                stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+even.get(p)).setMOVE_ORDER(seq)
                                stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+even.get(p-1)).setMOVE_ORDER(seq)
                                stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+even.get(p)).setWORKFLOW(3+"")
                                stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+even.get(p-1)).setWORKFLOW(3+"")
                                p = p-2
                            } else {
                                stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+even.get(p)).setMOVE_ORDER(seq)
                                stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+even.get(p)).setWORKFLOW(1+"")
                                p = p-1
                            }
                        }
                        seq++;
                    }
                    for(int p = 0; p < odd.size();) {//再对奇数排处理，从小到大
                        if(p == odd.size()-1) {
                            stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+odd.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+odd.get(p)).setWORKFLOW(1+"")
                            p++
                        } else {
                            if(odd.get(p)+2 == odd.get(p+1)) {
                                stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+odd.get(p)).setMOVE_ORDER(seq)
                                stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+odd.get(p+1)).setMOVE_ORDER(seq)
                                stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+odd.get(p)).setWORKFLOW(3+"")
                                stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+odd.get(p+1)).setWORKFLOW(3+"")
                                p = p+2
                            } else {
                                stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+odd.get(p)).setMOVE_ORDER(seq)
                                stringListMap_D_40.get(largeBay.get(0)+"."+tiers.get(i)+"."+odd.get(p)).setWORKFLOW(1+"")
                                p = p+1
                            }
                        }
                        seq++;
                    }
                }
                //保存处理结果
                for(Map.Entry<String, PreStowageData> entry : stringListMap_D_40) {
                    resultList.add(entry.getValue())
                }
                for(Map.Entry<String, PreStowageData> entry : stringListMap_L_40) {
                    resultList.add(entry.getValue())
                }
                //清空数据
                VRW_ROWNO_D_40.clear()
                VRW_ROWNO_L_40.clear()
                stringListMap_D_40.clear()
                stringListMap_L_40.clear()
            }
        }
        //02倍，卸船结束

        //01倍卸船,开始
        tiers.clear()
        VTR_TIERNOss.clear()
        for(PreStowageData preStowageData : preStowageDataList01) {
            if(!VTR_TIERNOss.contains(preStowageData.getVTR_TIERNO())) {
                VTR_TIERNOss.add(preStowageData.getVTR_TIERNO())
                tiers.add(Integer.valueOf(preStowageData.getVTR_TIERNO()))
            }//统计层数
        }
        Collections.sort(tiers)
        for(int n = tiers.size()-1; n >= 0; n--) {
           int tier = tiers.get(n)
            for(PreStowageData preStowageData : preStowageDataList01) {
                if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                        "D".equals(preStowageData.getLDULD()) && "20".equals(preStowageData.getSIZE())) {//01小倍卸船的箱子取出
                    VRW_ROWNO_D_20.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                    String key = Integer.valueOf(preStowageData.getVBY_BAYID()) + "." +Integer.valueOf(preStowageData.getVTR_TIERNO()) + "." + Integer.valueOf(preStowageData.getVRW_ROWNO())
                    stringListMap_D_20.put(key, preStowageData)
                }
            }
            if(tier < 50) {
                //先处理卸船，全是单吊具，甲板下由中央向两边
                Collections.sort(VRW_ROWNO_D_20)
                println tier+"  20尺卸船的排号有："+VRW_ROWNO_D_20
                for(int i = 0; i < VRW_ROWNO_D_20.size(); i++) {
                    stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(i)).setMOVE_ORDER(seq)
                    stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(i)).setWORKFLOW(1+"")
                    seq++
                }
            }
            if(tier > 50) {
                //先处理卸船，全是单吊具，甲板上由陆侧向海侧
                List<Integer> even = new ArrayList<>()//排号为偶数
                List<Integer> odd = new ArrayList<>()//排号为奇数
                println tier+"  20尺卸船的排号有："+VRW_ROWNO_D_20
                for(Integer rowNum : VRW_ROWNO_D_20) {
                    if(rowNum%2 == 0) {
                        even.add(rowNum)
                    } else {
                        odd.add(rowNum)
                    }
                }
                Collections.sort(even)
                Collections.sort(odd)
                for(int j = even.size()-1; j >= 0; j--) {//先对偶数排处理,从大到小;甲板上卸船编顺序，由陆侧向海侧
                    stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                    stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                    seq++
                }
                for(int j = 0; j < odd.size(); j++) {//再对奇数排处理，从小到大
                    stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                    stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                    seq++
                }
            }
            //保存处理结果
            for(Map.Entry<String, PreStowageData> entry : stringListMap_D_20) {
                resultList.add(entry.getValue())
            }
            for(Map.Entry<String, PreStowageData> entry : stringListMap_L_20) {
                resultList.add(entry.getValue())
            }
            //清空数据
            VRW_ROWNO_D_20.clear()
            VRW_ROWNO_L_20.clear()
            stringListMap_D_20.clear()
            stringListMap_L_20.clear()
        }
        //01倍,结束

        //03倍卸船,开始
        tiers.clear()
        VTR_TIERNOss.clear()
        for(PreStowageData preStowageData : preStowageDataList03) {
            if(!VTR_TIERNOss.contains(preStowageData.getVTR_TIERNO())) {
                VTR_TIERNOss.add(preStowageData.getVTR_TIERNO())
                tiers.add(Integer.valueOf(preStowageData.getVTR_TIERNO()))
            }//统计层数
        }
        Collections.sort(tiers)
        for(int n = tiers.size()-1; n >= 0; n--) {
            int tier = tiers.get(n)
            for(PreStowageData preStowageData : preStowageDataList03) {
                if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                        "D".equals(preStowageData.getLDULD()) && "20".equals(preStowageData.getSIZE())) {//01小倍卸船的箱子取出
                    VRW_ROWNO_D_20.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                    String key = Integer.valueOf(preStowageData.getVBY_BAYID()) + "." +Integer.valueOf(preStowageData.getVTR_TIERNO()) + "." + Integer.valueOf(preStowageData.getVRW_ROWNO())
                    stringListMap_D_20.put(key, preStowageData)
                }
            }
            if(tier < 50) {
                //先处理卸船，全是单吊具，甲板下由中央向两边
                Collections.sort(VRW_ROWNO_D_20)
                println tier+"  20尺卸船的排号有："+VRW_ROWNO_D_20
                for(int i = 0; i <VRW_ROWNO_D_20.size(); i++) {
                    stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_D_20.get(i)).setMOVE_ORDER(seq)
                    stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_D_20.get(i)).setWORKFLOW(1+"")
                    seq++
                }
            }
            if(tier > 50) {
                //先处理卸船，全是单吊具，甲板上由陆侧向海侧
                List<Integer> even = new ArrayList<>()//排号为偶数
                List<Integer> odd = new ArrayList<>()//排号为奇数
                println tier+"  20尺卸船的排号有："+VRW_ROWNO_D_20
                for(Integer rowNum : VRW_ROWNO_D_20) {
                    if(rowNum%2 == 0) {
                        even.add(rowNum)
                    } else {
                        odd.add(rowNum)
                    }
                }
                Collections.sort(even)
                Collections.sort(odd)
                for(int j = even.size()-1; j >= 0; j--) {//先对偶数排处理,从大到小;甲板上卸船编顺序，由陆侧向海侧
                    stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                    stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                    seq++
                }
                for(int j = 0; j < odd.size(); j++) {//再对奇数排处理，从小到大
                    stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                    stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                    seq++
                }

            }
            //保存处理结果
            for(Map.Entry<String, PreStowageData> entry : stringListMap_D_20) {
                resultList.add(entry.getValue())
            }
            //清空数据
            VRW_ROWNO_D_20.clear()
            stringListMap_D_20.clear()
        }
        //03倍卸船,结束

        //01倍装船，开始
        tiers.clear()
        VTR_TIERNOss.clear()
        for(PreStowageData preStowageData : preStowageDataList01) {
            if(!VTR_TIERNOss.contains(preStowageData.getVTR_TIERNO())) {
                VTR_TIERNOss.add(preStowageData.getVTR_TIERNO())
                tiers.add(Integer.valueOf(preStowageData.getVTR_TIERNO()))
            }//统计层数
        }
        Collections.sort(tiers)
        for(int n = 0; n <= tiers.size()-1; n++) {
            int tier = tiers.get(n)
            for(PreStowageData preStowageData : preStowageDataList01) {
                if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                        "L".equals(preStowageData.getLDULD()) && "20".equals(preStowageData.getSIZE())) {//将同一层、01小倍装船的船箱位取出
                    VRW_ROWNO_L_20.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                    String key = Integer.valueOf(preStowageData.getVBY_BAYID()) + "." +Integer.valueOf(preStowageData.getVTR_TIERNO()) + "." + Integer.valueOf(preStowageData.getVRW_ROWNO())
                    stringListMap_L_20.put(key, preStowageData)
                }
            }
            if(tier < 50) {
                //处理装船,全是单吊具，甲板下由中央向两边
                Collections.sort(VRW_ROWNO_L_20)
                println tier+"  20尺装船的排号有："+VRW_ROWNO_L_20
                for(int i = 0; i <VRW_ROWNO_L_20.size(); i++) {
                    stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(i)).setMOVE_ORDER(seq)
                    stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(i)).setWORKFLOW(1+"")
                    seq++
                }
            }
            if(tier > 50) {
                //先处理卸船，全是单吊具，甲板上由陆侧向海侧
                List<Integer> even = new ArrayList<>()//排号为偶数
                List<Integer> odd = new ArrayList<>()//排号为奇数//
                //处理装船,全是单吊具，甲板上由海侧向陆侧
                println tier+"  20尺装船的排号有："+VRW_ROWNO_L_20
                even.clear()
                odd.clear()
                for(Integer rowNum : VRW_ROWNO_L_20) {
                    if(rowNum%2 == 0) {
                        even.add(rowNum)
                    } else {
                        odd.add(rowNum)
                    }
                }
                Collections.sort(even)
                Collections.sort(odd)
                for(int j = odd.size()-1; j >= 0; j--) {//先对奇数排处理,从大到小;甲板上装船编顺序，由海侧向陆侧
                    stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                    stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                    seq++
                }
                for(int j = 0; j <even.size(); j++) {//再对偶数排处理，从小到大
                    stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                    stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                    seq++
                }
            }
            //保存处理结果
            for(Map.Entry<String, PreStowageData> entry : stringListMap_L_20) {
                resultList.add(entry.getValue())
            }
            //清空数据
            VRW_ROWNO_L_20.clear()
            stringListMap_L_20.clear()
        }
        //01倍装船，结束

        //03倍装船，开始
        tiers.clear()
        VTR_TIERNOss.clear()
        for(PreStowageData preStowageData : preStowageDataList03) {
            if(!VTR_TIERNOss.contains(preStowageData.getVTR_TIERNO())) {
                VTR_TIERNOss.add(preStowageData.getVTR_TIERNO())
                tiers.add(Integer.valueOf(preStowageData.getVTR_TIERNO()))
            }//统计层数
        }
        Collections.sort(tiers)
        for(Integer tier : tiers) {
            for(PreStowageData preStowageData : preStowageDataList03) {
                if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                        "L".equals(preStowageData.getLDULD()) && "20".equals(preStowageData.getSIZE())) {//将同一层、01小倍装船的船箱位取出
                    VRW_ROWNO_L_20.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                    String key = Integer.valueOf(preStowageData.getVBY_BAYID()) + "." +Integer.valueOf(preStowageData.getVTR_TIERNO()) + "." + Integer.valueOf(preStowageData.getVRW_ROWNO())
                    stringListMap_L_20.put(key, preStowageData)
                }
            }
            if(tier < 50) {
                //处理20尺装船,全是单吊具，甲板下由中央向两边
                Collections.sort(VRW_ROWNO_L_20)
                println tier+"  20尺装船的排号有："+VRW_ROWNO_L_20
                for(int i = 0; i <VRW_ROWNO_L_20.size(); i++) {
                    stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_L_20.get(i)).setMOVE_ORDER(seq)
                    stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_L_20.get(i)).setWORKFLOW(1+"")
                    seq++
                }
            }
            if(tier > 50) {
                List<Integer> even = new ArrayList<>()//排号为偶数
                List<Integer> odd = new ArrayList<>()//排号为奇数
                //处理20尺装船,全是单吊具，甲板上由海侧向陆侧
                println tier+"  20尺装船的排号有："+VRW_ROWNO_L_20
                even.clear()
                odd.clear()
                for(Integer rowNum : VRW_ROWNO_L_20) {
                    if(rowNum%2 == 0) {
                        even.add(rowNum)
                    } else {
                        odd.add(rowNum)
                    }
                }
                Collections.sort(even)
                Collections.sort(odd)
                for(int j = odd.size()-1; j >= 0; j--) {//先对奇数排处理,从大到小;甲板上装船编顺序，由海侧向陆侧
                    stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                    stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                    seq++
                }
                for(int j = 0; j <even.size(); j++) {//再对偶数排处理，从小到大
                    stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                    stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                    seq++
                }
            }
            //保存处理结果
            for(Map.Entry<String, PreStowageData> entry : stringListMap_L_20) {
                resultList.add(entry.getValue())
            }
            //清空数据
            VRW_ROWNO_L_20.clear()
            stringListMap_L_20.clear()
        }
        //03倍装船，结束

        //02倍，装船开始
        tiers.clear()
        VTR_TIERNOss.clear()
        if(preStowageDataList02.size() > 0) {
            for(PreStowageData preStowageData : preStowageDataList02) {
                if(!VTR_TIERNOss.contains(preStowageData.getVTR_TIERNO())) {
                    VTR_TIERNOss.add(preStowageData.getVTR_TIERNO())
                    tiers.add(Integer.valueOf(preStowageData.getVTR_TIERNO()))
                }//统计层数
            }
            Collections.sort(tiers)
            for(Integer tier : tiers) {
                for(PreStowageData preStowageData : preStowageDataList02) {
                    if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                            "L".equals(preStowageData.getLDULD()) && ("40".equals(preStowageData.getSIZE()) || "45".equals(preStowageData.getSIZE()))) {//将同一层、卸船的船箱位取出
                        VRW_ROWNO_L_40.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                        String key = Integer.valueOf(preStowageData.getVBY_BAYID()) + "." +Integer.valueOf(preStowageData.getVTR_TIERNO()) + "." + Integer.valueOf(preStowageData.getVRW_ROWNO())
                        stringListMap_L_40.put(key, preStowageData)
                    }
                }
                if(tier < 50) {
                    //装40或45尺的箱子，甲板下由中央向两边
                    Collections.sort(VRW_ROWNO_L_40)
                    println tier+"  40尺装船的排号有："+VRW_ROWNO_D_40
                    for(Integer row : VRW_ROWNO_L_40) {
                        stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+row).setMOVE_ORDER(seq)
                        stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+row).setWORKFLOW(1+"")
                        seq++
                    }
                }
                if(tier > 50) {
                    List<Integer> even = new ArrayList<>()//排号为偶数
                    List<Integer> odd = new ArrayList<>()//排号为奇数
                    //再装40尺的箱子
                    println tier+"  40尺装船的排号有："+VRW_ROWNO_L_40
                    even.clear()
                    odd.clear()
                    for(Integer rowNum : VRW_ROWNO_L_40) {
                        if(rowNum%2 == 0) {
                            even.add(rowNum)
                        } else {
                            odd.add(rowNum)
                        }
                    }
                    Collections.sort(even)
                    Collections.sort(odd)
                    for(int p = odd.size()-1; p >= 0;) {//先对奇数排处理,从大到小
                        if(p == 0) {
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                            p = p-1
                        } else {
                            if(odd.get(p)-2 == odd.get(p-1)) {
//                                        println "倍.层.排"+VBY_BAYIDs.get(1)+"."+tier+"."+odd1.get(p)
                                stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                                stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p-1)).setMOVE_ORDER(seq)
                                stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(3+"")
                                stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p-1)).setWORKFLOW(3+"")
                                p = p-2
                            } else {
                                stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                                stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                                p = p-1
                            }
                        }
                        seq++;
                    }
                    for(int p = 0; p < even.size(); ) {//再对偶数排处理，从小到大
                        if(p == even.size()-1) {
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                            p++
                        } else {
                            if(even.get(p)+2 == even.get(p+1)) {
                                stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                                stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p+1)).setMOVE_ORDER(seq)
                                stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(3+"")
                                stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p+1)).setWORKFLOW(3+"")
                                p = p+2
                            } else {
                                stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                                stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                                p = p+1
                            }
                        }
                        seq++;
                    }
                }
                //保存处理结果
                for(Map.Entry<String, PreStowageData> entry : stringListMap_L_40) {
                    resultList.add(entry.getValue())
                }
                //清空数据
                VRW_ROWNO_L_40.clear()
                stringListMap_L_40.clear()
            }
        }
        //02倍，装船结束
    }

    public static List<PreStowageData> workBayDischarge(int seq, List<PreStowageData> dataList, List<Integer> smallBay, List<Integer> largeBay, List<Integer> VTR_TIERNOs,
                                      List<Integer> VRW_ROWNO_D_20, Map<String, PreStowageData> stringListMap_D_20,
                                      List<Integer> VRW_ROWNO_L_20, Map<String, PreStowageData> stringListMap_L_20,
                                      List<Integer> VRW_ROWNO_D_40, Map<String, PreStowageData> stringListMap_D_40,
                                      List<Integer> VRW_ROWNO_L_40, Map<String, PreStowageData> stringListMap_L_40,
                                      List<PreStowageData> resultList) {
        List<PreStowageData> resultReturn = new ArrayList<>()

        for(int n = VTR_TIERNOs.size()-1; n >= 0; n--) {
            int tier = VTR_TIERNOs.get(n)
            for(PreStowageData preStowageData : dataList) {//分别将20尺、40尺的卸船和装船的数据取出来，分开计算
                if(VTR_TIERNOs.get(n) == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                        "D".equals(preStowageData.getLDULD()) && "20".equals(preStowageData.getSIZE())) {//将同一层、卸船的船箱位取出
                    VRW_ROWNO_D_20.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                    String key = Integer.valueOf(preStowageData.getVBY_BAYID()) + "." +Integer.valueOf(preStowageData.getVTR_TIERNO()) + "." + Integer.valueOf(preStowageData.getVRW_ROWNO())
                    stringListMap_D_20.put(key, preStowageData)
                }
                if(VTR_TIERNOs.get(n) == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                        "D".equals(preStowageData.getLDULD()) && ("40".equals(preStowageData.getSIZE()) || "45".equals(preStowageData.getSIZE()))) {//将同一层、卸船的船箱位取出
                    VRW_ROWNO_D_40.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                    String key = Integer.valueOf(preStowageData.getVBY_BAYID()) + "." +Integer.valueOf(preStowageData.getVTR_TIERNO()) + "." + Integer.valueOf(preStowageData.getVRW_ROWNO())
                    stringListMap_D_40.put(key, preStowageData)
                }
            }
            if(tier < 50) {//甲板下
                //先对同一层卸船的舱位进行处理，开始
                List<Integer> even = new ArrayList<>()//排号为偶数
                List<Integer> odd = new ArrayList<>()//排号为奇数
                //先卸40尺的箱子
                println tier+"  40尺卸船的排号有："+VRW_ROWNO_D_40
                for(Integer rowNum : VRW_ROWNO_D_40) {
                    if(rowNum%2 == 0) {
                        even.add(rowNum)
                    } else {
                        odd.add(rowNum)
                    }
                }
                Collections.sort(even)
                Collections.sort(odd)
                for(int p = 0; p < odd.size(); ) {//先对奇数排处理，从小到大
                    if(p == odd.size()-1) {
                        stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                        stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                        p++
                    } else {
                        if(odd.get(p)+2 == odd.get(p+1)) {
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p+1)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(3+"")
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p+1)).setWORKFLOW(3+"")
                            p = p+2
                        } else {
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                            p = p+1
                        }
                    }
                    seq++;
                }
                for(int p = 0; p < even.size();) {//再对偶数排处理,从小到大
                    if(p == even.size()-1) {
                        stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                        stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                        p = p+1
                    } else {
                        if(even.get(p)+2 == even.get(p+1)) {
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p+1)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(3+"")
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p+1)).setWORKFLOW(3+"")
                            p = p+2
                        } else {
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                            p = p+1
                        }
                    }
                    seq++;
                }

                //再卸20尺的箱子
                println tier+"  20尺卸船的排号有："+VRW_ROWNO_D_20
                even.clear()
                odd.clear()
                Collections.sort(VRW_ROWNO_D_20)//排序
                for(int j = 0; j < VRW_ROWNO_D_20.size(); ) {//从小到大，甲板下由中央向两边
                    if(j == VRW_ROWNO_D_20.size()-1) {
                        if(stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)) != null) {
                            stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setWORKFLOW(1+"")
                        } else {
                            stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setWORKFLOW(1+"")
                        }
                        j = j+1
                    } else {
                        if(VRW_ROWNO_D_20.get(j) == VRW_ROWNO_D_20.get(j+1)) {//同一层、同一排，倍位肯定不相等，则用双吊具2
                            stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_D_20.get(j+1)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setWORKFLOW(2+"")
                            stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_D_20.get(j+1)).setWORKFLOW(2+"")
                            j = j+2
                        } else {//同一层、不同排，对20尺的箱子来说用单吊具1
                            if(stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)) != null) {
                                stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setMOVE_ORDER(seq)
                                stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setWORKFLOW(1+"")
                            } else {
                                stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setMOVE_ORDER(seq)
                                stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setWORKFLOW(1+"")
                            }
                            j = j+1
                        }
                    }
                    seq++
                }
                //对同一层卸船的舱位进行处理，结束
            }
            if(tier > 50) {//甲板上
                //先对同一层卸船的舱位进行处理，开始
                List<Integer> even = new ArrayList<>()//排号为偶数
                List<Integer> odd = new ArrayList<>()//排号为奇数
                //先卸40尺的箱子
                println tier+"  40尺卸船的排号有："+VRW_ROWNO_D_40
                for(Integer rowNum : VRW_ROWNO_D_40) {
                    if(rowNum%2 == 0) {
                        even.add(rowNum)
                    } else {
                        odd.add(rowNum)
                    }
                }
                Collections.sort(even)
                Collections.sort(odd)
                for(int p = even.size()-1; p >= 0;) {//先对偶数排处理,从大到小
                    if(p == 0) {
                        stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                        stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                        p = p-1
                    } else {
                        if(even.get(p)-2 == even.get(p-1)) {
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p-1)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(3+"")
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p-1)).setWORKFLOW(3+"")
                            p = p-2
                        } else {
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                            p = p-1
                        }
                    }
                    seq++;
                }
                for(int p = 0; p < odd.size(); ) {//再对奇数排处理，从小到大
                    if(p == odd.size()-1) {
                        stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                        stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                        p++
                    } else {
                        if(odd.get(p)+2 == odd.get(p+1)) {
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p+1)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(3+"")
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p+1)).setWORKFLOW(3+"")
                            p = p+2
                        } else {
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                            p = p+1
                        }
                    }
                    seq++;
                }

                //再卸20尺的箱子
                println tier+"  20尺卸船的排号有："+VRW_ROWNO_D_20
                even.clear()
                odd.clear()
                for(Integer rowNum : VRW_ROWNO_D_20) {
                    if(rowNum%2 == 0) {
                        even.add(rowNum)
                    } else {
                        odd.add(rowNum)
                    }
                }
                Collections.sort(even)
                Collections.sort(odd)
                for(int j = even.size()-1; j >= 0;) {//先对偶数排处理,从大到小;甲板上卸船编顺序，由陆侧向海侧
                    if(j == 0) {
                        if(stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)) != null) {
                            stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                        } else {
                            stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                        }
                        j = j-1
                    } else {
                        if(even.get(j) == even.get(j-1)) {//同一层、同一排，倍位肯定不相等，则用双吊具2
                            stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+even.get(j-1)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(2+"")
                            stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+even.get(j-1)).setWORKFLOW(2+"")
                            j = j-2
                        } else {//同一层、不同排，对20尺的箱子来说用单吊具1
                            if(stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)) != null) {
                                stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                            } else {
                                stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                            }
                            j = j-1
                        }
                    }
                    seq++
                }
                for(int j = 0; j <odd.size(); ) {//再对奇数排处理，从小到大
                    if(j == odd.size()-1) {
                        if(stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)) != null) {
                            stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                        } else {
                            stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                        }
                        j = j+1
                    } else {
                        if(odd.get(j) == odd.get(j+1)) {//同一层、同一排，倍位肯定不相等，则用双吊具2
                            stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j+1)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(2+"")
                            stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j+1)).setWORKFLOW(2+"")
                            j = j+2
                        } else {//同一层、不同排，对20尺的箱子来说用单吊具1
                            if(stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)) != null) {
                                stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                stringListMap_D_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                            } else {
                                stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                stringListMap_D_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                            }
                            j = j+1
                        }
                    }
                    seq++
                }
                //对同一层卸船的舱位进行处理，结束
            }
            //保存处理结果

            for(Map.Entry<String, PreStowageData> entry : stringListMap_D_20) {
                resultList.add(entry.getValue())
                resultReturn.add(entry.getValue())
            }
            for(Map.Entry<String, PreStowageData> entry : stringListMap_D_40) {
                resultList.add(entry.getValue())
                resultReturn.add(entry.getValue())
            }
            //清空数据
            VRW_ROWNO_D_20.clear()
            VRW_ROWNO_D_40.clear()
            stringListMap_D_20.clear()
            stringListMap_D_40.clear()
        }
        return resultReturn
    }


    public static List<PreStowageData> workBayLoad(int seq, List<PreStowageData> dataList, List<Integer> smallBay, List<Integer> largeBay, List<Integer> VTR_TIERNOs,
                                                         List<Integer> VRW_ROWNO_D_20, Map<String, PreStowageData> stringListMap_D_20,
                                                         List<Integer> VRW_ROWNO_L_20, Map<String, PreStowageData> stringListMap_L_20,
                                                         List<Integer> VRW_ROWNO_D_40, Map<String, PreStowageData> stringListMap_D_40,
                                                         List<Integer> VRW_ROWNO_L_40, Map<String, PreStowageData> stringListMap_L_40,
                                                         List<PreStowageData> resultList) {
        List<PreStowageData> resultReturn = new ArrayList<>()
        for(Integer tier : VTR_TIERNOs) {
            for(PreStowageData preStowageData : dataList) {//分别将20尺、40尺的卸船和装船的数据取出来，分开计算
                if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                        "L".equals(preStowageData.getLDULD()) && "20".equals(preStowageData.getSIZE())) {//将同一层、卸船的船箱位取出
                    VRW_ROWNO_L_20.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                    String key = Integer.valueOf(preStowageData.getVBY_BAYID()) + "." +Integer.valueOf(preStowageData.getVTR_TIERNO()) + "." + Integer.valueOf(preStowageData.getVRW_ROWNO())
                    //println "fffff:" +key
                    stringListMap_L_20.put(key, preStowageData)
                }
                if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                        "L".equals(preStowageData.getLDULD()) && ("40".equals(preStowageData.getSIZE()) || "45".equals(preStowageData.getSIZE()))) {//将同一层、卸船的船箱位取出
                    VRW_ROWNO_L_40.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                    String key = Integer.valueOf(preStowageData.getVBY_BAYID()) + "." +Integer.valueOf(preStowageData.getVTR_TIERNO()) + "." + Integer.valueOf(preStowageData.getVRW_ROWNO())
                    //println "sdafjlskadjfksjf"+key
                    stringListMap_L_40.put(key, preStowageData)
                }
            }
            if(tier < 50) {//甲板下
                //对同一层装船的舱位进行处理，甲板下由中央向两边,开始
                //先装20尺的箱子
                println tier+"  20尺装船的排号有："+VRW_ROWNO_L_20
                Collections.sort(VRW_ROWNO_L_20)//排序
                for(int j = 0; j < VRW_ROWNO_L_20.size(); ) {//从小到大，甲板下由中央向两边
                    if(j == VRW_ROWNO_L_20.size()-1) {
                        if(stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)) != null) {
                            stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setMOVE_ORDER(seq)
                            stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setWORKFLOW(1+"")
                        } else {
                            stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setMOVE_ORDER(seq)
                            stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setWORKFLOW(1+"")
                        }
                        j = j+1
                    } else {
                        if(VRW_ROWNO_L_20.get(j) == VRW_ROWNO_L_20.get(j+1)) {//同一层、同一排，倍位肯定不相等，则用双吊具2
                            stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setMOVE_ORDER(seq)
                            stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_L_20.get(j+1)).setMOVE_ORDER(seq)
                            stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setWORKFLOW(2+"")
                            stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_L_20.get(j+1)).setWORKFLOW(2+"")
                            j = j+2
                        } else {//同一层、不同排，对20尺的箱子来说用单吊具1
                            if(stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)) != null) {
                                stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setMOVE_ORDER(seq)
                                stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setWORKFLOW(1+"")
                            } else {
                                stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setMOVE_ORDER(seq)
                                stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setWORKFLOW(1+"")
                            }
                            j = j+1
                        }
                    }
                    seq++
                }
                //再装40尺的箱子,（全部用单吊具1排）
                println tier+"  40尺装船的排号有："+VRW_ROWNO_L_40
                Collections.sort(VRW_ROWNO_L_40)
                for(int p = 0; p < VRW_ROWNO_L_40.size(); p++) {//从小到大
                    stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+VRW_ROWNO_L_40.get(p)).setMOVE_ORDER(seq)
                    stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+VRW_ROWNO_L_40.get(p)).setWORKFLOW(1+"")
                    seq++;
                }
                //对同一层装船的舱位进行处理，结束
            }
            if(tier > 50) {//甲板上
                List<Integer> even = new ArrayList<>()//排号为偶数
                List<Integer> odd = new ArrayList<>()//排号为奇数
                //再对同一层装船的舱位进行处理，甲板上装船编顺序，由海侧向陆侧,开始
                //先装20尺的箱子
                println tier+"  20尺装船的排号有："+VRW_ROWNO_L_20
                even.clear()
                odd.clear()
                for(Integer rowNum : VRW_ROWNO_L_20) {
                    if(rowNum%2 == 0) {
                        even.add(rowNum)
                    } else {
                        odd.add(rowNum)
                    }
                }
                Collections.sort(even)
                Collections.sort(odd)
                for(int j = odd.size()-1; j >= 0;) {//先对奇数排处理,从大到小;甲板上装船编顺序，由海侧向陆侧
                    if(j == 0) {
                        if(stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)) != null) {
                            stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                            stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                        } else {
                            stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                            stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                        }
                        j = j-1
                    } else {
                        if(odd.get(j) == odd.get(j-1)) {//同一层、同一排，倍位肯定不相等，则用双吊具2
                            stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                            stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j-1)).setMOVE_ORDER(seq)
                            stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(2+"")
                            stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j-1)).setWORKFLOW(2+"")
                            j = j-2
                        } else {//同一层、不同排，对20尺的箱子来说用单吊具1
                            if(stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)) != null) {
                                stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                            } else {
                                stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                            }
                            j = j-1
                        }
                    }
                    seq++
                }
                for(int j = 0; j <even.size(); ) {//再对偶数排处理，从小到大
                    if(j == even.size()-1) {
                        if(stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)) != null) {
                            stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                            stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                        } else {
                            stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                            stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                        }
                        j = j+1
                    } else {
                        if(even.get(j) == even.get(j+1)) {//同一层、同一排，倍位肯定不相等，则用双吊具2
                            stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                            stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+even.get(j+1)).setMOVE_ORDER(seq)
                            stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(2+"")
                            stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+even.get(j+1)).setWORKFLOW(2+"")
                            j = j+2
                        } else {//同一层、不同排，对20尺的箱子来说用单吊具1
                            if(stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)) != null) {
                                stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                stringListMap_L_20.get(smallBay.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                            } else {
                                stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                stringListMap_L_20.get(smallBay.get(1)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                            }
                            j = j+1
                        }
                    }
                    seq++
                }
                //再装40尺的箱子
                println tier+"  40尺装船的排号有："+VRW_ROWNO_L_40
                even.clear()
                odd.clear()
                for(Integer rowNum : VRW_ROWNO_L_40) {
                    if(rowNum%2 == 0) {
                        even.add(rowNum)
                    } else {
                        odd.add(rowNum)
                    }
                }
                Collections.sort(even)
                Collections.sort(odd)
                for(int p = odd.size()-1; p >= 0;) {//先对奇数排处理,从大到小
                    if(p == 0) {
                        stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                        stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                        p = p-1
                    } else {
                        if(odd.get(p)-2 == odd.get(p-1)) {
//                                        println "倍.层.排"+VBY_BAYIDs.get(1)+"."+tier+"."+odd1.get(p)
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p-1)).setMOVE_ORDER(seq)
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(3+"")
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p-1)).setWORKFLOW(3+"")
                            p = p-2
                        } else {
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                            p = p-1
                        }
                    }
                    seq++;
                }
                for(int p = 0; p < even.size(); ) {//再对偶数排处理，从小到大
                    if(p == even.size()-1) {
                        stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                        stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                        p++
                    } else {
                        if(even.get(p)+2 == even.get(p+1)) {
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p+1)).setMOVE_ORDER(seq)
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(3+"")
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p+1)).setWORKFLOW(3+"")
                            p = p+2
                        } else {
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                            stringListMap_L_40.get(largeBay.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                            p = p+1
                        }
                    }
                    seq++;
                }
                //对同一层装船的舱位进行处理，结束
            }
            //保存处理结果
            for(Map.Entry<String, PreStowageData> entry : stringListMap_L_20) {
                resultList.add(entry.getValue())
                resultReturn.add(entry.getValue())
            }
            for(Map.Entry<String, PreStowageData> entry : stringListMap_L_40) {
                resultList.add(entry.getValue())
                resultReturn.add(entry.getValue())
            }
            //清空数据
            VRW_ROWNO_L_20.clear()
            VRW_ROWNO_L_40.clear()
            stringListMap_L_20.clear()
            stringListMap_L_40.clear()
        }
        return resultReturn
    }
}
