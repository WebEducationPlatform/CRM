0. Создать директорию, в которую будет монтироваться докер контейнер, в ней будут лежать файлы jenkins'а, бэкапы базы и собранный рабочий проект.
В этой директории создать директорию db_backups

1.	Установить докер

https://docs.docker.com/install/linux/docker-ce/debian/

sudo apt-get install docker-ce docker-ce-cli containerd.io

Проверить работу докера:

sudo docker run hello-world

2.	Скачать репозиторий

docker pull shpi0/Jenkins-liquibase

3.	Запустить контейнер

docker run --restart=always -d -p 8080:8080 --name jenkins -v _DIR_:/var/jenkins_home shpi0/jenkins-liquibase

8080 (слева) – порт, по которому будет доступен Jenkins на хосте

_DIR_ - директория на хосте, в которую будет смонтирована директория Jenkins_home из контейнера

4.	Открываем web интерфейс http://localhost:8080

5.	Вводим пароль из _DIR_/secrets/initialAdminPassword

6.	Выбираем стандартные плагины

7.	Заполняем свои данные

8.	Настроить Jenkins -> конфигурация глобальных инструментов -> добавить maven, поставить галочку install automatically -> Save

9.	Создать задачу авто-сборки:
* Создать Item
* задать имя CRM, создать задачу со свободной конфигурацией, ok
* Управление исходным кодом - GIT, Repository URL: https://github.com/WebEducationPlatform/CRM.git
* Branches to build: */dev
* Триггеры сборки: Опрашивать SCM об изменениях, Расписание: H/10 * * * *
* Среда сборки: Delete workspace before build starts
* Сборка:
* Выполнить команду shell:

mysqldump -P 3306 -h host.docker.internal -u root -proot crm_new| gzip > `date +/var/jenkins_home/db_backups/crm_new.sql.%Y%m%d.%H%M%S.gz`

if [ -e /var/jenkins_home/db_backups/dbChangeLog.xml ]; then cp -rf '/var/jenkins_home/db_backups/dbChangeLog.xml' /var/jenkins_home/db_backups/dbChangeLog.xml.backup; fi

if [ -e /var/jenkins_home/workspace/CRM/db/dbChangeLog.xml ]; then cp -rf '/var/jenkins_home/workspace/CRM/db/dbChangeLog.xml' /var/jenkins_home/db_backups/dbChangeLog.xml; fi

if [ -e /var/jenkins_home/db_backups/dbChangeLog.xml ]; then /bin/liquibase/liquibase --username=root --password=root --changeLogFile=/var/jenkins_home/db_backups/dbChangeLog.xml --driver=com.mysql.jdbc.Driver --classpath=/bin/liquibase/lib/mysql-connector-java-5.1.46.jar --url=jdbc:mysql://host.docker.internal/crm_new?useUnicode=yes\&characterEncoding=UTF-8\&useSSL=false update; else echo "Changelog file not found at /var/jenkins_home/db_backups/dbChangeLog.xml"; exit; fi

* Вызвать цели Maven верхнего уровня, версия Maven: выбрать версию которую установили в начале, Цели: clean install
* Выполнить команду shell:

cp -rf '/var/jenkins_home/workspace/CRM/target/crm-1.2-CRM.jar' /var/jenkins_home/crm-release.jar

10. Создать задачу отката базы:
* Задать название - CRM_DB_ROLLBACK
* создать задачу со свободной конфигурацией, ok
* V Это - параметризованная сборка, добавить String parameter ROLLABCKS_COUNT
* Среда сборки: Delete workspace before build starts
* Сборка:
* Выполнить команду shell

mysqldump -P 3306 -h host.docker.internal -u root -proot crm_new| gzip > `date +/var/jenkins_home/db_backups/crm_new.sql.%Y%m%d.%H%M%S.gz`

/bin/liquibase/liquibase --username=root --password=root --driver=com.mysql.jdbc.Driver --classpath=/bin/liquibase/lib/mysql-connector-java-5.1.46.jar --url=jdbc:mysql://host.docker.internal/crm_new?useUnicode=yes\&characterEncoding=UTF-8\&useSSL=false --changeLogFile=/var/jenkins_home/db_backups/dbChangeLog.xml rollbackCount $ROLLABCKS_COUNT

11. Создать задачу октата проекта
* Задать название - CRM_PROJECT_ROLLBACK
* создать задачу со свободной конфигурацией, ok
* V Это - параметризованная сборка, добавить String parameter GIT_TAG
* Управление исходным кодом - GIT, Repository URL: https://github.com/WebEducationPlatform/CRM.git
* Branches to build: */tags/$GIT_TAG
* Среда сборки: Delete workspace before build starts
* Сборка:
* Выполнить команду shell

mysqldump -P 3306 -h host.docker.internal -u root -proot crm_new| gzip > `date +/var/jenkins_home/db_backups/crm_new.sql.%Y%m%d.%H%M%S.gz`

* Вызвать цели Maven верхнего уровня, версия Maven: выбрать версию которую установили в начале, Цели: clean install
* Выполнить команду shell:

cp -rf '/var/jenkins_home/workspace/CRM_PROJECT_ROLLBACK/target/crm-1.2-CRM.jar' /var/jenkins_home/crm-release.jar