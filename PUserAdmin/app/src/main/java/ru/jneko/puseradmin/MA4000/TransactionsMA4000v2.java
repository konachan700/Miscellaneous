package ru.jneko.puseradmin.MA4000;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ru.jneko.puseradmin.dao.SlotInfo;

public class TransactionsMA4000v2 {
    public static enum Datatype {
        INT, TEXT, TRUE_FALSE, ENABLE_DISABLE, ACTION_TELNET, SERVICE_OVERRIDE
    }

    public static final Set<String>
            writableParams = new HashSet<String>(){{
        add("voice1_enable");
        add("voice1_number");
        add("voice1_password");
        add("voice2_enable");
        add("voice2_number");
        add("voice2_password");
        add("sip_proxy");
        add("ppp_login");
        add("ppp_password");
        add("user_password");
        add("admin_password");
        add("wifi_enable");
        add("wifi_ssid");
        add("wifi_encoding");
        add("wifi_password");
        add("internet_vlanid");
        add("service_vlanid_1_ip");
        add("service_vlanid_2_ip");
        add("service_vlanid_3_ip");
        add("service_vlanid_4_ip");
        add("service_vlanid_5_ip");
        add("service_vlanid_6_ip");
        add("service_vlanid_7_ip");
        add("service_vlanid_8_ip");
        add("service_vlanid_1_ppp");
        add("service_vlanid_2_ppp");
        add("service_vlanid_3_ppp");
        add("service_vlanid_4_ppp");
        add("service_vlanid_5_ppp");
        add("service_vlanid_6_ppp");
        add("service_vlanid_7_ppp");
        add("service_vlanid_8_ppp");
        add("PON serial");
        add("Profile");

        add("URL");

        add("Description");
        //add("Serial");
        add("Profile services");
        add("Profile management");
        add("Profile multicast");
        add("Services override [0]");
        add("Services override [1]");
        add("Services override [2]");
        add("Services override [3]");
        add("Services override [4]");
        add("Services override [5]");
        add("Services override [6]");
    }};

    private static final Map<String, Datatype>
            types = new HashMap<String, Datatype>() {{
        put("Profile services", Datatype.INT);
        put("Profile management", Datatype.INT);
        put("Profile multicast", Datatype.INT);

        put("Services override [0]", Datatype.SERVICE_OVERRIDE);
        put("Services override [1]", Datatype.SERVICE_OVERRIDE);
        put("Services override [2]", Datatype.SERVICE_OVERRIDE);
        put("Services override [3]", Datatype.SERVICE_OVERRIDE);
        put("Services override [4]", Datatype.SERVICE_OVERRIDE);
        put("Services override [5]", Datatype.SERVICE_OVERRIDE);
        put("Services override [6]", Datatype.SERVICE_OVERRIDE);

        put("internet_vlanid", Datatype.INT);

        put("service_vlanid_1_ip", Datatype.INT);
        put("service_vlanid_2_ip", Datatype.INT);
        put("service_vlanid_3_ip", Datatype.INT);
        put("service_vlanid_4_ip", Datatype.INT);
        put("service_vlanid_5_ip", Datatype.INT);
        put("service_vlanid_6_ip", Datatype.INT);
        put("service_vlanid_7_ip", Datatype.INT);
        put("service_vlanid_8_ip", Datatype.INT);

        put("service_vlanid_1_ppp", Datatype.INT);
        put("service_vlanid_2_ppp", Datatype.INT);
        put("service_vlanid_3_ppp", Datatype.INT);
        put("service_vlanid_4_ppp", Datatype.INT);
        put("service_vlanid_5_ppp", Datatype.INT);
        put("service_vlanid_6_ppp", Datatype.INT);
        put("service_vlanid_7_ppp", Datatype.INT);
        put("service_vlanid_8_ppp", Datatype.INT);

        put("voice1_enable", Datatype.TRUE_FALSE);
        put("voice2_enable", Datatype.TRUE_FALSE);
        put("wifi_enable", Datatype.TRUE_FALSE);

        put("URL", Datatype.ACTION_TELNET);
    }};

    public static Datatype getType(String name) {
        final Datatype dt = types.get(name);
        return (dt == null) ? Datatype.TEXT : dt;
    }

    private static void writeAcsUser(CommandsMA4000v2 ma4000v2, ArrayList<Command> cmdACSUser, String field, String value, String setString) {
        cmdACSUser.add(new Command("acs", false, false));
        cmdACSUser.add(new Command("user", false, false));
        cmdACSUser.add(new Command("user " + ma4000v2.getCurrentUser(), false, false));
        if ((getType(field) == Datatype.INT) || (getType(field) == Datatype.TRUE_FALSE) || (getType(field) == Datatype.ENABLE_DISABLE))
            cmdACSUser.add(new Command("set " + setString + " " + value, false, false));
        else
            cmdACSUser.add(new Command("set " + setString + " \"" + value + "\"", false, false));
    }

