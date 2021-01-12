/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.caching.service.vsam;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.zowe.apiml.caching.config.VsamConfig;

@Service
@Slf4j
public class VsamInitializer {

    @Retryable(value = UnsupportedOperationException.class, maxAttempts = 10)
    public void storageWarmup(VsamConfig config) {
        try (VsamFile file = new VsamFile(config, VsamConfig.VsamOptions.WRITE, true)) {
            log.info("Vsam file open successful");
        }
    }
}
