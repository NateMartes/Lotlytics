aws dynamodb create-table \
    --table-name Events \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
        AttributeName=lotId,AttributeType=N \
        AttributeName=capturedAt,AttributeType=S \
        AttributeName=groupId,AttributeType=S \
    --key-schema \
        AttributeName=id,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --global-secondary-indexes '[
        {
            "IndexName": "LotCapturedAtIndex",
            "KeySchema": [
                {"AttributeName": "lotId", "KeyType": "HASH"},
                {"AttributeName": "capturedAt", "KeyType": "RANGE"}
            ],
            "Projection": {
                "ProjectionType": "ALL"
            }
        },
        {
            "IndexName": "GroupCapturedAtIndex",
            "KeySchema": [
                {"AttributeName": "groupId", "KeyType": "HASH"},
                {"AttributeName": "capturedAt", "KeyType": "RANGE"}
            ],
            "Projection": {
                "ProjectionType": "ALL"
            }
        }
    ]' \
    --endpoint-url http://lotlyics-db-events:8000 \
    --region us-east-1

echo "'Events' Table Created in Lotlyics_DB_Events"