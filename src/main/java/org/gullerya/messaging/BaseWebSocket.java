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
		super.onWebSocketClose(statusCode, reason);
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		super.onWebSocketError(cause);
	}

	@Override
	public void onWebSocketBinary(byte[] payload, int offset, int len) {
		System.out.println("binary: " + payload[0] + " - " + offset + " - " + len);
		if (payload[0] == 0) {
			System.out.println("this is binary, expected is " + (payload[1] * 1024 + 2));
		} else {
			int secondsToWait = payload[0];
			new Thread(() -> {
				try {
					Thread.sleep(secondsToWait * 1000);
				} catch (InterruptedException ie) {
					logger.error("interrupted during the wait");
				}
				try {
					getSession().getRemote().sendBytes(ByteBuffer.wrap(new byte[]{1, 1, 1}));
				} catch (IOException ioe) {
					logger.error("failed to send bytes back", ioe);
				}
			}).start();
		}
	}

	@Override
	public void onWebSocketText(String message) {
		if (message.startsWith("textToReturn:")) {
			try {
				getSession().getRemote().sendString(message.replace("textToReturn:", ""));
			} catch (IOException ioe) {
				logger.error("failed to send string back", ioe);
			}
		}
	}
}
