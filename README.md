# container-poc

This is the Docker [context](https://docs.docker.com/engine/reference/builder/) for the the container-poc.

## Prerequisites

### Installer 

To build locally copy a Harmony installer (e.g. http://www.cleo.com/SoftwareUpdate/harmony/release/jre1.8/InstData/Linux(64-bit)/VM/install.bin) to `target/install.bin`.

### Licenses 

To build locally copy four temporary licenses into `target/licenses` where the filenames are assumed to be `UH000[1-4]_license_key.txt`.

## Build

`docker build -t cleo/harmony:poc-0.0.1 .`

## Run

### License Index Check

Use environment variable `INDEX` to switch licenses:

```bash
$ docker run --rm -it -e INDEX=3 container-poc cat ./license_key.txt
#Fri Oct 30 15:42:26 EDT 2020
license.owner=Cleo
license.extensionb6=[nw|-mhAZ-5yLQ-ZFJ]-Yk3&-\#k{9-asMw-m$4z
license.extensionb5=0bw|-5{2k-ryLQ-ZVJR-s^9&-HkXC-as\#u-mRZU
license.extensionb4=0vw|-5I2k-ryLQ-ZVJR-s^9C-HkXC-as\#u-mRZU
license.extensionb3=0iw|-5$%k-ryLQ-ZVJR-s^9Z-HkXC-as\#u-mRZU
license.extensionb2=0nwA-5h2k-rZUQ-ZVJR-s^9Z-HkXC-as\#u-mRZU
license.hash=1K3CLLL|u7ieoy
license.extensionb1=0bwA-5{rk-zZUQ-ZVJR-s^9Z-HkXC-as\#u-mRZU
license.key=1vUA-uI&A-sZUQ-ZG8N-e[X}-&kXz-ns%c-CDLx
$
```

### Harmony License Check

Harmony will pick up the proper license index:

```
$ docker run --rm -it -e INDEX=3 container-poc ./Harmonyc -s license
Listening for transport dt_socket at address: 8000
License Key                 = 1vUA-uI&A-sZUQ-ZG8N-e[X}-&kXz-ns%c-CDLx
License Owner               = Cleo
Serial Number               = UH0003-CN0138
Host ID                     = DP6214
Key Expiration              = 2020/12/29
# of Hosts                  = Unlimited
# of Mailboxes              = Unlimited
Protocol Limits:
  HSP                       = Unlimited
  S3                        = Unlimited
  SMB                       = Unlimited
  User                      = Unlimited
  Any other                 = Unlimited
Platform                    = Any
Translator Integration      = Yes
VLProxy                     = Yes
Web Browser Interface       = Yes
File Tracker                = All
Large File Applet           = No
High Availability Backup    = No
Java API                    = Yes
System Monitor              = Yes
SNMP Agent                  = Yes
IP Filter                   = Yes
JavaScript Actions          = Yes
SAML                        = Yes
Trigger Pool Size           = 100
Unify in Portal             = Yes
FIPS Mode                   = Yes
Support Expiration          = 2020/12/29
$
```

## Composition

This is an example composition with four synchronized Harmony containers.

### Build

Build the example composition from this context using `docker-compose build`.

### Run

Run the example compostion from this context using `docker-compose up`. At this point you should be able to access the
Harmony UI via `http://localhost:5[1-4]80/Harmony` substituting the desired index `[1-4]` for the Harmony you wish to access.
The default credentials are:
`Username: administrator`
`Password: cleo`

### Verify

After cloning and activating a host (i.e. with `harmony1` clone and activate `CLEO SSH FTP System Test.xml`)
you should see the host is sync'ed to the other Harmony containers:

```bash
$ docker exec -it container_harmony2_1 ls -l hosts
total 36
-rw-r--r-- 1 harmony harmony  4111 Nov  2 18:40 'CLEO SSH FTP System Test.xml'
-rw-r--r-- 1 harmony harmony 14934 Nov  2 18:37 'Local Listener.xml'
drwxrwxr-x 1 harmony harmony  4096 Nov  2 18:37  preconfigured
drwxrwxr-x 1 harmony harmony  4096 Nov  2 18:38  support
drwxr-xr-x 2 harmony harmony  4096 Nov  2 18:40  unsynced

$ docker exec -it container_harmony3_1 ls -l hosts

total 36
-rw-r--r-- 1 harmony harmony  4111 Nov  2 18:40 'CLEO SSH FTP System Test.xml'
-rw-r--r-- 1 harmony harmony 14934 Nov  2 18:37 'Local Listener.xml'
drwxrwxr-x 1 harmony harmony  4096 Nov  2 18:37  preconfigured
drwxrwxr-x 1 harmony harmony  4096 Nov  2 18:38  support
drwxr-xr-x 2 harmony harmony  4096 Nov  2 18:40  unsynced

$ docker exec -it container_harmony4_1 ls -l hosts
total 36
-rw-r--r-- 1 harmony harmony  4111 Nov  2 18:40 'CLEO SSH FTP System Test.xml'
-rw-r--r-- 1 harmony harmony 14934 Nov  2 18:37 'Local Listener.xml'
drwxrwxr-x 1 harmony harmony  4096 Nov  2 18:37  preconfigured
drwxrwxr-x 1 harmony harmony  4096 Nov  2 18:38  support
drwxr-xr-x 2 harmony harmony  4096 Nov  2 18:40  unsynced
$
```

### Docker Hub Example

The composition image can be tagged using:

`docker tag container_harmony1 cleo/harmony:container-0.0.1`

Then pushed using:

`docker push cleo/harmony:container-0.0.1`

The image may then be pulled using:

`docker pull cleo/harmony:container-0.0.1`
