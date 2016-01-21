package importDataInfo;

/**
 * Created by leko on 2016/1/20.
 */
public class AutostowInfo {
    private static String UnitID;       //箱号
    private static Integer csize;        //尺寸
    private static String Areaposition; //箱区位置
    private static String Vesselposition;   //船上位置

    public static String getUnitID() {
        return UnitID;
    }

    public static void setUnitID(String unitID) {
        UnitID = unitID;
    }


    public static Integer getCsize() {
        return csize;
    }

    public static void setCsize(Integer csize) {
        AutostowInfo.csize = csize;
    }

    public static String getAreaposition() {
        return Areaposition;
    }

    public static void setAreaposition(String areaposition) {
        Areaposition = areaposition;
    }

    public static String getVesselposition() {
        return Vesselposition;
    }

    public static void setVesselposition(String vesselposition) {
        Vesselposition = vesselposition;
    }
}
