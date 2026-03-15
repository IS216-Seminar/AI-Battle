COMPOSE ?= podman compose
ENV_FILE ?= .env

.PHONY: up down logs format help

dev up:
	$(COMPOSE) up -d

dev down:
	$(COMPOSE) down

up: ## Start containers in detached mode (using $(ENV_FILE))
	$(COMPOSE) --env-file $(ENV_FILE) up -d --build

down: ## Stop and remove containers (using $(ENV_FILE))
	$(COMPOSE) --env-file $(ENV_FILE) down

logs: ## Tail container logs
	$(COMPOSE) logs -f --tail=200

format: ## Format Java source code
	mvn -q net.revelc.code.formatter:formatter-maven-plugin:2.24.1:format

help: ## Show available commands
	@printf "Available commands:\n"
	@grep -E '^[a-zA-Z_-]+:.*?## ' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-10s %s\n", $$1, $$2}'
