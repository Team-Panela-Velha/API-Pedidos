CR10 — Teste de carga com JMeter (API-Pedidos)
================================================

Arquivo do plano: load-test.jmx (parametrizado por propriedades -J).

Propriedades aceitas (com defaults):
  HOST=localhost   PROTOCOL=http   ENDPOINT=/products
  PORT=8080        THREADS=100     RAMP=10    LOOPS=10
  TOKEN=<vazio>    -> JWT usado no header Authorization: Bearer <TOKEN>

IMPORTANTE: a maioria dos endpoints exige JWT. Obtenha um token antes:
  POST /auth/login  { "email": "...", "password": "..." }
  Copie o token retornado e passe em -JTOKEN=<token>.

------------------------------------------------------------
PREPARO
------------------------------------------------------------
1) Gerar o JAR:
     ./mvnw clean package -DskipTests

2) Cenario A (sem balanceamento) — 1 instancia direta:
     java -jar target/api_pedidos-0.0.1-SNAPSHOT.jar --server.port=8080

   Cenario B (com balanceamento) — 2 instancias + NGINX:
     java -jar target/api_pedidos-0.0.1-SNAPSHOT.jar --server.port=8080
     java -jar target/api_pedidos-0.0.1-SNAPSHOT.jar --server.port=8081
     nginx -c "$(pwd)/nginx/nginx.conf"     (NGINX escuta na porta 80)

------------------------------------------------------------
EXECUCAO (modo nao-grafico, recomendado para medir)
------------------------------------------------------------
Cenario A (direto na 8080):
  jmeter -n -t tests/jmeter/load-test.jmx -JPORT=8080 -JTHREADS=100  -JTOKEN=<token> -l resultados/A-100.jtl  -e -o resultados/A-100-html
  jmeter -n -t tests/jmeter/load-test.jmx -JPORT=8080 -JTHREADS=500  -JTOKEN=<token> -l resultados/A-500.jtl  -e -o resultados/A-500-html
  jmeter -n -t tests/jmeter/load-test.jmx -JPORT=8080 -JTHREADS=1000 -JTOKEN=<token> -l resultados/A-1000.jtl -e -o resultados/A-1000-html

Cenario B (via NGINX na porta 80):
  jmeter -n -t tests/jmeter/load-test.jmx -JPORT=80 -JTHREADS=100  -JTOKEN=<token> -l resultados/B-100.jtl  -e -o resultados/B-100-html
  jmeter -n -t tests/jmeter/load-test.jmx -JPORT=80 -JTHREADS=500  -JTOKEN=<token> -l resultados/B-500.jtl  -e -o resultados/B-500-html
  jmeter -n -t tests/jmeter/load-test.jmx -JPORT=80 -JTHREADS=1000 -JTOKEN=<token> -l resultados/B-1000.jtl -e -o resultados/B-1000-html

(O dashboard HTML sai na pasta -o; os listeners Summary/Aggregate aparecem se abrir o .jmx no modo grafico.)

------------------------------------------------------------
METRICAS A COLETAR (por cenario e carga)
------------------------------------------------------------
- Tempo medio de resposta (ms)
- Throughput (req/s)
- Taxa de erro (%)

------------------------------------------------------------
RELATORIO (PDF) — montar fora do repo
------------------------------------------------------------
- Tabela comparativa A vs B para 100, 500 e 1000 usuarios.
- Graficos exportados do JMeter (dashboard HTML em resultados/*-html/index.html).
- Conclusao: a partir de qual carga o balanceamento (B) passa a compensar.
