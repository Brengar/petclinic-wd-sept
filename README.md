Инструкция по установке и запуску Spring Pet Clinic:

- скачать или склонировать git clone https://github.com/i-bonkin/spring-petclinic-rest.git
- в командой строке скачанного проекта выполнить mvnw spring-boot:run (запущен back-end)
- скачать или склонировать git clone https://github.com/i-bonkin/spring-petclinic-angular.git
- установить, если не установлен node.js сервер (https://nodejs.org/dist/v6.11.2/node-v6.11.2-x64.msi)
- в командой строке скачанного проекта выполнить команды
    
    npm uninstall -g angular-cli @angular/cli
    
    npm cache clean

    npm install -g @angular/cli@latest
    
    npm install --save-dev @angular/cli@latest

    npm install
    
    ng serve
    
- убедиться, что в браузере по адресу localhost:4200 открылось приложение Spring PetClinic    

