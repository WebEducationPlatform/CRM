package com.ewp.crm.service.impl;

public class VkAdsReport {
    private String clientId = "1605137078";
    private String version = "5.78";
    private String access_token = "524fb23e95f0049733785f33b6f33b09d7cb397d40c03d2a08fadc84dcbab2834452908c75719a7a56a2f";
    private String vkAPI = "https://api.vk.com/method/";
}


/*
String uriBudget = "https://api.vk.com/method/" + "ads.getBudget" +
                "?account_id=" + "1605137078" +
                "&version=" + "5.78" +
                "&access_token=" + "524fb23e95f0049733785f33b6f33b09d7cb397d40c03d2a08fadc84dcbab2834452908c75719a7a56a2f";


    String uriStat = "https://api.vk.com/method/" + "ads.getStatistics" +
            "?account_id=" + "1605137078" +
            "&ids_type=" + "office" +
            "&ids=" + "1605137078" +
            "&period=" + "day" +
            "&date_from=" + "2019-03-15" +
            "&date_to=" + "2019-03-15" +
            "&version=" + "5.78" +
            "&access_token=" + "524fb23e95f0049733785f33b6f33b09d7cb397d40c03d2a08fadc84dcbab2834452908c75719a7a56a2f";



		получение токена
		https://oauth.vk.com/authorize?client_id=6886597&display=page&redirect_uri=http://example.com/callback&scope=friends&response_type=token&v=5.92&state=
https://oauth.vk.com/authorize?client_id=6886597&display=page&redirect_uri=http://localhost:9999/rest/vkontakte/ads&scope=ads,offline,groups&response_type=token&v=5.92&state=
c71b889e95665bbfd4a8f1480129f459f9899a789ee661210e9b13a8faa3bbdba6efbbf5d7f724bfd290a
этот токен действует вечно

	как получить рекламного vk баланс

Получение статистики рекламного кабинет в вк.
Произвести настройки перед запуском:
добавить адрес своего рекламного кабинета в vk.properties
https://vk.com/ads -> vk.accountId=1605137078 указать свой кабинет
в настройках приложения в вк указать redirect uri
http://localhost:9999/rest/vkontakte/ads
зайти на http://localhost:9999/vk-ads
статистика выведется в консоль и в браузер

accessToken = eb1134346a576830cacde3c61ce2f8525bda3012c3893853d23ce06100d6def88c027025d969372790df2
524fb23e95f0049733785f33b6f33b09d7cb397d40c03d2a08fadc84dcbab2834452908c75719a7a56a2f
expires = 0
https://api.vk.com/method/ads.getStatistics?account_id=1605137078&ids_type=office&ids=1605137078&period=day&date_from=2019-03-15&date_to=2019-03-15&version=5.78&access_token=524fb23e95f0049733785f33b6f33b09d7cb397d40c03d2a08fadc84dcbab2834452908c75719a7a56a2f
https://api.vk.com/method/ads.getBudget?account_id=1605137078&version=5.78&access_token=524fb23e95f0049733785f33b6f33b09d7cb397d40c03d2a08fadc84dcbab2834452908c75719a7a56a2f


guano (481-557-9415)
Идентификатор разработчика
PQFUFHeJkJFU4BMG7YCeHg


testguano (831-922-3882)
clientguanotest
740-899-9441
clientguanotest
748-585-6521


jmentortoapi
Идентификатор клиента
789730766343-9i6ih36puboskgkiaha1plmffrrpgjj1.apps.googleusercontent.com
Секрет клиента
8qnoVDkrdiMWlf2LJZbZ9-Q1
api.adwords.refreshToken=1/AMDrQEAQOWbgU9xeZkxDVvjLqh8ErC7fBuuUdOuS5rM
api.adwords.clientCustomerId=740-899-9441

https://github.com/googleads/googleads-java-lib


ID приложения:	6886597
Защищённый ключ:
L0TevlxjUg8oq2cZi2ZC
Сервисный ключ доступа:
af5ff643af5ff643af5ff64329af36e286aaf5faf5ff643f3220abcf47e709942696936


кабинет Сони 	1602201055
кабинет мой 	1605137078

            */