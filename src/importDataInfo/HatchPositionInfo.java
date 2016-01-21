package importDataInfo;

/**
 * Created by leko on 2016/1/18.
 */
public class HatchPositionInfo {
    private  String VHT_ID;         //舱位ID
    private  Integer LENGTH;        //舱位长度
    private  Integer POSITION;      //舱位左边缘的绝对位置

    public String getVHT_ID() {
        return VHT_ID;
    }

    public void setVHT_ID(String VHT_ID) {
        this.VHT_ID = VHT_ID;
    }

    public Integer getLENGTH() {
        return LENGTH;
    }

    public void setLENGTH(Integer LENGTH) {
        this.LENGTH = LENGTH;
    }

    public Integer getPOSITION() {
        return POSITION;
    }

    public void setPOSITION(Integer POSITION) {
        this.POSITION = POSITION;
    }
}
