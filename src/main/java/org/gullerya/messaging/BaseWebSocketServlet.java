package org.gullerya.messaging;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class BaseWebSocketServlet extends WebSocketServlet {

	public void configure(WebSocketServletFactory webSocketServletFactory) {
		webSocketServletFactory.getPolicy().setMaxTextMessageSize(256 * 1024);
		webSocketServletFactory.getPolicy().setMaxBinaryMessageSize(256 * 1024);
		webSocketServletFactory.register(BaseWebSocket.class);
	}
}
