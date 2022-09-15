FROM ubuntu:18.04

COPY app.js /home/ubuntu/mycontainers/sc_app/jstest-container/app.js


#set non interactive and update repo
ENV DEBIAN_FRONTEND=noninteractive
RUN apt update


#install node js
RUN apt install -y nodejs

#install package manager
RUN apt install -y npm


#Run node
CMD node /home/ubuntu/mycontainers/sc_app/jstest-container/app.js
