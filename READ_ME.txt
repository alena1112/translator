Архитертура приложения:
UIApp.java - точка входа программы. В методе prepareTranslator() создается объект SessionManager, который хранит в себе
объекты SessionContext, CashManagerAPI, Translator. SessionManager предоставляет доступ к объектам SessionContext, CashManagerAPI,
имеет метод перевода слова translateWord().
SessionContext - хранит классы хранилища и переводчика (должны считываться из файла настроек).
CashManagerAPI - класс, реализующий управление кэшем хранилища.

Translator - класс, реализующий переводчик.