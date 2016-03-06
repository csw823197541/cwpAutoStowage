package GenerateResult

import importDataInfo.PreStowageData

/**
 * Created by csw on 2016/3/5.
 */
class GeneratePreStowageFromKnowStowage {

    /**
     * 根据实配图生成预配图，生成作业序列和作业工艺的方法
     * @param preStowageDataList
     * @return
     */
    public static List<PreStowageData> getPreStowageResult(List<PreStowageData> preStowageDataList) {
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
//        List<PreStowageData> dataList1 = new ArrayList<>()
        for(String str : VHTIDs) {//
            List<PreStowageData> dataList1 = new ArrayList<>()
            for(PreStowageData preStowageData : preStowageDataListNew) {
                if(str.equals(preStowageData.getVHT_ID())) {
                    dataList1.add(preStowageData)
                }
            }
            stringListMap.put(str, dataList1)
//            dataList1.clear()
        }
        int i = 0;
        for(String str : VHTIDs) {//对每个舱进行作业序列和作业工艺的生成
            if(i++ >= 0) {
                int seq = 1
                List<PreStowageData> dataList = stringListMap.get(str)
                println "倍位"+str+"有"+dataList.size()+"个船箱位"
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
                if(VBY_BAYIDs.size() == 1) {//舱内只有一个倍，一般为40或45尺的箱子
                    this.workBayNumIsOne(seq, dataList, VBY_BAYIDs, VTR_TIERNOs, VRW_ROWNO_D_40, VRW_ROWNO_L_40,
                            stringListMap_D_40, stringListMap_L_40, resultList)
                }
                if(VBY_BAYIDs.size() == 2) {
                    this.workBayNumIsTwo(seq, dataList, VBY_BAYIDs, VTR_TIERNOs, VRW_ROWNO_D_20, stringListMap_D_20, resultList)
                }
                if(VBY_BAYIDs.size() == 3) {//有3个倍位的处理方法
                    for(Integer tier : VTR_TIERNOs) {
                        if(tier < 82) {//甲板下
                            for(PreStowageData preStowageData : dataList) {//分别将20尺、40尺的卸船和装船的数据取出来，分开计算
                                if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                                        "D".equals(preStowageData.getLDULD()) && "20".equals(preStowageData.getSIZE())) {//将同一层、卸船的船箱位取出
                                    VRW_ROWNO_D_20.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                                    String key = preStowageData.getVBY_BAYID() + "." +preStowageData.getVTR_TIERNO() + "." + preStowageData.getVRW_ROWNO()
                                    stringListMap_D_20.put(key, preStowageData)
                                }
                                if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                                        "D".equals(preStowageData.getLDULD()) && ("40".equals(preStowageData.getSIZE()) || "45".equals(preStowageData.getSIZE()))) {//将同一层、卸船的船箱位取出
                                    VRW_ROWNO_D_40.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                                    String key = preStowageData.getVBY_BAYID() + "." +preStowageData.getVTR_TIERNO() + "." + preStowageData.getVRW_ROWNO()
                                    stringListMap_D_40.put(key, preStowageData)
                                }
                                if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                                        "L".equals(preStowageData.getLDULD()) && "20".equals(preStowageData.getSIZE())) {//将同一层、卸船的船箱位取出
                                    VRW_ROWNO_L_20.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                                    String key = preStowageData.getVBY_BAYID() + "." +preStowageData.getVTR_TIERNO() + "." + preStowageData.getVRW_ROWNO()
                                    stringListMap_L_20.put(key, preStowageData)
                                }
                                if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                                        "L".equals(preStowageData.getLDULD()) && ("40".equals(preStowageData.getSIZE()) || "45".equals(preStowageData.getSIZE()))) {//将同一层、卸船的船箱位取出
                                    VRW_ROWNO_L_40.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                                    String key = preStowageData.getVBY_BAYID() + "." +preStowageData.getVTR_TIERNO() + "." + preStowageData.getVRW_ROWNO()
                                    stringListMap_L_40.put(key, preStowageData)
                                }
                            }
                            //先对同一层卸船的舱位进行处理，开始
                            //先卸20尺的箱子
                            List<Integer> even = new ArrayList<>()//排号为偶数
                            List<Integer> odd = new ArrayList<>()//排号为奇数
                            println tier+"  20尺卸船的排号有："+VRW_ROWNO_D_20
                            Collections.sort(VRW_ROWNO_D_20)//排序
                            for(int j = 0; j < VRW_ROWNO_D_20.size(); ) {//从小到大，甲板下由中央向两边
                                if(j == VRW_ROWNO_D_20.size()-1) {
                                    if(stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)) != null) {
                                        stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setWORKFLOW(1+"")
                                    } else {
                                        stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setWORKFLOW(1+"")
                                    }
                                    j = j+1
                                } else {
                                    if(VRW_ROWNO_D_20.get(j) == VRW_ROWNO_D_20.get(j+1)) {//同一层、同一排，倍位肯定不相等，则用双吊具2
                                        stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+VRW_ROWNO_D_20.get(j+1)).setMOVE_ORDER(seq)
                                        stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setWORKFLOW(2+"")
                                        stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+VRW_ROWNO_D_20.get(j+1)).setWORKFLOW(2+"")
                                        j = j+2
                                    } else {//同一层、不同排，对20尺的箱子来说用单吊具1
                                        if(stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)) != null) {
                                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setMOVE_ORDER(seq)
                                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setWORKFLOW(1+"")
                                        } else {
                                            stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setMOVE_ORDER(seq)
                                            stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setWORKFLOW(1+"")
                                        }
                                        j = j+1
                                    }
                                }
                                seq++
                            }
                            //再卸40尺的箱子
                            println tier+"  40尺卸船的排号有："+VRW_ROWNO_D_40
                            even.clear()
                            odd.clear()
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
                                    stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                                    stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                                    p++
                                } else {
                                    if(odd.get(p)+2 == odd.get(p+1)) {
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p+1)).setMOVE_ORDER(seq)
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setWORKFLOW(3+"")
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p+1)).setWORKFLOW(3+"")
                                        p = p+2
                                    } else {
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                                        p = p+1
                                    }
                                }
                                seq++;
                            }
                            for(int p = 0; p < even.size();) {//再对偶数排处理,从小到大
                                if(p == even.size()-1) {
                                    stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                                    stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                                    p = p+1
                                } else {
                                    if(even.get(p)+2 == even.get(p+1)) {
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p+1)).setMOVE_ORDER(seq)
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setWORKFLOW(3+"")
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p+1)).setWORKFLOW(3+"")
                                        p = p+2
                                    } else {
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                                        p = p+1
                                    }
                                }
                                seq++;
                            }
                            //对同一层卸船的舱位进行处理，结束
                            //再对同一层装船的舱位进行处理，甲板下由中央向两边,开始
                            //先装20尺的箱子
                            println tier+"  20尺装船的排号有："+VRW_ROWNO_L_20
                            Collections.sort(VRW_ROWNO_L_20)//排序
                            for(int j = 0; j < VRW_ROWNO_L_20.size(); ) {//从小到大，甲板下由中央向两边
                                if(j == VRW_ROWNO_L_20.size()-1) {
                                    if(stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)) != null) {
                                        stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setWORKFLOW(1+"")
                                    } else {
                                        stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setWORKFLOW(1+"")
                                    }
                                    j = j+1
                                } else {
                                    if(VRW_ROWNO_L_20.get(j) == VRW_ROWNO_L_20.get(j+1)) {//同一层、同一排，倍位肯定不相等，则用双吊具2
                                        stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+VRW_ROWNO_L_20.get(j+1)).setMOVE_ORDER(seq)
                                        stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setWORKFLOW(2+"")
                                        stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+VRW_ROWNO_L_20.get(j+1)).setWORKFLOW(2+"")
                                        j = j+2
                                    } else {//同一层、不同排，对20尺的箱子来说用单吊具1
                                        if(stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)) != null) {
                                            stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setMOVE_ORDER(seq)
                                            stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setWORKFLOW(1+"")
                                        } else {
                                            stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setMOVE_ORDER(seq)
                                            stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+VRW_ROWNO_L_20.get(j)).setWORKFLOW(1+"")
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
                                stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+VRW_ROWNO_L_40.get(p)).setMOVE_ORDER(seq)
                                stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+VRW_ROWNO_L_40.get(p)).setWORKFLOW(1+"")
                                seq++;
                            }
                            //对同一层装船的舱位进行处理，结束
                        }
                        if(tier >= 82) {//甲板上
                            for(PreStowageData preStowageData : dataList) {//分别将20尺、40尺的卸船和装船的数据取出来，分开计算
                                if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                                        "D".equals(preStowageData.getLDULD()) && "20".equals(preStowageData.getSIZE())) {//将同一层、卸船的船箱位取出
                                    VRW_ROWNO_D_20.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                                    String key = preStowageData.getVBY_BAYID() + "." +preStowageData.getVTR_TIERNO() + "." + preStowageData.getVRW_ROWNO()
                                    stringListMap_D_20.put(key, preStowageData)
                                }
                                if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                                        "D".equals(preStowageData.getLDULD()) && ("40".equals(preStowageData.getSIZE()) || "45".equals(preStowageData.getSIZE()))) {//将同一层、卸船的船箱位取出
                                    VRW_ROWNO_D_40.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                                    String key = preStowageData.getVBY_BAYID() + "." +preStowageData.getVTR_TIERNO() + "." + preStowageData.getVRW_ROWNO()
                                    stringListMap_D_40.put(key, preStowageData)
                                }
                                if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                                        "L".equals(preStowageData.getLDULD()) && "20".equals(preStowageData.getSIZE())) {//将同一层、卸船的船箱位取出
                                    VRW_ROWNO_L_20.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                                    String key = preStowageData.getVBY_BAYID() + "." +preStowageData.getVTR_TIERNO() + "." + preStowageData.getVRW_ROWNO()
                                    stringListMap_L_20.put(key, preStowageData)
                                }
                                if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                                        "L".equals(preStowageData.getLDULD()) && ("40".equals(preStowageData.getSIZE()) || "45".equals(preStowageData.getSIZE()))) {//将同一层、卸船的船箱位取出
                                    VRW_ROWNO_L_40.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                                    String key = preStowageData.getVBY_BAYID() + "." +preStowageData.getVTR_TIERNO() + "." + preStowageData.getVRW_ROWNO()
                                    stringListMap_L_40.put(key, preStowageData)
                                }
                            }
                            //先对同一层卸船的舱位进行处理，开始
                            //先卸20尺的箱子
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
                            for(int j = even.size()-1; j >= 0;) {//先对偶数排处理,从大到小;甲板上卸船编顺序，由陆侧向海侧
                                if(j == 0) {
                                    if(stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)) != null) {
                                        stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                                    } else {
                                        stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                                    }
                                    j = j-1
                                } else {
                                    if(even.get(j) == even.get(j-1)) {//同一层、同一排，倍位肯定不相等，则用双吊具2
                                        stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+even.get(j-1)).setMOVE_ORDER(seq)
                                        stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(2+"")
                                        stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+even.get(j-1)).setWORKFLOW(2+"")
                                        j = j-2
                                    } else {//同一层、不同排，对20尺的箱子来说用单吊具1
                                        if(stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)) != null) {
                                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                                        } else {
                                            stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                            stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                                        }
                                        j = j-1
                                    }
                                }
                                seq++
                            }
                            for(int j = 0; j <odd.size(); ) {//再对奇数排处理，从小到大
                                if(j == odd.size()-1) {
                                    if(stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)) != null) {
                                        stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                                    } else {
                                        stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                                    }
                                    j = j+1
                                } else {
                                    if(odd.get(j) == odd.get(j+1)) {//同一层、同一排，倍位肯定不相等，则用双吊具2
                                        stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+odd.get(j+1)).setMOVE_ORDER(seq)
                                        stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(2+"")
                                        stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+odd.get(j+1)).setWORKFLOW(2+"")
                                        j = j+2
                                    } else {//同一层、不同排，对20尺的箱子来说用单吊具1
                                        if(stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)) != null) {
                                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                                        } else {
                                            stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                            stringListMap_D_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                                        }
                                        j = j+1
                                    }
                                }
                                seq++
                            }
                            //再卸40尺的箱子
                            println tier+"  40尺卸船的排号有："+VRW_ROWNO_D_40
                            even.clear()
                            odd.clear()
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
                                    stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                                    stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                                    p = p-1
                                } else {
                                    if(even.get(p)-2 == even.get(p-1)) {
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p-1)).setMOVE_ORDER(seq)
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setWORKFLOW(3+"")
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p-1)).setWORKFLOW(3+"")
                                        p = p-2
                                    } else {
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                                        p = p-1
                                    }
                                }
                                seq++;
                            }
                            for(int p = 0; p < odd.size(); ) {//再对奇数排处理，从小到大
                                if(p == odd.size()-1) {
                                    stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                                    stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                                    p++
                                } else {
                                    if(odd.get(p)+2 == odd.get(p+1)) {
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p+1)).setMOVE_ORDER(seq)
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setWORKFLOW(3+"")
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p+1)).setWORKFLOW(3+"")
                                        p = p+2
                                    } else {
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                                        stringListMap_D_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                                        p = p+1
                                    }
                                }
                                seq++;
                            }
                            //对同一层卸船的舱位进行处理，结束
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
                                    if(stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)) != null) {
                                        stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                                    } else {
                                        stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                                    }
                                    j = j-1
                                } else {
                                    if(odd.get(j) == odd.get(j-1)) {//同一层、同一排，倍位肯定不相等，则用双吊具2
                                        stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+odd.get(j-1)).setMOVE_ORDER(seq)
                                        stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(2+"")
                                        stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+odd.get(j-1)).setWORKFLOW(2+"")
                                        j = j-2
                                    } else {//同一层、不同排，对20尺的箱子来说用单吊具1
                                        if(stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)) != null) {
                                            stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                            stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                                        } else {
                                            stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                            stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                                        }
                                        j = j-1
                                    }
                                }
                                seq++
                            }
                            for(int j = 0; j <even.size(); ) {//再对偶数排处理，从小到大
                                if(j == even.size()-1) {
                                    if(stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)) != null) {
                                        stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                                    } else {
                                        stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                                    }
                                    j = j+1
                                } else {
                                    if(even.get(j) == even.get(j+1)) {//同一层、同一排，倍位肯定不相等，则用双吊具2
                                        stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                        stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+even.get(j+1)).setMOVE_ORDER(seq)
                                        stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(2+"")
                                        stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+even.get(j+1)).setWORKFLOW(2+"")
                                        j = j+2
                                    } else {//同一层、不同排，对20尺的箱子来说用单吊具1
                                        if(stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)) != null) {
                                            stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                            stringListMap_L_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                                        } else {
                                            stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                            stringListMap_L_20.get(VBY_BAYIDs.get(2)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
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
                                    stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                                    stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                                    p = p-1
                                } else {
                                    if(odd.get(p)-2 == odd.get(p-1)) {
//                                        println "倍.层.排"+VBY_BAYIDs.get(1)+"."+tier+"."+odd1.get(p)
                                        stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                                        stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p-1)).setMOVE_ORDER(seq)
                                        stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setWORKFLOW(3+"")
                                        stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p-1)).setWORKFLOW(3+"")
                                        p = p-2
                                    } else {
                                        stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                                        stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                                        p = p-1
                                    }
                                }
                                seq++;
                            }
                            for(int p = 0; p < even.size(); ) {//再对偶数排处理，从小到大
                                if(p == even.size()-1) {
                                    stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                                    stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                                    p++
                                } else {
                                    if(even.get(p)+2 == even.get(p+1)) {
                                        stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                                        stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p+1)).setMOVE_ORDER(seq)
                                        stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setWORKFLOW(3+"")
                                        stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p+1)).setWORKFLOW(3+"")
                                        p = p+2
                                    } else {
                                        stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                                        stringListMap_L_40.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                                        p = p+1
                                    }
                                }
                                seq++;
                            }
                            //对同一层装船的舱位进行处理，结束
                        }
                        //保存处理结果
                        for(Map.Entry<String, PreStowageData> entry : stringListMap_D_20) {
                            resultList.add(entry.getValue())
                        }
                        for(Map.Entry<String, PreStowageData> entry : stringListMap_D_40) {
                            resultList.add(entry.getValue())
                        }
                        for(Map.Entry<String, PreStowageData> entry : stringListMap_L_20) {
                            resultList.add(entry.getValue())
                        }
                        for(Map.Entry<String, PreStowageData> entry : stringListMap_L_40) {
                            resultList.add(entry.getValue())
                        }
                        //清空数据
                        VRW_ROWNO_D_20.clear()
                        VRW_ROWNO_D_40.clear()
                        VRW_ROWNO_L_20.clear()
                        VRW_ROWNO_L_40.clear()
                        stringListMap_D_20.clear()
                        stringListMap_D_40.clear()
                        stringListMap_L_20.clear()
                        stringListMap_L_40.clear()
                    }
                }
