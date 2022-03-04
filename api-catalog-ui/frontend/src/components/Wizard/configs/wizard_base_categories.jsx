import { wizRegex } from './wizard_regex_restrictions';

// eslint-disable-next-line import/prefer-default-export
export const baseCategories = [
    {
        text: 'Basic info',
        content: {
            serviceId: {
                value: '',
                question: 'A unique identifier for the API (service ID):',
                maxLength: 40,
                lowercase: true,
                tooltip: 'e.g. sampleservice',
            },
            title: {
                value: '',
                question: 'The name of the service (human readable):',
                tooltip: 'e.g. Hello API ML',
            },
        },
    },
    {
        text: 'Description',
        content: {
            description: {
                value: '',
                question: 'A concise description of the service:',
                tooltip: 'e.g. Sample API ML REST Service.',
            },
        },
    },
    {
        text: 'Base URL',
        content: {
            baseUrl: {
                value: '',
                question: 'The base URL of the service (the consistent part of the web address):',
                validUrl: true,
                tooltip: 'https://${samplehost}: ${sampleport}/${sampleservice}',
            },
        },
    },
    {
        text: 'Prefer IP address',
        content: {
            preferIpAddress: {
                value: false,
                question: 'Advertise service IP address instead of its hostname',
            },
        },
    },
    {
        text: 'Scheme info',
        content: {
            scheme: {
                value: 'https',
                question: 'Service scheme:',
                tooltip: 'https',
            },
            hostname: {
                value: '',
                question: 'Service hostname:',
                tooltip: 'hostname can be externalized by specifying -Dapiml.service.hostname command line parameter',
            },
            port: {
                value: '',
                question: 'Service port:',
                tooltip: 'port can be externalized by specifying -Dapiml.service.port command line parameter',
            },
            contextPath: {
                value: '',
                question: 'Context path:',
                tooltip:
                    "By default the contextPath is set to be the same as apiml.service.serviceId, but doesn't have to be the same",
            },
        },
    },
    {
        text: 'IP address info',
        content: {
            serviceIpAddress: {
                value: '',
                question: 'The service IP address:',
                optional: true,
                regexRestriction: [wizRegex.ipAddress],
                tooltip: 'e.g. https://localhost:3000/',
            },
        },
    },
    {
        text: 'URL',
        content: {
            homePageRelativeUrl: {
                value: '',
                question: 'The relative path to the home page of the service:',
                optional: true,
                regexRestriction: [wizRegex.validRelativeUrl],
                tooltip: 'e.g. /application/home',
            },
            statusPageRelativeUrl: {
                value: '',
                question: 'The relative path to the status page of the service:',
                optional: true,
                regexRestriction: [wizRegex.validRelativeUrl],
                tooltip: 'e.g. /application/info',
            },
            healthCheckRelativeUrl: {
                value: '',
                question: 'The relative path to the health check endpoint of the service:',
                optional: true,
                regexRestriction: [wizRegex.validRelativeUrl],
                tooltip: 'e.g. /application/health',
            },
        },
    },
    {
        text: 'Discovery Service URL',
        content: {
            discoveryServiceUrls: {
                value: '',
                question: 'Discovery Service URL:',
                validUrl: true,
                tooltip: 'e.g. https://${discoveryServiceHost1}: ${discoveryServicePort1}/eureka',
            },
        },
        multiple: false,
        noKey: true,
    },
    {
        text: 'Routes',
        content: {
            gatewayUrl: {
                value: '',
                question: 'Expose the Service API on Gateway under context path:',
                tooltip: 'Format: /api/vX, Example: /api/v1',
                regexRestriction: [wizRegex.gatewayUrl],
            },
            serviceUrl: {
                value: '',
                question: 'Service API common context path:',
                tooltip: 'e.g. /sampleservice/api/v1',
            },
        },
        help:
            'For service: <service>/allOfMyEndpointsAreHere/** exposed on Gateway under <gateway>/<serviceid>/api/v1/**\nFill in:\ngatewayUrl: /api/v1\nserviceUrl: /allOfMyEndpointsAreHere',
        multiple: true,
    },

    {
        text: 'Authentication',
        content: {
            scheme: {
                value: 'bypass',
                question: 'Authentication:',
                options: ['bypass', 'zoweJwt', 'httpBasicPassTicket', 'zosmf', 'x509'],
            },
            applid: {
                value: '',
                question: 'A service APPLID (valid only for the httpBasicPassTicket authentication scheme ):',
                dependencies: { scheme: 'httpBasicPassTicket' },
                tooltip: 'e.g. ZOWEAPPL',
            },
            headers: {
                value: 'X-Certificate-Public',
                question: 'For the x509 scheme use the headers parameter to select which values to send to a service',
                dependencies: { scheme: 'x509' },
                options: ['X-Certificate-Public', 'X-Certificate-DistinguishedName', 'X-Certificate-CommonName'],
            },
        },
        help:
            'The following service authentication schemes are supported by the API Gateway: bypass, zoweJwt, httpBasicPassTicket, zosmf, x509. ',
        helpUrl: {
            title: 'More information about the authentication parameters',
            link:
                'https://docs.zowe.org/stable/extend/extend-apiml/onboard-plain-java-enabler/#api-catalog-information',
        },
    },
    {
        text: 'API Info',
        content: {
            apiId: {
                value: '',
                question: 'A unique identifier to the API in the API ML:',
                tooltip: 'e.g. zowe.apiml.sampleservice',
            },
            version: {
                value: '',
                question: 'API version:',
                tooltip: 'API version',
                regexRestriction: [wizRegex.version],
            },
            gatewayUrl: {
                value: '',
                question: 'The base path at the API Gateway where the API is available:',
                tooltip: 'Format: api/vX, Example: api/v1',
                regexRestriction: [wizRegex.gatewayUrl],
            },
            swaggerUrl: {
                value: '',
                question: 'The Http or Https address where the Swagger JSON document is available:',
                optional: true,
                tooltip: 'e.g. http://${sampleServiceSwaggerHost}: ${sampleServiceSwaggerPort}/ sampleservice/api-doc',
            },
            documentationUrl: {
                value: '',
                question: 'Link to the external documentation:',
                optional: true,
                tooltip: 'http://',
            },
        },
    },
    {
        text: 'Catalog',
        content: {
            type: {
                value: 'Custom',
                question: 'Choose existing catalog tile or create a new one:',
                options: ['Custom'],
                hidden: true,
            },
            id: {
                value: '',
                question: 'The unique identifier for the product family of API services:',
                tooltip: 'reverse domain name notation, e.g. org.zowe.apiml',
            },
            title: {
                value: '',
                question: 'The title of the product family of the API service:',
                tooltip: 'e.g. Hello API ML',
            },
            description: {
                value: '',
                question: 'A description of the API service product family:',
                tooltip: 'e.g. Sample application to demonstrate exposing a REST API in the ZOWE API ML',
            },
            version: {
                value: '',
                question: 'The semantic version of this API Catalog tile (increase when adding changes):',
                tooltip: 'e.g. 1.0.0',
            },
        },
        interference: 'catalog',
    },
    {
        text: 'SSL',
        content: {
            verifySslCertificatesOfServices: {
                value: false,
                question: 'Verify SSL certificates of services:',
            },
            protocol: {
                value: 'TLSv1.2',
                question: 'The TLS protocol version used by Zowe API ML Discovery Service:',
            },
            keyAlias: {
                value: '',
                question: 'The alias used to address the private key in the keystore',
                tooltip: 'e.g. localhost',
            },
            keyPassword: {
                value: '',
                question: 'The password associated with the private key:',
                type: 'password',
            },
            keyStore: {
                value: '',
                question: 'The keystore file used to store the private key (keyring: set to SAF keyring location):',
                tooltip: 'e.g. keystore/localhost.keystore.p12',
            },
            keyStorePassword: {
                value: '',
                question: 'The password used to unlock the keystore:',
                type: 'password',
            },
            keyStoreType: {
                value: 'PKCS12',
                question: 'Type of the keystore:',
                options: ['PKCS12', 'JKS', 'JCERACFKS'],
            },
            trustStore: {
                value: '',
                question: 'The truststore file used to keep other parties public keys and certificates:',
                tooltip: 'e.g. keystore/localhost.truststore.p12',
            },
            trustStorePassword: {
                value: '',
                question: 'The password used to unlock the truststore:',
                type: 'password',
            },
            trustStoreType: {
                value: 'PKCS12',
                question: 'Truststore type:',
                options: ['PKCS12', 'JKS', 'JCERACFKS'],
            },
        },
    },
];
