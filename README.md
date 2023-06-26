# 5.8.0.6 Patched Container

This is the Docker [context](https://docs.docker.com/engine/reference/builder/) for the the container-poc.
This is a forked project from the [container-poc](https://github.com/cleo/harmony-container-poc/tree/main) project that includes a Harmony patch install. This particular example uses the Harmony 5.8.0.0 installer as its base and applies the 5.8.0.6 patch during initialization.

## Prerequisites

### Installer 

To build locally copy a Harmony installer (e.g. http://www.cleo.com/SoftwareUpdate/harmony/release/jre1.8/InstData/Linux(64-bit)/VM/install.bin) to `target/install.bin`.
Also be sure the 0.6.zip patch file (e.g. https://portal.cleo.com/download/LexiComDL.asp?SN=%HY0001-BQ6792%&OS=Linux(64-bit)&PROD=Harmony&PATCH=Y) is copied to `target/0.6.zip`.

### Licenses 

To build locally copy four temporary licenses into `target/licenses` where the filenames are assumed to be `UH000[1-4]_license_key.txt`.

## Build

`docker build -t olsonjacob/harmony-container-poc:poc-0.0.2 .`
Use Kubernetes build below to automatically create and launch a patched container. If you need to edit the patch version the steps are as follows:
1. Download the desired patch zip and place in the local `target/<patchFile.zip>`
2. Update the dockerfile to COPY the correct zip file into the container
3. Update the `harmony-statefulset.yaml` file to import the correct patch in the init-container. The syntax for this is 
`./Harmonyc -i <patchFile.zip>`
which requires the following syntax within the statefulset yaml configuration:
`command: [/opt/init-harmony/Harmonyc, -i, /opt/harmony/<patchFile.zip>]`

### Kubernetes Example

A fully formed Kubernetes configuration can be run using:

`kubectl create -f harmony-statefulset.yaml`

Once the components are created and deployed you can access the Harmony UI through the virtual load balancer at `http://localhost:80/Harmony`

