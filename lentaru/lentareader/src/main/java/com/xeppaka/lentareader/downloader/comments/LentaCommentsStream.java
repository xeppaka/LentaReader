package com.xeppaka.lentareader.downloader.comments;

import android.util.Log;
import android.widget.Toast;

import com.xeppaka.lentareader.utils.LentaConstants;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.logging.Logger;

/**
 * Created by kacpa01 on 3/6/14.
 */
public class LentaCommentsStream {
    private static final String HYPERCOMMENTS_STREAMS[] =
            new String[] {"ws://c1n1.hypercomments.com/stream/subscribe?stream_id=%s&widget_id=%s",
                          "ws://c1n2.hypercomments.com/stream/subscribe?stream_id=%s&widget_id=%s",
                          "ws://c1n3.hypercomments.com/stream/subscribe?stream_id=%s&widget_id=%s"};
    private WebSocketClient wsClient;

    public void connect(String streamId, StreamListener listener) {
        if (wsClient != null) {
            throw new IllegalStateException("Already connected. Call method disconnect before connecting again.");
        }

        connect(streamId, listener, 0);
    }

    private void connect(final String streamId, final StreamListener listener, final int retry) {
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
                listener.onClose(code, reason, remote);
            }

            @Override
            public void onError(Exception ex) {
                Log.d(LentaConstants.LoggerMainAppTag, "Error occured while connecting with web socket.", ex);

                if (retry + 1 < HYPERCOMMENTS_STREAMS.length) {
                    LentaCommentsStream.this.connect(streamId, listener, retry + 1);
                } else {
                    listener.onError(ex);
                }
            }
        };

        wsClient.connect();
    }

    public void disconnect() {
        if (wsClient != null) {
            wsClient.close();
            wsClient = null;
        }
    }
}
