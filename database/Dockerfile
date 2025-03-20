FROM postgres:16-alpine

# .env
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=9478
ENV POSTGRES_DB=database_name

WORKDIR /docker-entrypoint-initdb.d
COPY schema.sql /docker-entrypoint-initdb.d/

ENV CN=Asia/China

# 可選：設置 PostgreSQL 配置參數，可以取消下面的註釋
# COPY postgresql.conf /etc/postgresql/postgresql.conf
# CMD ["postgres", "-c", "config_file=/etc/postgresql/postgresql.conf"]

# 暴露 PostgreSQL 默認端口
EXPOSE 5432