# Lotlytics – Travel Better, Park Smarter

[![Last Commit](https://img.shields.io/github/last-commit/NateMartes/lotlytics)](https://github.com/NateMartes/lotlytics/commits)
[![License](https://img.shields.io/github/license/NateMartes/lotlytics)](LICENSE)
![Next.js](https://img.shields.io/badge/Next.js-000?logo=next.js&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=spring-boot&logoColor=white)
![Python](https://img.shields.io/badge/Python-3776AB?logo=python&logoColor=white)
| Live demo | API docs | API endpoint |
|-----------|----------|--------------|
| [lotlytics.nathanielmartes.com](https://lotlytics.nathanielmartes.com) | [lotlytics-api-docs.pages.dev](https://lotlytics-api-docs.pages.dev) | [lotlytics-api.nathanielmartes.com](https://lotlytics-api.nathanielmartes.com) |

Lotlytics is a cloud-native parking-intelligence platform.  
Edge devices count vehicles in real time; the web app shows live availability and turn-by-turn directions so drivers can plan trips and skip the circling.

---

## Quick Start (local)

```bash
git clone https://github.com/NateMartes/Lotlytics.git
cd Lotlytics
docker compose up        # spins up client, api, postgres, dynamodb-local
```

The edge-agent is packaged separately—run it locally or in its own container.

---

## Service Overview

| Service | Tech stack | Local dev | Build artifact | Deploy target |
|---------|------------|-----------|----------------|---------------|
| **Client** | Next.js | `npm run dev` | `npm run build` → `./out` | Vercel |
| **API** | Spring Boot | `./mvnw spring-boot:run` | `./mvnw package` → `.jar` | AWS Lambda (SAM) |
| **SQL DB** | PostgreSQL | `docker compose up` | n/a | RDS (CloudFormation) |
| **Events DB** | DynamoDB | `docker compose up` | n/a | DynamoDB (CloudFormation) |
| **Edge Agent** | Python 3 + YOLOv8 | `python3 main.py` | Docker image | Device or ECS |
| **API Docs** | Swagger UI / Redoc | `docker compose up` | static HTML | Any static host |

---

## 1. Client (`./client`)

```bash
npm i
npm run dev        # http://localhost:3000
npm run build      # export to ./out
```

Deploys to Vercel; no CloudFormation needed.

---

## 2. API (`./api`)

```bash
./mvnw clean package
java -jar target/lotlytics-*.jar
```

SAM deployment:

```bash
sam build --no-use-container
sam deploy --stack-name lotlytics-api \
  --parameter-overrides \
    DatabaseUrl=$LOTLYTICS_DB_ENDPOINT \
    DatabaseUsername=$LOTLYTICS_DB_USERNAME \
    DatabasePassword=$LOTLYTICS_DB_PASSWORD \
    DynamoDBTable=$LOTLYTICS_DB_EVENTS_TABLE_NAME \
    CorsAllowedOrigin=$LOTLYTICS_SITE_ORIGIN
```

**Notes**  
- `DatabaseUrl` must be a full JDBC URL; if the DB does not exist the first start is very slow.  
- First deploy creates an API-Gateway domain—add the supplied CNAME to your DNS. You can find this domain name in the AWS console for instance.

---

## 3. PostgreSQL (`./database/postgres`)

Local:

```bash
docker compose up
```

Production (public IP **not** recommended):

```bash
aws cloudformation deploy \
  --stack-name lotlytics-db \
  --template ./template.yml \
  --parameter-overrides \
    DBUser=$LOTLYTICS_DB_USERNAME \
    DBPassword=$LOTLYTICS_DB_PASSWORD \
    VPCCidrBlock=$LOTLYTICS_DB_VPC_CIDR_BLOCK \
    Subnet1CidrBlock=$LOTLYTICS_DB_SUBNET_1_CIDR_BLOCK \
    Subnet2CidrBlock=$LOTLYTICS_DB_SUBNET_2_CIDR_BLOCK
```

Initialise schema:

```bash
PGPASSWORD=$LOTLYTICS_DB_PASSWORD \
psql -h $LOTLYTICS_DB_DOMAIN -U $LOTLYTICS_DB_USERNAME -d lotlytics-db -f init_db.sql
```

**Security**  
Put the RDS instance in private subnets + NAT Gateway for production.

---

## 4. DynamoDB Events Table (`./database/dynamodb`)

```bash
aws cloudformation deploy \
  --stack-name lotlytics-events-db \
  --template ./database/dynamodb/template.yml \
  --parameter-overrides TableName=$LOTLYTICS_DB_EVENTS_TABLE_NAME
```

---

## 5. Edge Agent (`./agent`)

Requirements: Python ≥3.8, camera at `/dev/video0`.

```bash
pip install -r requirements.txt
python3 main.py
```

Docker (mounts `/dev/video0` and `/dev/video1`):

```bash
docker compose up
```

### Configuration (`config.toml`)

| Key | Purpose |
|-----|---------|
| `mode` | `production` or `testing` |
| `valid_objects` | COCO classes to track (default: `["car", "truck", "bus"]`) |
| `confidence_percentage` | Detection threshold |
| `keep_alive_frames` | Frames before object forgotten |
| `minimum_frames_before_detection` | Frames before object acknowledged |
| `iou_threshold` | Min IOU to merge boxes |
| `line_start`, `line_gap` | Virtual counter lines |
| `entrance_side`, `exit_side` | `top`, `bottom`, `left`, `right` (camera POV) |
| `data_server` | HTTPS endpoint for counts |
| `group`, `lot` | Lotlytics identifiers |

### MQTT (optional)

| Key | Purpose |
|-----|---------|
| `broker_address` | MQTT host |
| `broker_port` | MQTT port |
| `topic` | Command topic (agent subscribes) |

---

## 6. API Documentation (`./api-docs`)

```bash
docker compose up   # serves Swagger UI on localhost:6601
```

Generate a static site:

```bash
npx @redocly/cli build-docs swagger.yml
```

Host the resulting `redoc-static.html` anywhere.

---

## Environment Variables

```bash
LOTLYTICS_DB_ENDPOINT
LOTLYTICS_DB_USERNAME
LOTLYTICS_DB_PASSWORD
LOTLYTICS_DB_EVENTS_TABLE_NAME
LOTLYTICS_SITE_ORIGIN
LOTLYTICS_DB_VPC_CIDR_BLOCK
LOTLYTICS_DB_SUBNET_1_CIDR_BLOCK
LOTLYTICS_DB_SUBNET_2_CIDR_BLOCK
```

---

## Common Pitfalls

1. **“500 when adding users to groups”**  
   Ensure the `MEMBER` role exists within the database `roles` table.

2. **First API request hangs**  
   Double-check the JDBC URL; Spring will retry for minutes if the DB is unreachable. The Lambda function default timeout is ~5 seconds. Your database should exists before making any API requests.

3. **Agent not counting**  
   Verify the virtual counter lines overlap vehicle paths; adjust `line_start`/`line_gap`.

4. **Public RDS**  
   The supplied CloudFormation template creates a publicly accessible instance—lock it down before going live.

---