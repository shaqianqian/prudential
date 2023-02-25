1. api design: https://www.processon.com/view/link/63f9ad167cc60a365b00aa4b
2. database design: https://www.processon.com/view/link/63f9ad58621e3702bf0ec1cd
3. github: https://github.com/shaqianqian/prudential/tree/master
4. The key point of this requirement is how to allocate limited car resources by reasonably arranging users to use different time slices. I use the lazy loading method to calculate whether there are free vehicle resources when the user wants to rent a car
5. tech used: java11; H2 in memory database; Hibernate; Gradle; swagger
6. check h2 db by http://localhost:8080/car/h2-admin/, jdbc host: jdbc:h2:file:./data/dbAccount, user/password: root
7. swagger link: local: http://localhost:8080/car/swagger-ui/index.html  server: http://119.23.78.158:8080/interview/swagger-ui/index.html
8. server to deploy: http://119.23.78.158:8080/interview, can be tested by swagger or postman