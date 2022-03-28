package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

@Slf4j
@TestExecutionListeners(
	// we add our listener to add our mocks to the spring bean context
	listeners = {AbstractRouteTest.PreSpringMockListener.class},
	// we want all the other listeners, too
	mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public abstract class AbstractRouteTest {

	@Slf4j
	public static class PreSpringMockListener implements Ordered, TestExecutionListener {

		@Override
		public int getOrder() {
			// this is a magic number - it needs to be more than 1950 (mockito) but less than 2000 (spring)
			return 1969;
		}

		@Override
		public void afterTestMethod(final TestContext testContext) throws Exception {
			replaceSpringBeansWithMocks(testContext);
		}

		@Override
		public void prepareTestInstance(final TestContext testContext) throws Exception {
			replaceSpringBeansWithMocks(testContext);
		}

		protected void replaceSpringBeansWithMocks(final TestContext testContext) {

			log.info("preparing test context...");
			final ApplicationContext context = testContext.getApplicationContext();
			if (context instanceof AnnotationConfigApplicationContext applicationContext) {
				final SingletonBeanRegistry beanRegistry = applicationContext.getBeanFactory();
				final var beanFactory = applicationContext.getDefaultListableBeanFactory();
				final List<Field> fields = new ArrayList<>();
				ReflectionUtils.doWithFields(
					testContext.getTestClass(),
					field -> {
						if (field.isAnnotationPresent(Mock.class)) {
							field.setAccessible(true);
							fields.add(field);
						}
					}
				);

				fields.forEach(field -> {
					try {
						final var key = field.getType().getAnnotation(Component.class).value();
						final var testInstance = testContext.getTestInstance();
						final var value = getOrCreateMockObject(field, testInstance);
						final var springVersion = beanFactory.getBean(key);
						if (!Mockito.mockingDetails(springVersion).isMock()) {
							log.debug(
								"adding field {} to spring registry (hash: {})",
								field.getName(),
								value.hashCode()
							);
							beanFactory.destroySingleton(key);
							beanRegistry.registerSingleton(key, value);
						} else {
							reset(value);
						}
					} catch (final IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				});
			}
			log.info("prepared test context...");

		}

		private Object getOrCreateMockObject(
			final Field field,
			final Object instance
		) throws IllegalAccessException {
			log.debug("checking {} on {}", field, instance);
			final Object value = field.get(instance);
			if (null == value) {
				log.debug("value was null - creating mock");
				final Object mock = mock(field.getType());
				field.set(instance, mock);
				return mock;
			}
			log.debug("value was set - using it");
			return value;
		}

	}

}
