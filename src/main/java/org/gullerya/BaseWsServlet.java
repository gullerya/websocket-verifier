package org.gullerya;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class BaseWsServlet extends WebSocketServlet {

	public void configure(WebSocketServletFactory webSocketServletFactory) {
		webSocketServletFactory.getPolicy().setIdleTimeout(15000);
		webSocketServletFactory.register(BaseWebSocket.class);
	}
}
