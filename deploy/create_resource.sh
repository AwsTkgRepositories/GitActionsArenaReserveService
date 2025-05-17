# EventBridgeルールを作成
# aws events put-rule --name "ArenaReservableSearchServiceShinagawa-trigger" --schedule-expression "cron(30 0 * * ? *)" --region ap-northeast-1

# EventBridgeルールのターゲットを指定
# aws events put-targets --rule "ArenaReservableSearchServiceShinagawa-trigger" --targets "Id"="1","Arn"="arn:aws:ecs:ap-northeast-1:536697254314:cluster/arena-reserve-cluster","RoleArn"="arn:aws:iam::536697254314:role/ecsEventsRole","EcsParameters"={\"TaskDefinitionArn\"=\"arn:aws:ecs:ap-northeast-1:536697254314:task-definition/arena-reserve-task\",\"LaunchType\"=\"FARGATE\",\"NetworkConfiguration\"={\"awsvpcConfiguration\"={\"Subnets\"=[\"subnet-xxxxxxxx\"],\"AssignPublicIp\"=\"ENABLED\"}}}

# ECSクラスターを作成
# aws ecs create-cluster --cluster-name arena-reserve-cluster --region ap-northeast-1

# ロググループを作成
# aws logs create-log-group --log-group-name /ecs/arena-reserve-service

# ECSタスクを起動
# aws ecs run-task \
#   --cluster arena-reserve-cluster \
#   --launch-type FARGATE \
#   --network-configuration 'awsvpcConfiguration={subnets=["<Subnet-ID>"],securityGroups=["<SG-ID>"],assignPublicIp="ENABLED"}' \
#   --task-definition arena-reserve-service-task
