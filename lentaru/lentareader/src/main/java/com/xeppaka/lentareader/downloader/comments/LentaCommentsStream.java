package com.xeppaka.lentareader.downloader.comments;

import android.util.Log;
import com.xeppaka.lentareader.utils.LentaConstants;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kacpa01 on 3/6/14.
 */
public class LentaCommentsStream {
    private static final String HYPERCOMMENTS_STREAMS[] =
            new String[] {"ws://c1n1.hypercomments.com/stream/subscribe?stream_id=%s&widget_id=%s",
                          "ws://c1n2.hypercomments.com/stream/subscribe?stream_id=%s&widget_id=%s",
                          "ws://c1n3.hypercomments.com/stream/subscribe?stream_id=%s&widget_id=%s"};

    private static final int PING_TIME = 15000;
    private static final int RECONNECT_TIME_AFTER_CLOSE = 500;
    private static String TIMER_RECONNECT_NAME = "Web Socket auto reconnect";
    private static String TIMER_PING_NAME = "Web Socket ping timer";

    private String streamId;
    private boolean activeConnection;
    private WebSocketClient wsClient;
    private Object webSocketSync = new Object();
    private Timer pingTimer;

    public LentaCommentsStream(String streamId) {
        this.streamId = streamId;
    }

    public void connect(StreamListener listener) {
        if (wsClient != null) {
            synchronized (webSocketSync) {
                if (wsClient != null) {
                    throw new IllegalStateException("Already connected. Call method disconnect before connecting again.");
                }
            }
        }

        activeConnection = true;
        connect(listener, 0);

        pingTimer = new Timer(TIMER_PING_NAME);
        pingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (wsClient != null) {
                    synchronized (webSocketSync) {
                        if (wsClient != null) {
                            wsClient.send("0");
                        }
                    }
                }
            }
        }, PING_TIME, PING_TIME);
    }

    private void connect(final StreamListener listener, final int retry) {
        final String uri = String.format(HYPERCOMMENTS_STREAMS[retry], streamId, LentaConstants.COMMENTS_WIDGET_ID);

        Log.d(LentaConstants.LoggerMainAppTag, uri.toString());

        synchronized (webSocketSync) {
            wsClient = new WebSocketClient(URI.create(uri)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    listener.onOpen(handshakedata);
                }

                @Override
                public void onMessage(String message) {
                    listener.onMessage(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(LentaConstants.LoggerMainAppTag, "Web Socket connection closed.");

                    if (activeConnection) {
                        final Timer timer = new Timer(TIMER_RECONNECT_NAME);
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                LentaCommentsStream.this.connect(listener, retry);
                            }
                        }, RECONNECT_TIME_AFTER_CLOSE);
                    }

                    listener.onClose(code, reason, remote);
                }

                @Override
                public void onError(Exception ex) {
                    Log.d(LentaConstants.LoggerMainAppTag, "Web socket error.", ex);

                    listener.onError(ex);
                }
            };

            wsClient.connect();
        }
    }

    public void disconnect() {
        activeConnection = false;

        if (wsClient != null) {
            synchronized (webSocketSync) {
                if (wsClient != null) {
                    wsClient.close();
                    wsClient = null;
                }
            }
        }
    }
}
