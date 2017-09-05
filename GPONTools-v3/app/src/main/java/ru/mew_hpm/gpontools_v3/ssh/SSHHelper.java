package ru.mew_hpm.gpontools_v3.ssh;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import ru.mew_hpm.gpontools_v3.dao.SSHCommand;
import ru.mew_hpm.gpontools_v3.tools.AppUtils;

public class SSHHelper extends AsyncTask {
    private static final Map<String, SSHHelper>
            connectionsPool = new HashMap<>();

    private Map<String, SSHHelperEventListener>
            eventsListeners = new HashMap<>();

    private final Context
            context;

    private volatile boolean
            runThread = true;

    private volatile boolean
            connectDisabled = true;

    private JSch
            jsch = null;

    private String shellPrefix = "rconnect";

    private Session
            currSSHSess = null;

    private ChannelShell
            channelShell;

    private final ConcurrentLinkedQueue<SSHCommand>
            cmdExecRequests = new ConcurrentLinkedQueue<>();

    private volatile String sshAddr = null;
    private volatile String sshUser = null;
    private volatile String sshPass = null;

    private OutputStream sshOutputStream = null;

    private final String
            tName;

    private final Activity
            fragmentActivity;

    private volatile int
            sshPort = 22;

    private SSHHelper(Context c, Activity f, String threadName) {
        context = c;
        tName = threadName;
        fragmentActivity = f;
    }

    public static SSHHelper add(Context c, Activity f, String name) {
        if (connectionsPool.containsKey(name)) return connectionsPool.get(name);

        final SSHHelper ssh = new SSHHelper(c, f, name);
        connectionsPool.put(name, ssh);
        ssh.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return ssh;
    }

    public static void addEventsListener(String threadName, String listenerName, SSHHelperEventListener el) {
        if (!connectionsPool.containsKey(threadName)) return;

        final SSHHelper ssh = connectionsPool.get(threadName);
        if (ssh.getEventsListeners().containsKey(listenerName)) return;

        ssh.getEventsListeners().put(listenerName, el);
    }

    public static void stopAll() {
        for (SSHHelper ssh : connectionsPool.values()) {
            ssh.setRunThread(false);
        }
    }

    public static SSHHelper get(String name) {
        return connectionsPool.get(name);
    }

    public boolean isRunThread() {
        return runThread;
    }

    public void setRunThread(boolean runThread) {
        this.runThread = runThread;
    }

    void errorState(final Exception e) {
        fragmentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (String name : getEventsListeners().keySet()) {
                    getEventsListeners().get(name).OnError(tName, name, e);
                }
            }
        });
    }

    void errorState() {
        errorState(null);
    }

    void connected() {
        fragmentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (String name : getEventsListeners().keySet()) {
                    getEventsListeners().get(name).OnConnect(tName);
                }
            }
        });
    }

    void progress(final String data) {
        fragmentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (String name : getEventsListeners().keySet()) {
                    getEventsListeners().get(name).OnProgress(data);
                }
            }
        });
    }

    public void exec(ArrayList<SSHCommand> cmds) {
        for (int i=0; i<cmds.size(); i++) {
            cmdExecRequests.add(cmds.get(i));
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        jsch = new JSch();
        BufferedReader readerIn = null;

        while (runThread) {
            if (isConnectDisabled()) {
                if (currSSHSess != null) {
                    channelShell.disconnect();
                    currSSHSess.disconnect();
                    currSSHSess = null;
                    fragmentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (String name : getEventsListeners().keySet()) {
                                getEventsListeners().get(name).OnDisconnect(tName);
                            }
                        }
                    });
                }
                continue;
            }
            if ((sshAddr == null) || (sshUser == null) || (sshPass == null)) continue;

            if (currSSHSess == null) {
                try {
                    progress("Connecting...");

                    currSSHSess = jsch.getSession(sshUser, sshAddr, sshPort);
                    currSSHSess.setPassword(sshPass);

                    Log.d("!!!!!", "login: "+sshUser+"; password: "+sshPass);

                    Properties prop = new Properties();
                    prop.put("StrictHostKeyChecking", "no");
                    currSSHSess.setConfig(prop);
                    currSSHSess.connect();

                    channelShell = (ChannelShell) currSSHSess.openChannel("shell");
                    channelShell.connect();
                    channelShell.start();

                    sshOutputStream = channelShell.getOutputStream();
                    readerIn = new BufferedReader(new InputStreamReader(channelShell.getInputStream()));
                    final String retCmd = AppUtils.ReaderToStringDNU(readerIn, shellPrefix);

                    connected();
                } catch (IOException | JSchException e) {
                    e.printStackTrace();
                    currSSHSess = null;
                    errorState();
                }
            } else {
                if (currSSHSess.isConnected() && (readerIn != null)) {
                    if (!cmdExecRequests.isEmpty()) {
                        final SSHCommand cmdExecText = cmdExecRequests.poll();
                        Log.d("### COMMAND ###", cmdExecText.getCommand());

                        try {
                            sshOutputStream.write((cmdExecText.getCommand()+"\n").getBytes());
                            sshOutputStream.flush();
                            final String retCmd = AppUtils.ReaderToStringDNU(readerIn, shellPrefix, cmdExecText);
                            if (retCmd != null)
                                cmdExecText.setCmdOut(retCmd);
                            else
                                cmdExecText.setErrorOut("timeout");

                            fragmentActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (cmdExecText.getResultListener() != null)
                                        cmdExecText.getResultListener().OnCmdExecResult(cmdExecText);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            errorState(e);
                            Log.d("### COMMAND ###", "ERROR");
                        }
                    }
                } else {
                    currSSHSess = null;
                    errorState();
                }
            }
        }

        if (currSSHSess != null) currSSHSess.disconnect();
        if (jsch != null) jsch = null;

        return null;
    }

    public String getSshAddr() {
        return sshAddr;
    }

    public void setSshAddr(String sshAddr) {
        this.sshAddr = sshAddr;
    }

    public String getSshUser() {
        return sshUser;
    }

    public void setSshUser(String sshUser) {
        this.sshUser = sshUser;
    }

    public String getSshPass() {
        return sshPass;
    }

    public void setSshPass(String sshPass) {
        this.sshPass = sshPass;
    }

    public boolean isConnectDisabled() {
        return connectDisabled;
    }

    public void setConnectDisabled(boolean connectDisabled) {
        this.connectDisabled = connectDisabled;
    }

    public Map<String, SSHHelperEventListener> getEventsListeners() {
        return eventsListeners;
    }

    public void setEventsListeners(Map<String, SSHHelperEventListener> eventsListeners) {
        this.eventsListeners = eventsListeners;
    }

    public void setSshPort(String port) {
        try {
            sshPort = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            sshPort = 22;
            return;
        }
    }
}
