package ru.jneko.puseradmin.MA4000;

import android.content.Context;
import android.util.Log;

import com.jcraft.jsch.Session;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.UiThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.jneko.puseradmin.connect.SSHWorker;
import ru.jneko.puseradmin.connect.SSHWorker.SSHWorkerResult;
import ru.jneko.puseradmin.dao.ONTInfo;
import ru.jneko.puseradmin.dao.ONTMACInfo;
import ru.jneko.puseradmin.dao.ONTStatusInfo;
import ru.jneko.puseradmin.dao.SlotInfo;

public class CommandsMA4000v2 implements SSHWorkerResult, OnCommandExecuted {
    public static final String
            CMD__SHOW_ONT_LIST              = "show ont list",
            CMD__SHOW_MAC                   = "show mac",
            CMD__SHOW_SLOTS                 = "show slots",
            CMD__SHOW_STATE                 = "show state",
            CMD__SHOW_CONFIG                = "show config";

    private static Map<String, CommandsMA4000v2>
            parsersPool = new HashMap<>();

    private Map<Commands, ArrayList<Command>>
            commandData = new HashMap<>();

    private ArrayList<SlotInfo>
            slots = new ArrayList<>();

    private Commands
            currentCommand = Commands.VOID;

    private volatile int
            currentSlot = 0;
    private volatile int onTextReceivedCounter = 0;

    private volatile String
            currentONTID = "ELTX00000000";

    private volatile String
            currentUser = "nobody";

    private final SSHWorker
            sshWX;

    private final Map<String, ONTInfo>
            ontInfoList = new HashMap<>();

    private volatile boolean
            commandProcessing = false;

    private final StringBuilder
            tempBuffer = new StringBuilder();

    public static CommandsMA4000v2 addParserToPool(String name) {
        if (parsersPool.containsKey(name))
            return parsersPool.get(name);

        final CommandsMA4000v2 ma400v2 = new CommandsMA4000v2();
        parsersPool.put(name, ma400v2);
        return ma400v2;
    }

    public static CommandsMA4000v2 getParserFromPool(String name) {
        return parsersPool.get(name);
    }

    public static void removeParserFromPool(String name) {
        parsersPool.remove(name);
    }

    private CommandsMA4000v2() {
        sshWX = SSHWorker.getWorkerFromPool("ma4000ssh");
        if (sshWX != null) {
            sshWX.addActionListener(this);
        }

        commandData.put(Commands.GET_ONT_DETAIL_CONFIG,
                new ArrayList<Command>() {{
                    add(new Command("slot 0", false, true));
                    add(new Command("pon", false, false));
                    add(new Command("ont_sn XXXX", false, true));
                    add(new Command(CMD__SHOW_STATE, true, false));
                    add(new Command(CMD__SHOW_CONFIG, true, false));
                }});

        commandData.put(Commands.GET_ACS_ONT_CONFIG,
                new ArrayList<Command>() {{
                    add(new Command("acs", false, true));
                    add(new Command("ont", false, false));
                    add(new Command("ont XXXX", false, true));
                    add(new Command(CMD__SHOW_CONFIG, true, false));
                }});

        commandData.put(Commands.GET_ACS_USER_CONFIG,
                new ArrayList<Command>() {{
                    add(new Command("acs", false, true));
                    add(new Command("user", false, false));
                    add(new Command("user XXXX", false, true));
                    add(new Command(CMD__SHOW_CONFIG, true, false));
                }});

        commandData.put(Commands.GET_ALL_ONTs,
                new ArrayList<Command>() {{
                    add(new Command("slot 0", false, true));
                    add(new Command("pon", false, false));
                    add(new Command(CMD__SHOW_ONT_LIST, true, false));
                    add(new Command(CMD__SHOW_MAC, true, false));
        }});

        commandData.put(Commands.GET_SLOTS,
                new ArrayList<Command>() {{
                    add(new Command("express", false, false));
                    add(new Command(CMD__SHOW_SLOTS, true, false));
                }});
    }

