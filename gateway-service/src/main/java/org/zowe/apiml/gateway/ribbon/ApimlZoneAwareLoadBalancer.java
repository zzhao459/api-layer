/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.gateway.ribbon;

import org.zowe.apiml.gateway.cache.ServiceCacheEvictor;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;

/**
 * Custom implementation of load balancer. This implementation register on creating into ServiceCacheEvictor. It allows
 * faster reaction. When a service is registred or unregistred, discovery service call CacheServiceController. It
 * cooperates with bean CacheEvictor. Purspose of CacheEvictor is to remember changed services until new instance
 * registry is loaded. After that is is posible to update also load balancer, especially list of servers. Otherwise
 * gateway try to send requests, but load balncer can send them to missing service or dont know about new one.
 *
 * @param <T> ussually Server class
 */
public class ApimlZoneAwareLoadBalancer<T extends Server> extends ZoneAwareLoadBalancer<T> {

    public ApimlZoneAwareLoadBalancer(
        IClientConfig clientConfig,
        IRule rule,
        IPing ping,
        ServerList<T> serverList,
        ServerListFilter<T> filter,
        ServerListUpdater serverListUpdater,
        ServiceCacheEvictor serviceCacheEvictor
    ) {
        super(clientConfig, rule, ping, serverList, filter, serverListUpdater);
        serviceCacheEvictor.setApimlZoneAwareLoadBalancer(this);
    }

    /**
     * Update list of servers, to handle services in right way
     */
    public void serverChanged() {
        updateListOfServers();
    }

}