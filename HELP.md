# Getting Started

### Reference Documentation

After running the docker-compose. 
Run the following command to create the queues.

aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name vnoxiaene_pagamentos_parciais
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name vnoxiaene_pagamentos_totais
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name vnoxiaene_pagamentos_excedentes
