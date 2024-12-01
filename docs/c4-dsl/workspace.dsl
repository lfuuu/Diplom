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

        auth = container "auth (Маршрутизатор звонков)" "" "typescript" "next.js" {
           radiusServer = component "Радиус сервер" "" "" ""
           auth = component "Авторизатор" "" "" ""
           router = component "Маршрутизатор" "" "" ""
           dictRepo = component "Репозиторий Справочники" "" "" ""
           radiusServer -> auth "Авторизация вызова"
           auth -> router "Вычисление маршрута"
           router -> radiusServer "Маршруты"

           dictRepo -> router "Узнаем настройки"
           dictRepo -> auth "Узнаем настройки"
        }


        ##############################################################################

        acc = container "acc (Тарификатор звонков)" "" "typescript" "next.js" {
           radiusServer = component "Радиус сервер" "" "" ""
           cdrRepo = component "Репозиторий CDR" "" "" ""
           dictRepo = component "Репозиторий Справочники" "" "" ""

           cdrBuff =  component "Буфер новых cdr" "" "" ""
           billing = component "Биллингация вызовов" "" "" ""

           rawRepo = component "Репозиторий Calls-Raw" "" "" ""
           radiusServer -> cdrRepo "Сохраняет звонки"

           billing -> rawRepo "Передаем в созранение звонки"

           cdrBuff -> billing "Новые звонки"
           dictRepo -> billing "Узнаем прайслисты"

        }

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

          abonentPage = component "Управление абонентами список/свойства" "" "" ""
          abonentRepo = component "Репозиторий абонентов" "" "" ""
          abonentPage -> abonentRepo
          httpServer -> abonentPage

          operatorPage = component "Управление операторами список/свойства" "" "" ""
          operatorRepo = component "Репозиторий операторов" "" "" ""
          operatorPage -> operatorRepo
          httpServer -> operatorPage

          pricelistPage = component "Управление прайслистами список/свойства" "" "" ""
          pricelistRepo = component "Репозиторий прайслистов" "" "" ""
          pricelistPage -> pricelistRepo
          httpServer -> pricelistPage

          reportPage = component "Отчеты" "" "" ""
          reportRepo = component "Репозиторий Отчетов" "" "" ""
          reportPage -> reportRepo
          httpServer -> reportPage


        }
    
        adminApiFront.authRepo -> adminApiBackend
        adminApiFront.abonentRepo -> adminApiBackend
        adminApiFront.operatorRepo -> adminApiBackend
        adminApiFront.pricelistRepo -> adminApiBackend
        adminApiFront.reportRepo -> adminApiBackend
        
        ##############################################################################


        acc.cdrRepo -> db "Сохраняет звонки"

        # acc -> db
        db -> auth.dictRepo "Получаем сведения"
        db -> acc.cdrBuff "Получаем новые звонки"
        db -> acc.dictRepo "Получаем сведения"
        acc.rawRepo -> db "Сохраняет отбиллингованные звонки"

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
      openSwitch ->  tellbillService.auth.radiusServer "Маршрутизирует звонок"
      
      #abonent -> tellbillService.lkApiFront "Смотрит ЛК"
      admin   -> tellbillService.adminApiFront.httpServer "Смотрит ЛК"

    }
    views {
        # подключаем стили
        !include styles.dsl 

        systemLandscape  "SystemLandscape" {
            include *
            autoLayout lr
        }

        systemContext tellbillService "tellbillServiceContext" "Диаграмма контекта сервиса tellbill" {
           include *
           exclude *->*
           include *->tellbillService
           include tellbillService->*
           autoLayout lr
        }
    
        container tellbillService "Containers" {
            include *
            animation {
                tellbillService.acc
                tellbillService.auth
                tellbillService.db
            }
            description "Диаграмма контейнера ТелБилл"
            autoLayout tb

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
            autoLayout tb
        }


        component tellbillService.auth "Component_auth" {
            include *
            # exclude *->*
            # include *->tellbillService.acc
            # include tellbillService.acc->*
            # animation {
            #      tellbillService.acc
            # }

            description "auth (Маршрутизатор звонков)"
            autoLayout bt
        }


        component tellbillService.lkApiFront "Component_lkFront" {
            include *
            description "lkApiFront (Веб интерфейс ЛК-пользователя)"
            autoLayout lr
        }

        component tellbillService.AdminApiFront "Component_AdminFront" {
            include *
            description "AdminApiFront (Веб интерфейс админимстратора)"
            autoLayout lr
        }



    }

}