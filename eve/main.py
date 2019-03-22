from eve import Eve
from eve.io.mongo import Validator
from uuid import UUID

class MyValidator(Validator):
    def _validate_type_uuid(self, value):
        try:
            UUID(value) # would throw error if value is not correct UUID
            return True
        except ValueError:
            pass
            return False

app = Eve(validator=MyValidator)

if __name__ == '__main__':
    app.run()
