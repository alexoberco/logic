#!/usr/bin/env bash
set -e

# Función para gestionar errores
function error_exit {
    echo "[ERROR] $1" 1>&2
    exit 1
}

echo "=== Instalando kubectl ==="
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" || error_exit "Error descargando kubectl."
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl || error_exit "Error instalando kubectl."

echo "=== Instalando minikube ==="
curl -LO https://github.com/kubernetes/minikube/releases/latest/download/minikube-linux-amd64 || error_exit "Error descargando minikube."
sudo install minikube-linux-amd64 /usr/local/bin/minikube || error_exit "Error instalando minikube."
rm minikube-linux-amd64

echo "=== Instalando Helm ==="
curl https://baltocdn.com/helm/signing.asc | gpg --dearmor | sudo tee /usr/share/keyrings/helm.gpg > /dev/null || error_exit "Error descargando la llave de Helm."
sudo apt-get install apt-transport-https --yes || error_exit "Error instalando apt-transport-https."
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/helm.gpg] https://baltocdn.com/helm/stable/debian/ all main" | sudo tee /etc/apt/sources.list.d/helm-stable-debian.list || error_exit "Error añadiendo repositorio de Helm."
sudo apt-get update || error_exit "Error actualizando apt."
sudo apt-get install helm -y || error_exit "Error instalando Helm."

echo "=== Iniciando Minikube ==="
minikube start --driver=docker || error_exit "Error iniciando minikube."

echo "=== Configurando repositorios Helm ==="
helm repo add startechnica https://startechnica.github.io/apps || error_exit "Error añadiendo el repo startechnica."
helm repo update || error_exit "Error actualizando repositorios (startechnica)."

helm repo add bitnami https://charts.bitnami.com/bitnami || error_exit "Error añadiendo el repo bitnami."
helm repo update || error_exit "Error actualizando repositorios (bitnami)."

echo "=== Desplegando Adminer ==="
helm upgrade --install adminer startechnica/adminer \
    --namespace postgress \
    --create-namespace \
    --set service.port=8081 || error_exit "Error instalando Adminer."

echo "=== Desplegando PostgreSQL ==="
helm upgrade --install my-postgres bitnami/postgresql \
    --namespace postgress \
    --create-namespace \
    --set auth.username=admin \
    --set auth.password=admin \
    --set auth.database=baseTaller \
    --set primary.persistence.size=1Gi \
    --values init-values.yaml || error_exit "Error instalando PostgreSQL."

echo "=== Desplegando RabbitMQ ==="
helm upgrade --install my-rabbitmq bitnami/rabbitmq \
    --namespace postgress \
    --create-namespace \
    --set auth.username=guest \
    --set auth.password=guest || error_exit "Error instalando RabbitMQ."

echo "=== Esperando a que RabbitMQ esté presente ==="

# Definir un timeout e intervalo de chequeo
timeout=300
interval=10
elapsed=0

while true; do
  # Obtener la cantidad de pods con la etiqueta "app.kubernetes.io/name=rabbitmq"
  podCount=$(kubectl get pods -n postgress -l "app.kubernetes.io/name=rabbitmq" --no-headers 2>/dev/null | wc -l)
  if [ "$podCount" -gt "0" ]; then
    echo "Pod de RabbitMQ detectado. Esperando a que esté listo..."
    break
  fi
  sleep $interval
  elapsed=$(( elapsed + interval ))
  if [ $elapsed -ge $timeout ]; then
    error_exit "No se detectaron pods de RabbitMQ en $timeout segundos."
  fi
done

# Ahora espera a que los pods estén listos
kubectl wait --for=condition=Ready pod -l "app.kubernetes.io/name=rabbitmq" -n postgress --timeout=300s || error_exit "RabbitMQ no está listo."
echo "=== Opción de instalación de la capa lógica ==="
# Se pregunta si se desea instalar la aplicación usando el Helm Chart
read -t 5 -p "¿Deseas instalar la capa lógica mediante el Helm Chart? [y/N]: " helmAnswer || helmAnswer="n"
echo ""

if [[ "$helmAnswer" =~ ^[Yy]$ ]]; then
    echo "Instalando la capa lógica utilizando el Helm Chart..."
    # Cambiamos al directorio del Helm Chart (asumiendo que se encuentra en ./helm-chart)
    cd helm-chart || error_exit "No se pudo acceder a la carpeta helm-chart."
    # Instalar el chart; puedes ajustar el nombre de release y los parámetros según tus necesidades
    helm upgrade --install my-quarkus-app . --namespace postgress --create-namespace || error_exit "Error instalando el Helm Chart de la capa lógica."
    cd ..
else
    echo "Instalando la capa lógica utilizando los YAMLs (instalación normal)..."
    kubectl apply -f k8s/ || error_exit "Error aplicando YAMLs en k8s/."
fi

echo "=== Esperando a que el Deployment de Quarkus esté listo ==="
kubectl rollout status deployment/quarkus-app -n postgress || error_exit "Rollout del Deployment de Quarkus falló."

echo "=== Despliegue completado exitosamente ==="
echo "Acceso a la aplicación Quarkus: kubectl port-forward -n postgress svc/quarkus-app 8080:8080"
echo "Acceso a Adminer: kubectl port-forward -n postgress svc/adminer 8081:8080"
echo "Acceso a RabbitMQ Management: kubectl port-forward -n postgress svc/my-rabbitmq 15672:15672"
