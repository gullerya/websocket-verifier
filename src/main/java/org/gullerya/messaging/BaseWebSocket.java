package org.gullerya.messaging;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class BaseWebSocket extends WebSocketAdapter {
	private static final Logger logger = LoggerFactory.getLogger(BaseWebSocket.class);
	public static final List<Session> sessions = new LinkedList<>();

	@Override
	public void onWebSocketConnect(Session session) {
		super.onWebSocketConnect(session);
		sessions.add(session);
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		sessions.remove(getSession());
		super.onWebSocketClose(statusCode, reason);
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		logger.error("WS encountered error", cause);
	}

	@Override
	public void onWebSocketBinary(byte[] payload, int offset, int len) {
		if (payload[0] == 0) {
			try {
				getRemote().sendString(String.valueOf(len - 2));
			} catch (IOException ioe) {
				logger.error("failed to send string back", ioe);
			}
		} else {
			int secondsToWait = payload[0];
			new Thread(() -> {
				try {
					Thread.sleep(secondsToWait * 1000);
				} catch (InterruptedException ie) {
					logger.error("interrupted during the wait");
				}
				try {
					byte[] contentToSendBack = new byte[payload[1] * 1024];
					for (int i = 0; i < contentToSendBack.length; i++) contentToSendBack[i] = 7;
					getRemote().sendBytes(ByteBuffer.wrap(contentToSendBack));
				} catch (IOException ioe) {
					logger.error("failed to send bytes back", ioe);
				}
			}).start();
		}
	}

	@Override
	public void onWebSocketText(String message) {
		try {
			if (message.startsWith("textToReturn:")) {
				getRemote().sendString(message.replace("textToReturn:", ""));
			} else {
				getRemote().sendString(String.valueOf(message.length()));
			}
		} catch (IOException ioe) {
			logger.error("failed to send string back", ioe);
		}
	}
}
