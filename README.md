# Text Indexer

Библиотека для индексации текстовых файлов по словам с поддержкой многопоточного доступа, мониторинга файловой системы и расширяемого механизма токенизации.

## Требования
- Java 21+
- Maven 3.8+

## Сборка и запуск

```bash
mvn clean package
```

Команда компилирует исходный код, прогоняет тесты и собирает артефакт в директорию target/.

## Использование CLI

```bash
# Рекурсивная индексация директории
java -cp target/text-indexer-1.0-SNAPSHOT.jar com.valerix.textindexer.app.Main --add /path/to/dir --recursive

# Поиск вхождения слова
java -cp target/text-indexer-1.0-SNAPSHOT.jar com.valerix.textindexer.app.Main --search "keyword"

# Вывод списка проиндексированных файлов
java -cp target/text-indexer-1.0-SNAPSHOT.jar com.valerix.textindexer.app.Main --list

# Мониторинг директории (автоматическое обновление индекса)
java -cp target/text-indexer-1.0-SNAPSHOT.jar com.valerix.textindexer.app.Main --watch /path/to/dir --recursive
```

## Тестирование
Проект покрыт модульными и интеграционными тестами (JUnit 5).
```bash
mvn test
```

