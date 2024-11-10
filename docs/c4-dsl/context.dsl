# Можно для наглядности добавить пользователя
Architector = person "Архитектор" 
softwareSystem SRTUCTURIZR {
# Системный контекст на сейчас
Architector -> SPARXEA "Ведение архитектуры" "Ручной ввод" "ASIS"
SPARXEA -> SRTUCTURIZR "Выгрузка данных по системам ит ландшафта" "Ручной ввод" "ASIS,sync"
# Системный контекст на 2025 год
Architector -> SRTUCTURIZR "Ведение архитектуры" "Архитектура как код" "ASIS,Q25"
SRTUCTURIZR -> SPARXEA "выгрузка результатов" "dsl" "Q25"
}