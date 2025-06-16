# Use Ubuntu 22.04 as the base image
FROM ubuntu:22.04

# Install dependencies
RUN apt-get update && \
    apt-get install -y ffmpeg python3 python3-pip openssh-server sudo && \
    rm -rf /var/lib/apt/lists/*

# Create a non-root user
RUN useradd -ms /bin/bash devuser && \
    echo "devuser:devuser" | chpasswd && \
    adduser devuser sudo

# Set up SSH for the new user
RUN mkdir -p /home/devuser/.ssh && \
    chmod 700 /home/devuser/.ssh && \
    chown devuser:devuser /home/devuser/.ssh

# Configure SSH server
RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin no/' /etc/ssh/sshd_config && \
    sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config && \
    mkdir -p /run/sshd

# Expose SSH port
EXPOSE 22

# Set the default command to start SSH daemon
CMD ["/usr/sbin/sshd", "-D"]
