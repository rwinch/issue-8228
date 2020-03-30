package ch4mpy.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ch4mpy.WithMockAuthentication;

@ExtendWith(SpringExtension.class)
@Import(SampleServiceTest.TestConfig.class)
class SampleServiceTest {

	@Autowired
	SampleService sampleService;

	@Test
	void whenNoAnnotationThenTestSecurityContextHasNoAuthentication() {
		assertNull(TestSecurityContextHolder.getContext().getAuthentication());
	}

	@Test()
	@WithMockAuthentication(authorities = "ROLE_USER")
	void whenAnnotatedWithRoleUserThenAccessIsDenied() {
		final var who = TestSecurityContextHolder.getContext().getAuthentication();
		assertThrows(AccessDeniedException.class, () -> sampleService.greet(who));
	}

	@Test
	@WithMockAuthentication(name = "Rob", authorities = { "ROLE_USER", "ROLE_AUTHORIZED" })
	void whenAnnotatedWithRoleAuthorizedThenGreetingIsReturned() {
		final var who = TestSecurityContextHolder.getContext().getAuthentication();
		assertEquals("Hello Rob!", sampleService.greet(who));
	}

	@TestConfiguration
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	@ComponentScan(basePackageClasses = SampleService.class)
	public static class TestConfig {
	}
}
