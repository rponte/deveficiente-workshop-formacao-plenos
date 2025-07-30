# Messaging

## Tópico 1: Introdução a processamento assíncrono
O que vocês entendem por “assíncrono”?

1. Discutir com os alunos sobre o entendimento que eles tem sobre processamento assincrono de forma geral;
2. A ideia é estabelecer uma definição compartilhada e (quase) única durante toda a aula;
3. Quero pontuar que falaremos sobre async na perspectiva de mensageria e sistemas distribuidos;

## Tópico 2: Introdução a mensageria com brokers
Discutir sobre os modelos de comunicação "sync vs. async", entendendo producers, consumers e brokers. Tipos de mensageria, como point-to-point e pub/sub.


1. Revisitando o modelo de comunicação Request-Response (RPC);
2. Entendendo o problema de disponibilidade e acoplamento temporal entre serviços;
3. Sugerindo um modelo de comunicação async e eliminando o acoplamento temporal;
4. Conhecendo os atores no modelo de comunicação async com mensageria: broker, producer e consumer;
5. Conhecendo os tipos/patterns de comunicação async com mensageria
    - 5.1. Point-to-Point (P2P): Queues;
    - 5.2. Publisher-Subscriber (Pub/Sub): Topics;
6. Brokers e suas peculiaridades
    - 6.1. ActiveMQ: queues e topics;
    - 6.2. SQS e SNS: queus e topics;
    - 6.3. Kakfa: topics only;
    - 6.4. RabbitMQ: exchanges, queues e topics;
    - 6.5. Explorando RabbitMQ com [RabbitMQ Simulator](https://tryrabbitmq.com/);

## Tópico 3: Lidando com perdas de mensagens
Fire and Forget e At-Most Once delivery

1. Confirmação de processamento/recebimento
    - 1.1. Modelo sync com HTTP;
    - 1.2. Modelo async com mensageria (ACKs);
2. Perdendo mensagens
    - 2.1. Modelo sync com HTTP;
    - 2.2. Modelo async no lado producer;
    - 2.3. Modelo async no lado consumer;
    - 2.4. (?) Modelo async no lado broker: in-memory, restarts, crashes; persistent but short retention; persistent but no replication;
3. Rotulando essa semântica de comunicação
    - 3.1. **At-Most Once Delivery**;
    - 3.2. Geralmente implementada via Fire and Forget;
    - 3.3. Quando ela é interessante?
    - 3.4. E se eu precisar garantir a entrega?
4. Maximizando as garantias de entrega
    - 4.1. Lado producer: esperando o ACK do broker;
    - 4.2. Lado consumer: enviando o ACK pro broker após fim do processamento;
    - 4.3. Lado producer e consumer: adicionando **retries** na equação;

## Tópico 4: Lidando com mensagens duplicadas
Retry no lado producer, retry no lado consumer, idempotência e At-Least Once delivery;

1. Garantimos a entrega, mas a que custo?
2. O que acontece se eu retentar? Mensagens duplicadas 😱
    - 2.1. No lado producer: mensagens duplicadas na fila;
    - 2.2. No lado consumer: processamento executado múltiplas vezes;
2. Rotulando essa semântica de comunicação
    - 2.1. **At-Least Once Delivery**;
    - 2.2. Essa semântica ocorre sempre que há re-tentativas;
    - 2.3. Quando ela é interessante?
    - 2.4. Como evitar os efeitos colaterais?
3. Lidando com os efeitos colaterais de mensagens duplicadas
    - 3.1. No lado producer: de-duplication;
    - 3.2. No lado consumer: de-duplication e/ou idempotência
    - 3.3. No lado broker  : de-duplication;
    - 3.4. Ou ignora e deixa quebrar. Resolve-se operacionalmente com ser humano, batch processing etc;
4. Refletindo sobre At-Least Once Delivery
    - 4.1. A rede não é confiável;
    - 4.2. É o padrão na maioria dos brokers;
    - 4.3. Podemos assumir que todo serviço ou sistema pode (eventualmente) receber requests, eventos ou mensagens duplicadas;
    - 4.4. Todo endpoint DEVERIA ser tolerante a falhas via idempotência;

## Tópico 5: Lidando com Dual writes
Escrevendo em dois sistemas diferentes ao mesmo tempo, garantindo consistência com Outbox pattern;

1. O que acontece quando o microsserviço precisa escrever em 2 sistemas ao mesmo tempo, como em um banco local e em uma fila do broker?
2. Entendendo o problema
    - 2.1. Ausência de escrita atômica;
    - 2.2. Problema conhecido como **Dual write**;
    - 2.3. Uso de (stateless) retries resolve o problema?
3. Transactional Outbox Pattern
    - 3.1. E se escrevessemos SOMENTE em um único sistema? Mas qual? E por que?
    - 3.2. Abraçando transações atômicas e propriedades ACID do seu banco de dados;
    - 3.3. Implementação comum: resolvendo com uma tabela + background job;
4. Refletindo sobre Outbox pattern
    - 4.1. Acabamos com a escrita dupla? Não, nós apenas movemos ela para outra parte do sistema;
    - 4.2. Tolerância a falhas está na combinação de uma fila no banco com stateful retries;
    - 4.3. Preciso sempre do Outbox pattern? Não, mas tenha clareza dos seus requisitos de garantia de entrega;
5. Discutindo outras possíveis soluções
    - 5.1. Outbox pattern com CDC (Change-Data Capture);
    - 5.2. Listen-to-yourself pattern;
    - 5.3. Event sourcing;
    - 5.4. Two-Phase Commit (2PC), XA Transactions and Saga;
    

## Tópico 6: Lidando com races conditions
Melhorando throughput com Competing Consumers pattern e lidando com race conditions;

1. E se 1 único consumidor não for rápido o suficiente?
    - 1.1. Trabalhando com múltiplos consumers;
    - 1.2. Também conhecido como **Competing Consumers Pattern**;
2. O que ganhamos ao ter múltiplos consumidores:
    - 2.1. Alta disponibilidade;
    - 2.2. Maior throughput no processamento das mensagens;
3. O que perdemos? Quais os trade-offs?
    - 3.1. Complexidade arquiterural e infraestrutura, custo de cloud etc;
    - 3.2. Race conditions;
    - 3.3. Perda na ordem das mensagens (sútil 👀);
4. Como resolvemos o problema de race conditions?
    - 4.1. Estratégias de locking, constraints, isolation level, distributed locks, partitioning/sharding etc;
5. Como resolvemos o problema de out-of-order messages?
    - 5.1. Não importa, pois ordem não critica para meu contexto (Best-Effort Ordering);
    - 5.1. E se meu sistema precisar de **ordem estrita** das mensagens (Strict Ordering)?
    

## Tópico 7: Lidando com mensagens fora de ordem
Strict Ordering, SQS FIFO, single-threaded producer, single-threaded consumer, aceitando menor throughput;

1. Diferenciando garantias de ordem:
    1. **Best-Effort Ordering**: A ordem pode ser tentada, mas não é garantida — especialmente em presença de falhas, múltiplos consumidores, retries ou reordenação de rede;
    2. **Strict Ordering**: A ordem das mensagens é preservada exatamente como foram enviadas, mesmo em cenários com falhas, retries ou múltiplos consumidores;
2. Preciso de Strict Ordering, e agora?
    - 2.1. Deixe o broker te ajudar a resolver: leia a documentação e siga as recomendações;
    - 2.2. RabbitMQ: single-consumer + retry-or-giveup (cuidado com `requeue=true`);
    - 2.3. Kafka: consumer-group with a single-consumer + partition by key;
    - 2.4. SQS FIFO: message-group;
    - 2.5. E claro, ninguém te fala, mas garanta um **single-threaded producer**;
    - 2.6. Mensagens duplicadas são um problema? Implemente idempotência ou de-duplication;
3. Não se engane, as vezes o broker sozinho não é suficiente
    - 3.1. Trata-se de uma dinâmica End-to-End;
    - 3.2. Abrace At-Least Once Delivery: idempotência ou de-duplication;
    - 3.3. Não confie na ordem do broker, mas sim na "Business event order";
    - 3.4. Favoreça comutatividade em vez de ordenação;


Piada: tweet sobre exactly-once delivery;

https://gist.github.com/rponte/9477858e619d8b986e17771c8be7827f?permalink_comment_id=5316367#gistcomment-5316367


## Tópico 8: Garantindo a entrega de exatamente 1 mensagem
Revisando At-Most Once delivery (AMOD), At-Least Once delivery (ALOD) e Exactly Once delivery (EOD).

```
EOD = ALOD + AMOD
```

1. E quando não podemos perder mensagens ou ter mensagens duplicadas? 😰
    - 1.1. **Exactly-Once Delivery**;
    - 1.2. Exactly-Once Delivery é impossível a nível de transporte;
    - 1.3. Na perspectiva do negócio, o que queremos na verdade é **Exactly-once Processing**: fingir que estamos processando uma única vez, sem efeitos colaterais;
    - 1.4. Trata-se de uma dinâmica End-to-End: todas as partes precisam colaborar;
2. O mundo seria mais fácil com transações distribuídas
    - 2.1. Imagine uma transação de ponta a ponta?
    - 2.2. Quebrando em partes: lado producer e consumer;
    - 2.3. Lado producer: de-duplication;
    - 2.4. Lado consumer: de-duplication, idempotência e transações (ACID);
3. Abrace seu broker e coopere com ele
    - 3.1. Leia a documentação e siga as recomendações;
    - 3.2. Kafka Transactions: tudo funciona dentro do mundinho do Kafka;
    - 3.3. SQS FIFO: de-duplication no lado producer;
    - 3.4. Conheça as limitações do seu broker e trabalhe ao redor delas com: de-duplication, idempotência e transações (ACID), commit log etc;
4. Talvez usar um broker não seja o que você precise
    - 4.1. Talvez você precise de banco de dados compartilhado ou commit log;
    - 4.2. Um banco ACID te permitirá obter as invariantes de Exactly-Once: uniqueness e strict ordering;

https://gist.github.com/rponte/0318f237753c55dd429fd656115a48d4


## Tópico 9: Refletindo sobre error handling, cooperação entre as partes e end-to-end principle
