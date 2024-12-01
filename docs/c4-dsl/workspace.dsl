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

        lkApiBackend = container "lkApiBackend" "" "typescript" "next.js" {
          httpServer = component "http сервер" "" "" ""

          authRepo = component "Репозиторий пользователей" "" "" ""
          accountRepo = component "Репозиторий баланс пользователя" "" "" ""
          callsRepo = component "Peпозиторий звонки пользователя" "" "" ""
          billsRepo = component "Peпозиторий расходы пользователя" "" "" ""

          httpServer -> authRepo
          httpServer -> accountRepo
          httpServer -> callsRepo
          httpServer -> billsRepo
        }

        ##############################################################################

        lkApiFront = container "lkApiFront" "" "typescript" "next.js" {

          pageController = component "page controller" "" "" ""

          authPage = component "авторизация пользователя" "" "" ""
          accountPage = component "Страница баланс пользователя" "" "" ""
          callsPage = component "Страница звонки пользователя" "" "" ""
          billsPage = component "Страница расходы пользователя" "" "" ""

          authRepo = component "Репозиторий пользователей" "" "" ""
          accountRepo = component "Репозиторий баланс пользователя" "" "" ""
          callsRepo = component "Peпозиторий звонки пользователя" "" "" ""
          billsRepo = component "Peпозиторий расходы пользователя" "" "" ""


          pageController -> authPage
          pageController -> accountPage
          pageController -> callsPage
          pageController -> billsPage

          authPage -> authRepo
          accountPage -> accountRepo
          callsPage -> callsRepo 
          billsPage -> billsRepo
          
        }

        lkApiFront.authRepo -> lkApiBackend.httpServer
        lkApiFront.accountRepo -> lkApiBackend.httpServer
        lkApiFront.callsRepo -> lkApiBackend.httpServer
        lkApiFront.billsRepo -> lkApiBackend.httpServer

        ##############################################################################

        adminApiBackend = container "AdminApiBackend" "" "typescript" "next.js" {

          httpServer = component "http сервер" "" "" ""
          
          authRepo = component "Репозиторий пользователей" "" "" ""
          abonentRepo = component "Репозиторий абонентов" "" "" ""
          operatorRepo = component "Репозиторий операторов" "" "" ""
          pricelistRepo = component "Репозиторий прайслистов" "" "" ""
          reportRepo = component "Репозиторий Отчетов" "" "" ""
          
           
          httpServer -> abonentRepo
          httpServer -> operatorRepo
          httpServer -> pricelistRepo
          httpServer -> reportRepo
          httpServer -> authRepo

        }

        ##############################################################################

        adminApiFront = container "AdminApiFront" "" "typescript" "next.js"        {

          pageController = component "page controller" "" "" ""

          authPage = component "авторизация пользователя" "" "" ""
          authRepo = component "Репозиторий пользователей" "" "" ""
          authPage -> authRepo
          pageController -> authPage

          abonentPage = component "Управление абонентами список/свойства" "" "" ""
          abonentRepo = component "Репозиторий абонентов" "" "" ""
          abonentPage -> abonentRepo
          pageController -> abonentPage

          operatorPage = component "Управление операторами список/свойства" "" "" ""
          operatorRepo = component "Репозиторий операторов" "" "" ""
          operatorPage -> operatorRepo
          pageController -> operatorPage

          pricelistPage = component "Управление прайслистами список/свойства" "" "" ""
          pricelistRepo = component "Репозиторий прайслистов" "" "" ""
          pricelistPage -> pricelistRepo
          pageController -> pricelistPage

          reportPage = component "Отчеты" "" "" ""
          reportRepo = component "Репозиторий Отчетов" "" "" ""
          reportPage -> reportRepo
          pageController -> reportPage


        }
    
        adminApiFront.authRepo -> adminApiBackend.httpServer
        adminApiFront.abonentRepo -> adminApiBackend.httpServer
        adminApiFront.operatorRepo -> adminApiBackend.httpServer
        adminApiFront.pricelistRepo -> adminApiBackend.httpServer
        adminApiFront.reportRepo -> adminApiBackend.httpServer
        
        adminApiBackend.authRepo -> db 
        adminApiBackend.abonentRepo -> db 
        adminApiBackend.operatorRepo -> db 
        adminApiBackend.pricelistRepo -> db 
        adminApiBackend.reportRepo -> db 

        lkApiBackend.authRepo -> db
        lkApiBackend.accountRepo -> db
        lkApiBackend.callsRepo -> db
        lkApiBackend.billsRepo -> db

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

      }

      abonent -> tellbillService.lkApiFront.pageController
      
      openSwitch ->  tellbillService.acc.radiusServer "Тарифицирует звонок"
      openSwitch ->  tellbillService.auth.radiusServer "Маршрутизирует звонок"
      
      #abonent -> tellbillService.lkApiFront "Смотрит ЛК"
      admin   -> tellbillService.adminApiFront.pageController "Смотрит ЛК"



        stage = deploymentEnvironment "Stage" {

                    sormvm = deploymentNode "sorm-vm" "" "Ubuntu 16.04 LTS" "" 1 {

                        containerInstance tellbillService.auth
                        containerInstance tellbillService.acc

                    }

                        deploymentNode "Database Server" "#3" {
                            containerInstance tellbillService.db {
                                description "dbname: legs"
                            }
                        }
                }

        development = deploymentEnvironment "Development" {
                              sormvm = deploymentNode "sorm-vm" "" "Ubuntu 16.04 LTS" "" 1 {

                        containerInstance tellbillService.auth
                        containerInstance tellbillService.acc

                    }

                        deploymentNode "Database Server" "#3" {
                            containerInstance tellbillService.db {
                                description "dbname: legs"
                            }
                        }
        }

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

        component tellbillService.adminApiBackend "Component_adminApiBackend" {
            include *
            description "adminApiBackend (бекенд интерфейса админимстратора)"
            autoLayout lr
        }

        component tellbillService.lkApiBackend "Component_lkApiBackend" {
            include *
            description "lkApiBackend (бекенд интерфейса абонента)"
            autoLayout lr
        }


        deployment * "Stage" "StageDeployment" {
            include *
            include *->*
            description "Схема разворота окружения тестирования"
            autoLayout lr
        }

        deployment * "Development" "DevelopmentDeployment" {
            include *
            include *->*
            description "Схема разворота окружения разработчика"
            autoLayout lr
        }


    }

}