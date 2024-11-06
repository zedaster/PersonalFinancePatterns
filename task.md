## Задание 2. Singleton

Класс: StandardCategoryConfiguration [(ссылка)](src/main/java/ru/naumen/personalfinancebot/configuration/StandardCategoryConfiguration.java)

### Причины реализации паттерна
* Приложению необходим лишь один класс этого экземпляра
* В конструкторе этого класса идет считывание файла конфигурации. Singleton помогает избежать повторного вызова этой 
операции.
* Отсутствие повторного вызова также гарантирует консистентность данных (список категорий будет всегда такой же)



## Задание 7. Abstract Factory

Пакет: ru.naumen.personalfinancebot.repository  [(ссылка)](src/main/java/ru/naumen/personalfinancebot/repository)

В этом пакете [RepositoryFactory](src/main/java/ru/naumen/personalfinancebot/repository/RepositoryFactory.java) 
является абстрактной фабрикой для создания репозиториев, а 
[HibernateRepositoryFactory](src/main/java/ru/naumen/personalfinancebot/repository/HibernateRepositoryFactory.java) - 
одной из имплементаций этой фабрики.

Указанная абстрактная фабрика и ее реализация используются в 
[Main](src/main/java/ru/naumen/personalfinancebot/Main.java)

### Причины реализации паттерна
* Объединение классов одного семейства в одну порождающую конструкцию
* Изоляция конкретных классов (HibernateCategoryRepository, HibernateOperationRepository и т.д.) для повышения 
модульности и гибкости системы
* Упрощается переход с Hibernate на другую платформы (например, JDBC или JPA). Нужно поменять только фабрику, интерфейсы
остаются те же.

### Признаки паттерна
* Наличие абстрактной фабрики RepositoryFactory. Ее методы создают и возвращают абстракции объектов
* Наличие реализации абстрактной фабрики HibernateRepositoryFactory. Ее методы создают и возвращают реализации 
* абстракций объектов.
* Все репозитории относятся к одному семейству, т.к. работают на одной платформе (в данном случае на Hibernate, но 
* могут и на любой другой)

## Задание 8. Adapter

Класс: TelegramBot [(ссылка)](src/main/java/ru/naumen/personalfinancebot/bot/TelegramBot.java)

### Причины реализации паттерна
* Необходимость интеграции библиотеки (для работы с Телеграм ботами) с интерфейсом Bot, заложенном в системе. 

### Признаки паттерна
TelegramBot является адаптером, т.к.:
* Реализует интерфейс Bot, описывающий функционал чат-бота в проекте
* Наследует класс из сторонней библиотеки, который без адаптера не совместим с интерфейсом Bot
* Методы, реализованные из интерфейса Bot, используют функционал библиотеки

## Задание 9. Bridge

Интерфейсы: Bot [(ссылка)](src/main/java/ru/naumen/personalfinancebot/bot/Bot.java) и FormatMode [(ссылка)](src/main/java/ru/naumen/personalfinancebot/mode/FormatMode.java)

### Причины реализации паттерна
* Необходимость разделения 2 классификаций (Чат-боты и режимы форматирования) на 2 разные иерархии
* Избежать лишних классов при расширении приложения. К примеру, если понадобятся новая реализация бота
(для Дискорда, ВК и т.д.) или новый режим форматирования, то наличие 2 иерархий позволит добавлять лишь один класс 
вместо множества.

## Задание 10. Composite
Пакет: model/category [(ссылка)](src/main/java/ru/naumen/personalfinancebot/model/category)

### Причины реализации паттерна
* Необходимость добавление одной функциональности для множества классов одного семейства.

## Задание 11. Decorator
Класс: LoggingDecorator [(ссылка)](src/main/java/ru/naumen/personalfinancebot/handler/command/LoggingDecorator.java)

### Причины реализации паттерна
* Необходимость добавление одной функциональности для множества классов одного семейства.

## Задание 12. Facade
Класс: ArgumentValidator [(ссылка)](src/main/java/ru/naumen/personalfinancebot/handler/validator/ArgumentValidator.java)

### Причины реализации паттерна
* Необходимость создать простой интерфейс к множеству классов для валидации аргументов (NumberParseService, DateParseService и т.д.)
* Сократить количество зависимостей в обработчиках команд

## Задание 13. Flyweight
Класс: Task [(ссылка)](src/main/java/example/patterns/flyweight/models/Task.java) и TaskContext 
[(ссылка)](src/main/java/example/patterns/flyweight/models/TaskContext.java)

### Причины реализации паттерна
* Оптимизация памяти. Есть задачи, которые имеют длинное описание и присваиваются нескольким пользователям одновременно.
В таком случае разбиение задачи на Task и TaskContext помогает сократить использование памяти.

## Задание 14. Proxy
Классы: BudgetRepository [(ссылка)](src/main/java/ru/naumen/personalfinancebot/repository/budget/BudgetRepository.java), 
CategoryRepository [(ссылка)](src/main/java/ru/naumen/personalfinancebot/repository/category/CategoryRepository.java),
OperationRepository [(ссылка)](src/main/java/ru/naumen/personalfinancebot/repository/operation/OperationRepository.java),
UserRepository [(ссылка)](src/main/java/ru/naumen/personalfinancebot/repository/user/UserRepository.java).

### Признаки паттерна
* Являются "представителями" операций с удаленными объектами в базе данных
* В архитектуре представляют собой отдельный слой работы с базой данных в приложении

## Задание 15. Chain of Responsibility
Класс: MessageBuilder [(ссылка)](src/main/java/ru/naumen/personalfinancebot/message/format/MessageBuilder.java)

Пример использования: Creat [(ссылка)](src/main/java/ru/naumen/personalfinancebot/handler/command/budget/CreateBudgetHandler.java)

### Причины реализации паттерна
* Обработка в определенном порядке значений несколькими способами