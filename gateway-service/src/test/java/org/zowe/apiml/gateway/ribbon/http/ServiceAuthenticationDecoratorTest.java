/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.gateway.ribbon.http;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.zuul.context.RequestContext;
import java.security.cert.X509Certificate;
import java.util.stream.Stream;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.AuthenticationException;
import org.zowe.apiml.gateway.security.service.ServiceAuthenticationServiceImpl;
import org.zowe.apiml.gateway.security.service.schema.AuthenticationCommand;
import org.zowe.apiml.gateway.security.service.schema.ServiceAuthenticationService;
import org.zowe.apiml.auth.Authentication;
import org.zowe.apiml.gateway.security.service.schema.source.AuthSchemeException;
import org.zowe.apiml.gateway.security.service.schema.source.AuthSource;
import org.zowe.apiml.gateway.security.service.schema.source.AuthSourceService;
import org.zowe.apiml.gateway.security.service.schema.source.JwtAuthSource;

import java.util.Optional;
import org.zowe.apiml.gateway.security.service.schema.source.X509AuthSource;
import org.zowe.apiml.message.core.MessageService;
import org.zowe.apiml.message.yaml.YamlMessageService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.zowe.apiml.gateway.ribbon.ApimlLoadBalancer.LOADBALANCED_INSTANCE_INFO_KEY;

class ServiceAuthenticationDecoratorTest {

    private static final String AUTHENTICATION_COMMAND_KEY = "zoweAuthenticationCommand";
    private static final MessageService messageService = new YamlMessageService("/gateway-messages.yml");

    ServiceAuthenticationService serviceAuthenticationService = mock(ServiceAuthenticationService.class);
    AuthSourceService authSourceService = mock(AuthSourceService.class);
    InstanceInfo info = InstanceInfo.Builder.newBuilder().setInstanceId("instanceid").setAppName("appname").build();

    ServiceAuthenticationDecorator decorator;
    HttpRequest request;

    @BeforeEach
    void setUp() {
       decorator = new ServiceAuthenticationDecorator(serviceAuthenticationService, authSourceService, messageService);
       request = new HttpGet("/");
       RequestContext.getCurrentContext().clear();
    }

    @Test
    void givenContextWithoutCommand_whenProcess_thenNoAction() {
        HttpRequest request = new HttpGet("/");

        decorator.process(request);

        verify(serviceAuthenticationService, never()).getAuthenticationCommand(any(Authentication.class), any());
        verify(serviceAuthenticationService, never()).getAuthenticationCommand(any(String.class), any(Authentication.class), any());
    }

    @ParameterizedTest
    @MethodSource("provideAuthSources")
    void givenContextWithAnyOtherCommand_whenProcess_thenNoAction(AuthSource authSource) {
        HttpRequest request = new HttpGet("/");
        AuthenticationCommand cmd = mock(ServiceAuthenticationServiceImpl.LoadBalancerAuthenticationCommand.class);
        prepareContext(cmd, authSource);

        decorator.process(request);

        verify(serviceAuthenticationService, never()).getAuthenticationCommand(any(Authentication.class), any());
        verify(serviceAuthenticationService, never()).getAuthenticationCommand(any(String.class), any(Authentication.class), any());
    }

    @ParameterizedTest
    @MethodSource("provideAuthSources")
    void givenContextWithCorrectKey_whenProcess_thenShouldRetrieveCommand(AuthSource authSource) {
        AuthenticationCommand universalCmd = mock(ServiceAuthenticationServiceImpl.UniversalAuthenticationCommand.class);
        prepareContext(universalCmd, authSource);
        doReturn(true).when(authSourceService).isValid(any());

        decorator.process(request);

        verify(serviceAuthenticationService, atLeastOnce()).getAuthentication(info);
        verify(universalCmd, times(1)).applyToRequest(request);
    }

    @Test
    void givenContextWithoutInstanceInfo_whenProcess_thenShouldThrowRequestStoppingException() {
        AuthenticationCommand universalCmd = mock(ServiceAuthenticationServiceImpl.UniversalAuthenticationCommand.class);
        RequestContext.getCurrentContext().set(AUTHENTICATION_COMMAND_KEY, universalCmd);

        assertThrows(RequestContextNotPreparedException.class, () -> decorator.process(request),
            "Should fail on RequestContext without InstanceInfo set by LoadBalancer impl.");
    }

