package ch4mpy.domain;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class SampleService {

	@PreAuthorize("hasRole('ROLE_AUTHORIZED')")
	public String greet(Authentication who) {
		return String.format("Hello %s!", who.getName());
	}

}
