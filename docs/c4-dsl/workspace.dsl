workspace "telbill" "ИС управления телефонным узлом" {
   
    # https://www.youtube.com/watch?v=AKFYzNOoALE

    #softwareSystem <name> [description] [tags]
    #container <name> [description] [tags]
     
    model {
      
      properties {
        "structrurizr.groupseparator" "/"
      }
      
      admin = person "Администратор узла"
      abonent = person "Абонент"

      openSwitch = softwareSystem "openSwitch" "Телефонный коммутатор" "ExternalSystem"
      accounting = softwareSystem "accounting" "Бухгалтерия" "ExternalSystem"
      elk = softwareSystem "elk" "Аналитическая система" "ExternalSystem"

      tellbillService = softwareSystem "tellbill" "ИС управления телефонным узлом" "InternalSystem" {
        acc = container "Тарификатор звонков" "" "typescript" "next.js"
        auth = container "Маршрутизатор звонков" "" "typescript" "next.js"
        db = container "Хранилище состояний" "" "posgresql" "Database"

        lkApi = container "backend" "" "typescript" "next.js"
        lkFront = container "front" "" "typescript" "next.js"

      }

      openSwitch ->  acc "Тарифицирует звонок"
      openSwitch ->  auth "Маршрутизирует звонок"
      
      abonent -> lkFront "Смотрит ЛК"
      admin   -> lkApi "Смотрит ЛК"

    }
    views {
        # подключаем стили
        !include styles.dsl 

        systemLandscape  "SystemLandscape" {
            include *
        }

        systemContext tellbillService "tellbillServiceContext" "Диаграмма контекта сервиса tellbill" {
           include *
           exclude *->*
           include *->tellbillService
           include tellbillService->*
           autoLayout
        }
    

    }
}