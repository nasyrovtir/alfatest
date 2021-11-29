**Описание**

Сервис возвращает тематическую GIF-картинку в зависимости от изменения курса интересующей валюты относительно USD.<br>
Взаимодействие осуществляется посредством GET запросов на
`
/ api / gif ? base = КОД ВАЛЮТЫ
`

**Стек технологий**

- Java 11
- Gradle
- Git
- Spring Boot
- Docker
- Junit
- Mockito
---
**Инструкция по запуску**

- Java (JDK) 11+.
- Gradle
- Git
- Docker

**Для сборки приложения выполнить команды:**
```
git clone https://github.com/nasyrovtir/alfatest.git alfatest
cd alfatest
gradle build
```
**Для запуска программы в docker контейнере выполнить команду:**
```
docker build -t alfatest_image .
docker run --rm --name alfatest_container -p 8080:8080 alfatest_image
```
**Для запуска программы в консоли выполнить команды:**
```
cd build/libs
java -jar alfatest-0.0.1-SNAPSHOT.jar
```
---
**Список кодов ответа сервера**

- 200 OK<br>
- 400 Bad Request - не найден код интересующей валюты<br>
- 500 Internal Server Error - внутренняя ошибка сервера<br>
---
**Разработчик**

Насыров Тимур Радикович <br>
[nasyrovtir@mail.ru](mailto:nasyrovtir@mail.ru)
