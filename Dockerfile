FROM node:22-alpine AS dependencies
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --ignore-scripts

FROM node:22-alpine AS build
WORKDIR /app
ARG NEXT_PUBLIC_API_BASE_URL
ENV NEXT_PUBLIC_API_BASE_URL=${NEXT_PUBLIC_API_BASE_URL}
COPY --from=dependencies /app/node_modules ./node_modules
COPY . .
RUN npm run build

FROM node:22-alpine AS runtime
WORKDIR /app
ENV NODE_ENV=production
RUN addgroup -g 10001 -S omya && adduser -u 10001 -S omya -G omya
COPY --from=build --chown=omya:omya /app/package.json /app/package-lock.json ./
COPY --from=build --chown=omya:omya /app/node_modules ./node_modules
COPY --from=build --chown=omya:omya /app/dist ./dist
COPY --from=build --chown=omya:omya /app/.openai ./.openai
COPY --from=build --chown=omya:omya /app/scripts ./scripts
USER 10001:10001
EXPOSE 3000
CMD ["npm", "run", "start", "--", "--host", "0.0.0.0", "--port", "3000"]
