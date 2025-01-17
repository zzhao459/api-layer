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
import org.apache.http.HttpRequest;
import org.zowe.apiml.cache.EntryExpiration;

import java.io.Serializable;

/**
 * This command represented a code, which distribute right access to a service. Gateway translates requests
 * to a service and by login in there generate or translate authentication to the service.
 *
 * Responsible for this translation is the filter {@link org.zowe.apiml.gateway.filters.pre.ServiceAuthenticationFilter}
 */
public abstract class AuthenticationCommand implements EntryExpiration, Serializable {

    private static final long serialVersionUID = -4519869709905127608L;

    public static final AuthenticationCommand EMPTY = new AuthenticationCommand() {

        private static final long serialVersionUID = 5280496524123534478L;

        @Override
        public void apply(InstanceInfo instanceInfo) {
            // do nothing
        }

        @Override
        public void applyToRequest(HttpRequest request) {
            // do nothing
        }
    };

    /**
     * Apply the command, if it is necessary, it is possible to use a specific instance for execution. This is
     * using for loadBalancer command, where are not available all information in step of command creation.
     * In all other case call apply(null).
     * @param instanceInfo Specific instanceIf if it is needed
     */
    public abstract void apply(InstanceInfo instanceInfo);

    /**
     * This method identify if for this authentication command, schema is required to be logged. Main purpose is
     * to make differences between bypass and other schema's type. Schema shouldn't change anything, but for some other
     * it is required be logged and send valid authentication source.
     * @return true is valid authentication source is required, otherwise false
     */
    public boolean isRequiredValidSource() {
        return false;
    }

    @Override
    public boolean isExpired() {
        return false;
    }


    /**
     * Used for deferred processing of command during Ribbon Retry.
     * There exists case when {@link org.zowe.apiml.gateway.filters.pre.ServiceAuthenticationFilter} cannot
     * decide which command to apply, when there are service instances with multiple security schemas.
     * In that case, the filter applies {@link org.zowe.apiml.gateway.security.service.ServiceAuthenticationServiceImpl.LoadBalancerAuthenticationCommand}
     * and defers the processing to happen during Ribbon's Retry.
     */
    @Deprecated
    public void applyToRequest(HttpRequest request) {
        throw new UnsupportedOperationException();
    }
}
