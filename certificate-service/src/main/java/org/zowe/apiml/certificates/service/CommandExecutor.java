/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.certificates.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CommandExecutor {

    public static String execute(String command) throws IOException {
        ProcessBuilder procBuilder = new ProcessBuilder("echo",command);
        Process process = procBuilder.start();
        InputStream stream = process.getInputStream();
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while((line =reader.readLine())!=null){
            sb.append(line);
        }
        return sb.toString();
    }
}
