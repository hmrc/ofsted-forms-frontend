DOMAIN={
    'forms' : {
        'resource_methods': ['GET', 'POST'],

        'item_methods' : ["GET", "PUT"],

        'schema' : {
            'formId': {
                'type': 'uuid',
                'unique': True
            },
            'data' : {
                'type': 'dict',
                'default': {}
            }
        },

        'additional_lookup': {
            'field' : 'formId',
            'url': 'regex("[0-9a-fA-F]{8}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{12}")'
        },

        'mongo_indexes' : {
            'formId' : [('formId', 1)]
        }
    }
}

HATEOAS=False
