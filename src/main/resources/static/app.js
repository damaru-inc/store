'use strict';
const messaging = new Messaging();

const sleep = (milliseconds) => {
    return new Promise(resolve => setTimeout(resolve, milliseconds))
}

class ItemComponent extends React.Component {
    render() {
        return (
           <span> {this.props.item.id} {this.props.item.description} category: {this.props.item.category} </span>);
    }
}

class ItemListComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            error: null,
            isLoaded: false,
            hi: "Hi There",
            items: []
        };
        this.getItems = this.getItems.bind(this);
    }


    async waitForMessaging(resolve, reject) {
        let count = 1;
        while (!messaging.messagingUp) {
            await sleep(1000);
            if (count++ === 30) {
                console.log("Timed out waiting for solace.");
                reject("Timeout");
            }
        }
        resolve("foo");
    }

    getItems(resolve, reject) {

        let callback = function(message) {
            console.log("Got the message " + message.getBinaryAttachment());
            var json = message.getBinaryAttachment();
            var obj = JSON.parse(json);
            var arr = obj.itemView;
            // faking it:
            let ret = [];
            let item = {id: 333, description: "Coffee", price: 18.99, category: "coffee"};
            //console.log("isLoaded: " + this.state.hi);
            ret.push(item);
            // end if faking it. Return arr.
            resolve(ret);
        }
        console.log(`about to send ${messaging.publisherUp}`);
        messaging.sendQuery(callback);
    }

    componentDidMount() {
        (new Promise(this.waitForMessaging))
            .then(
                (result) => {
                    (new Promise(this.getItems))
                        .then(
                            (result) => {
                                this.setState({
                                    isLoaded: true,
                                    items: result
                                });
                            },
                            // Note: it's important to handle errors here
                            // instead of a catch() block so that we don't swallow
                            // exceptions from actual bugs in components.
                            (error) => {
                                this.setState({
                                    isLoaded: true,
                                    error
                                });
                            }
                        );
                },
                (error) => {
                        this.setState({
                            isLoaded: true,
                            error
                        });
                });
          }

    render() {
        const { error, isLoaded, items } = this.state;
        if (error) {
            return <div>Error: {error.message}</div>;
        } else if (!isLoaded) {
            return <div>Loading...</div>;
        } else {
            return (
                <ul>
                    {items.map(item => (
                        <li key={item.id}>
                            <ItemComponent item={item} />
                        </li>
                    ))}
                </ul>
            );
        }
    }
}

ReactDOM.render(<ItemListComponent/>, document.getElementById('root'));
