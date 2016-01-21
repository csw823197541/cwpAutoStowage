package importDataInfo;

/**
 * Created by leko on 2016/1/15.
 */
public class ContainerInfo {
    private String IYC_CNTRNO;                    //唯一箱号
    private String IYC_CNTR_AREA_ID;             //箱所在箱区号
    private Integer IYC_VOYID;                     //航次ID

    private String IYC_CTYPECD;                   //箱型
    private String IYC_CSZ_CSIZECD;              //尺寸
    private String IYC_PORTCD;                    //目的港

    private Integer IYC_WEIGHT;                     //箱重

    private String IYC_DNGFG;                      //是否危险品(Y,N)
    private String IYC_REFFG;                      //是否冷藏(Y,N)
    private String IYC_YLOCATION;                 //场箱位
    private String IYC_PLANFG;                    //是否可配载
    private String IYC_RETIME;                    //当前作业该箱所需的倒箱时间

    public Integer getIYC_VOYID() {
        return IYC_VOYID;
    }

    public void setIYC_VOYID(Integer IYC_VOYID) {
        this.IYC_VOYID = IYC_VOYID;
    }

    public Integer getIYC_WEIGHT() {
        return IYC_WEIGHT;
    }

    public void setIYC_WEIGHT(Integer IYC_WEIGHT) {
        this.IYC_WEIGHT = IYC_WEIGHT;
    }

    public String getIYC_CNTRNO() {
        return IYC_CNTRNO;
    }

    public void setIYC_CNTRNO(String IYC_CNTRNO) {
        this.IYC_CNTRNO = IYC_CNTRNO;
    }

    public String getIYC_CNTR_AREA_ID() {
        return IYC_CNTR_AREA_ID;
    }

    public void setIYC_CNTR_AREA_ID(String IYC_CNTR_AREA_ID) {
        this.IYC_CNTR_AREA_ID = IYC_CNTR_AREA_ID;
    }

    public String getIYC_CTYPECD() {
        return IYC_CTYPECD;
    }

    public void setIYC_CTYPECD(String IYC_CTYPECD) {
        this.IYC_CTYPECD = IYC_CTYPECD;
    }

    public String getIYC_CSZ_CSIZECD() {
        return IYC_CSZ_CSIZECD;
    }

    public void setIYC_CSZ_CSIZECD(String IYC_CSZ_CSIZECD) {
        this.IYC_CSZ_CSIZECD = IYC_CSZ_CSIZECD;
    }

    public String getIYC_PORTCD() {
        return IYC_PORTCD;
    }

    public void setIYC_PORTCD(String IYC_PORTCD) {
        this.IYC_PORTCD = IYC_PORTCD;
    }

   public String getIYC_DNGFG() {
        return IYC_DNGFG;
    }

    public void setIYC_DNGFG(String IYC_DNGFG) {
        this.IYC_DNGFG = IYC_DNGFG;
    }

    public String getIYC_REFFG() {
        return IYC_REFFG;
    }

    public void setIYC_REFFG(String IYC_REFFG) {
        this.IYC_REFFG = IYC_REFFG;
    }

    public String getIYC_YLOCATION() {
        return IYC_YLOCATION;
    }

    public void setIYC_YLOCATION(String IYC_YLOCATION) {
        this.IYC_YLOCATION = IYC_YLOCATION;
    }

    public String getIYC_PLANFG() {
        return IYC_PLANFG;
    }

    public void setIYC_PLANFG(String IYC_PLANFG) {
        this.IYC_PLANFG = IYC_PLANFG;
    }

    public String getIYC_RETIME() {
        return IYC_RETIME;
    }

    public void setIYC_RETIME(String IYC_RETIME) {
        this.IYC_RETIME = IYC_RETIME;
    }
}
