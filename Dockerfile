# Usamos una imagen de OpenJDK 21
FROM eclipse-temurin:21-jdk-alpine AS build
# Copiamos TODOS los archivos del proyecto (incluyendo pom.xml y mvnw)
COPY . .
# Le damos permisos de ejecución en Linux por si se perdieron desde Windows
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Imagen final ligera para ejecución
FROM eclipse-temurin:21-jre-alpine
COPY --from=build /target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]