    private static void writeSlotPonOntsn(CommandsMA4000v2 ma4000v2, ArrayList<Command> cmdACSUser, String field, String value, String setString) {
        cmdACSUser.add(new Command("slot " + ma4000v2.getCurrentSlot(), false, false));
        cmdACSUser.add(new Command("pon", false, false));
        cmdACSUser.add(new Command("ont_sn " + ma4000v2.getCurrentONTID(), false, false));
        if ((getType(field) == Datatype.INT) || (getType(field) == Datatype.TRUE_FALSE) || (getType(field) == Datatype.ENABLE_DISABLE))
            cmdACSUser.add(new Command("set " + setString + " " + value, false, false));
        else
            cmdACSUser.add(new Command("set " + setString + " \"" + value + "\"", false, false));
        cmdACSUser.add(new Command("do commit", false, false));
        cmdACSUser.add(new Command("do confirm", false, false));
    }

    private static void write2(Context c, ArrayList<Command> cmd) {
        final ProgressDialog pd = ProgressDialog.show(c, "Loading", "Please wait...", true);
        final CommandsMA4000v2 ma4000v2 = CommandsMA4000v2.getParserFromPool("ma4000v2parser");
        ma4000v2.writeUserCMD(c, cmd, true, new OnUserCommandExecuted() {
            @Override
            public void completed() {
                pd.dismiss();
            }
        });
    }

    public static void resetONTToDefaults(Context c, final String ontSerial, final int slot) {
        final ArrayList<Command> cmd = new ArrayList<Command>() {{
            add(new Command("slot " + slot, false, false));
            add(new Command("pon", false, false));
            add(new Command("ont_sn " + ontSerial, false, false));
            add(new Command("default", false, false));
        }};
        write2(c, cmd);
    }

    public static void setWIFIParams(Context c, final String ontUser, final String wifiSSID, final String wifiPassword) {
        final ArrayList<Command> cmd = new ArrayList<Command>() {{
            add(new Command("acs", false, false));
            add(new Command("user", false, false));
            add(new Command("user " + ontUser, false, false));
            add(new Command("set wifi_enable enable", false, false));
            add(new Command("set wifi_ssid \"" + wifiSSID + "\"", false, false));
            add(new Command("set wifi_password \"" + wifiPassword + "\"", false, false));
            add(new Command("setfactdef", false, false));
        }};
        write2(c, cmd);
    }

    public static void setVlan(Context c, final String ontUser, final String ontSerial, final int vlan, final int slot) {
        final ArrayList<Command> cmd = new ArrayList<Command>() {{
            add(new Command("slot " + slot, false, false));
            add(new Command("pon", false, false));
            add(new Command("ont_sn " + ontSerial, false, false));
            add(new Command("set services_override 0 enable true", false, false));
            add(new Command("set services_override 0 customer_vid " + vlan, false, false));
            add(new Command("top", false, false));
            add(new Command("acs", false, false));
            add(new Command("user", false, false));
            add(new Command("user " + ontUser, false, false));
            add(new Command("set internet_vlanid " + vlan, false, false));
            add(new Command("set service_vlanid 1 ppp " + vlan, false, false));
            add(new Command("set service_vlanid 1 ip " + vlan, false, false));
            add(new Command("setfactdef", false, false));
            add(new Command("top", false, false));
            add(new Command("commit", false, false));
            add(new Command("confirm", false, false));
        }};
        write2(c, cmd);
    }

    public static void removeONT(Context c, final String ontUser, final String ontSerial, final ArrayList<SlotInfo> sil) {
        final ArrayList<Command> cmd = new ArrayList<Command>() {{
            for (SlotInfo si : sil) {
                add(new Command("slot " + si.getSlot(), false, false));
                add(new Command("pon", false, false));
                add(new Command("delete ont " + ontSerial, false, false));
                add(new Command("top", false, false));
            }

            add(new Command("acs", false, false));
            add(new Command("user", false, false));
            add(new Command("delete user " + ontUser, false, false));
            add(new Command("top", false, false));
            add(new Command("commit", false, false));
            add(new Command("confirm", false, false));
        }};
        write2(c, cmd);
    }

