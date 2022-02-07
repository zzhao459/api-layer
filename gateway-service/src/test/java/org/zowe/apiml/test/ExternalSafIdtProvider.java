/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.test;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.zowe.apiml.gateway.security.service.saf.SafIdtProvider;

/**
 *
 *
 */
@Component
@ConditionalOnProperty(name = "apiml.security.saf.provider", havingValue = "ccsIdt")
public class ExternalSafIdtProvider implements SafIdtProvider {

    @Override
    public Optional<String> generate(String username) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean verify(String safToken) {
        // TODO Auto-generated method stub
        return false;
    }

}
