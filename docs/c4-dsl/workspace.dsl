workspace "telbill" "ИС управления телефонным узлом" {
   
    # https://www.youtube.com/watch?v=AKFYzNOoALE

    #softwareSystem <name> [description] [tags]
    #container <name> [description] [tags]

    !identifiers hierarchical

    model {
      
      properties {
        "structrurizr.groupseparator" "/"
      }
      
      admin = person "Администратор узла"
      abonent = person "Абонент"

      openSwitch = softwareSystem "openSwitch" "Телефонный коммутатор" "ExternalSystem"
      accounting = softwareSystem "accounting" "Бухгалтерия" "ExternalSystem"
      elk = softwareSystem "elk" "Аналитическая система" "ExternalSystem"
      sorm = softwareSystem "sorm" "Выгрузка в спецслужбы" "ExternalSystem"

      tellbillService = softwareSystem "tellbill" "ИС управления телефонным узлом" "InternalSystem" {
        acc = container "Тарификатор звонков" "" "typescript" "next.js"
        auth = container "Маршрутизатор звонков" "" "typescript" "next.js"
        db = container "Хранилище состояний" "" "posgresql" "Database"

        lkApiBackend = container "lkApiBackend" "" "typescript" "next.js"
        lkApiFront = container "lkApiFront" "" "typescript" "next.js"

        adminApiBackend = container "AdminApiBackend" "" "typescript" "next.js"
        adminApiFront = container "AdminApiFront" "" "typescript" "next.js"


        acc -> db
        auth -> db

        db -> elk
        db -> sorm
        db -> accounting

        lkApiFront -> lkApiBackend
        adminApiFront -> adminApiBackend

        lkApiBackend -> db
        adminApiBackend -> db
      }

      openSwitch ->  tellbillService.acc "Тарифицирует звонок"
      openSwitch ->  tellbillService.auth "Маршрутизирует звонок"
      
      abonent -> tellbillService.lkApiFront "Смотрит ЛК"
      admin   -> tellbillService.adminApiFront "Смотрит ЛК"

      tellbillService -> accounting "Передает данные о звонках"
      tellbillService -> sorm "Передает данные о звонках"
      tellbillService -> elk "Передает данные о звонках"

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
    
        container tellbillService "Containers" {
            include *
            animation {
                tellbillService.acc
                tellbillService.auth
                tellbillService.db
            }

            description "The container diagram for the Internet Banking System."
        }


    }
}