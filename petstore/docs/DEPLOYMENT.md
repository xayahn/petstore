# Petstore Deployment Guide

Production deployment instructions for the Petstore peer-to-peer pet marketplace.

**Status**: Phase 1 - Deployment Configuration  
**Last Updated**: 2026-05-05

## 🚀 Deployment Overview

This guide covers deploying Petstore to production using Docker, Kubernetes, and cloud platforms.

### Deployment Checklist

- [ ] Environment variables configured
- [ ] SSL/TLS certificates obtained (Let's Encrypt)
- [ ] Database backup configured
- [ ] Database migrations verified
- [ ] Secrets management setup (AWS Secrets Manager, HashiCorp Vault)
- [ ] Monitoring and logging configured (CloudWatch, DataDog, ELK)
- [ ] Load balancer configured
- [ ] CDN configured (CloudFront, Cloudflare)
- [ ] Rate limiting and WAF enabled
- [ ] Health checks configured
- [ ] Auto-scaling policies defined
- [ ] Disaster recovery plan documented

## 🐳 Docker Deployment

### Build Docker Images

```bash
# Build backend image
docker build -t petstore-backend:1.0.0 ./backend

# Build frontend image
docker build -t petstore-frontend:1.0.0 ./frontend

# Tag for registry
docker tag petstore-backend:1.0.0 <registry>/petstore-backend:1.0.0
docker tag petstore-frontend:1.0.0 <registry>/petstore-frontend:1.0.0

# Push to registry
docker push <registry>/petstore-backend:1.0.0
docker push <registry>/petstore-frontend:1.0.0
```

### Docker Compose Deployment

```bash
# Production docker-compose.yml
docker-compose -f docker-compose.prod.yml up -d

# View logs
docker-compose logs -f backend frontend

# Scale services
docker-compose up -d --scale backend=3
```

## ☸️ Kubernetes Deployment

### Prerequisites

- Kubernetes cluster (EKS, GKE, AKS, or self-managed)
- kubectl CLI configured
- Docker images pushed to registry
- Persistent volume provisioner

### Deployment Files

**backend-deployment.yaml**:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: petstore-backend
  namespace: production
spec:
  replicas: 3
  selector:
    matchLabels:
      app: petstore-backend
  template:
    metadata:
      labels:
        app: petstore-backend
    spec:
      containers:
      - name: backend
        image: registry/petstore-backend:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            secretKeyRef:
              name: db-config
              key: url
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: petstore-backend
  namespace: production
spec:
  type: LoadBalancer
  ports:
  - port: 80
    targetPort: 8080
  selector:
    app: petstore-backend
```

### Deploy to Kubernetes

```bash
# Create namespace
kubectl create namespace production

# Create secrets
kubectl create secret generic db-config \
  --from-literal=url='jdbc:postgresql://...' \
  --from-literal=username='...' \
  --from-literal=password='...' \
  -n production

# Deploy
kubectl apply -f backend-deployment.yaml
kubectl apply -f frontend-deployment.yaml

# Check status
kubectl get deployments -n production
kubectl get pods -n production
kubectl get svc -n production

# View logs
kubectl logs -f deployment/petstore-backend -n production

# Update deployment (rolling update)
kubectl set image deployment/petstore-backend backend=registry/petstore-backend:2.0.0 -n production
```

## ☁️ Cloud Platform Deployment

### AWS Deployment

**Option 1: ECS (Elastic Container Service)**
- Push images to ECR (Elastic Container Registry)
- Create ECS task definitions
- Create ECS services with auto-scaling
- Configure Application Load Balancer (ALB)
- Use RDS for PostgreSQL

**Option 2: EKS (Elastic Kubernetes Service)**
- Use EKS cluster
- Push images to ECR
- Deploy using kubectl
- Use RDS PostgreSQL
- Configure ALB with ingress controller

**Example: ALB + Auto Scaling**
```bash
# Create Application Load Balancer
aws elbv2 create-load-balancer \
  --name petstore-alb \
  --subnets subnet-1 subnet-2

# Create target group
aws elbv2 create-target-group \
  --name petstore-backend \
  --protocol HTTP \
  --port 8080 \
  --target-type instance

# Create auto-scaling group
aws autoscaling create-auto-scaling-group \
  --auto-scaling-group-name petstore-asg \
  --launch-configuration-name petstore-lc \
  --min-size 2 \
  --max-size 10 \
  --desired-capacity 3
```

### Google Cloud (GCP) Deployment

- **Cloud Run**: Serverless containers (for frontend)
- **Cloud SQL**: Managed PostgreSQL
- **Compute Engine**: VMs with custom Docker images
- **GKE**: Kubernetes cluster
- **Cloud Load Balancing**: Global load balancer

### Microsoft Azure Deployment

- **Container Instances**: Serverless containers
- **App Service**: Managed hosting
- **Azure Kubernetes Service (AKS)**: Kubernetes
- **Azure Database for PostgreSQL**: Managed database
- **Application Gateway**: Load balancer

## 🔒 Security Hardening

### HTTPS/TLS

```bash
# Obtain SSL certificate (Let's Encrypt)
certbot certonly --standalone -d petstore.com

# Configure Nginx to use certificate
server {
  listen 443 ssl;
  server_name petstore.com;
  
  ssl_certificate /etc/letsencrypt/live/petstore.com/fullchain.pem;
  ssl_certificate_key /etc/letsencrypt/live/petstore.com/privkey.pem;
  
  ssl_protocols TLSv1.2 TLSv1.3;
  ssl_ciphers HIGH:!aNULL:!MD5;
}
```

### Environment Variables & Secrets

Use secrets management instead of .env files:

```bash
# AWS Secrets Manager
aws secretsmanager create-secret --name petstore/jwt-secret --secret-string "..."

# Retrieve in app
java -Dspring.config.import=aws-secretsmanager://petstore/jwt-secret
```

### Rate Limiting & WAF

```yaml
# Configure rate limiting at ALB
LimitRequests:
  RateLimit: 1000  # 1000 requests per minute
  Action: BLOCK

# Enable AWS WAF
- Rule: SQLi (SQL Injection)
  Action: BLOCK
- Rule: XSS (Cross-Site Scripting)
  Action: BLOCK
```

## 📊 Monitoring & Logging

### CloudWatch (AWS)

```bash
# Push logs to CloudWatch
aws logs create-log-group --log-group-name /petstore/backend
aws logs put-metric-alarm \
  --alarm-name high-error-rate \
  --metric-name ErrorRate \
  --threshold 5 \
  --comparison-operator GreaterThanThreshold
```

### Datadog

```yaml
# datadog-agent config
apiVersion: v1
kind: ConfigMap
metadata:
  name: datadog-agent-config
data:
  datadog.yaml: |
    api_key: ${DATADOG_API_KEY}
    app_key: ${DATADOG_APP_KEY}
    hostname: petstore-backend
    logs_enabled: true
    apm_enabled: true
    apm_config:
      enabled: true
      apm_non_local_traffic: true
      receiver_port: 8126
```

### ELK Stack (Elasticsearch, Logstash, Kibana)

```yaml
# Logback config for JSON logging
<appender name="logstash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
  <destination>logstash.example.com:5000</destination>
</appender>
```

## 🔄 CI/CD Pipeline

### GitHub Actions Example

```yaml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Build Docker images
        run: |
          docker build -t petstore-backend:${{ github.sha }} ./backend
          docker build -t petstore-frontend:${{ github.sha }} ./frontend
      
      - name: Push to ECR
        run: |
          aws ecr get-login-password | docker login --username AWS --password-stdin $ECR_REGISTRY
          docker push $ECR_REGISTRY/petstore-backend:${{ github.sha }}
      
      - name: Deploy to EKS
        run: |
          kubectl set image deployment/petstore-backend backend=$ECR_REGISTRY/petstore-backend:${{ github.sha }}
```

## 🆘 Troubleshooting Deployment

### Container won't start

```bash
# Check logs
docker logs <container-id>

# Check environment variables
docker inspect <container-id> | grep -A 20 Env

# Test locally
docker run -it petstore-backend:latest /bin/bash
```

### Database connection issues

```bash
# Test connection
pg_isready -h db.example.com -U postgres

# Check credentials
echo "select 1;" | psql -h db.example.com -U postgres -d petstore
```

### Load balancer health checks failing

```bash
# Test health endpoint
curl http://localhost:8080/actuator/health

# Check security groups allow traffic
aws ec2 describe-security-groups --query 'SecurityGroups[0].IpPermissions'
```

## 📝 Backup & Disaster Recovery

### Database Backup

```bash
# Automated daily backups
0 2 * * * pg_dump -U postgres petstore > /backups/petstore-$(date +\%Y\%m\%d).sql

# S3 backup (AWS)
aws s3 sync /backups/ s3://petstore-backups/ --delete

# Restore from backup
psql -U postgres < petstore-20260505.sql
```

### Application Disaster Recovery

- **RTO** (Recovery Time Objective): < 1 hour
- **RPO** (Recovery Point Objective): < 15 minutes
- Multi-region deployment (future)
- Database replicas in different availability zones

## 📈 Scaling Strategy

### Horizontal Scaling

```bash
# Auto-scaling policy
TargetTrackingScaling:
  TargetValue: 70  # Target 70% CPU utilization
  ScaleUpThreshold: 80
  ScaleDownThreshold: 40
  MinInstances: 2
  MaxInstances: 10
```

### Vertical Scaling

```yaml
# Increase resource limits
resources:
  limits:
    memory: "2Gi"
    cpu: "2000m"
```

## 🔍 Performance Tuning

### Database Optimization

```sql
-- Enable query statistics
ALTER SYSTEM SET log_min_duration_statement = 100;  -- Log slow queries >100ms
SELECT pg_reload_conf();

-- Analyze tables
VACUUM ANALYZE;

-- Update statistics
ANALYZE users;
```

### Application Tuning

```java
// Increase connection pool
spring.datasource.hikari.maximum-pool-size=30

// Cache warm-up
@PostConstruct
public void warmupCache() {
  List<Pet> popularpets = petService.getPopular(1000);
}
```

---

## 📚 Related Documents

- **[ARCHITECTURE.md](./ARCHITECTURE.md)** - System architecture
- **[DATABASE.md](./DATABASE.md)** - Database configuration
- **[backend/README.md](../backend/README.md)** - Backend configuration

---

**Last Updated**: 2026-05-05  
**Deployment Version**: 1.0.0  
**Platforms Supported**: AWS, GCP, Azure, Kubernetes, Docker Compose
