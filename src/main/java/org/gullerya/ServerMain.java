package org.gullerya;

import org.eclipse.jetty.http.CookieCompliance;
import org.eclipse.jetty.http.HttpCompliance;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.gullerya.rest.BaseHttpServlet;
import org.gullerya.messaging.BaseWebSocketServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMain {
	private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);

	public static void main(String[] args) throws Exception {
		//  obtain configuration
		Configurer.Configuration configuration = Configurer.getConfiguration();
		new ServerMain(configuration);
	}

	private ServerMain(Configurer.Configuration configuration) throws Exception {
		logger.info("server will be running with the following configuration: " + configuration);

		//  init server
		Server server = new Server();
		addHttpConnector(server, configuration.getHttpPort());
		addSslConnector(server, configuration.getHttpsPort());

		//  add HTTP servlet
		ServletContextHandler httpServletContextHandler = new ServletContextHandler(null, "/rest");
		httpServletContextHandler.addServlet(BaseHttpServlet.class, "/status");

		//  add WS servlet
		ServletContextHandler wsServletContextHandler = new ServletContextHandler(null, "/messaging", ServletContextHandler.SESSIONS);
		wsServletContextHandler.addServlet(BaseWebSocketServlet.class, "/test");

		server.setHandler(new HandlerList(httpServletContextHandler, wsServletContextHandler));
		server.start();
		//server.dumpStdErr();
		server.join();
	}

	private static void addHttpConnector(Server server, int port) {
		//  http configuration
		HttpConfiguration httpConfiguration = new HttpConfiguration();
		httpConfiguration.setCookieCompliance(CookieCompliance.RFC6265);
		httpConfiguration.setIdleTimeout(30000);

		//  http connection factory
		HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration, HttpCompliance.RFC7230);

		//  http connector
		ServerConnector httpConnector = new ServerConnector(server, httpConnectionFactory);
		httpConnector.getSelectorManager().setConnectTimeout(15000);
		httpConnector.setPort(port);
		server.addConnector(httpConnector);
	}

	private static void addSslConnector(Server server, int port) throws Exception {
		//  ssl context
		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.addExcludeProtocols("SSL", "SSLv2", "SSLv2Hello", "SSLv3");
		sslContextFactory.setRenegotiationAllowed(false);
		sslContextFactory.setKeyStoreResource(Resource.newClassPathResource("keystore"));
		sslContextFactory.setKeyStorePassword("87654321");

		//  ssl connection factory
		SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());

		//  https configuration
		HttpConfiguration config = new HttpConfiguration();
		config.setSecureScheme("https");
		config.setSecurePort(port);
		HttpConfiguration sslConfiguration = new HttpConfiguration(config);
		HttpConnectionFactory httpsConnectionFactory = new HttpConnectionFactory(sslConfiguration);

		//  ssl connector
		ServerConnector httpsConnector = new ServerConnector(server, sslConnectionFactory, httpsConnectionFactory);
		httpsConnector.getSelectorManager().setConnectTimeout(15000);
		httpsConnector.setPort(port);
		server.addConnector(httpsConnector);
	}
}
