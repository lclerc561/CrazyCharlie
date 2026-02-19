# Étape 1 : Compilation avec Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# copie des fichier de config et le code source
COPY pom.xml .
COPY src ./src

# On génère le fichier .jar
RUN mvn clean package -DskipTests

# Étape 2 : Image d'exécution légère
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# On récupère le .jar généré à l'étape 1
COPY --from=build /app/target/*.jar app.jar

# Port d'écoute
EXPOSE 5000

ENTRYPOINT ["java", "-jar", "app.jar"]
