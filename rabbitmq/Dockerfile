FROM rabbitmq:3.9.3-management-alpine

ADD definitions.json /etc/rabbitmq/
RUN rabbitmq-plugins enable rabbitmq_auth_mechanism_ssl &&\
	chown rabbitmq:rabbitmq /etc/rabbitmq/definitions.json
