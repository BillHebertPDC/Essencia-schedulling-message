package com.example.studio_many_scheduling_actions_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"whatsapp.provider=noop",
		"spring.rabbitmq.username=teste",
		"spring.rabbitmq.password=teste",
		"spring.rabbitmq.listener.simple.auto-startup=false"
})
class StudioManySchedullingActionsServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
