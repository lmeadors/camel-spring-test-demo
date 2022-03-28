package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

@Slf4j
@Component("demoBean")
public class DemoBean {

	public DemoBean() {
		log.debug("wtf?!");
	}

	@Handler
	public long doSomething() {
		final var now = System.currentTimeMillis();
		log.debug("hello - i am not a mock and the time is now: {}", now);
		return now;
	}

}
