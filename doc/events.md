# Events

This document describes the events that flow through the system.

There are three kinds of events:

- Command

A command event will trigger an action. It may be a query or
 it may post some new data.
 
- Data

A data event will simply contain data. It may be the response to a query.

- Notification

This may be a login/logout event.

## Event Catalog

### Query

A Query is a command event. It contains a field indicating what object is being queried,
i.e item, catagory, account. It may contain a filter field. Example:

```json
{
"eventType" : "query",
"entityType" : "item",
"filter" : {
  "id" :  "333"}
}
```
### QueryResponse

A QueryResponse contains the data in response to a Query.
It has a list of entities.
There is a seperate event type for each different entity type so that
they can be better represented in strongly typed languages such as Java.

```json
{
"eventType" : "QueryResponseItem",
"entityType" : "item",
"items" : [
    { 
      "id" :  "333",
      "description": "Verona Coffee",
      "price":  18.99 
    }
  ]
}
```
