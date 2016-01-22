package importDataInfo;

/**
 * Created by leko on 2016/1/22.
 */
public class AutoStowResultInfo {
    private String unitID;       //箱号
    private String vesselPosition;   //船上位置
    private String areaPosition;     //箱区位置
    private String size;        //尺寸

    public String getUnitID() {
        return unitID;
    }

    public void setUnitID(String unitID) {
        this.unitID = unitID;
    }

    public String getVesselPosition() {
        return vesselPosition;
    }

    public void setVesselPosition(String vesselPosition) {
        this.vesselPosition = vesselPosition;
    }

    public String getAreaPosition() {
        return areaPosition;
    }

    public void setAreaPosition(String areaPosition) {
        this.areaPosition = areaPosition;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
