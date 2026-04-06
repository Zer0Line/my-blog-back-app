# Тренировочный проект-блог на Sping Framework

### Сборка проекта:
./gradlew build

### Сборка war архива:
./gradlew war

### Запуск тестов:
./gradlew test

### Деплой проекта:
Для деплоя проекта необходимо указать путь к Tomcat в tomcat.homepath в свойствах проекта  
src/main/resources/application.properties
Деплой
./gradlew deploy

Для перезапуска
./gradlew redeploy
