# Лабораторна робота 3: Spring Core & Boot

## Звіт про виконання

### Мета роботи
Ознайомитися з принципами інверсії керування (IoC) та ін'єкції залежностей (DI) у Spring. Навчитися створювати біни, керувати їхнім життєвим циклом та застосовувати автоконфігурацію Spring Boot.

---

## 1. Теоретична частина

### 1.1. Інверсія керування (Inversion of Control, IoC)

**Інверсія керування** — це принцип проектування, при якому контроль над життєвим циклом об'єктів передається фреймворку (Spring Container), а не самому застосунку.

**До Spring:**
```java
// Програміст сам створює об'єкти
JdbcBookRepository repo = new JdbcBookRepository();
CatalogService service = new CatalogService(repo);
```

**З Spring:**
```java
// Spring Container створює та керує об'єктами
@Autowired
private CatalogService catalogService; // Spring автоматично інжектує залежності
```

**Переваги IoC:**
- Зменшення зв'язаності між компонентами
- Легше тестування (можна підміняти залежності)
- Централізоване керування конфігурацією
- Автоматичне управління життєвим циклом

### 1.2. Ін'єкція залежностей (Dependency Injection, DI)

**Ін'єкція залежностей** — це механізм реалізації IoC, при якому залежності передаються об'єкту ззовні (інжектуються), а не створюються всередині.

**Способи ін'єкції залежностей у Spring:**

#### A. Ін'єкція через конструктор (Constructor Injection) — **рекомендований спосіб**

```java
@Service
public class CommentService {
    private final CommentRepositoryPort repo;

    // DI через конструктор
    public CommentService(CommentRepositoryPort repo) {
        this.repo = repo;
    }
}
```

**Переваги:**
- Гарантує ініціалізацію всіх залежностей
- Полегшує тестування
- Робить залежності явними
- Підтримує immutable поля (final)

**Приклад з проєкту:** `CommentService`, `JdbcCommentRepository`, `JdbcBookRepository`

#### B. Ін'єкція через поле (Field Injection)

```java
@Controller
public class BooksController {
    // DI через поле
    @Autowired
    private CatalogRepositoryPort bookRepository;
}
```

**Переваги:**
- Компактний код
- Зручно для швидкого прототипування

**Недоліки:**
- Важче тестувати (потрібні рефлексія або Mockito)
- Не можна використовувати final поля
- Менш явна залежність

**Приклад з проєкту:** `BooksController`, `BooksApiController`, `CommentsController`

### 1.3. Автоконфігурація Spring Boot

**Автоконфігурація** — механізм Spring Boot, який автоматично налаштовує компоненти на основі:
- Залежностей у classpath
- Налаштувань у `application.properties`/`application.yaml`
- Існуючих бінів

**Приклади автоконфігурації у проєкті:**

1. **DataSource** — автоматично налаштовується з `application.properties`:
```properties
spring.datasource.url=jdbc:h2:file:./data/guest
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

2. **JdbcTemplate** — автоматично створюється, якщо є `spring-boot-starter-jdbc`

3. **Embedded Tomcat** — автоматично запускається, якщо є `spring-boot-starter-web`

4. **View Resolver** — налаштовується з `application.properties`:
```properties
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp
```

---

## 2. Практична реалізація

### 2.1. Структура проєкту

```
MPF_project/
├── core/                    # Бізнес-логіка
│   └── service/
│       ├── CatalogService.java      (@Service, DI через поле)
│       └── CommentService.java      (@Service, DI через конструктор)
├── persistence/             # Доступ до даних
│   └── jdbc/
│       ├── JdbcBookRepository.java   (@Repository, DI через конструктор)
│       ├── JdbcCommentRepository.java (@Repository, DI через конструктор)
│       └── DbInit.java               (@Component, @PostConstruct)
└── web/                     # Веб-шар
    ├── Application.java              (@SpringBootApplication)
    ├── config/
    │   ├── AppConfig.java            (@Configuration, @Bean)
    │   └── WebMvcConfig.java         (@Configuration)
    └── web/
        ├── BooksController.java      (@Controller, DI через поле)
        ├── BooksApiController.java    (@RestController, DI через конструктор та поле)
        └── CommentsController.java    (@Controller, DI через конструктор та поле)
