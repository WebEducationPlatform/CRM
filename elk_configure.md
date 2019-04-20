1. Клонировать репозиторий
git clone https://github.com/shpi0/docker-elk.git

2. При необходимости настроить в файле docker-compose.yml:
* порты:
9200, 9300 - для elasticsearch
5000, 9600 - logstash
5601 - kibana
* директорию на хосте для хранения БД elasticsearch: /etc/elasticsearch/data
* остальные настройки уже сделаны

3. В директории репозитория выполнить команду запуска контейнеров: 
docker-compose up -d

4. Зайти на http://localhost:5601 убедиться что kibana поднялась

5. Добавить в проект appender для logback:

    <appender name="stash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>127.0.0.1:5000</destination>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeCallerData>true</includeCallerData>
            <customFields>{"appname":"CRM"}</customFields>
        </encoder>
    </appender>

    <root level="ALL">
        <appender-ref ref="stash" />
    </root>
	
6. Необходимо, чтобы проект отправил какой-либо лог в logstash, только после этого можно идти дальше.

7. В кибане зайти anagement - Index petterns - Create index pettern и добавить паттерн "logstash-*" без кавычек, штамп времени указать стандартный @timestamp.

8. В Discover настроить фильтр для отбора по проекту, из которого прилетели логи: Add filter - Filter: "message" -> "is one of" -> "appname":"CRM" -> Save