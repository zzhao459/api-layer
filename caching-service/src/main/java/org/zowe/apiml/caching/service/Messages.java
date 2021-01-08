/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.caching.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum Messages {
    DUPLICATE_KEY("org.zowe.apiml.cache.keyCollision", HttpStatus.CONFLICT),
    KEY_NOT_PROVIDED("org.zowe.apiml.cache.keyNotProvided", HttpStatus.BAD_REQUEST),
    KEY_NOT_IN_CACHE("org.zowe.apiml.cache.keyNotInCache", HttpStatus.NOT_FOUND),
    INVALID_PAYLOAD("org.zowe.apiml.cache.invalidPayload", HttpStatus.BAD_REQUEST);

    private final String key;
    private final HttpStatus status;
}