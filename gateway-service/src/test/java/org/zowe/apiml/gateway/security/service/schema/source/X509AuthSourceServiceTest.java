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

import com.netflix.zuul.context.RequestContext;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.client.ResourceAccessException;
import org.zowe.apiml.gateway.security.login.x509.X509AbstractMapper;
import org.zowe.apiml.gateway.security.service.AuthenticationService;
import org.zowe.apiml.gateway.security.service.TokenCreationService;
import org.zowe.apiml.gateway.security.service.schema.source.AuthSource.Origin;
import org.zowe.apiml.gateway.security.service.schema.source.AuthSource.Parsed;
import org.zowe.apiml.gateway.utils.CleanCurrentRequestContextTest;
import org.zowe.apiml.security.common.error.AuthenticationTokenException;
import org.zowe.apiml.security.common.error.InvalidCertificateException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class X509AuthSourceServiceTest extends CleanCurrentRequestContextTest {
    private RequestContext context;
    private HttpServletRequest request;
    private X509Certificate x509Certificate;
    private final X509Certificate[] x509Certificates = new X509Certificate[1];

    @BeforeEach
    void init() {
        x509Certificate = mock(X509Certificate.class);
    }

    @Test
    void givenNullSource_returnNullLtpa() {
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        X509AuthSourceService service = new X509AuthSourceService(null, null, authenticationService);
        assertNull(service.getLtpaToken(null));
    }

    @Nested
    class GivenX509SourceTest {
        TokenCreationService tokenCreationService;
        X509AuthSourceService service;
        AuthenticationService authenticationService;
        X509AbstractMapper mapper;

        @BeforeEach
        void setup() {
            authenticationService = mock(AuthenticationService.class);
            tokenCreationService = mock(TokenCreationService.class);
            mapper = mock(X509AbstractMapper.class);
            service = new X509AuthSourceService(mapper, tokenCreationService, authenticationService);
        }

        @Test
        void givenX509Source_returnNullLtpa() {

            X509AuthSource source = new X509AuthSource(null);
            assertThrows(UserNotMappedException.class, () -> service.getLtpaToken(source));
        }

        @Test
        void givenX509Source_thenTranslateException() {
            X509AuthSource source = new X509AuthSource(null);
            when(mapper.mapCertificateToMainframeUserId(any())).thenReturn("user1");
            when(tokenCreationService.createJwtTokenWithoutCredentials(any())).thenThrow(new ResourceAccessException("I/O exception"));
            assertThrows(AuthenticationTokenException.class, () -> service.getJWT(source));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class X509MFAuthSourceServiceTest {
        private X509AbstractMapper mapper;
        private X509AuthSourceService serviceUnderTest;

        @BeforeEach
        void init() {
            mapper = mock(X509AbstractMapper.class);
            serviceUnderTest = new X509AuthSourceService(mapper, null, null);
        }

        @Nested
        class GivenNullAuthSource {
            @Test
            void whenNoClientCertInRequest_thenAuthSourceIsNotPresent() {
                context = spy(RequestContext.class);
                request = mock(HttpServletRequest.class);
                RequestContext.testSetCurrentContext(context);
                when(context.getRequest()).thenReturn(request);
                Optional<AuthSource> authSource = serviceUnderTest.getAuthSourceFromRequest();
                verify(request, times(1)).getAttribute("client.auth.X509Certificate");
                assertFalse(authSource.isPresent());
            }

            @Test
            void whenValidate_thenFalse() {
                AuthSource authSource = null;
                assertFalse(serviceUnderTest.isValid(authSource));
                verifyNoInteractions(mapper);
            }

            @Test
            void whenParse_thenNull() {
                assertNull(serviceUnderTest.parse(null));
                verifyNoInteractions(mapper);
            }
        }

        @Nested
        class GiveNullRawSource {
            @Test
            void whenValidate_thenReturnFalse() {
                assertFalse(serviceUnderTest.isValid(new X509AuthSource(null)));
            }
        }

        @Nested
        class GivenValidAuthSource {
            @BeforeEach
            void setup() {
                context = spy(RequestContext.class);
                request = mock(HttpServletRequest.class);
                RequestContext.testSetCurrentContext(context);
            }

            @Test
            void whenClientCertInRequestInCustomAttribute_thenAuthSourceIsPresent() {
                when(context.getRequest()).thenReturn(request);
                when(request.getAttribute("client.auth.X509Certificate")).thenReturn(Arrays.array(x509Certificate));
                when(mapper.isClientAuthCertificate(any())).thenReturn(true);

                Optional<AuthSource> authSource = serviceUnderTest.getAuthSourceFromRequest();

                verify(request, times(1)).getAttribute("client.auth.X509Certificate");
                verify(request, times(0)).getAttribute("javax.servlet.request.X509Certificate");

                Assertions.assertTrue(authSource.isPresent());
                Assertions.assertTrue(authSource.get() instanceof X509AuthSource);
                Assertions.assertEquals(x509Certificate, authSource.get().getRawSource());
            }

            @Test
            void whenValidate_thenCorrect() {
                when(mapper.isClientAuthCertificate(x509Certificate)).thenReturn(true);
                Assertions.assertTrue(serviceUnderTest.isValid(new X509AuthSource(x509Certificate)));
                verify(mapper, times(1)).isClientAuthCertificate(x509Certificate);
            }

            @Test
            void whenParse_thenCorrect() throws CertificateEncodingException {
                String cert = "clientCertificate";
                String encodedCert = Base64.getEncoder().encodeToString(cert.getBytes());
                String distinguishedName = "distinguishedName";

                Parsed expectedParsedSource = new X509AuthSource.Parsed("user", null, null, Origin.X509, encodedCert, distinguishedName);
                when(mapper.mapCertificateToMainframeUserId(x509Certificate)).thenReturn("user");
                when(x509Certificate.getEncoded()).thenReturn(cert.getBytes());

                Principal principal = mock(Principal.class);
                when(x509Certificate.getSubjectDN()).thenReturn(principal);
                when(principal.toString()).thenReturn(distinguishedName);
                Parsed parsedSource = serviceUnderTest.parse(new X509AuthSource(x509Certificate));

                verify(mapper, times(1)).mapCertificateToMainframeUserId(x509Certificate);
                Assertions.assertNotNull(parsedSource);
                Assertions.assertEquals(expectedParsedSource, parsedSource);
            }
        }

        @Nested
        class GivenIncorrectAuthSource {
            @BeforeEach
            void setup() {
                context = spy(RequestContext.class);
                request = mock(HttpServletRequest.class);
                RequestContext.testSetCurrentContext(context);
            }

            @Test
            void whenServerCertInRequestInCustomAttribute_thenAuthSourceIsNotPresent() {
                when(context.getRequest()).thenReturn(request);
                when(request.getAttribute("client.auth.X509Certificate")).thenReturn(Arrays.array(x509Certificate));
                when(mapper.isClientAuthCertificate(any())).thenReturn(false);

                Optional<AuthSource> authSource = serviceUnderTest.getAuthSourceFromRequest();

                verify(request, times(1)).getAttribute("client.auth.X509Certificate");
                verify(request, times(0)).getAttribute("javax.servlet.request.X509Certificate");

                Assertions.assertFalse(authSource.isPresent());
            }

            @Test
            void whenInternalApimlCertInRequestInStandardAttribute_thenAuthSourceIsNotPresent() {
                when(context.getRequest()).thenReturn(request);
                doReturn(new X509Certificate[0]).when(request).getAttribute("client.auth.X509Certificate");

                Optional<AuthSource> authSource = serviceUnderTest.getAuthSourceFromRequest();

                verify(request, times(1)).getAttribute("client.auth.X509Certificate");
                verify(request, times(0)).getAttribute("javax.servlet.request.X509Certificate");

                Assertions.assertFalse(authSource.isPresent());
            }

            @Test
            void whenIncorrectAuthSourceType_thenIsValidFalse() {
                assertFalse(serviceUnderTest.isValid(new JwtAuthSource("")));
            }

            @Test
            void whenAuthenticationServiceException_thenThrowWhenValidate() {
                AuthSource authSource = new X509AuthSource(x509Certificate);
                when(mapper.isClientAuthCertificate(x509Certificate)).thenThrow(new AuthenticationServiceException("Can't get extensions from certificate"));
                assertFalse(serviceUnderTest.isValid(authSource));
                verify(mapper, times(1)).isClientAuthCertificate(x509Certificate);
            }

            @Test
            void whenUnknownAuthSource_thenParsedIsNull() {
                assertNull(serviceUnderTest.parse(new JwtAuthSource("")));
                verifyNoInteractions(mapper);
            }

            @Test
            void whenAuthenticationServiceException_thenThrowWhenParse() {
                AuthSource authSource = new X509AuthSource(x509Certificate);
                when(mapper.mapCertificateToMainframeUserId(x509Certificate)).thenThrow(new AuthenticationServiceException("Can't get extensions from certificate"));
                assertThrows(AuthenticationServiceException.class, () -> serviceUnderTest.parse(authSource));
                verify(mapper, times(1)).mapCertificateToMainframeUserId(x509Certificate);
            }

            @Test
            void returnNullWhenCertificateEncodingExceptionIsThrown() throws CertificateEncodingException {
                AuthSource authSource = new X509AuthSource(x509Certificate);
                when(x509Certificate.getEncoded()).thenThrow(new CertificateEncodingException(""));
                assertThrows(InvalidCertificateException.class, () -> serviceUnderTest.parse(authSource));
            }

            @Nested
            class WhenNotAClientCertificate {
                @Test
                void thenThrowWhenValidate() {
                    AuthSource authSource = new X509AuthSource(x509Certificate);
                    when(mapper.isClientAuthCertificate(x509Certificate)).thenReturn(false);
                    assertFalse(serviceUnderTest.isValid(authSource));
                    verify(mapper, times(1)).isClientAuthCertificate(x509Certificate);
                }
            }
        }
    }

    private void givenClientCertInRequest_testGetAuthSourceFromRequest(AuthSourceService serviceUnderTest) {
        x509Certificates[0] = x509Certificate;
        context = spy(RequestContext.class);
        request = mock(HttpServletRequest.class);
        RequestContext.testSetCurrentContext(context);
        when(context.getRequest()).thenReturn(request);
        when(request.getAttribute("client.auth.X509Certificate")).thenReturn(x509Certificates);

        Optional<AuthSource> authSource = serviceUnderTest.getAuthSourceFromRequest();

        verify(request, times(1)).getAttribute("client.auth.X509Certificate");

        Assertions.assertTrue(authSource.isPresent());
        Assertions.assertTrue(authSource.get() instanceof X509AuthSource);
        Assertions.assertEquals(x509Certificates[0], authSource.get().getRawSource());
    }

    private void givenNoClientCertInRequest_testGetAuthSourceFromRequest(AuthSourceService serviceUnderTest) {
        context = spy(RequestContext.class);
        request = mock(HttpServletRequest.class);
        RequestContext.testSetCurrentContext(context);
        when(context.getRequest()).thenReturn(request);
        Optional<AuthSource> authSource = serviceUnderTest.getAuthSourceFromRequest();
        verify(request, times(1)).getAttribute("client.auth.X509Certificate");
        Assertions.assertFalse(authSource.isPresent());
    }

    private void givenValidAuthSource_testParse(AuthSourceService serviceUnderTest, X509AbstractMapper mapper)
        throws CertificateEncodingException {
        String cert = "clientCertificate";
        String encodedCert = Base64.getEncoder().encodeToString(cert.getBytes());
        String distinguishedName = "distinguishedName";

        Parsed expectedParsedSource = new X509AuthSource.Parsed("user", null, null, Origin.X509, encodedCert, distinguishedName);
        when(mapper.mapCertificateToMainframeUserId(x509Certificate)).thenReturn("user");
        when(x509Certificate.getEncoded()).thenReturn(cert.getBytes());

        Principal principal = mock(Principal.class);
        when(x509Certificate.getSubjectDN()).thenReturn(principal);
        when(principal.toString()).thenReturn(distinguishedName);
        Parsed parsedSource = serviceUnderTest.parse(new X509AuthSource(x509Certificate));

        verify(mapper, times(1)).mapCertificateToMainframeUserId(x509Certificate);
        Assertions.assertNotNull(parsedSource);
        Assertions.assertEquals(expectedParsedSource, parsedSource);
    }
}