//                break;
            }
        }
        return resultList
    }

    public static void workBayNumIsOne(int seq, List<PreStowageData> dataList, List<Integer> VBY_BAYIDs, List<Integer> VTR_TIERNOs,
                                       List<Integer> VRW_ROWNO_D_40,List<Integer> VRW_ROWNO_L_40,
                                       Map<String, PreStowageData> stringListMap_D_40,
                                       Map<String, PreStowageData> stringListMap_L_40,List<PreStowageData> resultList) {
        for(Integer tier : VTR_TIERNOs) {
            if(tier < 82) {
                for(PreStowageData preStowageData : dataList) {//将40或45尺的卸船和装船的数据取出来，分开计算
                    if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                            "D".equals(preStowageData.getLDULD()) && ("40".equals(preStowageData.getSIZE()) || "45".equals(preStowageData.getSIZE()))) {//将同一层、卸船、40或45尺的船箱位取出
                        VRW_ROWNO_D_40.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                        String key = preStowageData.getVBY_BAYID() + "." +preStowageData.getVTR_TIERNO() + "." + preStowageData.getVRW_ROWNO()
                        stringListMap_D_40.put(key, preStowageData)
                    }
                    if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                            "L".equals(preStowageData.getLDULD()) && ("40".equals(preStowageData.getSIZE()) || "45".equals(preStowageData.getSIZE()))) {//将同一层、装船、40或45尺的船箱位取出
                        VRW_ROWNO_L_40.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                        String key = preStowageData.getVBY_BAYID() + "." +preStowageData.getVTR_TIERNO() + "." + preStowageData.getVRW_ROWNO()
                        stringListMap_L_40.put(key, preStowageData)
                    }
                }
                //先对同一层卸船的舱位进行处理，开始
                //卸40尺的箱子
                println tier+"  40尺卸船的排号有："+VRW_ROWNO_D_40
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
                for(int p = 0; p < odd.size(); ) {//先对奇数排处理，从小到大
                    if(p == odd.size()-1) {
                        stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                        stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                        p++
                    } else {
                        if(odd.get(p)+2 == odd.get(p+1)) {
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p+1)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(3+"")
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p+1)).setWORKFLOW(3+"")
                            p = p+2
                        } else {
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                            p = p+1
                        }
                    }
                    seq++;
                }
                for(int p = 0; p < even.size();) {//再对偶数排处理,从小到大
                    if(p == even.size()-1) {
                        stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                        stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                        p = p+1
                    } else {
                        if(even.get(p)+2 == even.get(p+1)) {
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p+1)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(3+"")
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p+1)).setWORKFLOW(3+"")
                            p = p+2
                        } else {
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                            p = p+1
                        }
                    }
                    seq++;
                }
                //对同一层卸船的舱位进行处理，结束
                //再对同一层装船的舱位进行处理，甲板下由中央向两边,开始
                //装40尺的箱子,（全部用单吊具1排）
                println tier+"  40尺装船的排号有："+VRW_ROWNO_L_40
                Collections.sort(VRW_ROWNO_L_40)
                for(int p = 0; p < VRW_ROWNO_L_40.size(); p++) {//从小到大
                    stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_L_40.get(p)).setMOVE_ORDER(seq)
                    stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_L_40.get(p)).setWORKFLOW(1+"")
                    seq++;
                }
                //对同一层装船的舱位进行处理，结束

            }
            if(tier >= 82) {
                for(PreStowageData preStowageData : dataList) {//将40或45尺的卸船和装船的数据取出来，分开计算
                    if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                            "D".equals(preStowageData.getLDULD()) && ("40".equals(preStowageData.getSIZE()) || "45".equals(preStowageData.getSIZE()))) {//将同一层、卸船、40或45尺的船箱位取出
                        VRW_ROWNO_D_40.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                        String key = preStowageData.getVBY_BAYID() + "." +preStowageData.getVTR_TIERNO() + "." + preStowageData.getVRW_ROWNO()
                        stringListMap_D_40.put(key, preStowageData)
                    }
                    if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                            "L".equals(preStowageData.getLDULD()) && ("40".equals(preStowageData.getSIZE()) || "45".equals(preStowageData.getSIZE()))) {//将同一层、装船、40或45尺的船箱位取出
                        VRW_ROWNO_L_40.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                        String key = preStowageData.getVBY_BAYID() + "." +preStowageData.getVTR_TIERNO() + "." + preStowageData.getVRW_ROWNO()
                        stringListMap_L_40.put(key, preStowageData)
                    }
                }
                //对同一层卸船的舱位进行处理，结束
                println tier+"  40尺卸船的排号有："+VRW_ROWNO_D_40
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
                        stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                        stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                        p = p-1
                    } else {
                        if(even.get(p)-2 == even.get(p-1)) {
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p-1)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(3+"")
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p-1)).setWORKFLOW(3+"")
                            p = p-2
                        } else {
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                            p = p-1
                        }
                    }
                    seq++;
                }
                for(int p = 0; p < odd.size(); ) {//再对奇数排处理，从小到大
                    if(p == odd.size()-1) {
                        stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                        stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                        p++
                    } else {
                        if(odd.get(p)+2 == odd.get(p+1)) {
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p+1)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(3+"")
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p+1)).setWORKFLOW(3+"")
                            p = p+2
                        } else {
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                            stringListMap_D_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                            p = p+1
                        }
                    }
                    seq++;
                }
                //对同一层卸船的舱位进行处理，结束
                //再对同一层装船的舱位进行处理，甲板上装船编顺序，由海侧向陆侧,开始
                //装40尺的箱子
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
                        stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                        stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                        p = p-1
                    } else {
                        if(odd.get(p)-2 == odd.get(p-1)) {
//                                        println "倍.层.排"+VBY_BAYIDs.get(1)+"."+tier+"."+odd1.get(p)
                            stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                            stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p-1)).setMOVE_ORDER(seq)
                            stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(3+"")
                            stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p-1)).setWORKFLOW(3+"")
                            p = p-2
                        } else {
                            stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setMOVE_ORDER(seq)
                            stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(p)).setWORKFLOW(1+"")
                            p = p-1
                        }
                    }
                    seq++;
                }
                for(int p = 0; p < even.size(); ) {//再对偶数排处理，从小到大
                    if(p == even.size()-1) {
                        stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                        stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                        p++
                    } else {
                        if(even.get(p)+2 == even.get(p+1)) {
                            stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                            stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p+1)).setMOVE_ORDER(seq)
                            stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(3+"")
                            stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p+1)).setWORKFLOW(3+"")
                            p = p+2
                        } else {
                            stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setMOVE_ORDER(seq)
                            stringListMap_L_40.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(p)).setWORKFLOW(1+"")
                            p = p+1
                        }
                    }
                    seq++;
                }
                //对同一层装船的舱位进行处理，结束
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

    public
    static void workBayNumIsTwo(int seq, List<PreStowageData> dataList, List<Integer> VBY_BAYIDs,
                                List<Integer> VTR_TIERNOs, List<Integer> VRW_ROWNO_D_20,
                                Map<String, PreStowageData> stringListMap_D_20, List<PreStowageData> resultList) {
        for(Integer tier : VTR_TIERNOs) {
            if(tier < 82) {
                for(PreStowageData preStowageData : dataList) {//分别将20尺、40尺的卸船和装船的数据取出来，分开计算
                    if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                            "D".equals(preStowageData.getLDULD()) && "20".equals(preStowageData.getSIZE())) {//将同一层、卸船的船箱位取出
                        VRW_ROWNO_D_20.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                        String key = preStowageData.getVBY_BAYID() + "." +preStowageData.getVTR_TIERNO() + "." + preStowageData.getVRW_ROWNO()
                        stringListMap_D_20.put(key, preStowageData)
                    }
                }
                //先对同一层卸船的舱位进行处理，开始
                //先卸20尺的箱子
                List<Integer> even = new ArrayList<>()//排号为偶数
                List<Integer> odd = new ArrayList<>()//排号为奇数
                println tier+"  20尺卸船的排号有："+VRW_ROWNO_D_20
                Collections.sort(VRW_ROWNO_D_20)//排序
                for(int j = 0; j < VRW_ROWNO_D_20.size(); ) {//从小到大，甲板下由中央向两边
                    if(j == VRW_ROWNO_D_20.size()-1) {
                        if(stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)) != null) {
                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setWORKFLOW(1+"")
                        } else {
                            stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setWORKFLOW(1+"")
                        }
                        j = j+1
                    } else {
                        if(VRW_ROWNO_D_20.get(j) == VRW_ROWNO_D_20.get(j+1)) {//同一层、同一排，倍位肯定不相等，则用双吊具2
                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+VRW_ROWNO_D_20.get(j+1)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setWORKFLOW(2+"")
                            stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+VRW_ROWNO_D_20.get(j+1)).setWORKFLOW(2+"")
                            j = j+2
                        } else {//同一层、不同排，对20尺的箱子来说用单吊具1
                            if(stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)) != null) {
                                stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setMOVE_ORDER(seq)
                                stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setWORKFLOW(1+"")
                            } else {
                                stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setMOVE_ORDER(seq)
                                stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+VRW_ROWNO_D_20.get(j)).setWORKFLOW(1+"")
                            }
                            j = j+1
                        }
                    }
                    seq++
                }
            }
            if(tier >= 82) {
                for(PreStowageData preStowageData : dataList) {//分别将20尺、40尺的卸船和装船的数据取出来，分开计算
                    if(tier == Integer.valueOf(preStowageData.getVTR_TIERNO()) &&
                            "D".equals(preStowageData.getLDULD()) && "20".equals(preStowageData.getSIZE())) {//将同一层、卸船的船箱位取出
                        VRW_ROWNO_D_20.add(Integer.valueOf(preStowageData.getVRW_ROWNO()))
                        String key = preStowageData.getVBY_BAYID() + "." +preStowageData.getVTR_TIERNO() + "." + preStowageData.getVRW_ROWNO()
                        stringListMap_D_20.put(key, preStowageData)
                    }
                }
                //卸20尺的箱子
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
                for(int j = even.size()-1; j >= 0;) {//先对偶数排处理,从大到小;甲板上卸船编顺序，由陆侧向海侧
                    if(j == 0) {
                        if(stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)) != null) {
                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                        } else {
                            stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                        }
                        j = j-1
                    } else {
                        if(even.get(j) == even.get(j-1)) {//同一层、同一排，倍位肯定不相等，则用双吊具2
                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(j-1)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(2+"")
                            stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(j-1)).setWORKFLOW(2+"")
                            j = j-2
                        } else {//同一层、不同排，对20尺的箱子来说用单吊具1
                            if(stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)) != null) {
                                stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                            } else {
                                stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(j)).setMOVE_ORDER(seq)
                                stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+even.get(j)).setWORKFLOW(1+"")
                            }
                            j = j-1
                        }
                    }
                    seq++
                }
                for(int j = 0; j <odd.size(); ) {//再对奇数排处理，从小到大
                    if(j == odd.size()-1) {
                        if(stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)) != null) {
                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                        } else {
                            stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                        }
                        j = j+1
                    } else {
                        if(odd.get(j) == odd.get(j+1)) {//同一层、同一排，倍位肯定不相等，则用双吊具2
                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(j+1)).setMOVE_ORDER(seq)
                            stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(2+"")
                            stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(j+1)).setWORKFLOW(2+"")
                            j = j+2
                        } else {//同一层、不同排，对20尺的箱子来说用单吊具1
                            if(stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)) != null) {
                                stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                stringListMap_D_20.get(VBY_BAYIDs.get(0)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                            } else {
                                stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(j)).setMOVE_ORDER(seq)
                                stringListMap_D_20.get(VBY_BAYIDs.get(1)+"."+tier+"."+odd.get(j)).setWORKFLOW(1+"")
                            }
                            j = j+1
                        }
                    }
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
    }
}
