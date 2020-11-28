/*
 *
 *  * Copyright 2019-2020 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package test.org.springdoc.api.app148;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import test.org.springdoc.api.TestCommon;

import org.springframework.boot.actuate.autoconfigure.web.server.LocalManagementPort;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.WebClient;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;


@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT,
		properties = { "management.endpoints.web.exposure.include:*",
				"springdoc.show-actuator=true",
				"management.server.port=9098",
				"server.port=6666",
				"springdoc.use-management-port=true",
				"management.server.base-path=/test",
				"management.endpoints.web.base-path=/application" })
public class SpringDocApp148Test extends TestCommon {

	@SpringBootApplication
	@ComponentScan(basePackages = { "org.springdoc", "test.org.springdoc.api.app148" })
	static class SpringDocTestApp {}

	@LocalManagementPort
	private int managementPort;

	private WebClient webClient;

	@PostConstruct
	void init() {
		webClient =	WebClient.builder().baseUrl("http://localhost:" + this.managementPort)
				.build();
	}

	@Test
	public void testApp() throws Exception {
		String result =webClient.get().uri("/test/application/openapi/users").retrieve()
				.bodyToMono(String.class).block();

		String expected = getContent("results/app148-1.json");
		assertEquals(expected, result, true);
	}

	@Test
	public void testApp2() throws Exception {
		String result = webClient.get().uri("/test/application/openapi/x-actuator").retrieve()
				.bodyToMono(String.class).block();

		String expected = getContent("results/app148-2.json");
		assertEquals(expected, result, true);
	}

}
