## jmeter cli mode

jmeter -Jusername=USER -Jpassword=validPassword -n -t andrej-caching-profiling.jmx -l output/result3 -e -o output/test-results3 -j output/result3.log 

jmeter -Jusername=USER -Jpassword=validPassword -Jhost=localhost -Jport=10010 -Jjmeter.reportgenerator.overall_granularity=1000 -n -t david-caching-profiling.jmx -l output/result -e -o output/test-results -j output/result.log 

## jmeter using taurus

* not working at the moment

bzt taurus/taurus_config.yml
