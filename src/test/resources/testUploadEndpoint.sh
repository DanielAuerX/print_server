#!/bin/bash

FILE_PATH="./testfile.pdf"
DESTINATION_PATH="/tmp/upload/"

if [[ ! -f "$FILE_PATH" ]]; then
    echo "Error: File $FILE_PATH does not exist"
    exit 1
fi

curl -X POST http://localhost:8080/api/v1/upload \
     -F "file=@$FILE_PATH"

echo ""
echo "checking $DESTINATION_PATH"
ls "$DESTINATION_PATH"