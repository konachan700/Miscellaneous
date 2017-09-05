package ru.mew_hpm.gpontools_v3.dao;

public class ONTInfo {
    private String idNumber = null;
    private String serial = "ERROR FIELD";
    private String ontId;
    private String gponPort;
    private String status;
    private String rssi = "";
    private String version = "";
    private String equipmentId = "";
    private String ontPassword = "";
    private String description = "";
    private String globalPort;


    public static boolean isDataValid(String eLine) {
        return ((eLine.length() > 110) && (!eLine.contains("##")) && eLine.contains("ELTX"));
    }

    public ONTInfo(String eLine) {
        if (eLine.length() < 110) return;

        final String[] lines = eLine.replaceAll("\\s{2,}", " ").trim().split(" ");
        if (lines.length < 5) return;

        setIdNumber(lines[0]);
        setSerial(lines[1]);
        setOntId(lines[2]);
        setGponPort(lines[3]);
        setStatus(lines[4]);
        setRssi(lines[5]);
        if (lines.length > 8) {
            setVersion(lines[6]);
            setEquipmentId(lines[7]);
            setOntPassword(lines[8]);
            if (lines.length > 9) setDescription(lines[9]);
        }

        /*setIdNumber(eLine.substring(0, 10).trim());
        setSerial(eLine.substring(10, 26).trim());
        setOntId(eLine.substring(26, 36).trim());
        setGponPort(eLine.substring(36, 49).trim());
        setStatus(eLine.substring(49, 59).trim());
        setRssi(eLine.substring(59, 72).trim());
        setVersion(eLine.substring(72, 86).trim());
        setEquipmentId(eLine.substring(86, 101).trim());
        setOntPassword(eLine.substring(101, 115).trim());
        setDescription(eLine.substring(115).trim());*/
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getOntId() {
        return ontId;
    }

    public void setOntId(String ontId) {
        this.ontId = ontId;
    }

    public String getGponPort() {
        return gponPort;
    }

    public void setGponPort(String gponPort) {
        this.gponPort = gponPort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getOntPassword() {
        return ontPassword;
    }

    public void setOntPassword(String ontPassword) {
        this.ontPassword = ontPassword;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGlobalPort() {
        return globalPort;
    }

    public void setGlobalPort(String globalPort) {
        this.globalPort = globalPort;
    }
}
