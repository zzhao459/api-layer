## jmeter cli mode

jmeter -Jusername=USER -Jpassword=validPassword -n -t andrej-caching-profiling.jmx -l output/result3 -e -o output/test-results3 -j output/result3.log 

jmeter -Jusername=USER -Jpassword=validPassword -Jhost=localhost -Jport=10010 -Jthreads=10 -Jrampup=120 -Jhold=120 -Jjmeter.reportgenerator.overall_granularity=1000 -n -t david-caching-profiling.jmx -l output/result -e -o output/test-results -j output/result.log

## jmeter using taurus

* not working at the moment

bzt taurus/taurus_config.yml

### Actual performance test

1) Caching service with InMemmory impl.

2) 1 Caching service with Vsam

3) 2 Caching services with Vsam

#### Thread levels: 

Low load: 3 threads

jmeter -Jusername=USER -Jpassword=validPassword -Jhost=localhost -Jport=10010 -Jthreads=3 -Jrampup=120 -Jhold=120 -Jjmeter.reportgenerator.overall_granularity=1000 -n -t david-caching-profiling.jmx -l output/result -e -o output/test-results -j output/result.log

Medium load: 15 threads

jmeter -Jusername=USER -Jpassword=validPassword -Jhost=localhost -Jport=10010 -Jthreads=15 -Jrampup=120 -Jhold=120 -Jjmeter.reportgenerator.overall_granularity=1000 -n -t david-caching-profiling.jmx -l output/result -e -o output/test-results -j output/result.log

High load: 50 threads

jmeter -Jusername=USER -Jpassword=validPassword -Jhost=localhost -Jport=10010 -Jthreads=50 -Jrampup=120 -Jhold=120 -Jjmeter.reportgenerator.overall_granularity=1000 -n -t david-caching-profiling.jmx -l output/result -e -o output/test-results -j output/result.log


