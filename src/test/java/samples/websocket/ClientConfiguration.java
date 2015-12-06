package samples.websocket;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import samples.websocket.tomcat.client.SimpleClientWebSocketHandler;

@Configuration
public class ClientConfiguration implements CommandLineRunner {

	private static final Log LOGGER = LogFactory.getLog(ClientConfiguration.class);

	@Value("${websocket.uri}")
	private String webSocketUri;

	private final CountDownLatch latch = new CountDownLatch(1);

	private final AtomicReference<String> messagePayload = new AtomicReference<String>();

	@Override
	public void run(String... args) throws Exception {
		LOGGER.info("Waiting for response: latch=" + this.getLatch().getCount());
		if (this.getLatch().await(10, TimeUnit.SECONDS)) {
			LOGGER.info("Got response: " + this.getMessagePayload().get());
		} else {
			LOGGER.info("Response not received: latch=" + this.getLatch().getCount());
		}
	}

	@Bean
	public WebSocketConnectionManager wsConnectionManager() {

		WebSocketConnectionManager manager = new WebSocketConnectionManager(client(), handler(), this.webSocketUri);
		manager.setAutoStartup(true);

		return manager;
	}

	@Bean
	public StandardWebSocketClient client() {
		return new StandardWebSocketClient();
	}

	@Bean
	public SimpleClientWebSocketHandler handler() {
		return new SimpleClientWebSocketHandler(() -> "Hello!", this.getLatch(), this.getMessagePayload());
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	public AtomicReference<String> getMessagePayload() {
		return messagePayload;
	}

}