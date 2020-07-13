class Messaging {

    constructor() {
        this.callback = null;
        solace.SolclientFactory.setLogLevel(solace.LogLevel.DEBUG);
        var factoryProps = new solace.SolclientFactoryProperties();
        factoryProps.profile = solace.SolclientFactoryProfiles.version10;
        solace.SolclientFactory.init(factoryProps);
        var self = this;
        this.originatorId = 'webclient';
        this.queryTopic = `estore/command/query/${this.originatorId}`;
        this.queryResponseTopic = `estore/data/queryResponse/${this.originatorId}`;
        let query = {};
        query.entityType = 'item';
        query.eventType = 'query';
        this.itemQueryPayload = JSON.stringify(query);

        this.message = solace.SolclientFactory.createMessage();
        this.message.setDestination(solace.SolclientFactory.createTopicDestination(this.queryTopic));
        this.message.setDeliveryMode(solace.MessageDeliveryModeType.DIRECT);
        this.message.setBinaryAttachment(this.itemQueryPayload);

        console.log('---------- VERSION: ' + solace.Version);

        this.publisher = {};
        this.subscriber = {};

        this.publisher.session = solace.SolclientFactory.createSession({
            url:      connectionProperties.host,
            vpnName:  connectionProperties.vpn,
            userName: connectionProperties.username,
            password: connectionProperties.password,
        });

        this.subscriber.session = solace.SolclientFactory.createSession({
            url:      connectionProperties.host,
            vpnName:  connectionProperties.vpn,
            userName: connectionProperties.username,
            password: connectionProperties.password,
        });

        this.publisher.session.on(solace.SessionEventCode.UP_NOTICE, function (sessionEvent) {
            console.log('=== Successfully connected and ready to publish messages. ===');
            self.publisherUp = true;
            if (self.subscriberUp) {
                self.messagingUp = true;
            }
        });

        this.publisher.session.on(solace.SessionEventCode.CONNECT_FAILED_ERROR, function (sessionEvent) {
            console.log('Connection failed to the message router: ' + sessionEvent.infoStr +
                ' - check correct parameter values and connectivity!');
            self.publisherUp = false;
            self.messagingUp = false;
        });
        this.publisher.session.on(solace.SessionEventCode.DISCONNECTED, function (sessionEvent) {
            console.log('Disconnected.');
            if (self.publisher.session !== null) {
                self.publisher.session.dispose();
                self.publisher.session = null;
            }
            self.publisherUp = false;
            self.messagingUp = false;
        });

        this.subscriber.session.on(solace.SessionEventCode.UP_NOTICE, function (sessionEvent) {
            console.log('=== Successfully connected and ready to subscribe. ===');
            try {
                self.subscriber.session.subscribe(
                    solace.SolclientFactory.createTopic(self.queryResponseTopic),
                    true,
                    self.queryResponseTopic,
                    10000
                );
            } catch (error) {
                console.log(error.toString());
            }
            //this.subscriber.session.subscribe();
        });

        this.subscriber.session.on(solace.SessionEventCode.CONNECT_FAILED_ERROR, function (sessionEvent) {
            console.log('Connection failed to the message router: ' + sessionEvent.infoStr +
                ' - check correct parameter values and connectivity!');
            self.subscriberUp = false;
            self.messagingUp = false;
        });
        this.subscriber.session.on(solace.SessionEventCode.DISCONNECTED, function (sessionEvent) {
            self.subscriber.log('Disconnected.');
            if (self.subscriber.session !== null) {
                self.subscriber.session.dispose();
                self.subscriber.session = null;
            }
            self.subscriberUp = false;
            self.messagingUp = false;
        });

        this.subscriber.session.on(solace.SessionEventCode.SUBSCRIPTION_ERROR, function (sessionEvent) {
            console.log('Cannot subscribe to topic: ' + sessionEvent.correlationKey);
        });
        this.subscriber.session.on(solace.SessionEventCode.SUBSCRIPTION_OK, function (sessionEvent) {
            self.subscriber.subscribed = true;
            console.log('Successfully subscribed to topic: ' + sessionEvent.correlationKey);
            console.log('=== Ready to receive messages. ===');
            self.subscriberUp = true;
            if (self.publisherUp) {
                self.messagingUp = true;
            }
        });

        this.subscriber.session.on(solace.SessionEventCode.MESSAGE, function (message) {
            if (self.callback) {
                self.callback(message);
            }
            console.log('Received message: "' + message.getBinaryAttachment() + '", details:\n' + message.dump());
        });

        try {
            this.publisher.session.connect();
            this.subscriber.session.connect();
        } catch (error) {
            console.log(error.toString());
        }

    }

    sendQuery(callback) {
        this.callback = callback;
        if (this.publisherUp) {
            try {
                this.publisher.session.send(this.message);
                console.log('Message published.');
            } catch (error) {
                console.log(error.toString());
            }
        } else {
            console.log('Cannot publish because not connected to Solace message router.');
        }
    }
}
