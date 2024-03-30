# Запуск всех тестов
```shell
./mvnw test
```

# Запуск одного теста
```shell
./mvnw -Dtest="SuggestsTest#firstSuggestsStartingWithSearchQueryTest" test
```

# Запуск нескольких тестов
```shell
./mvnw -Dtest="SuggestsTest#firstSuggestsStartingWithSearchQueryTest,SuggestsTest#searchQueryIsBoldTest" test
```

# Генерация отчета
```shell
./mvnw allure:report
```
Отчет будет доступен в target/site/allure-maven-plugin/index.html

# Просмотр отчета
```shell
./mvnw allure:serve
```