    @ParameterizedTest
    @MethodSource("provideAuthSources")
    void givenContextWithCorrectKeyAndJWT_whenJwtNotAuthenticated_thenShouldAbort(AuthSource authSource) {
        AuthenticationCommand universalCmd = mock(ServiceAuthenticationServiceImpl.UniversalAuthenticationCommand.class);
        prepareContext(universalCmd, authSource);
        when(authSourceService.isValid(any())).thenReturn(false);

        assertThrows(RequestAbortException.class, () ->
            decorator.process(request),
            "Exception is not RequestAbortException");
        verify(universalCmd, times(0)).applyToRequest(request);
    }

    @ParameterizedTest
    @MethodSource("provideAuthSources")
    void givenContextWithCorrectKeyAndJWT_whenAuthenticationThrows_thenShouldAbort(AuthSource authSource) {
        AuthenticationCommand universalCmd = mock(ServiceAuthenticationServiceImpl.UniversalAuthenticationCommand.class);
        prepareContext(universalCmd, authSource);
        AuthenticationException ae = mock(AuthenticationException.class);
        doThrow(ae).when(authSourceService).isValid(any());

        assertThrows(RequestAbortException.class, () ->
                decorator.process(request),
            "Exception is not RequestAbortException");
        verify(universalCmd, times(0)).applyToRequest(request);
    }

    @ParameterizedTest
    @MethodSource("provideAuthSources")
    void givenContextWithoutAuthentication_whenAuthSchemeThrows_thenShouldAbort(AuthSource authSource) {
        AuthenticationCommand universalCmd = mock(ServiceAuthenticationServiceImpl.UniversalAuthenticationCommand.class);
        prepareContext(universalCmd, authSource);
        doThrow(new AuthSchemeException("org.zowe.apiml.gateway.security.schema.missingAuthentication")).when(serviceAuthenticationService).getAuthenticationCommand(any(Authentication.class), eq(authSource));

        assertThrows(RequestAbortException.class, () ->
                decorator.process(request),
            "Exception is not RequestAbortException");
        verify(universalCmd, times(0)).applyToRequest(request);
    }

    @ParameterizedTest
    @MethodSource("provideAuthSources")
    void givenWrongContext_whenProcess_thenReturnWhenCmdIsNull(AuthSource authSource) throws RequestAbortException {
        AuthenticationCommand universalCmd = mock(ServiceAuthenticationServiceImpl.UniversalAuthenticationCommand.class);
        prepareWrongContextForCmdNull(universalCmd, authSource);

        decorator.process(request);

        verify(serviceAuthenticationService, atLeastOnce()).getAuthentication(info);
        verify(universalCmd, times(0)).applyToRequest(request);
    }

    private void prepareContext(AuthenticationCommand command, AuthSource authSource) {
        RequestContext.getCurrentContext().set(AUTHENTICATION_COMMAND_KEY, command);
        RequestContext.getCurrentContext().set(LOADBALANCED_INSTANCE_INFO_KEY, info);
        doReturn(Optional.of(authSource)).when(serviceAuthenticationService).getAuthSourceByAuthentication(any());
        Authentication authentication = mock(Authentication.class);

        when(serviceAuthenticationService.getAuthentication(info)).thenReturn(authentication);
        when(serviceAuthenticationService.getAuthenticationCommand(authentication, authSource)).thenReturn(command);
        doReturn(true).when(command).isRequiredValidSource();
    }

    private void prepareWrongContextForCmdNull(AuthenticationCommand command, AuthSource authSource) {
        RequestContext.getCurrentContext().set(AUTHENTICATION_COMMAND_KEY, command);
        RequestContext.getCurrentContext().set(LOADBALANCED_INSTANCE_INFO_KEY, info);
        doReturn(Optional.of(authSource)).when(authSourceService).getAuthSourceFromRequest();
        Authentication authentication = mock(Authentication.class);

        when(serviceAuthenticationService.getAuthentication(info)).thenReturn(authentication);
        when(serviceAuthenticationService.getAuthenticationCommand(authentication, authSource)).thenReturn(null);
        doReturn(true).when(command).isRequiredValidSource();
    }

    private static Stream<AuthSource> provideAuthSources() {
        return Stream.of(
            new JwtAuthSource("token"),
            new X509AuthSource(mock(X509Certificate.class))
        );
    }
}
