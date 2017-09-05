package ru.jneko.puseradmin.dao;

public class SlotInfo {
    private int slot;

    private String slotStr;
    private String confDevice;
    private String confVersion;
    private String realDevice;
    private String realVersion;
    private String realSerialNumber;
    private String link;
    private String state;

    public static boolean isValidInfo(String eLine) {
        try {
            Integer.parseInt(eLine.substring(0, 1), 10);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public SlotInfo(String eLine) {
        //Log.d("###SlotInfo eLine", eLine);

        if (eLine.isEmpty()) return;
        if (eLine.length() < 100) return;

        setSlotStr(eLine.substring(0, 7).trim());
        setConfDevice(eLine.substring(7, 24).trim());
        setConfVersion(eLine.substring(24, 41).trim());
        setRealDevice(eLine.substring(41, 56).trim());
        setRealVersion(eLine.substring(56, 71).trim());
        setRealSerialNumber(eLine.substring(71, 92).trim());
        setLink(eLine.substring(92, 99).trim());
        setState(eLine.substring(99).trim());

        try {
            setSlot(Integer.parseInt(getSlotStr()));
        } catch (NumberFormatException e) {
            link = null;
            return;
        }
    }

    public boolean isLinkUp() {
        if (link == null) return false;
        return link.contains("up");
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String getSlotStr() {
        return slotStr;
    }

    public void setSlotStr(String slotStr) {
        this.slotStr = slotStr;
    }

    public String getConfDevice() {
        return confDevice;
    }

    public void setConfDevice(String confDevice) {
        this.confDevice = confDevice;
    }

    public String getConfVersion() {
        return confVersion;
    }

    public void setConfVersion(String confVersion) {
        this.confVersion = confVersion;
    }

    public String getRealDevice() {
        return realDevice;
    }

    public void setRealDevice(String realDevice) {
        this.realDevice = realDevice;
    }

    public String getRealVersion() {
        return realVersion;
    }

    public void setRealVersion(String realVersion) {
        this.realVersion = realVersion;
    }

    public String getRealSerialNumber() {
        return realSerialNumber;
    }

    public void setRealSerialNumber(String realSerialNumber) {
        this.realSerialNumber = realSerialNumber;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
