/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.gateway.security.service.schema;

import com.netflix.zuul.context.RequestContext;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.zowe.apiml.auth.Authentication;
import org.zowe.apiml.auth.AuthenticationScheme;
import org.zowe.apiml.gateway.security.service.schema.source.AuthSchemeException;
import org.zowe.apiml.gateway.security.service.schema.source.AuthSource;
import org.zowe.apiml.gateway.security.service.schema.source.AuthSource.Origin;
import org.zowe.apiml.gateway.security.service.schema.source.AuthSourceService;
import org.zowe.apiml.gateway.security.service.schema.source.JwtAuthSource;
import org.zowe.apiml.gateway.security.service.schema.source.X509AuthSource;
import org.zowe.apiml.gateway.security.service.schema.source.X509AuthSource.Parsed;
import org.zowe.apiml.gateway.utils.CleanCurrentRequestContextTest;
import org.zowe.apiml.gateway.utils.X509Utils;
import org.zowe.apiml.security.common.config.AuthConfigurationProperties;

import javax.servlet.http.HttpServletRequest;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;
import org.zowe.apiml.security.common.token.TokenExpireException;
import org.zowe.apiml.security.common.token.TokenNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.zowe.apiml.gateway.security.service.schema.ZosmfScheme.ZosmfCommand.COOKIE_HEADER;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ZosmfSchemeTest extends CleanCurrentRequestContextTest {

    @Mock
    private AuthSourceService authSourceService;

    @Mock
    private AuthConfigurationProperties authConfigurationProperties;

    private ZosmfScheme zosmfScheme;

    private Authentication authentication;
    private AuthSource.Parsed parsedSourceZowe;
    private AuthSource.Parsed parsedSourceZosmf;
    private RequestContext requestContext;
    private HttpServletRequest request;

    @BeforeEach
    void prepareContextForTests() {
        Calendar calendar = Calendar.getInstance();
        authentication = new Authentication(AuthenticationScheme.ZOSMF, null);
        parsedSourceZowe = new JwtAuthSource.Parsed("username", calendar.getTime(), calendar.getTime(), Origin.ZOWE);
        parsedSourceZosmf = new JwtAuthSource.Parsed("username", calendar.getTime(), calendar.getTime(), Origin.ZOSMF);
        requestContext = spy(new RequestContext());
        RequestContext.testSetCurrentContext(requestContext);

        request = new MockHttpServletRequest();
        requestContext.setRequest(request);
        when(authConfigurationProperties.getTokenProperties()).thenReturn(new AuthConfigurationProperties.TokenProperties());
        zosmfScheme = new ZosmfScheme(authSourceService, authConfigurationProperties);
        ReflectionTestUtils.setField(zosmfScheme, "authProvider", "zosmf");
    }

    static Stream<Arguments> authSources() {
        return Stream.of(Arguments.of(new JwtAuthSource("jwtToken2")), Arguments.of(new X509AuthSource(mock(
            X509Certificate.class))));
    }

    @Nested
    class AuthSourceIndependentTests {
        @Test
        void returnCorrectScheme() {
            assertEquals(AuthenticationScheme.ZOSMF, zosmfScheme.getScheme());
        }

        @Test
        void testGetAuthSource() {
            doReturn(Optional.empty()).when(authSourceService).getAuthSourceFromRequest();

            zosmfScheme.getAuthSource();
            verify(authSourceService, times(1)).getAuthSourceFromRequest();
        }

        @Test
        void givenAuthSource_whenZosmfIsNotSetAsAuthProvider_thenThrowException() {
            ZosmfScheme zosmfScheme = new ZosmfScheme(authSourceService, null);
            JwtAuthSource authSource = new JwtAuthSource("jwt");
            assertThrows(AuthSchemeException.class, () -> zosmfScheme.createCommand(null, authSource));
        }

        @Test
        void givenAuthSourceWithoutContent_thenThrows() {
            AuthSource authSource = new X509AuthSource(null);
            assertThrows(AuthSchemeException.class, () -> zosmfScheme.createCommand(authentication, authSource));
        }

        @Test
        void givenNullParsingResult_thenThrows() {
            AuthSource authSource = new JwtAuthSource("jwt");
            doReturn(null).when(authSourceService).parse(any(AuthSource.class));
            assertThrows(IllegalStateException.class, () -> zosmfScheme.createCommand(authentication, authSource));
        }
    }

    @Nested
    class HttpRequestTest {
        @Nested
        class ValidAuthSourceTest {

            @ParameterizedTest(name = "whenApplyToRequest_thenTestJwtToken")
            @MethodSource("org.zowe.apiml.gateway.security.service.schema.ZosmfSchemeTest#authSources")
            void givenValidAuthSource_thenSendValidToken(AuthSource authSource) {
                AuthConfigurationProperties.CookieProperties cookieProperties = mock(AuthConfigurationProperties.CookieProperties.class);
                when(cookieProperties.getCookieName()).thenReturn("apimlAuthenticationToken");
                when(authConfigurationProperties.getCookieProperties()).thenReturn(cookieProperties);
                RequestContext requestContext = spy(new RequestContext());
                RequestContext.testSetCurrentContext(requestContext);

                HttpRequest httpRequest = new HttpGet("/test/request");
                httpRequest.setHeader(COOKIE_HEADER, "cookie1=1");
                Authentication authentication = new Authentication(AuthenticationScheme.ZOSMF, null);
                HttpServletRequest request = new MockHttpServletRequest();
                requestContext.setRequest(request);

                when(authSourceService.parse(any(JwtAuthSource.class))).thenReturn(parsedSourceZosmf);
                when(authConfigurationProperties.getCookieProperties().getCookieName()).thenReturn("apimlAuthenticationToken");
                when(authSourceService.getJWT(any(JwtAuthSource.class))).thenReturn("jwtToken2");
                zosmfScheme.createCommand(authentication, authSource).applyToRequest(httpRequest);

                assertEquals("cookie1=1;jwtToken=jwtToken2", httpRequest.getFirstHeader("cookie").getValue());
            }

            private void prepareAuthenticationService(String ltpaToken) {
                when(authSourceService.getLtpaToken(new JwtAuthSource("jwtToken2"))).thenReturn(ltpaToken);
                when(authSourceService.parse(new JwtAuthSource("jwtToken2"))).thenReturn(parsedSourceZowe);
            }

            @Test
            void givenRequestWithSetCookie_whenApplyToRequest_thenAppendSetCookie() {
                HttpRequest httpRequest = new HttpGet("/test/request");
                httpRequest.setHeader(COOKIE_HEADER, "cookie1=1");
                prepareAuthenticationService("ltpa2");

                zosmfScheme.createCommand(authentication, new JwtAuthSource("jwtToken2")).applyToRequest(httpRequest);

                assertEquals("cookie1=1;LtpaToken2=ltpa2", httpRequest.getFirstHeader("cookie").getValue());
            }

            @Test
            void givenRequest_whenApplyToRequest_thenSetAuthHeaderToNull() {
                HttpRequest httpRequest = new HttpGet("/test/request");
                prepareAuthenticationService("ltpa1");

                zosmfScheme.createCommand(authentication, new JwtAuthSource("jwtToken2")).applyToRequest(httpRequest);

                assertNull(httpRequest.getFirstHeader(HttpHeaders.AUTHORIZATION));
                assertEquals("LtpaToken2=ltpa1", httpRequest.getFirstHeader("cookie").getValue());
            }


        }

    }

    @Nested
    class ZuulRequestTest {
        @Nested
        class GivenZoweJwtAuthSourceTest {
            private final AuthSource jwtAuthSource = new JwtAuthSource("jwtToken1");

            @BeforeEach
            void setup() {
                when(authSourceService.getLtpaToken(new JwtAuthSource("jwtToken1"))).thenReturn("ltpa1");
                when(authSourceService.parse(jwtAuthSource)).thenReturn(parsedSourceZowe);
            }

            @Test
            void givenZoweJwtAuthSource_thenAddOnlyLtpaCookie() {
                zosmfScheme.createCommand(authentication, new JwtAuthSource("jwtToken1")).apply(null);
                assertEquals("LtpaToken2=ltpa1", requestContext.getZuulRequestHeaders().get(COOKIE_HEADER));
            }

            @Test
            void givenZoweJwtAuthSource_andExistingCookie_thenAppendCookieWithLtpa() {
                ((MockHttpServletRequest)request).addHeader(COOKIE_HEADER, "cookie1=1");
                zosmfScheme.createCommand(authentication, jwtAuthSource).apply(null);
                assertEquals("cookie1=1;LtpaToken2=ltpa1", requestContext.getZuulRequestHeaders().get(COOKIE_HEADER));
            }

            @Test
            void givenInvalidZoweJwtAuthSource_thenSetErrorHeader() {
                when(authSourceService.parse(jwtAuthSource)).thenThrow(new TokenNotValidException("Token is not valid"));

                assertThrows(AuthSchemeException.class, () -> zosmfScheme.createCommand(authentication, jwtAuthSource));
            }

            @Test
            void givenExpiredZoweJwtAuthSource_thenThrowJwtTokenException() {
                when(authSourceService.parse(jwtAuthSource)).thenThrow(new TokenExpireException("Token is expired"));

                assertThrows(AuthSchemeException.class, () -> zosmfScheme.createCommand(authentication, jwtAuthSource));
            }
        }

        @Nested
        class GivenZosmfAuthSourceTest {
            private final AuthSource jwtTokenZosmf = new JwtAuthSource("jwtTokenZosmf");
            @Test
            void thenOnlyJwtTokenIsForwardedInCookie() {
                AuthConfigurationProperties.CookieProperties cookieProperties = mock(AuthConfigurationProperties.CookieProperties.class);
                when(cookieProperties.getCookieName()).thenReturn("apimlAuthenticationToken");
                when(authConfigurationProperties.getCookieProperties()).thenReturn(cookieProperties);
                when(authSourceService.parse(jwtTokenZosmf)).thenReturn(parsedSourceZosmf);

                AuthenticationCommand command = zosmfScheme.createCommand(new Authentication(AuthenticationScheme.ZOSMF, null), jwtTokenZosmf);

                command.apply(null);

                verify(authSourceService, times(1)).parse(jwtTokenZosmf);
                verify(authSourceService, never()).getLtpaToken(jwtTokenZosmf);
            }
        }

        @Nested
        class GivenX509AuthSourceTest {
            X509Certificate certificate;
            X509AuthSource authSource;

            @BeforeEach
            void setup() {
                certificate = X509Utils.getCertificate("zowe");
                authSource = new X509AuthSource(certificate);
            }

            @Test
            void givenClientCertificate_thenAddZuulHeader() {
                when(authSourceService.getJWT(any())).thenReturn("jwt");
                when(authSourceService.parse(any(JwtAuthSource.class))).thenReturn(parsedSourceZosmf);
                AuthConfigurationProperties.CookieProperties cookieProperties = mock(AuthConfigurationProperties.CookieProperties.class);
                when(cookieProperties.getCookieName()).thenReturn("apimlAuthenticationToken");
                when(authConfigurationProperties.getCookieProperties()).thenReturn(cookieProperties);
                zosmfScheme.createCommand(authentication, authSource).apply(null);

                verify(requestContext, times(1)).addZuulRequestHeader(anyString(), anyString());
            }

            @Test
            void whenUserNotMappedToCertificate_thenThrows() {
                when(authSourceService.parse(authSource)).thenReturn(new X509AuthSource.Parsed("user", null, null, null, null, null));
                when(authSourceService.getJWT(authSource)).thenThrow(new AuthSchemeException("org.zowe.apiml.gateway.security.schema.x509.mappingFailed"));
                assertThrows(AuthSchemeException.class, () -> zosmfScheme.createCommand(null, authSource));
            }
        }

    }

    @Nested
    class ExpirationTest {
        @Test
        void givenAuthSourceWithoutExpiration_thenUseDefaultExpiration() {
            long defaultExpiration = System.currentTimeMillis() + authConfigurationProperties.getTokenProperties().getExpirationInSeconds() * 1000L;
            when(authSourceService.parse(new JwtAuthSource("jwtToken"))).thenReturn(new JwtAuthSource.Parsed("user", null, null, Origin.ZOWE));

            AuthenticationCommand command = zosmfScheme.createCommand(null, new JwtAuthSource("jwtToken"));

            Long expiration = (Long) ReflectionTestUtils.getField(command, "expireAt");
            assertNotNull(expiration);
            assertTrue(expiration >= defaultExpiration);
            assertFalse(command.isExpired());
        }

        @Test
        void givenAuthWithExpirationSetToNow_thenCommandIsExpired() {
            when(authSourceService.parse(new JwtAuthSource("jwtToken"))).thenReturn(new JwtAuthSource.Parsed("user", new Date(123), new Date(123), Origin.ZOWE));

            AuthenticationCommand command = zosmfScheme.createCommand(null, new JwtAuthSource("jwtToken"));

            assertNotNull(ReflectionTestUtils.getField(command, "expireAt"));
            assertEquals(123L, ReflectionTestUtils.getField(command, "expireAt"));
            assertTrue(command.isExpired());
        }

        private AuthSource.Parsed prepareParsedAuthSourceForTime(int amountOfSeconds) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.SECOND, amountOfSeconds);
            return new JwtAuthSource.Parsed("user", new Date(), c.getTime(), Origin.ZOWE);
        }

        @Test
        void givenTokenExpiredOneSecAgo_thenCommandIsExpired() {
            when(authSourceService.parse(new JwtAuthSource("jwtToken"))).thenReturn(prepareParsedAuthSourceForTime(-1));

            AuthenticationCommand command = zosmfScheme.createCommand(null, new JwtAuthSource("jwtToken"));

            assertTrue(command.isExpired());
        }

        @Test
        void givenTokenThatWillExpireSoon_thenCommandIsNotExpired() {
            when(authSourceService.parse(new JwtAuthSource("jwtToken"))).thenReturn(prepareParsedAuthSourceForTime(2));

            AuthenticationCommand command = zosmfScheme.createCommand(null, new JwtAuthSource("jwtToken"));

            assertFalse(command.isExpired());
        }

        @Test
        void whenCannotGetExpiration_thenUseDefaultExpiration() {
            AuthSource.Parsed parsedSource = new Parsed("commonName", new Date(), null, Origin.X509, "", "distName");
            doReturn(parsedSource).when(authSourceService).parse(any(AuthSource.class));
            ZosmfScheme.ZosmfCommand command = (ZosmfScheme.ZosmfCommand) zosmfScheme.createCommand(authentication, new JwtAuthSource("jwtToken"));

            assertNotNull(command);
            Long expiration = (Long) ReflectionTestUtils.getField(command, "expireAt");
            assertNotNull(expiration);
        }
    }

}
