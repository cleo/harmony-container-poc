# Docker Harmony Tool

The `docker-harmony-tool` contains some convenience tools that help with the initial configuration of a Harmony image while building a container. It is developed in Java, leverages some Harmony Java APIs, and is packaged as an executable `jar` file.

## Commands

### `pbkdf2`

Usage: `docker-harmony-tool pbkdf2 password`

Prints `password` hashed and encoded using the Harmony PBKDF2 scheme, ready for inclusion in the `conf/Users.xml` or other user file.

Requires a Harmony image. Usage using `java -jar` requires `$CLEO_HOME` be `/opt/harmony`. For any other `$CLEO_HOME` the wrapper script is required to set the classpath.

### `serial`

Usage: `docker-harmony-tool serial XX0001`

Expands the serial number prefix to the full serial number (e.g. `XX0001-CA1247`, which is required to configure VLProxy.

Intended for use in a VLProxy image. Does not require a Harmony installation or the wrapper script.

### `vlpconfig`

Usage: `docker-harmony-tool vlpconfig property=value...`

Compiles the list of `property=value` tokens into a binary file suitable for use as a `conf/VLProxy.properties` file in a VLProxy image. Assumes the default password of `Admin` is used, as in a freshly installed VLProxy image. Displays a summary of the configuration as would be displayed by `VLProxy -p` to confirm the settings.

| Token                         | Description            | Default Value |
|-------------------------------|------------------------|---------------|
| serialNumbers                 |  2. Serial Numbers | &nbsp; |
| proxyHTTPPorts                |  3. Internal Forward Proxy HTTP Ports | 8080 |
| internalAddress               |  4. Internal Address | &nbsp; |
| internalNetworkIDs            |  5. Internal Network IDs | &nbsp; |
| externalAddress               |  6. External Address | &nbsp; |
| reverseProxyHTTPPorts         |  7. External Reverse Proxy HTTP Ports | &nbsp; |
| reverseProxyHTTPsPorts        |  8. External Reverse Proxy HTTPs Ports | &nbsp; |
| reverseProxyFTPPorts          |  9. External Reverse Proxy FTP Ports | &nbsp; |
| reverseProxyExplicitFTPsPorts | 10. External Reverse Proxy FTPs Explicit Ports | &nbsp; |
| reverseProxyImplicitFTPsPorts | 11. External Reverse Proxy FTPs Implicit Ports | &nbsp; |
| reverseProxyFTPDataPorts      | 12. External Reverse Proxy FTP Data Ports | &nbsp; |
| reverseProxySSHFTPPorts       | 13. External Reverse Proxy SSH FTP Ports | &nbsp; |
| reverseProxyOFTPPorts         | 14. External Reverse Proxy OFTP Ports | &nbsp; |
| reverseProxyOFTPsPorts        | 15. External Reverse Proxy OFTPs Ports | &nbsp; |
| vlReadTimeout                 | 16. VersaLex Read Timeout (seconds) | 150 |
| remoteReadTimeout             | 17. Remote Read Timeout (seconds) | 150 |
| portBacklog                   | 18. Connection Backlog Size | 50 |
| mailServerAddress             | 19. SMTP Mail Server Address | &nbsp; |
| mailServerUsername            | 20. SMTP Mail Server Username | &nbsp; |
| mailServerPassword            | 21. SMTP Mail Server Password | &nbsp; |
| emailOnFailAddr               | 22. Email on Fail Addresses | &nbsp; |
| executeOnFailCommand          | 23. Execute on Failure Command | &nbsp; |
| maxLogFileSize                | 24. Max Log File Size (Mb) | 5 |
| logExternalAddress            | 25. Log External Address | No |
| unknownPartnerMsgAction       | 26. Unknown Partner Message Action  | Defer |
| reverseProxyLoadBalancing     | 27. Reverse Proxy Load Balancing | No |
| reverseProxyRetry             | 28. Reverse Proxy Retry | No |

Note that the `serialNumbers` can be provided in short form (`XX0001`) and will be automatically expanded. Separate multiple serial numbers with comma (`XX0001,XX0002`).

Intended for use in a VLProxy image. Does not require a Harmony installation or the wrapper script.

### `trust`

Usage: `docker-harmony-tool trust file...`

Imports the `file` or files as trusted CA certs in the Harmony Certificate Manager.

Requires a Harmony image at `$CLEO_HOME` and the wrapper script is required to set the classpath.

### `importp12`

Usage: `docker-harmony-tool importp12 alias file password`

Imports a private key and certificate from PKCS#12 file `file`, protected by `password`, into the Harmony Certificate Manager as a User key under `alias`.

Requires a Harmony image at `$CLEO_HOME` and the wrapper script is required to set the classpath.

### `generatekey`

Usage: `docker-harmony-tool generatekey alias password property...`

Generates a self-signed certificate and private key under `alias` and protected by `password` in the Harmony Certificate Manager. The follow `property` may be supplied:

| Property | Description |
|----------|----------|
| `cn=string`        | Set the X.509 common name to `string` |
| `c=string`         | Set the X.509 country to `string` |
| `email=string`     | Set the X.509 email address to `string` |
| `l=string`         | Set the X.509 locality (city) to `string` |
| `o=string`         | Set the X.509 organization to `string` |
| `ou=string`        | Set the X.509 organizational unit to `string` |
| `st=string`        | Set the X.509 state or province to `string` |
| `months=number`    | Set the validity period to `number` months |
| `md5`              | Set the signature algorithm to `md5` |
| `sha1`             | Set the signature algorithm to `sha1` |
| `sha256`           | Set the signature algorithm to `sha256` |
| `sha384`           | Set the signature algorithm to `sha384` |
| `sha512`           | Set the signature algorithm to `sha512` |
| `rsa`              | Set the public key algorithm to `rsa` |
| `dsa`              | Set the public key algorithm to `dsa` |
| `512`              | Set the key strength to `512` bits |
| `1024`             | Set the key strength to `1024` bits |
| `2048`             | Set the key strength to `2048` bits |
| `3072`             | Set the key strength to `3072` bits |
| `4096`             | Set the key strength to `4096` bits |
| `keyencipherment`  | Add the `keyEncipherment` key usage |
| `digitalsignature` | Add the `digitalSignature` key usage |
| `serverauth`       | Add the `serverAuth` extended key usage |
| `clientauth`       | Add the `clientAuth` extended key usage |

Note that the properties should be separated with spaces, not commas.

Requires a Harmony image at `$CLEO_HOME` and the wrapper script is required to set the classpath.

### `proxies`

Usage: `docker-harmony-tool proxies proxy...`

Creates a mew `Proxies.xml` file in `$CLEO_HOME/conf` with one or more `Proxy` elements defined.

Each proxy is described by a string of the form:

```
vlproxy://host:port;option=value;option=value...
```

The `vlproxy://` and `host` are required. `port` is optional and defaults to `8080`. `host` can be a list of hosts, separated by `,`, in which case the proxy configuration is repeated for each host in the list.

The following `options` are supported. If `=value` is not supplied, `=true` is the default (which can be convenient for setting boolean options).

| Option                      | Default |
|-----------------------------|----------|
| `Backupproxy`               | `false` |
| `Enablereverseproxying`     | `true` |
| `Forwardproxygroup`         | &nbsp; |
| `Loadbalance`               | `true` |
| `Reverseforwardconnections` | `false` |
| `Uselistenersshcerts`       | `true` |
| `Uselistenersslcerts`       | `true` |
| `Usesamecerts`              | `false` |

Requires a Harmony image in `$CLEO_HOME`. Usage using `java -jar` requires `$CLEO_HOME` be `/opt/harmony`. For any other `$CLEO_HOME` the wrapper script is required to set the classpath.

## How to download and run the tool

The tool is packaged as an executable JAR, published as a Maven repository on a branch in the [harmony-container-poc](https://github.com/cleo/harmony-container-poc/tree/repository)  project on GitHub. This is a public repository, so the current (5.7.0.0) release can be downloaded with `curl` (into `$CLEO_HOME`) as follows:

```
curl -o $CLEO_HOME/docker-harmony-tool.jar https://raw.githubusercontent.com/cleo/harmony-container-poc/repository/com/cleo/labs/docker-harmony-tool/5.7.0.0/docker-harmony-tool-5.7.0.0.jar
```

For commands that require that the tool connect to the Harmony instance installed in `$CLEO_HOME` on an image, a wrapper script is required to assemble the correct classpath:

```
echo '#!/bin/sh'$'\njava -cp $CLEO_HOME/docker-harmony-tool.jar:$(find $CLEO_HOME/lib -type d|sed "s|$|/*|"|paste -s -d : -):$CLEO_HOME/webserver/AjaxSwing/lib/ajaxswing.jar com.cleo.labs.docker.harmony.Tool "$@"' > $CLEO_HOME/docker-harmony-tool
chmod a+x $CLEO_HOME/docker-harmony-tool
```

The tool can now be run using the wrapper script:

```
$CLEO_HOME/docker-harmony-tool command ...
```


## How to build and publish the tool

* build the tool from this directory:

```
mvn package
```

* in a separate directory, checkout the `repository` branch of `harmony-container-poc`:

```
mkdir ../harmony-container-poc-repository
cd ../harmony-container-poc-repository
git clone git@github.com:cleo/harmony-container-poc.git
git checkout repository
```

* install the built tool as a maven artifact:

```
mvn install:install-file -DgroupId=com.cleo.labs -DartifactId=docker-harmony-tool -Dversion=5.7.0.0 -Dfile=../internal-harmony-container-poc/harmony-tool/target/docker-harmony-tool-5.7.0.0.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true
```

* commit and push the updated files:

```
git add -A .
git commit -m 'updating 5.7.0.0 release: reason'
git push
```

* all done
