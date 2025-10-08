aws dynamodb create-table \
    --table-name Events \
    --attribute-definitions AttributeName=id,AttributeType=N \
    --key-schema AttributeName=id,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --endpoint-url http://lotlyics-db-events:8000 \
    --region us-east-1

echo "'Events' Table Created in Lotlyics_DB_Events"