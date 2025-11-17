FROM node:25-alpine3.21

WORKDIR /client

COPY package.json /client

RUN npm install

COPY . /client/

RUN npm run build

EXPOSE 3000

CMD ["npm", "start"]