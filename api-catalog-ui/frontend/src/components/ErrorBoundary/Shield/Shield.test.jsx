/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
/* eslint-disable no-console */
import * as enzyme from 'enzyme';
import Shield from './Shield';

const Child = () => {
    // eslint-disable-next-line no-throw-literal
    throw 'error';
};

const pauseErrorLogging = (codeToRun) => {
    const logger = console.error;
    console.error = () => {};

    codeToRun();

    console.error = logger;
};
describe('>>> Shield component tests', () => {
    it('Should catches error and renders message', () => {
        const errorMessage = 'Display the error stack';
        pauseErrorLogging(() => {
            const wrapper = enzyme.mount(
                <Shield>
                    <Child />
                </Shield>
            );
            expect(wrapper.text()).toBe(errorMessage);
        });
    });
});
