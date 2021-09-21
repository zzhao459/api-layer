# Spring cloud gateway migration

### Challenges:

Asynchronous calls to REST, mutate api

Asynchronous calls to blocking api

Reactive security

Reactive method security - security context, etc

Reactive tests - webtestclient

Blocking code in reactive pipeline

## The following approach is not technically feasible
    because of incompatibility of MVC and reactive web (webflux)
    
    ### Phase 1) Land reactive within a gateway
    
    SSE - already in place
    
    Migrate isolated feature to reactive
    
    Gain understanding about calling service over REST in a non-blocking fashion
    
    Gain understanding about reactive security on migrated endpoint
    
    
    ### Phase 2) Reactive except Zuul
    
    Migrate everything to WebFlux using learnings from previous phase
    
    Keep Zuul but make adapters where necessary
    
    ### Phase 3) Spring cloud gateway swap
    
    This might be still too big
    
    
    Actually it is not needed to replace the whole service layer. It is not using Zuul. The problem here is with Zuul and Ribbon. So the real target is the gateway itself.
    
    The service layer is predominantly blocking so it would have to be handled on a scheduler
    
    
    Potential to gradually migrate gateway:
    
    About finding the right split point: the chain is
    
    Servlet > Servlet filters > spring security > zuul > ribbon
    
    Is it realistic to find the split somewhere?


### Challenges to solve:

Security has to be reactivesecurity, it seems to work on SCG

How to separate concerns effectively to prevent leaking SC GW api into GW business logic?

Reactor Netty does not seem to support multliple connectors

<https://github.com/spring-projects/spring-boot/issues/12035>

<https://github.com/reactor/reactor-netty/issues/67>

<https://github.com/JamesChenX/reactor-netty/commit/697bc166b2d6054994c36b66e5b1708efbc12764>

<https://www.baeldung.com/spring-boot-reactor-netty>

Why do we need multiple connectors? 
- Gateway Notifier: a tool for faster gateway route discovery
If we sacrifice this and rely on Eureka's eventual consistency, we might not need it.
- Internal vs external accessibility: Nginx might be able to play this role. Or a differently configured gateway might play this role.

# Proxy approach

What are the benefits of this contrary to Big Bang migration?

Introduce proxy service and gateway service and migrate between the two.

No breaking changes anticipated ? Maybe not true but maybe the breaking changes can be implemented first in the old gw and released like this.

There has to be a proxy, separated by a communication layer

This layer would be REST

Which service to be the proxy and which should be the new one

How to manage certificate?

How to manage trust between the services? Generated token or available certificate?

Discovery client synchronization issues will happen, how will this affect the reverse proxy? Can we be seeing route from scgw > gw for a route and then when route gets to scgw it would take over? We should prevent that. That means potentially routing has to be moved in one go from GW to SCGW.

Concerns about the proxification

Localhost resolution: will need configuration
Might not need to be onboarded to Gateway

## > apimlgw > scgw >

Spring security and all endpoints are already on the top level

To migrate something means to disable that function in GW and forward to SCGW through zuul

When would GW require changes?

Every time a migration happens, zuul routes would change

Once to estabilish trust

General downside that code changes in GW have to deal with lot of customized code

For example route \<==> service for us, this affects Ribbon a lot

Zuul cannot deal with it but it could be made

## > scgw > apimlgw >

Advantage is that nothing is implemented and the proxy logic is not tainted by anything

Disadvantage is that security has to be put in place to handle certificates equally

Seems that certificate is the only thing

AT-TLS? - should be ok if we move responsibility to the scgw and figure out certificate handling

To migrate something means: implement it in spring cloud gateway and stop the forward

This will create a version of gateway with proxied authentication logic for free

What is easy to migrate:

Static endpoints like actuator, version …

Medium effort:

Authenticated endpoints, especially with Certificate

High effort: routes

At that moment, everything gw reverse proxy specific has to go

When would GW require changes?

Once to estabilish x509 interop

Once to estabilish trust

# What breaking changes might happen

External vs internal port

Auditing plugin will break

Client with and without certificate switching might be challenging


# Benefits

oauth for free
<https://www.learnnowlab.com/Spring-Cloud-Gateway-with-Spring-Security/>


<https://dzone.com/articles/secure-spring-cloud-gateway-with-authentication-am>

# POC implementation notes

## SSL in spring cloud gateway HttpCLient

https://github.com/spring-cloud/spring-cloud-gateway/issues/1637

Http client bean can be provided instead of autoconfiguration
https://gist.github.com/cmhettinger/1c83824abeb880bcddb8538e6686def0

Maybe here we have an opportunity to do a switching http client as we have in Gateway, to create some sort of proxy as we have atm. But thanks to x509 auth schema I think we have to have this feature

Or option B

```
Currently, there is only one HttpClient for all routes, but there is a protected method in NettyRoutingFilter that would allow you to do something different per route. https://github.com/spring-cloud/spring-cloud-gateway/blob/master/spring-cloud-gateway-core/src/main/java/org/springframework/cloud/gateway/filter/NettyRoutingFilter.java#L254

Hi, I think I have a possible solution. My approximation is to extend NettyRoutingFilter, create a new custom NettyRoutingFilter (extending from the original one) and enabling it. The custom NettyRoutingFilter will override the getHttpclient method and in basis a new metadata on the route decides if it's necessary to use a different SSL configuration.`
```

## Understand client certificate and put it to request

https://stackoverflow.com/questions/68865665/spring-cloud-gateway-pass-client-certificate-information

this is done in the POC now and interop with Gateway is estabilished.