    private void write(String cmd) throws IOException {
        long time = System.currentTimeMillis() + 60000;

        commandProcessing = true;
        onTextReceivedCounter = 0;
        tempBuffer.delete(0, tempBuffer.length());

        sshWX.getSshOutputStream().write((cmd + "\n").getBytes());
        sshWX.getSshOutputStream().flush();

        while (commandProcessing) {
            if (time < System.currentTimeMillis())
                throw new IOException("writeCMD IO timeout: + " + cmd);
        }
    }

    private void writeCMD(Commands cmd, boolean isPrivCMD, OnCommandExecuted al) throws IOException {
        if (cmd == Commands.USER_DEFINED) return;
        currentCommand = cmd;

        final ArrayList<Command> cmdPacket = commandData.get(cmd);
        if (cmdPacket == null) return;

        if (isPrivCMD) write("enable");

        for (int i=0; i<cmdPacket.size(); i++) {
            String cmdTemp = cmdPacket.get(i).getCommand();
            if (cmdPacket.get(i).isHasReplacebleNumber())
                cmdTemp = cmdTemp
                        .replace("slot 0", "slot " + getCurrentSlot())
                        .replace("ont_sn XXXX", "ont_sn "+currentONTID)
                        .replace("ont XXXX", "ont "+currentONTID)
                        .replace("user XXXX", "user "+currentUser);

            write(cmdTemp);

            if (cmdPacket.get(i).isHasReturnData())
                al.commandExecuted(currentCommand, cmdPacket.get(i));
        }

        write("top");
        if (isPrivCMD) write("disable");
    }

    @UiThread
    private void hideWaitDialog(OnUserCommandExecuted al) {
        if (al != null) al.completed();
    }

