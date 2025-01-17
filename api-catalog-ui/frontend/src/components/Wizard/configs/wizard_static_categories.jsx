/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
import { wizRegex } from './wizard_regex_restrictions';

// eslint-disable-next-line import/prefer-default-export
export const staticSpecificCategories = [
    {
        text: 'URL for Static',
        content: {
            instanceBaseUrls: {
                value: '',
                question: 'The base URL of the instance (the consistent part of the web address):',
            },
        },
        multiple: false,
        noKey: true,
    },
    {
        text: 'Routes for Static & Node',
        content: {
            gatewayUrl: {
                value: '',
                question: 'Expose the Service API on Gateway under context path:',
                tooltip: 'Format: /api/vX, Example: /api/v1',
                regexRestriction: [wizRegex.gatewayUrl],
            },
            serviceRelativeUrl: {
                value: '',
                question: 'Service API common context path:',
            },
        },
        help: 'For service: <service>/allOfMyEndpointsAreHere/** exposed on Gateway under <gateway>/<serviceid>/api/v1/**\nFill in:\ngatewayUrl: /api/v1\nserviceUrl: /allOfMyEndpointsAreHere',
        multiple: true,
    },
    {
        text: 'Catalog info',
        content: {
            type: {
                value: 'Custom',
                question: 'Choose existing catalog tile or create a new one:',
                options: ['Custom'],
                hidden: true,
            },
            catalogUiTileId: {
                value: '',
                question: 'The id of the catalog tile:',
                regexRestriction: [wizRegex.noWhiteSpaces],
                dependencies: { type: 'Custom' },
            },
        },
        interference: 'staticCatalog',
    },
    {
        text: 'Catalog UI Tiles',
        content: {
            title: {
                value: '',
                question: 'The title of the API services product family:',
            },
            description: {
                value: '',
                question: 'The detailed description of the API Catalog UI dashboard tile:',
            },
        },
        indentationDependency: 'catalogUiTileId',
    },
];
