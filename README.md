**Kafka**

./kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic second_topic<br/>
./kafka-server-stop.sh

**Start Kafka Cluster**

./zookeeper-server-start.sh ../config/zookeeper.properties &<br/>
./kafka-server-start.sh ../config/server.properties &

**List topics**

./kafka-topics.sh --list --bootstrap-server localhost:9092<br/>

**Delete topic**

./kafka-topics.sh --bootstrap-server localhost:9092 --topic settlements --delete<br/>
./kafka-topics.sh --bootstrap-server localhost:9092 --topic settlement.public.settlement --delete<br/>

**Consumer groups**

./kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group payment-order --describe<br/>

**Consume logs**

./kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic settlements<br/> | jq

**Consumer group offset update**

./kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group payment-statement --topic settlements --reset-offsets --to-offset 1<br/>

**Kafka Connect**

curl -i -X POST -H "Accept:application/json" -H"Content-Type:application/json" http://localhost:8083/connectors/ -d@postgres-connector.json<br/>

**Delete connector**

curl -X DELETE localhost:8083/connectors/settlement-connector<br/>
curl localhost:8083/connectors/<br/>
curl -s localhost:8083/connectors/settlement-connector/status | jq<br/>
curl -s localhost:8083/connectors/settlement-connector/status | jq -c -M '[.name,.connector.state,.tasks[].state]|join(":|:")'<br/>
curl -s localhost:8083/connectors/settlement-connector/status | jq -c -M '[.name,.connector.state,.tasks[].state]|join(":|:")' | jq<br/>

**Log count in specific topic**

kafkacat -b localhost:9092 -t settlement.public.settlement -p 0 -o -1 -e<br/>
lsof -nP -i4TCP:2181 | grep LISTEN<br/>
curl -i -X POST -H "Accept:application/json" -H"Content-Type:application/json" http://localhost:8083/connectors/ -d@postgres-connector.json<br/>

**set offset with timestamp**

./kafka-consumer-groups.sh --bootstrap-server 10.10.43.49:9092 --group settlement-api --reset-offsets --topic settlements --to-datetime 2020-01-13T04:25:00.000<br/>
./kafka-consumer-groups.sh --bootstrap-server 10.250.221.72:9092 --group kafka2kafka --reset-offsets --topic claim-events-topic --to-datetime 2020-01-13T01:40:00.000 --execute<br/>