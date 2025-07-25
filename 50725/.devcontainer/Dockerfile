FROM mcr.microsoft.com/devcontainers/python:3.9

# Switch to a non-root user to avoid permission issues
USER vscode

# Copy the requirements file into the container
COPY --chown=vscode:vscode ../requirements.txt /tmp/pip-tmp/
RUN pip install --no-cache-dir -r /tmp/pip-tmp/requirements.txt \
    && rm -rf /tmp/pip-tmp

# Install ffmpeg
USER root
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends ffmpeg
USER vscode
