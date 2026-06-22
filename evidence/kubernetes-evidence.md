# Kubernetes Evidence

Aplicacion:
```powershell
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/infrastructure.yaml
kubectl apply -f k8s/microservices.yaml
kubectl apply -f k8s/ingress.yaml
```

Comandos obligatorios:
```powershell
kubectl get pods -o wide
kubectl get svc
kubectl get ingress
kubectl describe ingress logiflow-ingress
kubectl describe deployment ms-auth
kubectl describe deployment ms-pedidos
kubectl logs deployment/ms-auth --tail=100
kubectl logs deployment/ms-pedidos --tail=100
kubectl logs deployment/ms-ruteo --tail=100
kubectl logs deployment/ms-seguimiento --tail=100
kubectl logs deployment/graphql-gateway --tail=100
```

Capturas exactas:
- `kubectl get pods -o wide`: todos los pods en `Running` y `READY 1/1`.
- `kubectl get svc`: servicios `postgres`, `rabbitmq`, `ms-*`, `graphql-gateway`.
- `kubectl get ingress`: `logiflow-ingress` con ADDRESS asignado.
- `kubectl describe ingress logiflow-ingress`: rutas `/api/*`, `/ws`, `/ws/taller.wsdl`, `/ws/seguimiento`, `/graphql`.
- `kubectl describe deployment <nombre>`: seccion `Limits`, `Requests`, `Liveness`, `Readiness`.
- Logs de RabbitMQ: eventos `pedido.creado`, `pedido.cancelado`, `envio.asignado`, `posicion.actualizada`.
