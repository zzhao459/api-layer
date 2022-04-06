/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.gateway.security.service.schema.source;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zowe.apiml.gateway.security.service.AuthenticationService;
import org.zowe.apiml.gateway.security.service.schema.source.AuthSource.Origin;
import org.zowe.apiml.gateway.security.service.schema.source.AuthSource.Parsed;
import org.zowe.apiml.security.common.token.QueryResponse;
import org.zowe.apiml.security.common.token.QueryResponse.Source;
import org.zowe.apiml.security.common.token.TokenAuthentication;
import org.zowe.apiml.security.common.token.TokenExpireException;
import org.zowe.apiml.security.common.token.TokenNotValidException;

@ExtendWith(MockitoExtension.class)
class JwtAuthSourceServiceTest {
    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private JwtAuthSourceService serviceUnderTest;

    private final String token = "jwtToken";
    private JwtAuthSource authSource;
    private TokenAuthentication tokenAuthentication;
    private Parsed expectedParsedSource;

    @BeforeEach
    public void setup() {
        authSource = new JwtAuthSource("jwtToken");
        tokenAuthentication = TokenAuthentication.createAuthenticated("user", token);
        expectedParsedSource = new JwtAuthSource.Parsed("user", new Date(111), new Date(222), Origin.ZOSMF);
    }

    @Test
    void givenTokenInRequest_thenAuthSourceIsPresent() {
        when(authenticationService.getJwtTokenFromRequest(any())).thenReturn(Optional.of(token));

        Optional<AuthSource> authSource = serviceUnderTest.getAuthSourceFromRequest();

        verify(authenticationService, times(1)).getJwtTokenFromRequest(any());
        Assertions.assertTrue(authSource.isPresent());
        Assertions.assertTrue(authSource.get() instanceof JwtAuthSource);
        Assertions.assertEquals(token, authSource.get().getRawSource());
    }

    @Test
    void givenNoTokenInRequest_thenAuthSourceIsPresent() {
        when(authenticationService.getJwtTokenFromRequest(any())).thenReturn(Optional.empty());

        Optional<AuthSource> authSource = serviceUnderTest.getAuthSourceFromRequest();

        verify(authenticationService, times(1)).getJwtTokenFromRequest(any());
        Assertions.assertFalse(authSource.isPresent());
    }

