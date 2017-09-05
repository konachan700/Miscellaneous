package ru.jneko.puseradmin.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ONTInfo implements Serializable {
    private final Set<ONTMACInfo>
            macs = new HashSet<>();

    private ONTStatusInfo
            status = null;

    private final Map<String, String>
            cmdShowStateInfo = new HashMap<>();

    private final Map<String, String>
            cmdShowConfigInfo = new HashMap<>();

    private final Map<String, String>
            cmdShowConfigONTAcsInfo = new HashMap<>();

    private final Map<String, String>
            cmdShowConfigUserAcsInfo = new HashMap<>();

    private int slot = 0;
    private int channel = 0;
    private String ontSubscriber = "";

    public ONTInfo() { }


    public boolean containMAC(String find) {
        for (ONTMACInfo mac : macs) {
            if (mac.getOntMAC().toLowerCase().contains(find.toLowerCase()))
                return true;
        }
        return false;
    }

    public void parseShowConfigONTAcsCmd(String[] eBlock) {
        if (eBlock.length < 1) return;

        for (String line : eBlock) {
            if (line.contains("=")) {
                final String data = line.substring(21).trim();
                final String title = line.substring(0, 18).trim();

                if (title.length() > 0)
                    cmdShowConfigONTAcsInfo.put(title, data);

                if (title.contains("Subscriber"))
                    ontSubscriber = data;
            }
        }
    }

    public void parseShowConfigUserAcsCmd(String[] eBlock) {
        if (eBlock.length < 1) return;

        for (int i=3; i<6; i++) {
            final String data = eBlock[i].substring(20).replace('"', ' ').trim();
            final String title = eBlock[i].substring(0, 17).trim();

            if ((data.length() > 1) && (title.length() > 1))
                getCmdShowConfigUserAcsInfo().put(title, data);
        }

        for (int i=7; i<eBlock.length; i++) {
            final String[] kv = eBlock[i].split("\\:");
            if (kv.length > 1) {
                final String data = kv[1].replace('"', ' ').trim();
                final String title = kv[0].replace('"', ' ').trim();

                if (title.length() > 0)
                    getCmdShowConfigUserAcsInfo().put(title, data);
            }
        }
    }

    public void parseShowStateCmd(String[] eBlock) {
        if (eBlock.length < 1) return;

        for (String line : eBlock) {
            final String data = line.substring(25).trim();
            final String title = line.substring(0, 25).trim();

            if ((data.length() > 1) && (title.length() > 1))
                getCmdShowStateInfo().put(title.replace(':', ' ').trim(), data);
        }
    }

    public void parseShowConfigCmd(String[] eBlock) {
        if (eBlock.length < 40) return;

        for (int i=0; i<7; i++) {
            final String data = eBlock[i].substring(51).trim();
            final String title = eBlock[i].substring(0, 51).trim();

            if ((data.length() > 1) && (title.length() > 1))
                getCmdShowConfigInfo().put(title.replace(':', ' ').trim(), data);
        }

        for (int i=28; i<31; i++) {
            final String data = eBlock[i].substring(51).trim();
            final String title = eBlock[i].substring(0, 51).trim();

            if ((data.length() > 1) && (title.length() > 1))
                getCmdShowConfigInfo().put(title.replace(':', ' ').trim(), data);
        }

        for (int i=7; i<28; i=i+3) {
            final String enable = eBlock[i+1].substring(35).trim();
            final String vlan = eBlock[i+2].substring(35).trim();
            final String title = eBlock[i].trim();

            getCmdShowConfigInfo().put(title.replace(':', ' ').trim(), "vid: "+vlan+"; enable: "+enable);
        }
    }

    public void pushMACInfo(ONTMACInfo info) {
        macs.add(info);
    }

    public Set<ONTMACInfo> getMACs() {
        return macs;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public ONTStatusInfo getStatus() {
        return status;
    }

    public void setStatus(ONTStatusInfo status) {
        try {
            channel = Integer.parseInt(status.getRealChannel(), 10);
        } catch (NumberFormatException e) { channel = 0; }
        this.status = status;
    }

    public Map<String, String> getCmdShowStateInfo() {
        return cmdShowStateInfo;
    }

    public Map<String, String> getCmdShowConfigInfo() {
        return cmdShowConfigInfo;
    }

    public Map<String, String> getCmdShowConfigONTAcsInfo() {
        return cmdShowConfigONTAcsInfo;
    }

    public Map<String, String> getCmdShowConfigUserAcsInfo() {
        return cmdShowConfigUserAcsInfo;
    }

    public String getOntSubscriber() {
        return ontSubscriber;
    }

    public void setOntSubscriber(String ontSubscriber) {
        this.ontSubscriber = ontSubscriber;
    }
}
