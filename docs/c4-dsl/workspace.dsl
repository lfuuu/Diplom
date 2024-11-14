workspace "telbill" "ИС управления телефонным узлом" {
   
    # https://www.youtube.com/watch?v=AKFYzNOoALE

    #softwareSystem <name> [description] [tags]
    #container <name> [description] [tags]

    !identifiers hierarchical

    model {
      
      properties {
        "structrurizr.groupseparator" "/"
        "plantuml.animation" "true"
        "plantuml.shadow" "true"
      }
      
      admin = person "Администратор узла"
      abonent = person "Абонент"

      openSwitch = softwareSystem "openSwitch" "Телефонный коммутатор" "ExternalSystem"
      accounting = softwareSystem "accounting" "Бухгалтерия" "ExternalSystem"
      elk = softwareSystem "elk" "Аналитическая система" "ExternalSystem"
      sorm = softwareSystem "sorm" "Выгрузка в спецслужбы" "ExternalSystem"

      tellbillService = softwareSystem "tellbill" "ИС управления телефонным узлом" "InternalSystem" {

        ##############################################################################

        acc = container "acc (Тарификатор звонков)" "" "typescript" "next.js" {
           radiusServer = component "Радиус сервер" "" "" ""
           cdrRepo = component "Репозиторий CDR" "" "" ""        
           
           radiusServer -> cdrRepo "Сохраняет звонки"
        }

        auth = container "Маршрутизатор звонков" "" "typescript" "next.js"
        db = container "Хранилище состояний" "Database" "posgresql" "Database"

        ##############################################################################

        lkApiBackend = container "lkApiBackend" "" "typescript" "next.js"
        lkApiFront = container "lkApiFront" "" "typescript" "next.js" {
          httpServer = component "http сервер" "" "" ""

          authPage = component "авторизация пользователя" "" "" ""
          accountPage = component "Страница баланс пользователя" "" "" ""
          callsPage = component "Страница звонки пользователя" "" "" ""
          billsPage = component "Страница расходы пользователя" "" "" ""

          authRepo = component "Репозиторий пользователей" "" "" ""
          accountRepo = component "Репозиторий баланс пользователя" "" "" ""
          callsRepo = component "Peпозиторий звонки пользователя" "" "" ""
          billsRepo = component "Peпозиторий расходы пользователя" "" "" ""


          httpServer -> authPage
          httpServer -> accountPage
          httpServer -> callsPage
          httpServer -> billsPage

          authPage -> authRepo
          accountPage -> accountRepo
          callsPage -> callsRepo 
          billsPage -> billsRepo
          
        }

        lkApiFront.authRepo -> lkApiBackend
        lkApiFront.accountRepo -> lkApiBackend
        lkApiFront.callsRepo -> lkApiBackend
        lkApiFront.billsRepo -> lkApiBackend

        ##############################################################################

        adminApiBackend = container "AdminApiBackend" "" "typescript" "next.js" 

        ##############################################################################

        adminApiFront = container "AdminApiFront" "" "typescript" "next.js"        {

          httpServer = component "http сервер" "" "" ""

          authPage = component "авторизация пользователя" "" "" ""
          authRepo = component "Репозиторий пользователей" "" "" ""
          authPage -> authRepo
          httpServer -> authPage

        }
    
        adminApiFront.authRepo -> adminApiBackend
        

        ##############################################################################


        acc.cdrRepo -> db "Сохраняет звонки"

        # acc -> db
        auth -> db

        db -> elk "Передает данные о звонках"
        db -> sorm "Передает данные о звонках"
        db -> accounting "Передает данные о звонках"

        #lkApiFront -> lkApiBackend
        #adminApiFront -> adminApiBackend

        lkApiBackend -> db
        adminApiBackend -> db

        #adminApiBackend -> db
      }

      abonent -> tellbillService.lkApiFront.httpServer
      
      openSwitch ->  tellbillService.acc.radiusServer "Тарифицирует звонок"
      openSwitch ->  tellbillService.auth "Маршрутизирует звонок"
      
      #abonent -> tellbillService.lkApiFront "Смотрит ЛК"
      admin   -> tellbillService.adminApiFront.httpServer "Смотрит ЛК"

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

        component tellbillService.acc "Component_acc" {
            include *
            # exclude *->*
            # include *->tellbillService.acc
            # include tellbillService.acc->*
            # animation {
            #      tellbillService.acc
            # }

            description "acc (Тарификатор звонков)"
        }

        component tellbillService.lkApiFront "Component_lkFront" {
            include *
            description "lkApiFront (Веб интерфейс ЛК-пользователя)"
        }

        component tellbillService.AdminApiFront "Component_AdminFront" {
            include *
            description "AdminApiFront (Веб интерфейс админимстратора)"
        }



    }

}