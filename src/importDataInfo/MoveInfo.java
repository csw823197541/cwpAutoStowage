package importDataInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leko on 2016/1/22.
 */
public class MoveInfo {
    private String gkey;                    //唯一编号
    private Integer WORKINGSTARTTIME;     //开始时间
    private String batchId;                 //桥机号
    private int moveId;                     //桥机作业编号
    private String moveKind;                //作业类型
    private String unitId;                  //箱编号
    private String unitLength;              //箱长
    private String exFromPosition;      //计划提箱位置
    private String exToPosition;       //计划放箱位置

    //获取属性列表
    static public List getFiledsInfo() {
        Field[] fields = MoveInfo.class.getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        List list = new ArrayList();
        for (int i = 0; i < fields.length; i++) {
            list.add(fields[i].getName());
        }
        return list;
    }

    public String getGkey() {
        return gkey;
    }

    public void setGkey(String gkey) {
        this.gkey = gkey;
    }

    public Integer getWORKINGSTARTTIME() {
        return WORKINGSTARTTIME;
    }

    public void setWORKINGSTARTTIME(Integer WORKINGSTARTTIME) {
        this.WORKINGSTARTTIME = WORKINGSTARTTIME;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public int getMoveId() {
        return moveId;
    }

    public void setMoveId(int moveId) {
        this.moveId = moveId;
    }

    public String getMoveKind() {
        return moveKind;
    }

    public void setMoveKind(String moveKind) {
        this.moveKind = moveKind;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitLength() {
        return unitLength;
    }

    public void setUnitLength(String unitLength) {
        this.unitLength = unitLength;
    }

    public String getExFromPosition() {
        return exFromPosition;
    }

    public void setExFromPosition(String exFromPosition) {
        this.exFromPosition = exFromPosition;
    }

    public String getExToPosition() {
        return exToPosition;
    }

    public void setExToPosition(String exToPosition) {
        this.exToPosition = exToPosition;
    }
}
