# Doctor Fate
> There is one thing--and one thing only--we must do: nothing.
>
> -- Kent Nelson (Doctor Fate - DC)

A _Scala_ backend using _Akka-HTTP_ for Nabu.

## Test
### Unit Test
```bash
sbt test
```

### Integration Test
Make sure you have a postgres database running in the background for example with:
```bash
docker run -d -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 postgres:latest

```

## Running locally
```bash
sbt run
```

## Autoformatting
```bash
sbt scalafmtAll
```

## Running Scapegoat
```bash
sbt scapegoat
```

## Building jar
```bash
sbt assembly
```

