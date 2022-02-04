/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.gateway.context;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import lombok.extern.slf4j.Slf4j;

/**
 * Loader of Gateway extensions
 *
 */
@Slf4j
public class ExtensionsLoader implements ApplicationListener<ApplicationContextInitializedEvent> {

    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent event) {
        if (!(event.getApplicationContext() instanceof BeanDefinitionRegistry)) {
            SpringApplication.exit(event.getApplicationContext(), () -> 1);
        } else {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) event.getApplicationContext();
            ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);

            scanner.scan("com.somecompany"); // get this from somewhere else

            String[] names = scanner.getRegistry().getBeanDefinitionNames();
            for (String name : names) {
                if (!registry.containsBeanDefinition(name)) {
                    registry.registerBeanDefinition(name, scanner.getRegistry().getBeanDefinition(name));
                } else {
                    log.error("Bean with name " + name + " is already registered in the context");
                }
            }
        }
    }
}
