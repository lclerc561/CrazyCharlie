FROM node:20-slim

WORKDIR /app

# Copie des fichiers de dépendances
COPY package*.json ./

# Installation (npm ci est plus propre pour Docker)
RUN npm ci

# Copie du reste du code
COPY . .

EXPOSE 3000

CMD ["npm", "start"]
