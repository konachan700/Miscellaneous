package ru.jneko.puseradmin.connect;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class SSHWorker extends AsyncTask {
    public static interface SSHWorkerResult {
        public void Connected(Session s);
        public void ConnectError(Exception e);
        public void ConnectedTimer();
        public void OnTextReceived(Session s, String text, String currLine);
    }

    private static Map<String, SSHWorker>
        connPool = new HashMap<>();

    private final FragmentActivity cont;

    private volatile Exception lastError;

    private final Set<SSHWorkerResult> actionListeners = new HashSet<>();

    private volatile boolean stopTask = false;
    private volatile boolean connected = false;
    private volatile boolean tryConnect = false;

    private volatile int sshPort = 22;

    private String sshAddr = null;
    private String sshUser = null;
    private String sshPass = null;

    private long currTimer = 0;

    private JSch jsch = null;
    private Session currSSHSess = null;

    private Channel channel;

    private InputStream sshInputStream = null;
    private OutputStream sshOutputStream = null;

    private final StringBuilder incomingText = new StringBuilder();

    private SSHWorker(FragmentActivity c) {
        super();
        cont = c;
        jsch = new JSch();
    }

    public static SSHWorker addWorkerToPool(FragmentActivity c, String threadName) {
        if (connPool.containsKey(threadName))
            return connPool.get(threadName);

        final SSHWorker ssh = new SSHWorker(c);
        connPool.put(threadName, ssh);
        return ssh;
    }

    public static SSHWorker addWorkerToPool(FragmentActivity c, String threadName, String addr, int port, String user, String passw) {
        final SSHWorker ssh = new SSHWorker(c);
        ssh.setSshAddr(addr);
        ssh.setSshPort(port);
        ssh.setSshUser(user);
        ssh.setSshPass(passw);
        connPool.put(threadName, ssh);
        return ssh;
    }

    public static SSHWorker getWorkerFromPool(String threadName) {
        return connPool.get(threadName);
    }

    public static void removeWorkerFromPool(String threadName) {
        final SSHWorker ssh = connPool.get(threadName);
        if (ssh == null) return;
        ssh.disconnect();
        ssh.stop();
        connPool.remove(threadName);
    }

    public void stop() {
        stopTask = true;
    }

    public void addActionListener(SSHWorkerResult al) {
        actionListeners.add(al);
    }

    public void removeActionListener(SSHWorkerResult al) {
        actionListeners.remove(al);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        jsch = new JSch();

        while (true) {
            if (stopTask) {
                if (currSSHSess != null) currSSHSess.disconnect();
                if (jsch != null) jsch = null;
                return null;
            }

            if (currTimer < System.currentTimeMillis()) {
                currTimer = System.currentTimeMillis() + 500;
                if ((currSSHSess != null) && (currSSHSess.isConnected())) {
                    cont.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (SSHWorkerResult al : actionListeners) al.ConnectedTimer();
                        }
                    });
                }
            }

            if (connected) {
                try {
                    if (getSshInputStream().available() > 0) {
                        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        while (true) {
                            final int b = getSshInputStream().read();
                            if ((b < 0) || (b > 255)) break;
                            baos.write(b);

                            if ((b == '\n') || (b == '>') || (b == '#')) {
                                final String tempStr = new String(baos.toByteArray()); // create the unusable in this thread object, for mt safety
                                for (SSHWorkerResult al : actionListeners) al.OnTextReceived(currSSHSess, incomingText.toString(), tempStr);
                                incomingText.append(baos.toString());
                                baos.reset();
                            }
                        }
                    }
                } catch (IOException e) {
                    currSSHSess = null;
                    lastError = e;
                    connected = false;

                    cont.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (SSHWorkerResult al : actionListeners) al.ConnectError(lastError);
                        }
                    });
                }
            }

            if (tryConnect) {
                tryConnect = false;
                connected  = false;

                if ((sshAddr != null) && (sshUser != null) && (sshPass != null)) {
                    try {
                        currSSHSess = jsch.getSession(sshUser, sshAddr, sshPort);
                        currSSHSess.setPassword(sshPass);

                        Properties prop = new Properties();
                        prop.put("StrictHostKeyChecking", "no");
                        currSSHSess.setConfig(prop);

                        currSSHSess.connect();

                        channel = currSSHSess.openChannel("shell");
                        channel.connect();

                        sshInputStream = channel.getInputStream();
                        sshOutputStream = channel.getOutputStream();

                        connected = true;

                        cont.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (SSHWorkerResult al : actionListeners) al.Connected(currSSHSess);
                            }
                        });
                    } catch (JSchException | IOException e) {
                        e.printStackTrace();

                        currSSHSess = null;
                        lastError = e;

                        cont.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (SSHWorkerResult al : actionListeners) al.ConnectError(lastError);
                            }
                        });
                    }
                }
            }

            if ((currSSHSess != null) && (currSSHSess.isConnected())) {

            }
        }
    }

    public void truncateInputCache() {
        incomingText.delete(0, incomingText.length());
    }

    public void disconnect() {
        if ((currSSHSess != null) && (currSSHSess.isConnected())) currSSHSess.disconnect();
    }

    public void tryConnect() {
        if ((currSSHSess != null) && (currSSHSess.isConnected())) return;
        tryConnect = true;
    }

    public InputStream getSshInputStream() {
        return sshInputStream;
    }

    public OutputStream getSshOutputStream() {
        return sshOutputStream;
    }

    public int getSshPort() {
        return sshPort;
    }

    public void setSshPort(int sshPort) {
        this.sshPort = sshPort;
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
}
