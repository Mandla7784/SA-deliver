# Simple Deployment Script for Learning
#!/bin/bash

echo "🚀 SA-Deliver Kubernetes Learning Deployment"
echo "=========================================="

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed. Please install kubectl first."
    echo "   Visit: https://kubernetes.io/docs/tasks/tools/"
    exit 1
fi

# Check if kubectl can connect to cluster
if ! kubectl cluster-info &> /dev/null; then
    echo "❌ Cannot connect to Kubernetes cluster."
    echo "   Make sure your cluster is running and kubectl is configured."
    echo "   For local learning, try: minikube start"
    exit 1
fi

echo "✅ kubectl is available and connected to cluster"

# Create namespace
echo "📦 Creating namespace..."
kubectl apply -f k8s/simple-deployments.yaml

# Apply services
echo "🌐 Creating services..."
kubectl apply -f k8s/simple-services.yaml

# Apply configmaps
echo "⚙️  Creating configmaps..."
kubectl apply -f k8s/simple-configmaps.yaml

# Apply ingress (optional - only if you have ingress controller)
echo "🔀 Creating ingress..."
kubectl apply -f k8s/simple-ingress.yaml

# Wait for deployments to be ready
echo "⏳ Waiting for deployments to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/backend-deployment -n sa-deliver
kubectl wait --for=condition=available --timeout=300s deployment/frontend-deployment -n sa-deliver

# Show status
echo ""
echo "📊 Deployment Status:"
echo "====================="
kubectl get pods -n sa-deliver
echo ""
kubectl get services -n sa-deliver
echo ""
kubectl get ingress -n sa-deliver

echo ""
echo "🎉 Deployment Complete!"
echo "======================"
echo ""
echo "Access your application:"
echo ""

# Check if running in minikube
if kubectl config current-context | grep -q minikube; then
    echo "🔗 Minikube detected:"
    echo "   Frontend: minikube service frontend-nodeport -n sa-deliver"
    echo "   Backend:  minikube service backend-nodeport -n sa-deliver"
    echo ""
    echo "   Or get URLs:"
    echo "   Frontend: $(minikube service frontend-nodeport -n sa-deliver --url)"
    echo "   Backend:  $(minikube service backend-nodeport -n sa-deliver --url)"
elif kubectl config current-context | grep -q kind; then
    echo "🔗 Kind cluster detected:"
    echo "   Use port-forwarding:"
    echo "   kubectl port-forward service/frontend-service 80:80 -n sa-deliver"
    echo "   kubectl port-forward service/backend-service 8080:8080 -n sa-deliver"
else
    echo "🔗 Access via NodePort services:"
    echo "   Frontend: http://localhost:30000"
    echo "   Backend:  http://localhost:30080"
    echo ""
    echo "🔗 Or use port-forwarding:"
    echo "   kubectl port-forward service/frontend-service 80:80 -n sa-deliver"
    echo "   kubectl port-forward service/backend-service 8080:8080 -n sa-deliver"
fi

echo ""
echo "📚 Learning Commands:"
echo "===================="
echo "  View pods:           kubectl get pods -n sa-deliver"
echo "  View logs:           kubectl logs -f deployment/backend-deployment -n sa-deliver"
echo "  Scale deployment:    kubectl scale deployment backend-deployment --replicas=3 -n sa-deliver"
echo "  Delete everything:   kubectl delete namespace sa-deliver"
echo ""
echo "🎯 Happy Learning!"
