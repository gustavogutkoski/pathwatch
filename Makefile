COMPOSE := docker compose -f infra/docker-compose.yml
TF      := terraform -chdir=terraform

export GRAFANA_URL  ?= http://localhost:3000
export GRAFANA_AUTH ?= admin:admin

.DEFAULT_GOAL := help
.PHONY: help up down clean ps logs prom-reload tf-init tf-plan tf-apply traffic

help: ## Show available commands
	@grep -E '^[a-zA-Z_-]+:.*?## ' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-12s\033[0m %s\n", $$1, $$2}'

up: ## Build and start the local stack
	$(COMPOSE) up -d --build

down: ## Stop the stack (keeps data volumes)
	$(COMPOSE) down

clean: ## Stop the stack and delete all data volumes
	$(COMPOSE) down -v

ps: ## Show running containers
	$(COMPOSE) ps

logs: ## Follow logs (usage: make logs s=event-catalog)
	$(COMPOSE) logs -f $(s)

prom-reload: ## Reload Prometheus config without restarting
	$(COMPOSE) kill -s SIGHUP prometheus

tf-init: ## Initialize Terraform
	$(TF) init

tf-plan: ## Preview Terraform changes
	$(TF) plan

tf-apply: ## Apply Terraform changes
	$(TF) apply

traffic: ## Send sample traffic to event-catalog
	@for i in $$(seq 1 100); do \
		curl -s -o /dev/null localhost:8081/events; \
		curl -s -o /dev/null localhost:8081/events/evt-001; \
	done
	@echo "Sent 200 requests to event-catalog"