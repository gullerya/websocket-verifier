package org.gullerya.messaging;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class BaseWebSocket extends WebSocketAdapter {
	private static final Logger logger = LoggerFactory.getLogger(BaseWebSocket.class);
	public static final List<Session> sessions = new LinkedList<>();

	@Override
	public void onWebSocketBinary(byte[] payload, int offset, int len) {
		System.out.println("binary: " + payload[0] + " - " + offset + " - " + len);
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
	}

	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		sessions.add(sess);
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		super.onWebSocketError(cause);
	}

	@Override
	public void onWebSocketText(String message) {
		System.out.println("text: " + message);
	}
}
