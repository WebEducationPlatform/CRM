## 8. Настройка Telegram

### Установка TDLib в Windows

Для подключения функционала Telegram необходимо скомпилировать и установить библотеку TDLib. На примере ОС Windows 10:
1. Установить Microsoft Visual Studio 2015 или более новую.
2. Выбрать для установки компоненты (обязательно):
    * Windows 8.1 SDK
    * Инструменты Visual C++ для CMake
    * Пакет SDK для Windows 10
    * Английский языковой пакет
3. Скачать и установить gperf (https://sourceforge.net/projects/gnuwin32/files/gperf/3.0.1/), добавить путь к gperf.exe к системной переменной PATH.
4. Установить vcpkg (https://github.com/Microsoft/vcpkg#quick-start):
    * `git clone https://github.com/Microsoft/vcpkg.git`
    * `cd vcpkg`
    * `.\bootstrap-vcpkg.bat`
    * `.\vcpkg integrate install`
    * `.\vcpkg install sdl2 curl`
    * `.\vcpkg.exe install openssl:x64-windows openssl:x86-windows zlib:x64-windows zlib:x86-windows`
5. Скачайте и установить CMake (https://cmake.org/download/), обязательно отметьте пункт "Add CMake to the system PATH" при установке, либо вручную добавьте путь к CMake к системной переменной PATH.
6. Соберить TDLib:
    * `git clone https://github.com/tdlib/td.git`
    * `cd <ПУТЬ_К_TDLib>`
    * `mkdir jnibuild`
    * `cd jnibuild`
    * `cmake -DCMAKE_BUILD_TYPE=Release -DTD_ENABLE_JNI=ON -DCMAKE_INSTALL_PREFIX:PATH=../example/java/td .. –A x64 -DCMAKE_TOOLCHAIN_FILE=<ПУТЬ_К_РЕПОЗИТОРИЮ_VCPKG>/scripts/buildsystems/vcpkg.cmake`
    * `cmake --build . --target install --config Release`
    * `cd <ПУТЬ_К_TDLib>/example/java`
    * `mkdir build`
    * `cd build`
    * `cmake -DCMAKE_BUILD_TYPE=Release -DTd_DIR=<ПУТЬ_К_TDLib>/example/java/td/lib/cmake/Td -DCMAKE_INSTALL_PREFIX:PATH=.. ..`
    * `cmake --build . --target install --config Release`
7. TDLib собран! Теперь необходимо путь <ПУТЬ_К_TDLib>/example/java/build/Release добавить к системной переменной PATH.
8. Проверить TDLib:
    * `cd <ПУТЬ_К_TDLib>/example/java/bin`
    * `java -Djava.library.path=. org/drinkless/tdlib/example/Example`
9. Если TDLib "ругается" на отсутствующие библиотеки, перейдите в <ПУТЬ_К_TDLib>/example/java/build/Release и скопируйте файлы "LIBEAY32.dll", "SSLEAY32.dll", "zlib1.dll" в папку C:\Windows.
10. При необходимости, к параметрам запуска проекта добавьте настройки VM Options:
    * `"-Djava.library.path=<ПУТЬ_К_TDLib>\example\java\build\Release".`

### Установка TDLib в Debian 9

1. Необходимо установить/обновить дополнительные зависимости (при их отсутствии):
    * `sudo apt-get update`
    * `sudo apt-get upgrade`
    * `sudo cp /etc/apt/sources.list /etc/apt/sources.list.BACKUP`
    * `sudo add-apt-repository "deb http://security.debian.org/ wheezy/updates main"`
    * `sudo add-apt-repository "deb-src http://security.debian.org/ wheezy/updates main"`
    * `sudo add-apt-repository "deb http://ftp.us.debian.org/debian wheezy main non-free"`
    * `sudo add-apt-repository "deb-src http://ftp.us.debian.org/debian wheezy main non-free"`
    * `sudo add-apt-repository "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main"`
    * `sudo apt-get update`
    * `sudo apt-get install build-essential gcc-4.9 g++-4.9 openssl libssl-dev ccache gperf zlib1g zlib1g-dev cmake libreadline-dev git default-jre default-jdk software-properties-common oracle-java8-installer oracle-java8-set-default clang`
    * `sudo cp /etc/apt/sources.list.BACKUP /etc/apt/sources.list`
    * `sudo apt-get update`
2. Выбрать место установки TDLib и клонировать репозиторий TDLib в эту директорию, здесь для примера указан путь "/usr/lib":
    * `cd /usr/lib`
    * `git clone https://github.com/tdlib/td.git`
    * `cd td`
3. Собрать JNI:
    * `mkdir jnibuild`
    * `cd jnibuild`
    * `CXX=clang++ CC=clang cmake -DCMAKE_BUILD_TYPE=Release -DTD_ENABLE_JNI=ON -DCMAKE_INSTALL_PREFIX:PATH=../example/java/td ..`
    * `CXX=clang++ CC=clang cmake --build . --target install`
4. Собрать TDLib:
    * `cd /usr/lib/td/example/java`
    * `mkdir build`
    * `cd build`
    * `CXX=clang++ CC=clang cmake -DCMAKE_BUILD_TYPE=Release -DTd_DIR=/usr/lib/td/example/java/td/lib/cmake/Td -DCMAKE_INSTALL_PREFIX:PATH=.. ..`
    * `CXX=clang++ CC=clang cmake --build . --target install`
5. Проект запускать со ссылкой на директорию с готовой библиотекой:
    * `java –Djava.library.path=/usr/lib/td/example/java/bin –jar CRM.jar`
### Настройка Telegram
1. Зарегистрируйте аккаунт Telegram
2. Зайдите на https://my.telegram.org и авторизуйтесь
3. Перейдите по ссылке "API development tools" и заполните поля, выберите любое название приложения, тип приложения Desktop, краткое описание.
4. После успешного создания приложения, на следующие странице вы увидите данные приложения, нам необходимы "App api_id" и "App api_hash". Установите значения этих полей в конфигурационный файл [telegram.properties](../telegram.properties) в поля "telegram.apiId" и "telegram.apiHash".
5. Запустите CRM
6. После запуска CRM, зайдите под учетной записью администратора в настройки (Скриншот 24) и выберите пункт "Авторизация Telegram". Откроется диалоговое окно (Скриншот 54).
7. Введите номер телефона, к которому привязан ваш аккаунт телеграм, в международном формате (+70000000000) и нажмите иконку телефона справа.
8. Вам придет код (либо в Telegram, либо в SMS) для привязки приложения, введите этот код в поле и нажмите "Отправить".
9. Готово!
![alt text](https://pp.userapi.com/c844617/v844617291/1b83ef/KJjPnjBYadk.jpg)
***Авторизация в Telegram***