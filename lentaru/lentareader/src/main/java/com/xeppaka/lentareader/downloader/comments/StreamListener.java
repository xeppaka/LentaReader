package com.xeppaka.lentareader.downloader.comments;

import org.java_websocket.handshake.ServerHandshake;

/**
 * Created by kacpa01 on 3/6/14.
 */
public interface StreamListener {
    void onOpen( ServerHandshake handshakedata );
    void onMessage( String message );
    void onClose( int code, String reason, boolean remote );
    void onError( Exception ex );
}
