# Permissions

| Permission Node      | Description                                                   |
|----------------------|---------------------------------------------------------------|
| root                 | All permissions on all events                                 |
| event.create         | Allow creating events                                         |
| event.view           | Allow viewing events                                          |
| event.view.sensitive | Allow viewing sensitive data on events such as api token      |
| event.delete         | Allow deleting events                                         |
| event.all            | Grant access to all events regardless of token.allowed_events |
| token.create         | Allow creating tokens                                         |
