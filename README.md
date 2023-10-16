### Инструкция запуска приложения:

1. Создать БД со следующими параметрами подключения:
   - URL - jdbc:postgresql://localhost:5432/postgres;
   - логин - postgres;
   - пароль - password.
2. Заполнить БД тестовыми данными, выполнив скрипт \resources\init.sql.
3. Собрать проект
```
mvn clean package
```
4. Запустить приложение
```
java -jar target/aikamtask.jar search search.json output.json
```
или
```
java -jar target/aikamtask.jar stat stat.json output.json
```