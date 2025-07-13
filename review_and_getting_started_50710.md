# Review and Getting Started: Video Processing Environment

This document provides instructions on how to set up and use the Dockerized video processing environment.

## Overview

The environment includes:
- Ubuntu 22.04
- FFmpeg (version 4.4.2)
- Python (version 3.10.12)
- OpenSSH server for user `devuser`

A Python script (`scripts/rename_folders.py`) is provided to rename folders in a `vids/` directory from `mmdd_nnn` to `mmdd_(nnn+1)`.

## Getting Started

### 1. Build the Docker Image

Navigate to the repository root directory (where the `Dockerfile` is located) and run:
```bash
sudo docker build -t vidutils:latest .
```

### 2. Run the Docker Container

To run the container and map your local `vids/` directory for processing:

```bash
# Create a vids directory on your host if it doesn't exist
mkdir -p ./vids

# Run the container
sudo docker run -d \
  --name vidutils_active_container \
  -p 2222:22 \
  -v "$(pwd)/vids":/mnt/vids \
  vidutils:latest
```
- This maps your local `./vids` directory to `/mnt/vids` inside the container.
- SSH access will be available on port `2222` of your host machine.
- The container will be named `vidutils_active_container`.

### 3. SSH into the Container

To access the container via SSH:
```bash
ssh devuser@localhost -p 2222
```
You will be prompted for the password for `devuser`. The password is `devuser` (as set in the Dockerfile).

### 4. Using the `rename_folders.py` Script

The script is located at `scripts/rename_folders.py` in the repository.

**Option A: Copy script to container and execute (Recommended due to potential mount issues with `docker exec`)**

1. Copy the script to the running container:
   ```bash
   sudo docker cp scripts/rename_folders.py vidutils_active_container:/home/devuser/rename_folders.py
   ```
2. SSH into the container (if not already):
   ```bash
   ssh devuser@localhost -p 2222
   ```
3. Make the script executable and run it:
   ```bash
   chmod +x /home/devuser/rename_folders.py
   python3 /home/devuser/rename_folders.py /mnt/vids
   ```

**Option B: If direct script mounting works reliably in your setup**

If you choose to mount the `scripts` directory (e.g., by adding `-v "$(pwd)/scripts":/mnt/scripts` to your `docker run` command), you could try:

```bash
# Inside the container (via SSH)
chmod +x /mnt/scripts/rename_folders.py
python3 /mnt/scripts/rename_folders.py /mnt/vids
```
*Note: During testing, direct execution of scripts from a mounted volume via `docker exec` was unreliable. Copying the script into the container (`docker cp`) or ensuring it's part of the image is more robust.*

## Installed Tools & Versions
- **FFmpeg**: 4.4.2
- **Python**: 3.10.12
- **OpenSSH Server**

## Review Points & Considerations
- **Security**: The `devuser` has `sudo` privileges and a default password (`devuser`). For production or shared environments, change the password immediately after first login or use key-based SSH authentication. Consider removing `sudo` rights if not strictly necessary for the user's tasks.
- **Volume Mounts**: Ensure the paths for volume mounts (`-v` option in `docker run`) are correct and exist on your host machine.
- **Script Execution**: As noted, `docker cp` is the most reliable way found during testing to get the script into the container for execution if it's not part of the image. If you need to frequently update/change scripts, consider rebuilding the image with the scripts included or troubleshoot volume mounting for your specific Docker setup.
- **Error Handling in Script**: The `rename_folders.py` script includes basic error handling, but review it for your specific needs and edge cases.
- **Branching Strategy**: This work was done on the `0615vidtry` branch.
