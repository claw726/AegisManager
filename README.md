# Aegis Project README

![Aegis Logo](./icon/Check-icon.png){height=256}

## Overview

Aegis is a project management system designed to simplify task management for small teams and individual users. It focuses on core features like task creation, assignment, and progress tracking without unnecessary complexity.

## Project Structure

- **Backend**: Contains the server-side code for the project.
- **Client**: Contains the client-side code for the project, built with Vue.js.
- **README.md**: This file, providing an overview of the project and instructions for setup.

### Prerequisites

- Docker installed on your system.
- `docker-compose` installed on your system
- Vue.js installed globally (optional but recommended for development).

## Compilation Steps for Production

```tty
docker-compose up --build
```

This builds both the server and the client as well as the required volumes for the DB and SSL certificates

## Compilation Steps for Dev

- Create 2 terminals

**Terminal 1**:

```tty
cd ./client
npm ci
npm run dev
```

**Terminal 2**:

```tty
cd ./server
docker-compose up --build backend
```

### Step 3: Verify the Application

After running the container, you can verify that your frontend application is running by accessing [https://localhost:8443](https://localhost:8443) in your web browser. You should see your Vue.js application up and running.

> Get an error stating that the certificate is invalid? Go to `advanced` and click `accept anyways`

## License

This project is licensed under the GPL-3.0 License. See the [LICENSE](LICENSE) file for details.
