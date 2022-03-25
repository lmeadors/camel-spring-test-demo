package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DemoBean {
	public long doSomething() {
		final var now = System.currentTimeMillis();
		log.debug("hello - the time is now: {}", now);
		return now;
	}
}
