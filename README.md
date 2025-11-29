# Lotlytics - Travel Better, Park Smarter

Visit the site - https://lotlytics.nathanielmartes.com/

View the docs - https://lotlytics-api-docs.pages.dev/

**Lotlytics** is a web application that allows users to create and view parking lots from different services to plan their trips and reduce overall stress.  

Lotlytics aggregates the volume of vehicles in each lot and makes the information readily available using the cloud.  

Edge devices are deployed in parking lots to keep track of vehicles, and the volume data is updated accordingly.  
Users can then view the Lotlytics web application to discover parking lots and get directions to them.

## Running, Building, and Deploying Services

You can test Lotlytics locally with `docker`:
```bash
docker compose up
```

The agent must be started up seperately with `docker` or just locally.

### Main Website `./client`

The main website uses NextJS, so you can use `npm` to run the website in a 
development environment from the `./client` directory:

```bash
npm run dev
```

For building the website, you can use `npm` again:
```bash
npm run build
```

which will store the built website in the `./client/out` directory.
You can also use `docker` if wanted:
```bash
docker compose up
```

Deploying is done using Vercel since this is a NextJS app, so there is not CloudFormation template
for this service.

### Lotlytics API `./api`

The API uses Spring Boot and Maven so you can construct a package `.jar` file for it
by executing in the current directory:
```bash
./mvnw install
./mvnw clean package
```

Running can be done using maven aswell:
```bash
./mvnw spring-boot:run
```

And also docker too:
```bash
docker compose up
```

Project configuration can be viewed at `./api/src/main/resources/application-properties`.
There is also configuration files for `container` and `production` environments which are set by `SPRING_PROFILES_ACTIVE`.

Deploying is done to AWS using the Severless Application Model to deploy a Lambda fucntion for the API:
```bash
sam build --no-use-container
sam deploy \
--stack-name lotlytics-api \
--parameter-overrides \
DatabaseUrl=${LOTLYTICS_DB_ENDPOINT} \
DatabaseUsername=${LOTLYTICS_DB_USERNAME} \
DatabasePassword=${LOTLYTICS_DB_PASSWORD} \
DynamoDBTable=${LOTLYTICS_DB_EVENTS_TABLE_NAME} \
CorsAllowedOrigin=${LOTLYTICS_SITE_ORIGIN}
```

**Note**: The `DatabaseURL` paramter is the full JDBC url that the JDBC API will use to try and connect to the SQL database. If your database does not exist, it will be very, very slow.

### Lotlytics SQL Database `./database/postgres`

Lotlytics uses PostgreSQL by default, by any JDBC applicable database can be used.
Testing the database can be done with docker:
```bash
docker compose up
```
Deploying is done using AWS CloudFormation:
```bash
aws cloudformation deploy \
--stack-name lotlytics-db \
--template ./template.yml \
--parameter-overrides \
DBUser=${LOTLYTICS_DB_USERNAME} \
DBPassword=${LOTLYTICS_DB_PASSWORD} \
VPCCidrBlock=${LOTLYTICS_DB_VPC_CIDR_BLOCK} \
Subnet1CidrBlock=${LOTLYTICS_DB_SUBNET_1_CIDR_BLOCK} \
Subnet2CidrBlock=${LOTLYTICS_DB_SUBNET_2_CIDR_BLOCK}
```

**Note**: This current CloudFormation file creates a database with a **PUBLIC** IP address. For better security, consider setting up a NAT Gateway to access your database and remove the public IP address.

The database can then be initialize using a sql client like `psql` for PostgreSQL:
```bash
PGPASSWORD="${LOTLYTICS_DB_PASSWORD}" psql -h "${LOTLYTICS_DB_DOMAIN}" -U "${LOTLYTICS_DB_USERNAME}" -d lotlytics-db -f init_db.sql 
```
### Lotlytics Events Database `./database/dynamodb`

As of now, Lotlytics is tightly couple to the DynamoDB database. You can test the schema using `docker`:
```bash
docker compose up
```

Deploying is done using AWS CloudFormation:
```bash
aws cloudformation deploy \
--stack-name lotlytics-events-db \
--template ./database/dynamodb/template.yml \
--parameter-overrides \
TableName=${LOTLYTICS_DB_EVENTS_TABLE_NAME}
```

### Lotlytics Edge Device Agent

The edge device agent requires `python3` and some camera device at `/dev/video<X>`.
You can run the device locally using `python3`:
```bash
pip install -r requirements.text
python3 main.py
```

The edge device was not intended to use GPUs for object detection, so the Pytorch's GPU packages are excluded.
If you have a GPU then you can remove:
```
--index-url https://download.pytorch.org/whl/cpu
```

from the `requirements.txt` file.

If you would like to use `docker` you can:
```bash
docker compose up
```

The `docker-compose.yml` file by default mounts `/dev/video0` and `/dev/video1`.

Configuring the agent can be done via the `config.toml` file

#### Agent Configuration

- mode: production or testing

- valid_objects: Objects to be recongized. Objects can added from the COCO dataset https://docs.ultralytics.com/datasets/detect/coco/

- model_path: Where the model is store on the filesystem

- confidence_percentage: Confidence percentage to accept objects at

- keep_alive_frames: Keep alive frames are how many frames are allowed for the object not to be seen before it is forgotten

- minimum_frames_before_detection: Miniumn frames before detection is how many frames before an object is acknowledged

- iou_threshold: IOU Threshold is how different the objects should be before it is consider a new object

- line_start: Line start is the pixel where the first line should start to record volume

- line_gap: Line gap is the gap between the 2 lines. This means that if line 1 is at 100px, and the gap is 500, then line 2 would be at 600px

- entrance_side: Which side of the camera the entrance is on (Entrance and exit sides should be determined from the Camera's POV)

- exit_side: Which side of the camera the exit is on (Entrance and exit sides should be determined from the Camera's POV)

- data_server: Location of where to send data to over https

- group: The Lotlytics group this agent is apart of

- lot: The lot id this agent is apart of

#### Agent MQTT Configuration
The Agent also doubles as an MQTT client. This is for configuration updates only.

- broker_address: The address of the MQTT broker

- broker_port: The port number of the MQTT broker

- topic: The topic for the agent to listen for from the MQTT broker

### Lotlytics API Documentation `./api-docs`

The Lotlytics API Documentation site provides documentation for the API using an OpenAPI spec (`./swagger.yml`) generated from Swagger UI. You can view it with `docker`:
```bash
docker compose up
```

Deploy the docs site is quite simple, you can use a tool such as Redocly to read in an OpenAPI spec and return an HTML file.