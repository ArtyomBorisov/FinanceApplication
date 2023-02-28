<h2>Finance Application</h2>
<p><b>RESTful</b> application is developed for finance management. It is based on <b>microservice architecture</b>. The application is packaged with <b>Docker</b>.

Each microservice is responsible for its own functionality:
 - <b>account-service</b> is designed for CRUD operations with accounts and financial operations.
 - <b>account-scheduler-service</b> is responsible for planning of operations according to a schedule via Quartz.
 - <b>classifier-service</b> is for control over currencies and categories.
 - <b>mail-service</b> is sending a report by email.
 - <b>mail-scheduler-service</b> is sending a report every month.
 - <b>report-service</b> is designed for xlsx report creating via Apache POI. Reports are kept in Minio storage.
 - <b>user-service</b> is for multi-user usage via JWT.
<hr>
<p>Tech: Java 11, Spring Framework (Boot, Data JPA, Security), PostgreSQL, Quartz, Apache POI, Docker, Minio, Swagger.
