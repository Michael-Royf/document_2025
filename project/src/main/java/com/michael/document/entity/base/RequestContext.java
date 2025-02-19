package com.michael.document.entity.base;

public class RequestContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    private RequestContext() {
    }

    public static void start() {
        USER_ID.set(null);
    }

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }
}


/*
Класс RequestContext представляет собой утилитарный класс для управления контекстом запроса на уровне потока.
Он использует ThreadLocal для хранения и извлечения идентификатора пользователя (USER_ID) в пределах текущего потока.

Поля и Конструктор
public class RequestContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    private RequestContext() {
    }
}
USER_ID: Это статическая переменная типа ThreadLocal<Long>, которая используется для хранения идентификатора пользователя. ThreadLocal позволяет каждому потоку иметь свою собственную копию переменной, что делает ее безопасной для использования в многопоточном окружении.
Конструктор: Приватный конструктор предотвращает создание экземпляров класса RequestContext, делая его утилитарным классом.
Методы
start()

public static void start() {
    USER_ID.remove();
}
Метод start удаляет значение идентификатора пользователя из ThreadLocal. Это полезно для инициализации или очистки контекста в начале обработки запроса.

setUserId(Long userId)

public static void setUserId(Long userId) {
    USER_ID.set(userId);
}
Метод setUserId устанавливает значение идентификатора пользователя в ThreadLocal для текущего потока. Он принимает идентификатор пользователя в качестве параметра.

getUserId()

public static Long getUserId() {
    return USER_ID.get();
}
Метод getUserId возвращает значение идентификатора пользователя из ThreadLocal для текущего потока.
Если значение не было установлено, метод вернет null.

Пример использования
Инициализация контекста в начале запроса:
java

public void onRequestStart() {
    RequestContext.start();
}
Установка идентификатора пользователя после аутентификации:

public void authenticateUser(Long userId) {
    RequestContext.setUserId(userId);
}
Получение идентификатора пользователя в любом месте текущего потока:

public void processRequest() {
    Long userId = RequestContext.getUserId();
    if (userId != null) {
        // Используйте идентификатор пользователя для выполнения операций
    }
}
Преимущества использования ThreadLocal
Безопасность в многопоточном окружении: ThreadLocal гарантирует, что каждая нить (поток) имеет свою собственную копию переменной,
что предотвращает проблемы с параллелизмом.
Удобство хранения данных, специфичных для потока: Использование ThreadLocal упрощает управление данными,
 которые должны быть доступны только в пределах текущего потока.
Заключение
Класс RequestContext предоставляет простой и эффективный способ управления данными,
специфичными для текущего потока, такими как идентификатор пользователя.
 Используя ThreadLocal, он гарантирует, что данные будут безопасными и изолированными для каждого потока,
 что особенно полезно в многопоточном веб-приложении.
 */
