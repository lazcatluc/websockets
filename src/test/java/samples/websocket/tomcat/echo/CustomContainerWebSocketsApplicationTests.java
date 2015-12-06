/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package samples.websocket.tomcat.echo;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.SocketUtils;

import samples.websocket.ClientConfiguration;
import samples.websocket.tomcat.SampleTomcatWebSocketApplication;
import samples.websocket.tomcat.echo.CustomContainerWebSocketsApplicationTests.CustomContainerConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({ SampleTomcatWebSocketApplication.class, CustomContainerConfiguration.class })
@WebIntegrationTest
@DirtiesContext
public class CustomContainerWebSocketsApplicationTests {

	private static int PORT = SocketUtils.findAvailableTcpPort();

	@Test
	public void reverseEndpoint() throws Exception {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(ClientConfiguration.class,
				PropertyPlaceholderAutoConfiguration.class)
						.properties("websocket.uri:ws://localhost:" + PORT + "/ws/reverse")
						.run("--spring.main.web_environment=false");
		long count = context.getBean(ClientConfiguration.class).getLatch().getCount();
		AtomicReference<String> messagePayloadReference = context.getBean(ClientConfiguration.class)
				.getMessagePayload();
		context.close();
		assertEquals(0, count);
		assertEquals("Reversed: !dlrow olleH", messagePayloadReference.get());
	}

	@Configuration
	protected static class CustomContainerConfiguration {

		@Bean
		public EmbeddedServletContainerFactory embeddedServletContainerFactory() {
			return new TomcatEmbeddedServletContainerFactory("/ws", PORT);
		}

	}

}
