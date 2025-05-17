# Java 17 + Chrome入りのベースイメージ
FROM mcr.microsoft.com/devcontainers/java:17

# 必要な依存パッケージをインストール
RUN apt-get update && \
    apt-get install -y wget gnupg unzip curl && \
    rm -rf /var/lib/apt/lists/*

# Chrome 135.x を明示的にインストール（リポジトリは使わない）
RUN wget https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_135.0.4164.0-1_amd64.deb && \
    apt-get install -y ./google-chrome-stable_135.0.4164.0-1_amd64.deb && \
    rm google-chrome-stable_135.0.4164.0-1_amd64.deb

# ChromeDriverをChromeに合わせてインストール
RUN CHROME_VERSION=$(google-chrome --version | grep -oP '\d+\.\d+\.\d+') && \
    echo "Chrome version: $CHROME_VERSION" && \
    CHROME_MAJOR_VERSION=${CHROME_VERSION%%.*} && \
    echo "Chrome major version: $CHROME_MAJOR_VERSION" && \
    CHROMEDRIVER_VERSION=$(curl -sS https://chromedriver.storage.googleapis.com/LATEST_RELEASE_${CHROME_VERSION%.*}) || true && \
    if [ -z "$CHROMEDRIVER_VERSION" ]; then \
        echo "No version found for LATEST_RELEASE_${CHROME_VERSION%.*}, fallback to LATEST_RELEASE_${CHROME_MAJOR_VERSION}"; \
        CHROMEDRIVER_VERSION=$(curl -sS https://chromedriver.storage.googleapis.com/LATEST_RELEASE_${CHROME_MAJOR_VERSION}) || true; \
    fi && \
    if [ -z "$CHROMEDRIVER_VERSION" ]; then \
        echo "No version found for LATEST_RELEASE_${CHROME_MAJOR_VERSION}, fallback to LATEST_RELEASE"; \
        CHROMEDRIVER_VERSION=$(curl -sS https://chromedriver.storage.googleapis.com/LATEST_RELEASE) || true; \
    fi && \
    if [ -z "$CHROMEDRIVER_VERSION" ]; then \
        echo "Failed to get chromedriver version for Chrome $CHROME_VERSION"; \
        exit 1; \
    fi && \
    echo "Using Chromedriver version: $CHROMEDRIVER_VERSION" && \
    wget -O /tmp/chromedriver.zip https://chromedriver.storage.googleapis.com/${CHROMEDRIVER_VERSION}/chromedriver_linux64.zip && \
    unzip /tmp/chromedriver.zip -d /usr/local/bin/ && \
    chmod +x /usr/local/bin/chromedriver && \
    rm /tmp/chromedriver.zip

# Mavenプロジェクトをビルド
COPY . /app
WORKDIR /app
RUN mvn clean package -Dmaven.test.skip=true

# 実行コマンド
CMD ["mvn", "exec:java", "-Dexec.mainClass=com.copel.productpackages.arena.selenium.service.ArenaReservableSearchServiceShinagawa", "-Dexec.classpathScope=runtime"]
