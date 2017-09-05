package ru.jneko.puseradmin.dao;

public class ONTStatusInfo {
    private String id;
    private String ontSerial;
    private String ontID;
    private String assignedChannel;
    private String realChannel;
    private String status;

    private boolean dataValid = false;

    public ONTStatusInfo(String eLine) {
        if (eLine.isEmpty()) return;
        if (eLine.length() < 90) return;

        setId(eLine.substring(0, 6).trim());
        setOntSerial(eLine.substring(6, 31).trim());
        setOntID(eLine.substring(31, 41).trim());
        setAssignedChannel(eLine.substring(41, 61).trim());
        setRealChannel(eLine.substring(61, 72).trim());
        setStatus(eLine.substring(72).trim());

        dataValid = (getOntSerial().length() >= 8);
    }

    public boolean isDataValid() {
        return dataValid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOntSerial() {
        return ontSerial;
    }

    public void setOntSerial(String ontSerial) {
        this.ontSerial = ontSerial;
    }

    public String getOntID() {
        return ontID;
    }

    public void setOntID(String ontID) {
        this.ontID = ontID;
    }

    public String getAssignedChannel() {
        return assignedChannel;
    }

    public void setAssignedChannel(String assignedChannel) {
        this.assignedChannel = assignedChannel;
    }

    public String getRealChannel() {
        return realChannel;
    }

    public void setRealChannel(String realChannel) {
        this.realChannel = realChannel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
