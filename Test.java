import com.valerix.textindexer.core.index.InMemoryIndex;
import com.valerix.textindexer.core.tokenizer.SimpleTokenizer;
import java.nio.file.Paths;

public class Test {
    public static void main(String[] args) {
        // 1. Создаем индекс и токенайзер
        var index = new InMemoryIndex();
        var tokenizer = new SimpleTokenizer();

        // 2. Сразу добавляем файлы (в том же процессе!)
        index.addFile(Paths.get("test_docs/file1.txt"), tokenizer);
        index.addFile(Paths.get("test_docs/file3.txt"), tokenizer);

        // 3. Сразу ищем слово "java"
        var result = index.search("java", tokenizer);

        // 4. Печатаем результат
        System.out.println("Найдено файлов: " + result.getFiles().size());
        result.getFiles().forEach(f -> System.out.println(" - " + f.getPath()));
    }
}