# A Gentle Introduction to Docker

There are two Java projects with corresponding Dockerfile-s

## SIMPLE

Build project and Docker image:
```
mvn clean package
docker build -t markoradinovic/simple .
```

Run Docker container and expose port
```
docker run -d --name simple -p 8888:8080 markoradinovic/simple
```

Test running service:
```
curl http://localhost:8888
```

## SIMPLE_DB

Build project and Docker image:
```
mvn clean package
docker build -t markoradinovic/simple-db .
```
This projects depends on PostgreSql DB. To create PostgreSql container run:
```
docker run --name postgres -e POSTGRES_PASSWORD=pgadmin -e POSTGRES_USER=pgadmin -e POSTGRES_DB=demo -d -p 5432:5432 postgres:9.5
```

If you like to mount host directory as a Docker volume, then use this run command:
```
docker run --name postgres -e POSTGRES_PASSWORD=pgadmin -e POSTGRES_USER=pgadmin -e POSTGRES_DB=demo -d -p 5432:5432 -v ~/pg-data:/var/lib/postgresql/data postgres:9.5
```
where you can replace ```~/pg-data``` with your own local path.

Run simple-db Docker container and link it to PostgreSql container
```
docker run --name simple-db -e PG_URL=postgres:5432/demo -e PG_USER=pgadmin -e PG_PASSWORD=pgadmin --link=postgres -d -p 8080:8080 markoradinovic/simple-db
```

### Docker Compose version
Use provided ```docker-compose.yml``` and run:
```
docker-compose up -d
```
This should create ```simple-db``` and ```postgres``` container that is being linked with ```simple-db``` container.

Test running service:
```
curl http://localhost:8080
```

### Usefull Docker commands
- SSH to a running ```simple``` or ```simple-db``` container
```
docker exec -it simple /bin/ash
```

Show container log:
```
docker logs simpe
or
docker logs -f simple
```

Show port mappings:
```
docker port simple
```
