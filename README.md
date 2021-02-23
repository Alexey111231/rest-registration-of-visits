# rest-registration-of-visits

## Инструкция по запуску

- Сделать clone этого репозитория

- Поправить файл src/main/resources/application.properties, чтобы он мог подключиться к вашей базе данных

- Из коммандной строки в папке с pom.xml выполнить команду: maven clean install.(Или выполнить ее средствами intelliJ
  Idea)

- Перейти в папку target

- Выполнить команду: java -jar rest-registration-of-visits-1.0-SNAPSHOT.jar

- Embedded база данных для тестирования еще не встроена
