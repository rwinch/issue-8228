package ch4mpy.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import ch4mpy.WithMockAuthentication;
import ch4mpy.domain.SampleService;

@WebMvcTest(SampleController.class)
class SampleControllerTest {

	@MockBean
	SampleService sampleService;

	@Autowired
	MockMvc mockMvc;

	@Test
	void anonymousIsUnauthorizedAccessToMethod() throws Exception {
		mockMvc.perform(get("/method")).andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockAuthentication(name = "user", authorities = "ROLE_USER")
	void userIsForbidenAccessToMethod() throws Exception {
		mockMvc.perform(get("/method")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockAuthentication(name = "user", authorities = "ROLE_USER")
	void userIsForbidenAccessToEndpoint() throws Exception {
		mockMvc.perform(get("/endpoint")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockAuthentication(name = "Rob", authorities = { "ROLE_USER", "ROLE_AUTHORIZED" })
	void robCanAccessToMethod() throws Exception {
		mockMvc.perform(get("/method")).andExpect(content().string("Hey Rob, how are you?"));
	}

	@Test
	@WithMockAuthentication(name = "Rob", authorities = { "ROLE_USER", "ROLE_AUTHORIZED" })
	void robCanAccessToEndpoint() throws Exception {
		mockMvc.perform(get("/endpoint")).andExpect(content().string("You are granted with [ROLE_USER, ROLE_AUTHORIZED]."));
	}

}
