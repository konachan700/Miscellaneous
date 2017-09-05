package ru.jneko.puseradmin.dao;

public class ONTMACInfo {
private String ontSerial;
    private String ontMAC;
    private String priority;
    private String cvid;
    private String svid;
    private String strID;

    private boolean dataValid = false;

    public ONTMACInfo(String eLine) {
        if (eLine.isEmpty()) return;
        if (eLine.length() < 75) return;

        setStrID(eLine.substring(0, 11).trim());
        setOntSerial(eLine.substring(11, 27).trim());
        setPriority(eLine.substring(27, 39).trim());
        setCvid(eLine.substring(39, 47).trim());
        setSvid(eLine.substring(47, 55).trim());
        setOntMAC(eLine.substring(55).trim());

        dataValid = (getOntMAC().length() > 11);
    }

    public boolean isDataValid() {
        return dataValid;
    }

    public String getOntSerial() {
        return ontSerial;
    }

    public void setOntSerial(String ontSerial) {
        this.ontSerial = ontSerial;
    }

    public String getOntMAC() {
        return ontMAC;
    }

    public void setOntMAC(String ontMAC) {
        this.ontMAC = ontMAC;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCvid() {
        return cvid;
    }

    public void setCvid(String cvid) {
        this.cvid = cvid;
    }

    public String getSvid() {
        return svid;
    }

    public void setSvid(String svid) {
        this.svid = svid;
    }

    public String getStrID() {
        return strID;
    }

    public void setStrID(String strID) {
        this.strID = strID;
    }
}
