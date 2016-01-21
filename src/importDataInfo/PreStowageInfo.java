package importDataInfo;

/**
 * Created by leko on 2016/1/17.
 */
public class PreStowageInfo {
    private Integer VHT_ID;          //舱位ID
    private Integer VBY_BAYID;      //倍位ID
    private Integer VTR_TIERNO;     //层号
    private Integer VRW_ROWNO;      //排号
    private Integer SIZE;            //箱尺寸
    private String  GROUP_ID;       //属性组
    private Integer WEIGHT;         //重量等级
    private Integer MOVE_ORDER;    //move顺序

    public Integer getVHT_ID() {
        return VHT_ID;
    }

    public void setVHT_ID(Integer VHT_ID) {
        this.VHT_ID = VHT_ID;
    }

    public Integer getVBY_BAYID() {
        return VBY_BAYID;
    }

    public void setVBY_BAYID(Integer VBY_BAYID) {
        this.VBY_BAYID = VBY_BAYID;
    }

    public Integer getVTR_TIERNO() {
        return VTR_TIERNO;
    }

    public void setVTR_TIERNO(Integer VTR_TIERNO) {
        this.VTR_TIERNO = VTR_TIERNO;
    }

    public Integer getVRW_ROWNO() {
        return VRW_ROWNO;
    }

    public void setVRW_ROWNO(Integer VRW_ROWNO) {
        this.VRW_ROWNO = VRW_ROWNO;
    }

    public Integer getSIZE() {
        return SIZE;
    }

    public void setSIZE(Integer SIZE) {
        this.SIZE = SIZE;
    }

    public String getGROUP_ID() {
        return GROUP_ID;
    }

    public void setGROUP_ID(String GROUP_ID) {
        this.GROUP_ID = GROUP_ID;
    }

    public Integer getWEIGHT() {
        return WEIGHT;
    }

    public void setWEIGHT(Integer WEIGHT) {
        this.WEIGHT = WEIGHT;
    }

    public Integer getMOVE_ORDER() {
        return MOVE_ORDER;
    }

    public void setMOVE_ORDER(Integer MOVE_ORDER) {
        this.MOVE_ORDER = MOVE_ORDER;
    }
}
