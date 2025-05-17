# Java 17 + Chrome入りのベースイメージ
FROM mcr.microsoft.com/devcontainers/java:17

# Google Chromeインストール
RUN apt-get update && \
    apt-get install -y wget gnupg unzip && \
    wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable

# ChromeDriverを合わせる
RUN CHROME_VERSION=$(google-chrome --version | grep -oP '\d+\.\d+\.\d+') && \
    echo "Chrome version: $CHROME_VERSION" && \
    CHROME_MAJOR_VERSION=${CHROME_VERSION%%.*} && \
    echo "Chrome major version: $CHROME_MAJOR_VERSION" && \
    CHROMEDRIVER_VERSION=$(curl -sS https://chromedriver.storage.googleapis.com/LATEST_RELEASE_${CHROME_VERSION%.*}) || true && \
    if [ -z "$CHROMEDRIVER_VERSION" ]; then \
        echo "No version found for LATEST_RELEASE_${CHROME_VERSION%.*}, fallback to LATEST_RELEASE_${CHROME_MAJOR_VERSION}"; \
        CHROMEDRIVER_VERSION=$(curl -sS https://chromedriver.storage.googleapis.com/LATEST_RELEASE_${CHROME_MAJOR_VERSION}); \
    fi && \
    if [ -z "$CHROMEDRIVER_VERSION" ]; then \
        echo "Failed to get chromedriver version for Chrome $CHROME_VERSION"; \
        exit 1; \
    fi && \
    echo "Using Chromedriver version: $CHROMEDRIVER_VERSION" && \
    wget -O /tmp/chromedriver.zip https://chromedriver.storage.googleapis.com/${CHROMEDRIVER_VERSION}/chromedriver_linux64.zip && \
    unzip /tmp/chromedriver.zip -d /usr/local/bin/ && \
    chmod +x /usr/local/bin/chromedriver

# Mavenプロジェクトをビルド
COPY . /app
WORKDIR /app
RUN mvn clean package -Dmaven.test.skip=true

# 実行コマンド（ArenaReservableSearchServiceShinagawaを動かす）
CMD ["mvn", "exec:java", "-Dexec.mainClass=com.copel.productpackages.arena.selenium.service.ArenaReservableSearchServiceShinagawa", "-Dexec.classpathScope=runtime"]
