[![pipeline status](http://pttb.ru:8929/tieto/sus/badges/master/pipeline.svg)](http://pttb.ru:8929/tieto/sus/-/commits/master)
[![coverage report](http://pttb.ru:8929/tieto/sus/badges/master/coverage.svg)](http://pttb.ru:8929/tieto/sus/-/commits/master)

## StatusUpdate Service  
#### Для старта на тесте/проде (и вообще любом окружении):
```shell script
docker run -d --name is -p <YOUR_HTTP_APP_PORT>:8991 \
    -p <PORT_FOR_JMX_CONNECTION_TO_THIS_HOST>:22324 \
    -e CONSUL_HOST=<YOUR_CONSUL_AGENT_HOST> \
    -e jmx_host=<PUBLIC_IP_OR_HOSTNAME_OF_THIS_HOST> \
    -v <YOUR_IS_LOGS_DIR>:/app/logs \
    flexhamp/sus_image:latest
```
#### Пример запуска с параметрами dev-окружения:
```shell script
docker run -d --name is -p 8991:8991 \
    -p 22324:22324 \
    -e CONSUL_HOST=$DEPLOY_HOST \
    -e jmx_host=$DEPLOY_HOST \
    -v /var/log/is:/app/logs \
    flexhamp/sus_image:latest
```
#### Метрики (доступны также через JMX):
http://212.8.244.167:8991/actuator/metrics/find_by_acc
http://212.8.244.167:8991/actuator/metrics/find_by_acc_msisdn
http://212.8.244.167:8991/actuator/metrics/update
> Если с момента старта не было обращений к БД, вернут 404