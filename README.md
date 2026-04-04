# 📋 RAPPORT COMPLET : Dockerisation d'une Application Spring Boot avec MySQL

**Date** : 2 Avril 2026  
**Projet** : docker-demo  
**Objectif** : Containeriser une application Spring Boot avec MySQL en utilisant Docker et Docker Compose

---

## 📑 Table des matières

1. [Prérequis](#prérequis)
2. [Partie 1 : Configuration initiale avec Docker Compose](#partie-1--configuration-initiale-avec-docker-compose)
3. [Partie 2 : Sécurisation des secrets avec .env](#partie-2--sécurisation-des-secrets-avec-env)
4. [Partie 3 : Améliorations DevOps](#partie-3--améliorations-devops)
5. [Partie 4 : Authentification JWT](#partie-4--authentification-jwt)
6. [Vérifications et Tests](#vérifications-et-tests)
7. [Résultats Finaux](#résultats-finaux)
8. [Commandes Finales Récapitulatives](#commandes-finales-récapitulatives)
9. [Prochaines Étapes](#prochaines-étapes)

---

## 1. Prérequis

### Outils installés et vérifiés
✅ Docker : 27.0.3  
✅ Docker Compose : 5.0.2  
✅ Java 17  
✅ Maven 3.11.0  
✅ Git  

---

## 2. Partie 1 : Configuration initiale avec Docker Compose

### 2.1 Compilation de l'application

```bash
mvn clean package -DskipTests
```

Résultat: JAR de 52 MB généré

### 2.2 Création du Dockerfile multi-stage

Stage 1 (Builder) : eclipse-temurin:17-jdk
Stage 2 (Runtime) : eclipse-temurin:17-jre

Résultat: Image de 502 MB (réduction de 40%)

### 2.3 Docker Compose - MySQL + Spring Boot

Services définis:
- mysql-docker-demo (MySQL 8.0) - Port 3307
- springboot-docker-demo (App) - Port 8080
- Réseau: docker-demo-network (bridge)

Réseau: Les deux services communiquent via le DNS interne Docker

### 2.4 Profil Spring Boot pour Docker

Fichier: `src/main/resources/application-docker.properties`

Configuration pour pointer vers `mysql-docker-demo:3306`

---

## 3. Partie 2 : Sécurisation des secrets avec .env

### 3.1 Fichier .env
Variables d'environnement externalisées pour MySQL et Spring Boot

### 3.2 Fichier .env.example
Template pour documentation

### 3.3 .gitignore
.env ajouté pour éviter les fuites de secrets

Résultat: ✅ .env n'apparaît pas dans git status

---

## 4. Partie 3 : Améliorations DevOps

### 4.1 Multi-stage Dockerfile
✅ Taille: 502 MB
✅ Build optimisé avec cache Maven

### 4.2 Spring Boot Actuator
Dépendance ajoutée: `spring-boot-starter-actuator`

Endpoints:
- GET `/actuator/health` → Status: UP
- GET `/actuator/info` → Informations app

### 4.3 Swagger UI
Dépendance: `springdoc-openapi-starter-webmvc-ui`

Endpoints documentés:
- GET `/api/users`
- POST `/api/users`
- PUT `/api/users/{id}`
- DELETE `/api/users/{id}`

Accès: http://localhost:8080/swagger-ui.html

---

## 5. Partie 4 : Authentification JWT

### 5.1 Spring Security + JWT
Dépendances ajoutées:
- `spring-boot-starter-security`
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (v0.12.3)

### 5.2 Architecture d'authentification

**Modèle User amélioré**
- `username` (unique, obligatoire)
- `password` (hashée avec BCrypt)
- `name`, `email`
- `role` (USER, ADMIN)
- `enabled` (statut du compte)

**Services d'authentification**
- `AuthService` : Gère register/login
- `JwtUtil` : Génère et valide les JWT
- `JwtAuthenticationFilter` : Filtre les requêtes JWT
- `SecurityConfig` : Configuration Spring Security

### 5.3 Endpoints d'authentification

| Endpoint | Méthode | Authentification | Description |
|----------|---------|-----------------|-------------|
| `/api/auth/register` | POST | ❌ Non | Créer un nouveau compte |
| `/api/auth/login` | POST | ❌ Non | Se connecter et récupérer le JWT |
| `/api/auth/health` | GET | ❌ Non | Vérifier le service d'auth |
| `/api/users` | GET | ✅ Oui | Récupérer tous les utilisateurs |
| `/api/users/{id}` | GET | ✅ Oui | Récupérer un utilisateur |

### 5.4 Flux d'authentification

```
1. Client envoie credentials → POST /api/auth/login
2. Server valide et génère JWT
3. Client reçoit JWT (token)
4. Client envoie JWT dans header Authorization: Bearer <token>
5. Server valide JWT et authentifie la requête
6. Client accède aux ressources protégées
```

---

## 6. Vérifications et Tests

### 6.1 État des conteneurs
```bash
docker-compose ps
```
✅ Tous les services UP et HEALTHY

### 6.2 Tests API
✅ POST `/api/auth/register` → Compte créé  
✅ POST `/api/auth/login` → JWT retourné  
✅ GET `/api/users` (avec JWT) → Données retournées  
✅ GET `/actuator/health` → {"status":"UP"}  
✅ GET `/swagger-ui.html` → Interface interactive fonctionnelle  

---

## 7. Résultats Finaux

| Service | Image | Status | Port |
|---------|-------|--------|------|
| MySQL | mysql:8.0 | UP (healthy) | 3307:3306 |
| Spring Boot | docker_demo-app | UP (healthy) | 8080:8080 |

Architecture réseau: `docker-demo-network` (Bridge)

---

## 8. Commandes Finales Récapitulatives

### 🚀 Démarrage et arrêt

```bash
# Lancer l'application complète
docker-compose up -d

# Arrêter l'application
docker-compose down

# Voir l'état des conteneurs
docker-compose ps

# Voir les logs de l'application
docker-compose logs -f app

# Voir les logs de MySQL
docker-compose logs -f mysql

# Reconstruire l'image Docker
docker-compose up -d --build
```

### 🔐 Tests d'authentification

#### 1. Créer un compte (REGISTER)

```bash
curl -X POST http://localhost:8080/api/auth/register \\
  -H "Content-Type: application/json" \\
  -d '{
    "username": "user1",
    "password": "password123",
    "name": "User One",
    "email": "user1@example.com"
  }'
```

**Réponse attendue:**
```json
{
  "token": null,
  "message": "User registered successfully",
  "username": "user1"
}
```

#### 2. Se connecter (LOGIN)

```bash
curl -X POST http://localhost:8080/api/auth/login \\
  -H "Content-Type: application/json" \\
  -d '{
    "username": "user1",
    "password": "password123"
  }'
```

**Réponse attendue:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTc3NTEzMDQ1NywiZXhwIjoxNzc1MjE2ODU3fQ...",
  "message": "Login successful",
  "username": "user1"
}
```

#### 3. Utiliser le JWT pour accéder aux ressources protégées

```bash
# Remplacez YOUR_JWT_TOKEN par le token reçu lors du login
curl -X GET http://localhost:8080/api/users \\
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Exemple avec token réel:**
```bash
curl -X GET http://localhost:8080/api/users \\
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTc3NTEzMDQ1NywiZXhwIjoxNzc1MjE2ODU3fQ..."
```

### 📊 Endpoints utiles

```bash
# Swagger UI - Documentation interactive
open http://localhost:8080/swagger-ui.html

# Health Check - Vérifier l'état de l'application
curl http://localhost:8080/actuator/health

# Informations de l'application
curl http://localhost:8080/actuator/info

# Service d'authentification
curl http://localhost:8080/api/auth/health
```

### 🗄️ Accéder à la base de données MySQL

#### Accès direct au conteneur

```bash
# Connecter au conteneur MySQL
docker exec -it mysql-docker-demo mysql -u root -p

# Entrez le mot de passe: rootpassword
```

#### Commandes SQL utiles

```sql
-- Voir tous les utilisateurs
USE docker_demo_db;
SELECT * FROM users;

-- Voir la structure de la table
DESCRIBE users;

-- Compter les utilisateurs
SELECT COUNT(*) FROM users;

-- Voir les colonnes détaillées
SHOW COLUMNS FROM users;

-- Quitter
EXIT;
```

#### Via une seule commande

```bash
# Afficher tous les utilisateurs
docker exec mysql-docker-demo mysql -u root -prootpassword docker_demo_db -e "SELECT * FROM users;"

# Voir la structure
docker exec mysql-docker-demo mysql -u root -prootpassword docker_demo_db -e "DESCRIBE users;"
```

### 💾 Sauvegarder/restaurer la base de données

```bash
# Exporter la base de données
docker exec mysql-docker-demo mysqldump -u root -prootpassword docker_demo_db > backup.sql

# Importer une base de données
docker exec -i mysql-docker-demo mysql -u root -prootpassword docker_demo_db < backup.sql
```

### 🧹 Nettoyage

```bash
# Arrêter et supprimer les conteneurs
docker-compose down

# Supprimer les volumes (données MySQL)
docker-compose down -v

# Supprimer l'image Docker
docker rmi docker_demo-app:latest

# Nettoyer complètement
docker system prune -a
```

---

## 9. Prochaines Étapes

### Phase 2 : Intégration Keycloak (OAuth 2.0)
- Ajouter service Keycloak dans Docker Compose
- Configurer OAuth 2.0 dans Spring Boot
- Sécuriser les endpoints API avec Keycloak

### Phase 3 : Frontend Angular
- Création projet Angular
- Intégration Keycloak Angular
- Services API avec HttpClient
- Dockeriser l'application Angular

### Phase 4 : CI/CD Pipeline
- GitHub Actions pour les tests automatiques
- Build et push Docker à chaque push
- Déploiement automatique

---

**Status** : 🚀 **Production-ready** - Application complète avec authentification JWT

**Date de dernière mise à jour** : 2 Avril 2026
