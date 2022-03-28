package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@Slf4j
@CamelSpringBootTest
@SpringBootTest(properties = "demo.from=direct:demo.from")
public class DemoRouteTest extends AbstractRouteTest {

	@Produce("{{demo.from}}")
	private ProducerTemplate producerTemplate;

	@Mock
	@Autowired
	private DemoBean demoBean;

	@Test
	public void should_send_message_to_demo_bean_1() {

		when(demoBean.doSomething()).thenAnswer(invocation -> {
			log.debug("hey, i am a mock, not the real demo bean");
			return 123L;
		});

		producerTemplate.sendBody(null);
		verify(demoBean).doSomething();
		verifyNoMoreInteractions(demoBean);

	}

	@Test
	public void should_send_message_to_demo_bean_2() {

		when(demoBean.doSomething()).thenAnswer(invocation -> {
			log.debug("hey, i am a mock, not the real demo bean");
			return 123L;
		});

		producerTemplate.sendBody(null);
		verify(demoBean).doSomething();
		verifyNoMoreInteractions(demoBean);

	}

}
