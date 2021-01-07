## jmeter cli mode

jmeter -Jusername=<username> -Jpassword=<password> -n -t modified_caching-create.jmx -l result3 -e -o test-results3 -j result3.log 

## jmeter using taurus

bzt taurus_config.yml
