/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.util.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration parameters for DiscoverableClient
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscoverableClientConfiguration {
    private String scheme;
    private String applId;
    private String host;
    private int port;
    private int instances;
}
