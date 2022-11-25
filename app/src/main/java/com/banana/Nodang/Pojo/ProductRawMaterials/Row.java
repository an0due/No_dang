package com.banana.Nodang.Pojo.ProductRawMaterials;

import com.google.gson.annotations.SerializedName;

public class Row {
    @SerializedName("BSSH_NM")
    private String bsshNm;
    @SerializedName("PRDLST_REPORT_NO")
    private String prdlstReportNo;
    @SerializedName("PRDLST_NM")
    private String prdlstNm;
    @SerializedName("PRDLST_DCNM")
    private String prdlstDcnm;
    @SerializedName("RAWMTRL_NM")
    private String rawmtrlNm;
    @SerializedName("LCNS_NO")
    private String lcnsNo;

    public String getBsshNm() {
        return bsshNm;
    }

    public void setBsshNm(String bsshNm) {
        this.bsshNm = bsshNm;
    }

    public String getPrdlstReportNo() {
        return prdlstReportNo;
    }

    public void setPrdlstReportNo(String prdlstReportNo) {
        this.prdlstReportNo = prdlstReportNo;
    }

    public String getPrdlstNm() {
        return prdlstNm;
    }

    public void setPrdlstNm(String prdlstNm) {
        this.prdlstNm = prdlstNm;
    }

    public String getPrdlstDcnm() {
        return prdlstDcnm;
    }

    public void setPrdlstDcnm(String prdlstDcnm) {
        this.prdlstDcnm = prdlstDcnm;
    }

    public String getRawmtrlNm() {
        return rawmtrlNm;
    }

    public void setRawmtrlNm(String rawmtrlNm) {
        this.rawmtrlNm = rawmtrlNm;
    }
    public String getLcnsNo() {
        return lcnsNo;
    }
    public void setLcnsNo(String lcnsNo) {
        this.lcnsNo = lcnsNo;
    }

}