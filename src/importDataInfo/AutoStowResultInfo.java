package importDataInfo;

/**
 * Created by leko on 2016/1/22.
 */
public class AutoStowResultInfo {
    private static String unitID;       //箱号
    private static String vesselPosition;   //船上位置
    private static String areaPosition;     //箱区位置
    private static String size;        //尺寸

    public static String getUnitID() {
        return unitID;
    }

    public static void setUnitID(String unitID) {
        AutoStowResultInfo.unitID = unitID;
    }

    public static String getVesselPosition() {
        return vesselPosition;
    }

    public static void setVesselPosition(String vesselPosition) {
        AutoStowResultInfo.vesselPosition = vesselPosition;
    }

    public static String getAreaPosition() {
        return areaPosition;
    }

    public static void setAreaPosition(String areaPosition) {
        AutoStowResultInfo.areaPosition = areaPosition;
    }

    public static String getSize() {
        return size;
    }

    public static void setSize(String size) {
        AutoStowResultInfo.size = size;
    }
}