    @Background(serial = "writeUserCMD")
    public void writeUserCMD(Context c, ArrayList<Command> cData, boolean isPrivCMD, OnUserCommandExecuted al) {
        currentCommand = Commands.USER_DEFINED;

        try {
            if (isPrivCMD) write("enable");

            for (int i=0; i<cData.size(); i++)
                write(cData.get(i).getCommand());

            if (isPrivCMD) write("disable");
            write("top");

            hideWaitDialog(al);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshONTList() {
        getOntInfoList().clear();
        try {
            writeCMD(Commands.GET_SLOTS, false, this);
            if (getSlots().size() < 1) return;

            for (int i = 0; i< getSlots().size(); i++) {
                setCurrentSlot(getSlots().get(i).getSlot());
                writeCMD(Commands.GET_ALL_ONTs, false, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshCurrentONTInfo() {
        final ONTInfo info = ontInfoList.get(currentONTID);
        if (info == null) return;
        setCurrentSlot(info.getSlot());

        try {
            writeCMD(Commands.GET_ONT_DETAIL_CONFIG, false, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshCurrentACSInfo() {
        final ONTInfo info = ontInfoList.get(currentONTID);
        if (info == null) return;
        setCurrentSlot(info.getSlot());

        try {
            writeCMD(Commands.GET_ACS_ONT_CONFIG, false, this);
            if (!info.getOntSubscriber().isEmpty()) {
                currentUser = info.getOntSubscriber();
                writeCMD(Commands.GET_ACS_USER_CONFIG, false, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Connected(Session s) {

    }

    @Override
    public void ConnectError(Exception e) {

    }

    @Override
    public void ConnectedTimer() {

    }

    @Override
    public void OnTextReceived(Session s, String text, String currLine) {
        if (currLine.contains("More") && currLine.contains("Space") && currLine.contains("show the rest")) {
            //More? Enter - next line; Space - next page; Q - quit; R - show the rest.
            if (sshWX != null) try {
                sshWX.getSshOutputStream().write(" ".getBytes());
                sshWX.getSshOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (currLine.contains("Session expires")) {
            if (sshWX != null) try {
                sshWX.getSshOutputStream().write("top\n".getBytes());
                sshWX.getSshOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (currLine.contains("MA4000") && (currLine.contains(">") || currLine.contains("#"))) {
            commandProcessing = false;
            //Log.d("### MA4000", "****************************************** EOC ******************************************");
        } else if ((commandProcessing) && (onTextReceivedCounter > 0)) {
            //Log.d("MA4000", currLine);
            tempBuffer.append(currLine);
        }

        onTextReceivedCounter++;
    }

    @Override
    public void commandExecuted(Commands group, Command current) {
        if (currentCommand == Commands.USER_DEFINED) return;

        final String[] lines;
        switch (current.getCommand()) {
            case CMD__SHOW_STATE:
                lines = tempBuffer.toString().replace("\r", "").split("\n");
                if (lines.length <= 1) return;

                final ONTInfo info_state = ontInfoList.get(currentONTID);
                if (info_state == null) return;

                info_state.parseShowStateCmd(lines);
                break;
            case CMD__SHOW_CONFIG:
                lines = tempBuffer.toString().replace("\r", "").split("\n");
                if (lines.length <= 1) return;

                final ONTInfo info_config = ontInfoList.get(currentONTID);
                if (info_config == null) return;

                switch (currentCommand) {
                    case GET_ONT_DETAIL_CONFIG:
                        info_config.parseShowConfigCmd(lines);
                        break;
                    case GET_ACS_ONT_CONFIG:
                        info_config.parseShowConfigONTAcsCmd(lines);
                        break;
                    case GET_ACS_USER_CONFIG:
                        info_config.parseShowConfigUserAcsCmd(lines);
                        break;
                }
                break;
            case CMD__SHOW_ONT_LIST:
                lines = tempBuffer.toString().replace("\r", "").split("\n");
                if (lines.length <= 1) return;

                for (int i=1; i<lines.length; i++) {
                    final ONTStatusInfo sinfo = new ONTStatusInfo(lines[i]);
                    if (sinfo.isDataValid()) {
                        if (getOntInfoList().containsKey(sinfo.getOntSerial())) {
                            final ONTInfo info = getOntInfoList().get(sinfo.getOntSerial());
                            info.setStatus(sinfo);
                            info.setSlot(getCurrentSlot());
                        } else {
                            final ONTInfo info = new ONTInfo();
                            info.setStatus(sinfo);
                            getOntInfoList().put(sinfo.getOntSerial(), info);
                            info.setSlot(getCurrentSlot());
                        }
                    }
                }

                Log.d("# INFO", "STATUS TABLE SIZE IN SLOT " + getCurrentSlot() + ": " + (lines.length-1));
                break;
            case CMD__SHOW_MAC:
                lines = tempBuffer.toString().replace("\r", "").split("\n");
                if (lines.length <= 2) return;

                for (int i=2; i<lines.length; i++) {
                    final ONTMACInfo mi = new ONTMACInfo(lines[i]);
                    if (mi.isDataValid()) {
                        if (getOntInfoList().containsKey(mi.getOntSerial())) {
                            final ONTInfo info = getOntInfoList().get(mi.getOntSerial());
                            info.pushMACInfo(mi);
                            info.setSlot(getCurrentSlot());
                        } else {
                            final ONTInfo info = new ONTInfo();
                            info.pushMACInfo(mi);
                            info.setSlot(getCurrentSlot());
                            getOntInfoList().put(mi.getOntSerial(), info);
                        }
                    }
                }

                Log.d("# INFO", "MAC TABLE SIZE IN SLOT " + getCurrentSlot() + ": " + (lines.length-2));
                break;
            case CMD__SHOW_SLOTS:
                lines = tempBuffer.toString().replace("\r", "").split("\n");
                if (lines.length < 5) return;

                getSlots().clear();
                for (int i=5; i<lines.length; i++) {
                    final SlotInfo si = new SlotInfo(lines[i]);
                    if (si.isLinkUp()) getSlots().add(si);
                }

                Log.d("# INFO", "SLOTS ACTIVE COUNT: " + getSlots().size());
                break;
        }
    }

    @Override
    public void completed() {

    }

    public Map<String, ONTInfo> getOntInfoList() {
        return ontInfoList;
    }

    public String getCurrentONTID() {
        return currentONTID;
    }

    public void setCurrentONTID(String currentONTID) {
        this.currentONTID = currentONTID;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public int getCurrentSlot() {
        return currentSlot;
    }

    public void setCurrentSlot(int currentSlot) {
        this.currentSlot = currentSlot;
    }

    public ArrayList<SlotInfo> getSlots() {
        return slots;
    }

    public void setSlots(ArrayList<SlotInfo> slots) {
        this.slots = slots;
    }
}