    public static void write(Context c, String field, String value) {
        final CommandsMA4000v2 ma4000v2 = CommandsMA4000v2.getParserFromPool("ma4000v2parser");
        final ArrayList<Command> cmd = new ArrayList<Command>();

        switch (field) {
            case "voice1_enable":
                writeAcsUser(ma4000v2, cmd, field, value, "voice1_enable");
                break;
            case "voice1_number":
                writeAcsUser(ma4000v2, cmd, field, value, "voice1_number");
                break;
            case "voice1_password":
                writeAcsUser(ma4000v2, cmd, field, value, "voice1_password");
                break;
            case "voice2_enable":
                writeAcsUser(ma4000v2, cmd, field, value, "voice2_enable");
                break;
            case "voice2_number":
                writeAcsUser(ma4000v2, cmd, field, value, "voice2_number");
                break;
            case "voice2_password":
                writeAcsUser(ma4000v2, cmd, field, value, "voice2_password");
                break;
            case "sip_proxy":
                writeAcsUser(ma4000v2, cmd, field, value, "sip_proxy");
                break;
            case "ppp_login":
                writeAcsUser(ma4000v2, cmd, field, value, "ppp_login");
                break;
            case "ppp_password":
                writeAcsUser(ma4000v2, cmd, field, value, "ppp_password");
                break;
            case "user_password":
                writeAcsUser(ma4000v2, cmd, field, value, "user_password");
                break;
            case "admin_password":
                writeAcsUser(ma4000v2, cmd, field, value, "admin_password");
                break;
            case "wifi_enable":
                writeAcsUser(ma4000v2, cmd, field, value, "wifi_enable");
                break;
            case "wifi_ssid":
                writeAcsUser(ma4000v2, cmd, field, value, "wifi_ssid");
                break;
            case "wifi_encoding":
                writeAcsUser(ma4000v2, cmd, field, value, "wifi_encoding");
                break;
            case "wifi_password":
                writeAcsUser(ma4000v2, cmd, field, value, "wifi_password");
                break;
            case "internet_vlanid":
                writeAcsUser(ma4000v2, cmd, field, value, "internet_vlanid");
                break;
            case "service_vlanid_1_ip":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 1 ip");
                break;
            case "service_vlanid_2_ip":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 2 ip");
                break;
            case "service_vlanid_3_ip":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 3 ip");
                break;
            case "service_vlanid_4_ip":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 4 ip");
                break;
            case "service_vlanid_5_ip":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 5 ip");
                break;
            case "service_vlanid_6_ip":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 6 ip");
                break;
            case "service_vlanid_7_ip":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 7 ip");
                break;
            case "service_vlanid_8_ip":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 8 ip");
                break;
            case "service_vlanid_1_ppp":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 1 ppp");
                break;
            case "service_vlanid_2_ppp":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 2 ppp");
                break;
            case "service_vlanid_3_ppp":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 3 ppp");
                break;
            case "service_vlanid_4_ppp":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 4 ppp");
                break;
            case "service_vlanid_5_ppp":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 5 ppp");
                break;
            case "service_vlanid_6_ppp":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 6 ppp");
                break;
            case "service_vlanid_7_ppp":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 7 ppp");
                break;
            case "service_vlanid_8_ppp":
                writeAcsUser(ma4000v2, cmd, field, value, "service_vlanid 8 ppp");
                break;
            case "PON serial":
                writeAcsUser(ma4000v2, cmd, field, value, "pon_serial");
                break;
            case "Profile":
                writeAcsUser(ma4000v2, cmd, field, value, "profile");
                break;
            case "Description":
                writeSlotPonOntsn(ma4000v2, cmd, field, value, "description");
                break;
            //case "Serial":
            //    writeSlotPonOntsn(ma4000v2, cmd, field, value, "serial");
            //    break;
            case "Profile services":
                writeSlotPonOntsn(ma4000v2, cmd, field, value, "profile_services");
                break;
            case "Profile management":
                writeSlotPonOntsn(ma4000v2, cmd, field, value, "profile_management");
                break;
            case "Profile multicast":
                writeSlotPonOntsn(ma4000v2, cmd, field, value, "profile_multicast");
                break;
            case "Services override [0]":

                break;
            case "Services override [1]":

                break;
            case "Services override [2]":

                break;
            case "Services override [3]":

                break;
            case "Services override [4]":

                break;
            case "Services override [5]":

                break;
            case "Services override [6]":

                break;
            case "URL":

                break;


        }

        if (cmd.size() < 2) return;

        //try {
            ma4000v2.writeUserCMD(c, cmd, true, null);
        //} catch (IOException e) {
       //     e.printStackTrace();
       // }
    }
}