```

### 2.2. Головний клас Spring Boot Application

**Файл:** `web/src/main/java/sumdu/edu/ua/Application.java`

```java
@SpringBootApplication
@ComponentScan(basePackages = {"sumdu.edu.ua"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**Пояснення:**
- `@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`
- `@ComponentScan` — сканує пакети для пошуку компонентів
- `main()` — точка входу застосунку

### 2.3. Реалізація бінів

#### A. Сервіси (@Service)

**CatalogService** — демонструє DI через поле:
```java
@Service
public class CatalogService {
    @Autowired
    private CatalogRepositoryPort repository; // DI через поле
}
```

**CommentService** — демонструє DI через конструктор:
```java
@Service
public class CommentService {
    private final CommentRepositoryPort repo;

    public CommentService(CommentRepositoryPort repo) { // DI через конструктор
        this.repo = repo;
    }
}
```

#### B. Репозиторії (@Repository)

**JdbcBookRepository:**
```java
@Repository
public class JdbcBookRepository implements CatalogRepositoryPort {
    private final DataSource dataSource;

    public JdbcBookRepository(DataSource dataSource) { // DI через конструктор
        this.dataSource = dataSource;
    }
}
```

**JdbcCommentRepository:**
```java
@Repository
public class JdbcCommentRepository implements CommentRepositoryPort {
    private final JdbcTemplate jdbcTemplate;

    public JdbcCommentRepository(JdbcTemplate jdbcTemplate) { // DI через конструктор
        this.jdbcTemplate = jdbcTemplate;
    }
}
```

#### C. Контролери (@Controller, @RestController)

**BooksController:**
```java
@Controller
public class BooksController {
    @Autowired
    private CatalogRepositoryPort bookRepository; // DI через поле
}
```

**BooksApiController:**
```java
@RestController
@RequestMapping("/api/books")
public class BooksApiController {
    @Autowired
    private CatalogRepositoryPort bookRepository; // DI через поле

    private final ObjectMapper objectMapper; // DI через конструктор

    public BooksApiController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
```

### 2.4. Кастомний бін через @Bean

**Файл:** `web/src/main/java/sumdu/edu/ua/config/AppConfig.java`

```java
@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Можна додати додаткові налаштування
        return mapper;
    }
}
```

**Пояснення:**
- `@Configuration` — позначає клас як джерело конфігурації бінів
- `@Bean` — позначає метод як фабрику бінів
- Spring автоматично викличе метод і зареєструє результат як бін
- Бін можна інжектувати в інші компоненти

### 2.5. Конфігурація через application.properties

**Файл:** `web/src/main/resources/application.properties`

```properties
# Назва застосунку
spring.application.name=MPF Books Catalog

# Порт сервера
server.port=8080

# Контекстний шлях
server.servlet.context-path=/

# Налаштування бази даних H2
spring.datasource.url=jdbc:h2:file:./data/guest
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console (для розробки)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JSP налаштування
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp

# Логування
logging.level.root=INFO
logging.level.sumdu.edu.ua=DEBUG

# Власні параметри застосунку
app.books.default-page-size=20
app.books.max-page-size=100
app.comments.max-age-hours=24
```

### 2.6. Ініціалізація бази даних

**Файл:** `persistence/src/main/java/sumdu/edu/ua/jdbc/DbInit.java`

```java
@Component
public class DbInit {
    private final JdbcTemplate jdbcTemplate;

    public DbInit(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        // Ініціалізує схему БД при старті застосунку
    }
}
```

**Пояснення:**
- `@Component` — позначає клас як Spring компонент
- `@PostConstruct` — метод виконується після створення біна та ін'єкції залежностей
- Гарантує ініціалізацію БД перед використанням

---

## 3. Демонстрація роботи

### 3.1. Запуск застосунку

```bash
cd web
mvn spring-boot:run
```

Або через IDE: запустити метод `main()` у класі `Application`.

### 3.2. Перевірка роботи

1. **Веб-інтерфейс:**
   - http://localhost:8080/books — список книг
   - http://localhost:8080/comments?bookId=1 — коментарі до книги

2. **REST API:**
   - GET http://localhost:8080/api/books — список книг (JSON)
   - POST http://localhost:8080/api/books — створення книги (JSON)

3. **H2 Console:**
   - http://localhost:8080/h2-console — консоль бази даних

### 3.3. Перевірка завантаження бінів

У логах під час запуску видно:
```
=== Spring Boot Application Started ===
Database schema initialized successfully
```

---

## 4. Висновки

### Досягнуто:

✅ Проєкт модифіковано для запуску на базі Spring Boot Starter  
✅ Реалізовано сервіси у вигляді бінів (@Component, @Service, @Repository, @Configuration)  
✅ Продемонстровано ін'єкцію залежностей через конструктор та поле  
✅ Додано кастомний бін через анотацію @Bean у класі конфігурації  
✅ Налаштовано параметри застосунку у файлі application.properties  
✅ Створено Spring Boot застосунок, що піднімається через main() з @SpringBootApplication  

### Використані технології:

- Spring Boot 3.4.1
- Spring MVC
- Spring JDBC
- H2 Database
- JSP
- Maven

### Принципи:

- **IoC** — Spring Container керує життєвим циклом об'єктів
- **DI** — залежності інжектуються автоматично
- **Автоконфігурація** — Spring Boot автоматично налаштовує компоненти
- **Конвенція над конфігурацією** — мінімум конфігурації, максимум функціональності

---

## 5. Приклади коду

### 5.1. DI через конструктор

```java
@Service
public class CommentService {
    private final CommentRepositoryPort repo;

    public CommentService(CommentRepositoryPort repo) {
        this.repo = repo;
    }
}
```

### 5.2. DI через поле

```java
@Controller
public class BooksController {
    @Autowired
    private CatalogRepositoryPort bookRepository;
}
```

### 5.3. Кастомний бін

```java
@Configuration
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
```

### 5.4. Життєвий цикл (@PostConstruct)

```java
@Component
public class DbInit {
    @PostConstruct
    public void init() {
        // Виконується після створення біна
    }
}
```

---

**Дата виконання:** 2025  
**Виконавець:** [Ваше ім'я]

