package org.gullerya;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configurer's responsibility is to read/parse server's configuration and provide it to the rest of the application
 * - support configuration by properties file
 * - support configuration be system properties
 */
public class Configurer {
	private static final Logger logger = LoggerFactory.getLogger(Configurer.class);

	public static Configuration getConfiguration() {
		return Configuration.INSTANCE;
	}

	public static final class Configuration {
		private static final Configuration INSTANCE = new Configuration();
		private final int httpPort;
		private final int httpsPort;

		private Configuration() {
			httpPort = 8585;
			httpsPort = 8686;
		}

		public int getHttpPort() {
			return httpPort;
		}

		public int getHttpsPort() {
			return httpsPort;
		}
	}
}
