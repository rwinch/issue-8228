package ch4mpy.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch4mpy.domain.SampleService;

@RestController
public class SampleController {
	private final SampleService sampleService;

	@Autowired
	public SampleController(SampleService sampleService) {
		super();
		this.sampleService = sampleService;
	}

	@RequestMapping("/service")
	public ResponseEntity<String> securedService(Authentication auth) {
		return ResponseEntity.ok(this.sampleService.greet(auth));
	}

	@RequestMapping("/method")
	@PreAuthorize("hasRole('ROLE_AUTHORIZED')")
	public ResponseEntity<String> securedMethod(Authentication auth) {
		return ResponseEntity.ok(String.format("Hey %s, how are you?", auth.getName()));
	}

	@RequestMapping("/endpoint")
	public ResponseEntity<String> securedEndpoint(Authentication auth) {
		return ResponseEntity.ok(String.format("You are granted with %s.", auth.getAuthorities()));
	}
}
