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