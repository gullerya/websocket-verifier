package org.gullerya;

/**
 * Configurer's responsibility is to read/parse server's configuration and provide it to the rest of the application
 * - support configuration by properties file
 * - support configuration be system properties
 */
class Configurer {
	static Configuration getConfiguration() {
		return Configuration.INSTANCE;
	}

	static final class Configuration {
		private static final Configuration INSTANCE = new Configuration();
		private final int httpPort;
		private final int httpsPort;

		private Configuration() {
			httpPort = 8085;
			httpsPort = 8086;
		}

		int getHttpPort() {
			return httpPort;
		}

		int getHttpsPort() {
			return httpsPort;
		}

		@Override
		public String toString() {
			return "Configuration{" +
					"httpPort=" + httpPort +
					", httpsPort=" + httpsPort +
					'}';
		}
	}
}
