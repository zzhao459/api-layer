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

import com.netflix.appinfo.InstanceInfo;
import java.util.Optional;
import org.zowe.apiml.gateway.security.service.ServiceCacheEvict;
import org.zowe.apiml.auth.Authentication;
import org.zowe.apiml.gateway.security.service.schema.source.AuthSource;

/**
 * Interface with base method to get AuthenticationCommand by serviceId or Authentication.
 *
 * {@see org.zowe.apiml.gateway.security.service.ServiceAuthenticationServiceImpl}
 */
public interface ServiceAuthenticationService extends ServiceCacheEvict {

    /**
     * Get or create command to service's authentication using known Authentication object and jwtToken of current user
     * @param authentication Object describing authentication to the service
     * @param authSource authentication source of user (authentication can depends on user privilege)
     * @return authentication command to update request in ZUUL
     */
    AuthenticationCommand getAuthenticationCommand(Authentication authentication, AuthSource authSource);

    /**
     * Get or create command to service's authentication using serviceId and jwtToken of current user
     * @param serviceId ID of service to call
     * @param authentication Object describing authentication to the service
     * @param authSource authentication source of user (authentication can depends on user privilege)
     * @return authentication command to update request in ZUUL (or lazy command to be updated in load balancer)
     */
    AuthenticationCommand getAuthenticationCommand(String serviceId, Authentication authentication, AuthSource authSource);

    /**
     * Provides information about authentication scheme selected by a service registered in Eureka
     * @param instanceInfo InstanceInfo object of service instance, containing the security metadata
     * @return Authentication object representing instance's authentication schema
     */
    Authentication getAuthentication(InstanceInfo instanceInfo);

    /**
     * Get authentication for given Service ID
     * @param serviceId InstanceInfo object of service instance, containing the security metadata
     * @return Authentication object representing instance's authentication schema
     */
    Authentication getAuthentication(String serviceId);

    Optional<AuthSource> getAuthSourceByAuthentication(Authentication authentication);
}
