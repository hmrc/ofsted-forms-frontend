from eve import Eve
from eve.io.mongo import Validator
from uuid import UUID

class MyValidator(Validator):
    def _validate_type_uuid(self, value):
        try:
            UUID(value) # would throw error if value is not correct UUID
        except ValueError:
            pass

app = Eve(validator=MyValidator)

if __name__ == '__main__':
    app.run()
