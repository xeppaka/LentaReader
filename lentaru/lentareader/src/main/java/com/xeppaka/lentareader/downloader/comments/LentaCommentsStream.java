package com.xeppaka.lentareader.downloader.comments;

import android.util.Log;
import android.widget.Toast;

import com.xeppaka.lentareader.utils.LentaConstants;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * Created by kacpa01 on 3/6/14.
 */
public class LentaCommentsStream {
    private static final String HYPERCOMMENTS_STREAMS[] =
            new String[] {"ws://c1n1.hypercomments.com/stream/subscribe?stream_id=%s&widget_id=%s",
                          "ws://c1n2.hypercomments.com/stream/subscribe?stream_id=%s&widget_id=%s",
                          "ws://c1n3.hypercomments.com/stream/subscribe?stream_id=%s&widget_id=%s"};

    private static final int RECONNECT_TIME_REMOTE_CLOSE = 500;
    private static final int RECONNECT_TIME_ERROR = 1000;
    private static String TIMER_NAME = "Web Socket auto connect";

    private WebSocketClient wsClient;
    private String streamId;
    private boolean continueConnecting;

    public LentaCommentsStream(String streamId) {
        this.streamId = streamId;
    }

    public void connect(StreamListener listener) {
        if (wsClient != null) {
            throw new IllegalStateException("Already connected. Call method disconnect before connecting again.");
        }

        continueConnecting = true;

        connect(listener, 0);
    }

    private void connect(final StreamListener listener, final int retry) {
        final String url = String.format(HYPERCOMMENTS_STREAMS[retry], streamId, LentaConstants.COMMENTS_WIDGET_ID);

        wsClient = new WebSocketClient(URI.create(url)) {
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

                if (remote && continueConnecting) {
                    final Timer timer = new Timer(TIMER_NAME);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            LentaCommentsStream.this.connect(listener, retry);
                        }
                    }, RECONNECT_TIME_REMOTE_CLOSE);
                } else {
                    listener.onClose(code, reason, remote);
                }
            }

            @Override
            public void onError(Exception ex) {
                Log.d(LentaConstants.LoggerMainAppTag, "Error occured while connecting to web socket.", ex);

                if (continueConnecting) {
                    final Timer timer = new Timer(TIMER_NAME);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            LentaCommentsStream.this.connect(listener, (retry + 1) % HYPERCOMMENTS_STREAMS.length);
                        }
                    }, RECONNECT_TIME_ERROR);
                }
            }
        };

        wsClient.connect();
    }

    public void disconnect() {
        continueConnecting = false;

        if (wsClient != null) {
            wsClient.close();
            wsClient = null;
        }
    }
}
