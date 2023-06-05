default: help;

FRMT_NORM=\033[0m
FRMT_INVRS=\033[7m

.PHONY: help
help: ## Информация о доступных командах
	@egrep -h '\s##\s' Makefile | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'


.PHONY: up
up: ## Запустить сервер
	@echo "${FRMT_INVRS} Запуск сервера... ${FRMT_NORM}"
	docker-compose up -d


.PHONY: down
down: ## Остановить сервер
	@echo "${FRMT_INVRS} Остановка сервера... ${FRMT_NORM}"
	docker-compose down


.PHONY: build
build: ## Собрать проект
	@echo "${FRMT_INVRS} Сборка проекта... ${FRMT_NORM}"
	docker-compose build

.PHONY: deploy
deploy: ## Деплой проекта
	@echo "${FRMT_INVRS} Деплой проекта... ${FRMT_NORM}"
	git pull
	make build
	make up
