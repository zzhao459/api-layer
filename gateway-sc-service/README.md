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
