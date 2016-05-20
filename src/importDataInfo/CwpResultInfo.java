package importDataInfo;

import java.util.Date;

/**
 * Created by csw on 2016/1/20.
 */
public class CwpResultInfo {

    private String CRANEID;//桥机ID
    private Integer EndMoveID;//结束时，舱内的MoveOrder
    private String HATCHBWID;//倍位ID
    private String HATCHID;//舱ID
    private Integer MOVECOUNT;//Move总数
    private Integer QDC;//是否启动舱
    private Integer StartMoveID;//开始时，舱内的MoveOrder
    private String VESSELID;//航次ID
    private Integer WORKINGENDTIME;//结束时间
    private Integer WORKINGSTARTTIME;//起始时间
    private Integer REALWORKINGSTARTTIME;//起始时间
    private String MOVETYPE;//作业工艺
    private Double CranesPosition;//桥机当前位置
    private String LDULD;    //装卸船标志

    private Date workingStartTime;//时间格式的开始时间
    private Date workingEndTime;//时间格式的结束时间
    private Integer craneSeq;//作业某个舱所有桥机的作业顺序

    public Date getWorkingStartTime() {
        return workingStartTime;
    }

    public void setWorkingStartTime(Date workingStartTime) {
        this.workingStartTime = workingStartTime;
    }

    public Date getWorkingEndTime() {
        return workingEndTime;
    }

    public void setWorkingEndTime(Date workingEndTime) {
        this.workingEndTime = workingEndTime;
    }

    public Integer getCraneSeq() {
        return craneSeq;
    }

    public void setCraneSeq(Integer craneSeq) {
        this.craneSeq = craneSeq;
    }

    public String getMOVETYPE() {
        return MOVETYPE;
    }

    public void setMOVETYPE(String MOVETYPE) {
        this.MOVETYPE = MOVETYPE;
    }

    public String getCRANEID() {
        return CRANEID;
    }

    public void setCRANEID(String CRANEID) {
        this.CRANEID = CRANEID;
    }

    public Integer getEndMoveID() {
        return EndMoveID;
    }

    public void setEndMoveID(Integer endMoveID) {
        EndMoveID = endMoveID;
    }

    public String getHATCHBWID() {
        return HATCHBWID;
    }

    public void setHATCHBWID(String HATCHBWID) {
        this.HATCHBWID = HATCHBWID;
    }

    public String getHATCHID() {
        return HATCHID;
    }

    public void setHATCHID(String HATCHID) {
        this.HATCHID = HATCHID;
    }

    public Integer getMOVECOUNT() {
        return MOVECOUNT;
    }

    public void setMOVECOUNT(Integer MOVECOUNT) {
        this.MOVECOUNT = MOVECOUNT;
    }

    public Integer getQDC() {
        return QDC;
    }

    public void setQDC(Integer QDC) {
        this.QDC = QDC;
    }

    public Integer getStartMoveID() {
        return StartMoveID;
    }

    public void setStartMoveID(Integer startMoveID) {
        StartMoveID = startMoveID;
    }

    public String getVESSELID() {
        return VESSELID;
    }

    public void setVESSELID(String VESSELID) {
        this.VESSELID = VESSELID;
    }

    public Integer getWORKINGENDTIME() {
        return WORKINGENDTIME;
    }

    public void setWORKINGENDTIME(Integer WORKINGENDTIME) {
        this.WORKINGENDTIME = WORKINGENDTIME;
    }

    public Integer getWORKINGSTARTTIME() {
        return WORKINGSTARTTIME;
    }

    public void setWORKINGSTARTTIME(Integer WORKINGSTARTTIME) {
        this.WORKINGSTARTTIME = WORKINGSTARTTIME;
    }

    public Double getCranesPosition() {
        return CranesPosition;
    }

    public void setCranesPosition(Double cranesPosition) {
        CranesPosition = cranesPosition;
    }

    public String getLDULD() {
        return LDULD;
    }

    public void setLDULD(String LDULD) {
        this.LDULD = LDULD;
    }

    public Integer getREALWORKINGSTARTTIME() {
        return REALWORKINGSTARTTIME;
    }

    public void setREALWORKINGSTARTTIME(Integer REALWORKINGSTARTTIME) {
        this.REALWORKINGSTARTTIME = REALWORKINGSTARTTIME;
    }
}
