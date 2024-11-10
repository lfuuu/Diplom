workspace {
    name "наименование системы" 
    description "описание системы, понятноен психечески здоровым людям"
    model {
        # Подключаем ландшафт - справочник систем
        !include landscape.dsl 
        # Подключаем контекстную диаграмму
        !include context.dsl
        # Подключаем контейнерную диаграмму
        !include container.dsl
    }
    views {
        # подключаем стили
        !include styles.dsl 
        systemContext SRTUCTURIZR {
            title "Контекст на сейчас"
            include *
            # авто форматирование, есть разные настройки, можно посмотреть в документации
            autoLayout lr
            exclude relationship.tag!=ASIS
        }
        systemContext SRTUCTURIZR {
            title "Контекст на 2025"
            include *
            # autoLayout lr
            exclude relationship.tag!=Q25
        }
        container SRTUCTURIZR {
            title "Контейнеры"
            include *
            autoLayout lr
            # exclude relationship.tag!=Q25 
        }
    }
}