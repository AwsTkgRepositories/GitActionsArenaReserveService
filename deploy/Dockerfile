# Java用ビルドステージ
FROM mcr.microsoft.com/devcontainers/java:17 AS builder

# Mavenプロジェクトをビルド
WORKDIR /app
COPY . .
RUN mvn clean package -Dmaven.test.skip=true

# Selenium + Chrome + ChromeDriverの安定版
FROM selenium/standalone-chrome:latest

# Java 17とMavenをインストール
USER root
RUN apt-get update && apt-get install -y openjdk-17-jdk maven && \
    rm -rf /var/lib/apt/lists/*

# ビルド済みアプリをコピー
COPY --from=builder /app /app
WORKDIR /app

# 実行コマンド（ArenaReservableSearchServiceShinagawa）
CMD ["mvn", "exec:java", "-Dexec.mainClass=com.copel.productpackages.arena.selenium.service.ArenaReservableSearchServiceShinagawa", "-Dexec.classpathScope=runtime"]
