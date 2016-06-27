The following is the URL model for the config service (and fetchable by the config client)

GET
/config/api/v1/properties/list
    200:
    [ {
      "uuid" : "4bfb2f16-94c1-4d04-be9e-364bfc95eb29",
      "propertyName" : "propertyOne",
      "context" : "",
      "value" : "6789",
      "author" : "mooreb",
      "comments" : "this comment explains propertyOne",
      "lastModified" : 1433952077668,
      "uri" : "/config/api/properties/property-uuid/4bfb2f16-94c1-4d04-be9e-364bfc95eb29"
    }, {
      "uuid" : "548c0a41-8892-4e2a-a7b6-9f33743fbd63",
      "propertyName" : "propertyTwo",
      "context" : "",
      "value" : "strings are nice",
      "author" : "mooreb",
      "comments" : "This comment explains propertyTwo",
      "lastModified" : 1433952664607,
      "uri" : "/config/api/v1/properties/property-uuid/548c0a41-8892-4e2a-a7b6-9f33743fbd63"
    }
    ...]

GET
/config/api/v1/properties/property-uuid/{uuid}
  200:
  {
      "uuid":"aca618f7-b08a-492f-b42b-169713ffe07b",
      "propertyName":"7b495862-b461-4897-bcf0-dad8a32745c4",
      "context":"context",
      "value":"value",
      "author":"author",
      "comments":"these are comments as to why the variable was created",
      "lastModified":1433017325836
  }

POST
/config/api/v1/properties/create
form variables:
  uuid (optional, used in the propagate case)
  propertyName
  context
  value
  author
  comments
expect back 201 CREATED

POST
/config/api/v1/properties/edit
form variables:
  uuid
  newPropertyName
  newContext
  newValue
  newAuthor
  newComments
expect back 204 NO CONTENT

POST
/config/api/v1/properties/delete
form variables:
  uuid,
  author,
  comments
expect back 204 NO CONTENT

POST
/config/api/v1/properties/search
form variables:
  pattern
expect back
  200:
  [ {
      "uuid":"aca618f7-b08a-492f-b42b-169713ffe07b",
      "propertyName":"7b495862-b461-4897-bcf0-dad8a32745c4",
      "context":"context",
      "value":"value",
      "author":"author",
      "comments":"these are comments as to why the variable was created",
      "lastModified":1433017325836
    }
  ...
  ]

GET
/config/api/v1/auditlog/list
  200:
  [ {  "modificationTimestamp":1433151155238,
       "action":"create",
       "author":"mooreb",
       "oldProperty":null,
       "newProperty":{"uuid":"b5f87c05-d677-46ac-a466-ef7061109194","propertyName":"p1Name","context":"context","value":"value1","author":"mooreb","comments":"comments","lastModified":1433151155238},
       "comments":"comments"
    },

    {  "modificationTimestamp":1433094748498,
       "action":"create",
       "author": "mooreb",
       "oldProperty":null,
       "newProperty":{"uuid":"12038150-5f01-41de-a324-e5302e2eebd2","propertyName":"p2Name","context":"context","value":"value2","author":"mooreb","comments":"comments","lastModified":1433094748498},
       "comments":"comments"},
     ...
  ]
