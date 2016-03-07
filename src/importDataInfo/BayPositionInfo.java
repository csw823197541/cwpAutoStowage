package importDataInfo;

/**
 * Created by leko on 2016/1/18.
 */
public class BayPositionInfo {
    private String VHT_ID;         //舱位ID
    private String VBY_BAYID;      //倍位ID
    private Double VBY_POSITION;   //倍位中心相对船头距离

    public String getVHT_ID() {
        return VHT_ID;
    }

    public void setVHT_ID(String VHT_ID) {
        this.VHT_ID = VHT_ID;
    }

    public String getVBY_BAYID() {
        return VBY_BAYID;
    }

    public void setVBY_BAYID(String VBY_BAYID) {
        this.VBY_BAYID = VBY_BAYID;
    }

    public Double getVBY_POSITION() {
        return VBY_POSITION;
    }

    public void setVBY_POSITION(Double VBY_POSITION) {
        this.VBY_POSITION = VBY_POSITION;
    }
}
