# Курсовой проект "Сетевой чат"

## Сервер

- Установка порта для подключения клиентов через файл настроек (settings.txt);
- Возможность подключиться к серверу в любой момент и присоединиться к чату;
- Отправка новых сообщений клиентам;
- Запись всех отправленных через сервер сообщений с указанием имени пользователя и времени отправки;
- Обмен сообщениями происходит как в stdout так и ведется логирование в File.log;
- Реализованы патерн Singleton и Adapter с классами логирований;
- Классы Message Type и RxTx и settings.txt общие для клиента и сервера;
- Пытался реализовать IO на классах но на этапах тестирования по telnet возникли проблеммы.

## Клиент

- Выбор имени для участия в чате;
- Прочитать настройки приложения из файла настроек (settings.txt);
- Подключение к указанному в настройках серверу;
- Для выхода из чата нужно набрать команду выхода - “/exit”;
- Каждое сообщение участника записывается в текстовый файл - файл логирования. При каждом запуске приложения файл  дополняется.

## Реализация

- Сервер умеет одновременно ожидать новых пользователей и обрабатывать поступающие сообщения от пользователей;
- Использован сборщик пакетов gradle/maven;
- Код размещен на github;
- Проведены интеграционные тесты на подключение нескольких пользователей;
- Использованы элементы многопоточности на этапе переписки между пользователями, авторизация исполнена в однопоточном режиме.