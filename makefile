# =========================================
# Makefile - Product Catalog
# Simplifie les commandes Docker Compose
# =========================================

.PHONY: help dev prod build clean logs test monitoring ssl

# Variables
COMPOSE_DEV = docker compose
COMPOSE_PROD = docker compose -f docker-compose.prod.yml
IMAGE_NAME = bruno/product-catalog:1.0.0-jib

# Couleurs
GREEN  := \033[0;32m
YELLOW := \033[1;33m
NC     := \033[0m

##@ Configuration Basique (Dev/Cours)

dev: ## Démarrer en mode développement (basique)
	@echo "$(GREEN)Démarrage en mode DEV...$(NC)"
	$(COMPOSE_DEV) up -d
	@echo "$(GREEN)✅ Services démarrés$(NC)"
	@echo "Application: http://localhost:8080"
	@echo "PostgreSQL: localhost:15432"

dev-build: ## Build et démarrer en mode dev
	@echo "$(GREEN)Build et démarrage en mode DEV...$(NC)"
	$(COMPOSE_DEV) up -d --build

dev-logs: ## Voir les logs (dev)
	$(COMPOSE_DEV) logs -f

dev-down: ## Arrêter les services (dev)
	@echo "$(YELLOW)Arrêt des services DEV...$(NC)"
	$(COMPOSE_DEV) down

dev-clean: ## Arrêter et supprimer les volumes (dev)
	@echo "$(YELLOW)Nettoyage complet DEV...$(NC)"
	$(COMPOSE_DEV) down -v

##@ Configuration Production

prod: ## Démarrer en mode production
	@echo "$(GREEN)Démarrage en mode PRODUCTION...$(NC)"
	$(COMPOSE_PROD) up -d
	@echo "$(GREEN)✅ Services démarrés$(NC)"
	@echo "Application: https://products.localhost"
	@echo "Traefik: https://traefik.localhost:8080"

prod-monitoring: ## Démarrer en mode prod avec monitoring
	@echo "$(GREEN)Démarrage PRODUCTION + Monitoring...$(NC)"
	$(COMPOSE_PROD) --profile monitoring up -d
	@echo "$(GREEN)✅ Services démarrés$(NC)"
	@echo "Application: https://products.localhost"
	@echo "Traefik: https://traefik.localhost:8080"
	@echo "Prometheus: https://prometheus.localhost"
	@echo "Grafana: https://grafana.localhost"

prod-build: ## Build et démarrer en mode prod
	@echo "$(GREEN)Build et démarrage PRODUCTION...$(NC)"
	$(COMPOSE_PROD) up -d --build

prod-logs: ## Voir les logs (prod)
	$(COMPOSE_PROD) logs -f

prod-down: ## Arrêter les services (prod)
	@echo "$(YELLOW)Arrêt des services PRODUCTION...$(NC)"
	$(COMPOSE_PROD) down

prod-clean: ## Arrêter et supprimer les volumes (prod)
	@echo "$(YELLOW)Nettoyage complet PRODUCTION...$(NC)"
	$(COMPOSE_PROD) --profile monitoring down -v

##@ Build Application

build: ## Build l'image Docker avec Maven
	@echo "$(GREEN)Build de l'application...$(NC)"
	./mvnw clean package -Pjvm -DskipTests

build-cache: ## Build avec BuildKit cache
	@echo "$(GREEN)Build avec cache BuildKit...$(NC)"
	export DOCKER_BUILDKIT=1 && ./mvnw clean package -Pjvm-cache -DskipTests

build-native: ## Build native (lent)
	@echo "$(YELLOW)Build NATIVE (cela peut prendre 10 minutes)...$(NC)"
	./mvnw clean package -Pnative -DskipTests

##@ Utilitaires

ssl: ## Générer les certificats SSL auto-signés
	@echo "$(GREEN)Génération des certificats SSL...$(NC)"
	chmod +x generate-ssl-cert.sh
	./generate-ssl-cert.sh

hosts: ## Afficher les lignes à ajouter dans /etc/hosts
	@echo "$(YELLOW)Ajoutez ces lignes à /etc/hosts :$(NC)"
	@echo "127.0.0.1 products.localhost"
	@echo "127.0.0.1 traefik.localhost"
	@echo "127.0.0.1 prometheus.localhost"
	@echo "127.0.0.1 grafana.localhost"
	@echo ""
	@echo "Commande pour Linux/Mac:"
	@echo "sudo bash -c 'cat >> /etc/hosts << EOF"
	@echo "127.0.0.1 products.localhost"
	@echo "127.0.0.1 traefik.localhost"
	@echo "127.0.0.1 prometheus.localhost"
	@echo "127.0.0.1 grafana.localhost"
	@echo "EOF'"

logs: ## Voir tous les logs (dev et prod)
	@echo "$(YELLOW)Logs DEV:$(NC)"
	$(COMPOSE_DEV) logs --tail=50
	@echo ""
	@echo "$(YELLOW)Logs PROD:$(NC)"
	$(COMPOSE_PROD) logs --tail=50

ps: ## Liste des services actifs
	@echo "$(YELLOW)Services DEV:$(NC)"
	$(COMPOSE_DEV) ps
	@echo ""
	@echo "$(YELLOW)Services PROD:$(NC)"
	$(COMPOSE_PROD) ps

restart: ## Redémarrer l'application (dev et prod)
	@echo "$(GREEN)Redémarrage de l'application...$(NC)"
	$(COMPOSE_DEV) restart product-catalog || true
	$(COMPOSE_PROD) restart product-catalog || true

##@ Tests

test: ## Tester l'application (dev)
	@echo "$(GREEN)Test de l'application DEV...$(NC)"
	@curl -f http://localhost:8080/health || echo "❌ Service non accessible"
	@curl -f http://localhost:8080/health/ready || echo "❌ Service pas prêt"

test-prod: ## Tester l'application (prod)
	@echo "$(GREEN)Test de l'application PROD...$(NC)"
	@curl -k -f https://products.localhost/health || echo "❌ Service non accessible"
	@curl -k -f https://products.localhost/health/ready || echo "❌ Service pas prêt"

test-api: ## Tester l'API REST
	@echo "$(GREEN)Test de l'API...$(NC)"
	@echo "Création d'un produit:"
	@curl -X POST http://localhost:8080/products \
	  -H "Content-Type: application/json" \
	  -d '{"name":"Test Product","price":99.99}' | jq
	@echo ""
	@echo "Liste des produits:"
	@curl http://localhost:8080/products | jq

##@ Base de données

db: ## Se connecter à PostgreSQL (dev)
	$(COMPOSE_DEV) exec postgres psql -U tpuser -d products

db-prod: ## Se connecter à PostgreSQL (prod)
	$(COMPOSE_PROD) exec postgres psql -U tpuser -d products

db-backup: ## Backup de la base de données
	@echo "$(GREEN)Backup de la base de données...$(NC)"
	@mkdir -p backups
	$(COMPOSE_DEV) exec -T postgres pg_dump -U tpuser products > backups/backup-$$(date +%Y%m%d-%H%M%S).sql
	@echo "$(GREEN)✅ Backup créé dans backups/$(NC)"

db-restore: ## Restaurer la base (spécifier BACKUP=fichier.sql)
	@if [ -z "$(BACKUP)" ]; then \
		echo "$(YELLOW)Usage: make db-restore BACKUP=backups/backup-xxx.sql$(NC)"; \
		exit 1; \
	fi
	@echo "$(YELLOW)Restauration de $(BACKUP)...$(NC)"
	$(COMPOSE_DEV) exec -T postgres psql -U tpuser products < $(BACKUP)
	@echo "$(GREEN)✅ Restauration terminée$(NC)"

##@ Nettoyage

clean: ## Nettoyer tous les containers et volumes
	@echo "$(YELLOW)Nettoyage complet...$(NC)"
	$(COMPOSE_DEV) down -v || true
	$(COMPOSE_PROD) --profile monitoring down -v || true
	./mvnw clean || true
	@echo "$(GREEN)✅ Nettoyage terminé$(NC)"

clean-docker: ## Nettoyer les images Docker inutilisées
	@echo "$(YELLOW)Nettoyage des images Docker...$(NC)"
	docker image prune -f
	docker volume prune -f

clean-all: clean clean-docker ## Nettoyage complet (containers + images)
	@echo "$(GREEN)✅ Nettoyage complet terminé$(NC)"

##@ Aide

help: ## Afficher cette aide
	@awk 'BEGIN {FS = ":.*##"; printf "\n$(GREEN)Usage:$(NC)\n  make $(YELLOW)<target>$(NC)\n"} /^[a-zA-Z_-]+:.*?##/ { printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2 } /^##@/ { printf "\n$(GREEN)%s$(NC)\n", substr($$0, 5) } ' $(MAKEFILE_LIST)
	@echo ""
	@echo "$(GREEN)Exemples:$(NC)"
	@echo "  make dev              # Démarrer en mode développement"
	@echo "  make prod             # Démarrer en mode production"
	@echo "  make prod-monitoring  # Production avec monitoring"
	@echo "  make build            # Build l'application"
	@echo "  make test             # Tester l'application"
	@echo "  make logs             # Voir les logs"
	@echo "  make clean            # Tout nettoyer"

.DEFAULT_GOAL := help
