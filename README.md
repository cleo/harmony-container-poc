# Clustered Docker Compose Example with Volumes

This `docker-compose.yml` example shows how to create a Harmony server cluster using a Docker volume
to persist configuration and other Harmony data across invocations of each container. The example also
includes a clustered VLProxy setup that is automatically configured within the Harmony nodes.

To bring up the servers:

```
docker compose up
^C to terminate the servers
```

Navigate to [http://localhost:5180](http://localhost:5180) and login with the with username `administrator` password set within `ADMIN_PASSWORD:` in the `docker-compose.yml` file. Default is `cleo`.

## Resources

The docker-compose creates:

* a `harmony1` service,
  * based on the image described by the `Dockerfile`,
  * which mounts the `harmony1` volume at `/opt/harmony`,
  * with `http` enabled on port `5080`, mapped to `localhost:5180` on the host,
  * with `https` enabled on port `6080`, mapped to `localhost:6180` on the host,
  * with `sftp` enabled on port `10022`.
* a `harmony2` service,
  * based on the image described by the `Dockerfile`,
  * which mounts the `harmony2` volume at `/opt/harmony`,
  * with `http` enabled on port `5080`, mapped to `localhost:5280` on the host,
  * with `https` enabled on port `6080`, mapped to `localhost:6280` on the host,
  * with `sftp` enabled on port `10022`,
* a `vlproxy1` server,
  * based on the image in the Cleo GitHub Container Registry at `ghcr.io/cleo/vlproxy-alpine:5.7.0.1`,
  * with an `http` proxy listeneing on port `9080`, mapped to `localhost:6181` on the host,
  * with an `https` proxy listing on port `9443`, mapped to `localhost:6141` on the host,
  * with an `sftp` proxy listing on port `9022`	, mapped to `localhost:6121` on the host.
* a `vlproxy2` server,
  * based on the image in the Cleo GitHub Container Registry at `ghcr.io/cleo/vlproxy-alpine:5.7.0.1`,
  * with an `http` proxy listeneing on port `9080`, mapped to `localhost:6281` on the host,
  * with an `https` proxy listing on port `9443`, mapped to `localhost:6241` on the host,
  * with an `sftp` proxy listing on port `9022`, mapped to `localhost:6221` on the host.
* a `volumeprep1` service, which is used to prepare the `harmony1` volume,
  * based on the image in the Cleo GitHub Container Registry at `ghcr.io/cleo/harmony-alpine:5.7.0.1`,
* a `volumeprep2` service, which is used to prepare the `harmony2` volume,
  * based on the image in the Cleo GitHub Container Registry at `ghcr.io/cleo/harmony-alpine:5.7.0.1`,
* a `harmony` network.


There is a service dependency between `harmony1` and `volumeprep1`, and `harmony2` and `volumeprep2`:

```
  harmony1:
    depends_on:
      volumeprep1:
        condition: service_completed_successfully
```

This `harmony-alpine` image, which weighs in at 883.55 MB on my laptop,
functions like a Kubernetes init container. This image is used very simply to map
`/opt/harmony` to the `harmony1` volume, and on first use the volume is initialized
from the `/opt/harmony` in the image.

The Harmony service actually runs from the `Dockerfile` image, which contains only
the jre needed to run Harmony, and weighs in at only 108.47 MB on my laptop.
It mounts the `harmony1` volume to `/opt/harmony`, which persists between runs.
