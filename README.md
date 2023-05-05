# zinject

This is intended to be used for troubleshooting ZIO.Client when
it injects a large number of requests to this server.

```
io.netty.channel.AbstractChannel$AnnotatedConnectException: connect(..) failed: Cannot assign requested address:
```

## Usage

```
 sbt "run 100000 4"
```

It will send 100000 requests with a paralelism of 4.

For 1 million requests I add to increase the heap size when running on my WSL2 Ubuntu.
```
export SBT_OPTS="-Xmx8G  -XX:+UseG1GC"
```
