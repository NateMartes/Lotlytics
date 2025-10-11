FROM debian:bookworm-slim

RUN apt-get update && \
    apt-get install -y curl unzip groff less python3 && \
    rm -rf /var/lib/apt/lists/*

RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && \
    ./aws/install && \
    rm -rf awscliv2.zip aws

COPY init_db.sh /init_db.sh

RUN chmod +x /init_db.sh

CMD ["/bin/sh", "/init_db.sh"]