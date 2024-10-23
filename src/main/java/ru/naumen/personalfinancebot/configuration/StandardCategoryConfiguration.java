package ru.naumen.personalfinancebot.configuration;

import org.yaml.snakeyaml.Yaml;
import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.model.category.CategoryComponent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Класс, который работает с конфигурацией для стандартных категорий
 */
// Паттерн Singleton
public class StandardCategoryConfiguration {
    /**
     * Путь к ресурсу с конфигурацией
     */
    private static final String CONFIG_RESOURCE_PATH = "standard_categories.yaml";

    /**
     * Один единственный экземпляр конфигурации
     */
    private static final StandardCategoryConfiguration instance = new StandardCategoryConfiguration();

    /**
     * Хранит список категорий
     */
    private final List<CategoryComponent> categories;

    private StandardCategoryConfiguration() {
        this.categories = parseCategories();
    }

    /**
     * Возвращает единственный экземпляр для работы с конфигурацией для стандартных категорий
     */
    public static StandardCategoryConfiguration getInstance() {
        return instance;
    }

    /**
     * Возвращает неизменяемый список стандартных категорий
     */
    public List<CategoryComponent> getStandardCategories() {
        return Collections.unmodifiableList(this.categories);
    }

    /**
     * Парсит стандартные категории из файла
     */
    private List<CategoryComponent> parseCategories() {
        Map<String, Object> categoryTypesMap;

        try (InputStream inputStream = getConfigInputStream()) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlContent = yaml.load(inputStream);

            categoryTypesMap = (Map<String, Object>) yamlContent.get("standard_categories");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> expenseCategoryPaths = (List<String>) categoryTypesMap.get("expense");
        List<String> incomeCategoryPath = (List<String>) categoryTypesMap.get("income");

        List<CategoryComponent> expenseCategories = getCategoryList(CategoryType.EXPENSE, expenseCategoryPaths);
        List<CategoryComponent> incomeCategories = getCategoryList(CategoryType.INCOME, incomeCategoryPath);
        return Stream.concat(expenseCategories.stream(), incomeCategories.stream()).toList();
    }

    /**
     * Создает список категорий
     *
     * @param type          Тип категории
     * @param categoryPaths Названия для новых категорий
     * @return Список категорий типа type и переданными названиями
     */
    private List<CategoryComponent> getCategoryList(CategoryType type, List<String> categoryPaths) {
//        return categoryPaths.stream()
//                .map(name -> new CategoryRow(null, name, type))
//                .toList();
        // TODO
        return null;
    }

    /**
     * Создает и возвращает входной поток с конфигурационным файлом
     */
    private InputStream getConfigInputStream() {
        return this.getClass().getClassLoader().getResourceAsStream(CONFIG_RESOURCE_PATH);
    }


}