    @Test
    void givenInvalidAuthSource_thenAuthSourceIsInvalid() {
        TokenAuthentication tokenAuthentication = new TokenAuthentication("user");
        tokenAuthentication.setAuthenticated(false);
        when(authenticationService.validateJwtToken(anyString())).thenReturn(tokenAuthentication);

        Assertions.assertFalse(serviceUnderTest.isValid(authSource));
        verify(authenticationService, times(1)).validateJwtToken(token);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GivenInvalidAuthSource {
        @Nested
        class WhenNullAuthSource {
            @Test
            void thenAuthSourceIsInvalid() {
                Assertions.assertFalse(serviceUnderTest.isValid(null));
                verifyNoInteractions(authenticationService);
            }

            @Test
            void thenParsedIsNull() {
                Assertions.assertNull(serviceUnderTest.parse(null));
                verifyNoInteractions(authenticationService);
            }

            @Test
            void thenNullLtpa() {
                Assertions.assertNull(serviceUnderTest.getLtpaToken(null));
                verifyNoInteractions(authenticationService);
            }
        }

        @Nested
        class WhenNullTokenInAuthSource {
            private final AuthSource authSourceNullToken = new JwtAuthSource(null);
            @Test
            void givenNullTokenInAuthSource_thenAuthSourceIsInvalid() {
                Assertions.assertFalse(serviceUnderTest.isValid(authSourceNullToken));
                verifyNoInteractions(authenticationService);
            }

            @Test
            void givenNullTokenInAuthSource_thenParsedIsNull() {
                Assertions.assertNull(serviceUnderTest.parse(authSourceNullToken));
                verifyNoInteractions(authenticationService);
            }

            @Test
            void givenNullTokenInAuthSource_thenNullLtpa() {
                Assertions.assertNull(serviceUnderTest.getLtpaToken(authSourceNullToken));
                verifyNoInteractions(authenticationService);
            }
        }
    }

    @Nested
    class GivenUnknownAuthSource {
        private final AuthSource dummyAuthSource = new DummyAuthSource();
        @Test
        void whenParse_thenNull() {
            Assertions.assertNull(serviceUnderTest.parse(dummyAuthSource));
            verifyNoInteractions(authenticationService);
        }

        @Test
        void thenIsInvalid() {
            Assertions.assertFalse(serviceUnderTest.isValid(dummyAuthSource));
            verifyNoInteractions(authenticationService);
        }

        @Test
        void whenGetLtpa_thenNull() {
            Assertions.assertNull(serviceUnderTest.getLtpaToken(dummyAuthSource));
            verifyNoInteractions(authenticationService);
        }
    }

    @Nested
    class GivenValidAuthSource {
        @Test
        void thenIsValid() {
            when(authenticationService.validateJwtToken(anyString())).thenReturn(tokenAuthentication);

            Assertions.assertTrue(serviceUnderTest.isValid(authSource));
            verify(authenticationService, times(1)).validateJwtToken(token);
        }

        @Test
        void thenParseCorrectly() {
            when(authenticationService.parseJwtToken(anyString())).thenReturn(new QueryResponse("domain", "user", new Date(111), new Date(222), Source.ZOSMF));

            Parsed parsedSource = serviceUnderTest.parse(authSource);

            verify(authenticationService, times(1)).parseJwtToken(token);
            Assertions.assertNotNull(parsedSource);
            Assertions.assertEquals(expectedParsedSource, parsedSource);
        }

        @Test
        void theLtpaGenerated() {
            String ltpa = "ltpaToken";
            when(authenticationService.getLtpaTokenWithValidation(anyString())).thenReturn(ltpa);

            Assertions.assertEquals(ltpa, serviceUnderTest.getLtpaToken(authSource));
            verify(authenticationService, times(1)).getLtpaTokenWithValidation(token);
        }
    }

    @Nested
    class GivenTokenNotValidException {
        private final TokenNotValidException exception = new TokenNotValidException("token not valid");

        @Test
        void whenIsValid_thenThrow() {
            when(authenticationService.validateJwtToken(anyString())).thenThrow(exception);

            assertThrows(TokenNotValidException.class, () -> serviceUnderTest.isValid(authSource));
            verify(authenticationService, times(1)).validateJwtToken(token);
        }

        @Test
        void whenParse_thenThrow() {
            when(authenticationService.parseJwtToken(anyString())).thenThrow(exception);

            assertThrows(TokenNotValidException.class, () -> serviceUnderTest.parse(authSource));
            verify(authenticationService, times(1)).parseJwtToken(token);
        }

        @Test
        void whenGetLtpa_thenThrow() {
            when(authenticationService.getLtpaTokenWithValidation(anyString())).thenThrow(exception);

            assertThrows(TokenNotValidException.class, () -> serviceUnderTest.getLtpaToken(authSource));
            verify(authenticationService, times(1)).getLtpaTokenWithValidation(token);
        }
    }
        @Nested
    class GivenTokenExpireException {
        private final TokenExpireException exception = new TokenExpireException("token expired");

        @Test
        void whenIsValid_thenThrow() {
            when(authenticationService.validateJwtToken(anyString())).thenThrow(exception);

            assertThrows(TokenExpireException.class, () -> serviceUnderTest.isValid(authSource));
            verify(authenticationService, times(1)).validateJwtToken("jwtToken");
        }

        @Test
        void whenParse_thenThrow() {
            when(authenticationService.parseJwtToken(anyString())).thenThrow(exception);

            assertThrows(TokenExpireException.class, () -> serviceUnderTest.parse(authSource));
            verify(authenticationService, times(1)).parseJwtToken(token);
        }

        @Test
        void whenGetLtpa_thenThrow() {
            when(authenticationService.getLtpaTokenWithValidation(anyString())).thenThrow(exception);

            assertThrows(TokenExpireException.class, () -> serviceUnderTest.getLtpaToken(authSource));
            verify(authenticationService, times(1)).getLtpaTokenWithValidation(token);
        }
    }

    static class DummyAuthSource implements AuthSource {
        @Override
        public Object getRawSource() {
            return null;
        }

        @Override
        public AuthSourceType getType() {
            return null;
        }
    }
}