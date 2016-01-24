package importDataInfo;

/**
 * 船舶结构
 * Created by csw on 2016/1/16.
 */
public class VesselStructureInfo {

    private String VHTID;//舱次ID
    private Integer LENGTH;//舱位长度
    private Integer VHTPOSITION; //舱开始相对于船头位置
    private String VBYBAYID;//倍位ID
    private Integer VBYPOSITION;//倍位中心相对于船头位置
    private String VTRTIERNO;//层号
    private Integer VTRTIERSEQ;//层序号
    private String VRWROWNO;//排号
    private Integer VRWROWSEQ;//排序号
    private Integer VLCVWCID;//重量等级ID

    private String groupId;//属性组

    public String getVHTID() {
        return VHTID;
    }

    public void setVHTID(String VHTID) {
        this.VHTID = VHTID;
    }

    public Integer getLENGTH() {
        return LENGTH;
    }

    public void setLENGTH(Integer LENGTH) {
        this.LENGTH = LENGTH;
    }

    public Integer getVHTPOISITION() {
        return VHTPOSITION;
    }

    public void setVHTPOISITION(Integer VHTPOISITION) {
        this.VHTPOSITION = VHTPOISITION;
    }

    public String getVBYBAYID() {
        return VBYBAYID;
    }

    public void setVBYBAYID(String VBYBAYID) {
        this.VBYBAYID = VBYBAYID;
    }

    public Integer getVBYPOSITION() {
        return VBYPOSITION;
    }

    public void setVBYPOSITION(Integer VBYPOSITION) {
        this.VBYPOSITION = VBYPOSITION;
    }

    public Integer getVTRTIERSEQ() {
        return VTRTIERSEQ;
    }

    public void setVTRTIERSEQ(Integer VTRTIERSEQ) {
        this.VTRTIERSEQ = VTRTIERSEQ;
    }

    public Integer getVRWROWSEQ() {
        return VRWROWSEQ;
    }

    public void setVRWROWSEQ(Integer VRWROWSEQ) {
        this.VRWROWSEQ = VRWROWSEQ;
    }

    public Integer getVLCVWCID() {
        return VLCVWCID;
    }

    public void setVLCVWCID(Integer VLCVWCID) {
        this.VLCVWCID = VLCVWCID;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getVHTPOSITION() {
        return VHTPOSITION;
    }

    public void setVHTPOSITION(Integer VHTPOSITION) {
        this.VHTPOSITION = VHTPOSITION;
    }

    public String getVTRTIERNO() {
        return VTRTIERNO;
    }

    public void setVTRTIERNO(String VTRTIERNO) {
        this.VTRTIERNO = VTRTIERNO;
    }

    public String getVRWROWNO() {
        return VRWROWNO;
    }

    public void setVRWROWNO(String VRWROWNO) {
        this.VRWROWNO = VRWROWNO;
    }
}
