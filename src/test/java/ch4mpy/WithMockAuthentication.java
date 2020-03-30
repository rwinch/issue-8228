/*
 * Copyright 2019 Jérôme Wacongne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ch4mpy;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.annotation.AliasFor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithMockAuthentication.Factory.class)
public @interface WithMockAuthentication {

	@AliasFor("authorities")
	String[] value() default { "ROLE_USER" };

	String name() default "user";

	String[] authorities() default { "ROLE_USER" };
	
	boolean useMock() default true;

	@AliasFor(annotation = WithSecurityContext.class)
	TestExecutionEvent setupBefore() default TestExecutionEvent.TEST_METHOD;

	public static class Factory implements WithSecurityContextFactory<WithMockAuthentication> {
		@Override
		public SecurityContext createSecurityContext(WithMockAuthentication annotation) {
			final SecurityContext context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication(annotation.useMock() ? bogousAuthentication(annotation) : workingAuthentication(annotation));

			return context;
		}

		public Authentication bogousAuthentication(WithMockAuthentication annotation) {
			var auth = mock(Authentication.class);
			when(auth.getName()).thenReturn(annotation.name());
			when(auth.getAuthorities()).thenReturn((Collection) Stream.of(annotation.authorities()).map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
			when(auth.isAuthenticated()).thenReturn(true);
			return auth;
		}
		
		public Authentication workingAuthentication(WithMockAuthentication annotation) {
		    return new TestAuthentication(
		        annotation.name(), Stream.of(annotation.authorities()).map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
		}
	}
	
	public static class TestAuthentication implements Authentication {
		private static final long serialVersionUID = 1503468173338566812L;
		
		private final String name;
		private final Collection<GrantedAuthority> authorities;
		private boolean isAuthenticated = true;

		public TestAuthentication(String name, Collection<GrantedAuthority> authorities) {
			super();
			this.name = name;
			this.authorities = authorities;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return authorities;
		}

		@Override
		public Object getCredentials() {
			return name;
		}

		@Override
		public Object getDetails() {
			return name;
		}

		@Override
		public Object getPrincipal() {
			return name;
		}

		@Override
		public boolean isAuthenticated() {
			return isAuthenticated;
		}

		@Override
		public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
			this.isAuthenticated = isAuthenticated;
		}
	}
}
