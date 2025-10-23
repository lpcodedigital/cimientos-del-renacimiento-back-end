# ==============================================
# 🚀 Makefile Profesional - CimientosDelRenacimientoBackend
# ==============================================

# 🧱 Variables de configuración
SERVICE          := cr-backend
IMAGE_NAME       := cimientos-backend
COMPOSE_BASE     := docker-compose.yml
COMPOSE_DEV      := docker-compose.dev.yml
COMPOSE_PROD     := docker-compose.prod.yml

# 🎨 Colores para los mensajes
GREEN   := \033[0;32m
YELLOW  := \033[1;33m
BLUE    := \033[1;34m
RED     := \033[1;31m
RESET   := \033[0m

# ==============================================
# 🧩 Funciones internas
# ==============================================

# Verifica si Docker está instalado y corriendo
check-docker:
	@command -v docker > /dev/null 2>&1 || (echo "$(RED)❌ Docker no está instalado. Instálalo antes de continuar.$(RESET)" && exit 1)
	@docker info > /dev/null 2>&1 || (echo "$(RED)❌ Docker no está corriendo. Inícialo y vuelve a intentarlo.$(RESET)" && exit 1)
	@echo "$(GREEN)✅ Docker está listo.$(RESET)"

# ==============================================
# 🔧 Comandos principales
# ==============================================

# 🧩 Desarrollo (hot reload, puerto 8081)
dev: check-docker
	@echo "$(BLUE)🚧 Iniciando entorno de desarrollo...$(RESET)"
	@docker compose -f $(COMPOSE_BASE) -f $(COMPOSE_DEV) up


# 🚀 Producción (JAR empaquetado, puerto 8080)
prod: check-docker
	@echo "$(YELLOW)🚀  Iniciando entorno de producción...$(RESET)"
#	@echo "$(YELLOW)🧹  Eliminando contenedores e imágenes anteriores locales y descargadas...$(RESET)"
#	docker compose -f $(COMPOSE_BASE) -f $(COMPOSE_PROD) down -v --rmi all
	@echo "$(RED)🧹  Eliminando contenedores e imágenes anteriores locales...$(RESET)"
	docker compose -f docker-compose.yml -f docker-compose.dev.yml down -v --rmi local
	@echo "$(YELLOW)🔁  Reconstruyendo desde cero sin usar caché...$(RESET)"
	docker compose -f $(COMPOSE_BASE) -f $(COMPOSE_PROD) build --no-cache
	@echo "$(GREEN)🏭  Levantando el entorno de producción...$(RESET)"
	docker compose -f $(COMPOSE_BASE) -f $(COMPOSE_PROD) up
#	@docker compose -f $(COMPOSE_BASE) -f $(COMPOSE_PROD) up --build

# 🧹 Restaurar servicios y contenedores
restart:
	@echo "$(BLUE)🛑 Restaurando servicios y contenedores...$(RESET)"
	@docker compose restart

# 🧹 Detener servicios
stop:
	@echo "$(BLUE)🛑 Deteniendo servicios...$(RESET)"
	@docker compose stop

# 🧹 Detiene y elimina contenedores, servicios, redes, etc
down:
	@echo "$(BLUE)🛑 Deteniendo y eliminando contenedores, servicios, redes etc...$(RESET)"
	@docker compose down

# 🧼 Limpiar todo (general) (contenedores, volúmenes, imágenes huérfanas) 
clean:
	@echo "$(YELLOW)🧹 Iniciando Limpieza de los recursos del contenedor actual...$(RESET)"
	@echo "$(RED)🧹  Deteniendo y eliminando los contenedores, volumenes asociados e imagenes construidas o descargadas que fueron definidos en el compose...$(RESET)"
	@docker compose down -v --rmi all

#	@echo "$(RED)🧹  Deteniendo y eliminando los contenedores, volumenes asociados e imagenes construidas que fueron definidos en el compose...$(RESET)"
#	@docker compose down -v --rmi local

#	@echo "$(RED)🧹  Deteniendo y eliminando los contenedores, imagenes construidas que fueron definidos en el compose...$(RESET)"
#	@docker compose down --rmi local
	@echo "$(GREEN)✅ Limpieza completa.$(RESET)"

# 🧼 Limpiar todo (general) (contenedores, volúmenes, imágenes huérfanas) 
clean-global:
	@echo "$(YELLOW)🧹 Limpiando global de todos los recursos en docker compose...$(RESET)"
	@docker compose down -v --remove-orphans
	@docker system prune -f
	@echo "$(GREEN)✅ Limpieza completa.$(RESET)"

# 🏗️ Recompilar imagen sin caché
rebuild:
	@echo "$(YELLOW)♻️  Recompilando imagen sin caché...$(RESET)"
	@docker compose build --no-cache
	@echo "$(GREEN)✅ Imagen recompilada.$(RESET)"

# 🔍 Ver logs del backend
logs:
	@echo "$(BLUE)📜 Mostrando logs del backend... (Ctrl+C para salir)$(RESET)"
	@docker compose logs -f $(SERVICE)

# 🧪 Entrar al contenedor
shell:
	@echo "$(BLUE)🔧 Accediendo al contenedor $(SERVICE)...$(RESET)"
	@docker exec -it $$(docker ps -qf "name=$(SERVICE)") bash

# ==============================================
# 🧠 Información útil
# ==============================================

help:
	@echo ""
	@echo "$(GREEN)📘 Comandos disponibles:$(RESET)"
	@echo ""
	@echo "$(YELLOW)make dev$(RESET)       - Levanta entorno de desarrollo (hot reload)"
	@echo "$(YELLOW)make prod$(RESET)      - Levanta entorno de producción (JAR)"
	@echo "$(YELLOW)make restart$(RESET)   - Restaura servicios y contenedores"
	@echo "$(YELLOW)make stop$(RESET)      - Detiene contenedores y servicios"
	@echo "$(YELLOW)make down$(RESET)      - Detiene y elimina contenedores, servicios, redes, etc"
	@echo "$(YELLOW)make clean$(RESET)     - Limpia todo (contenedores, volúmenes, red)"
	@echo "$(YELLOW)make rebuild$(RESET)   - Recompila imágenes sin caché"
	@echo "$(YELLOW)make logs$(RESET)      - Muestra logs del backend"
	@echo "$(YELLOW)make shell$(RESET)     - Entra al contenedor del backend"
	@echo "$(YELLOW)make help$(RESET)      - Muestra esta ayuda"
	@echo ""

# ==============================================
# Bonus: alias en tu terminal
# Puedes agregar esto a tu ~/.bashrc o ~/.zshrc:
# alias mk='make -f /ruta/a/tu/proyecto/Makefile'
# Así solo escribes:
# mk dev
# mk prod
# =============================================