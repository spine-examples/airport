![Build Status][actions-badge] &nbsp;
[![license][license-badge]](http://www.apache.org/licenses/LICENSE-2.0)


[actions-badge]: https://github.com/actions/hello-world/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master
[license-badge]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat

# Airport

This repository contains an example of a multi-context Airport Management system.

The repository accompanies the [“Integration with a Third Party”][integration] guide.

[integration]: https://spine.io/docs/guides/integration.html

## Structure

The system consists of several modules. All of the modules represent a way of integrating with
the **Takeoffs & Landings** Bounded Context.

### Takeoffs & Landings

The **Takeoffs & Landings** system is responsible for making decisions about allowing or not
allowing aircraft to take off or land in the airport.

### Airplane Supplies Context

The **Airplane Supplies Context** system ensures that aircraft are fueled and prepared for the
upcoming flight.

The **Airplane Supplies Context** Bounded Context and the **Takeoffs & Landings** Bounded Context
constitute a Customer/Supplier models pair.

### Weather Context

The **Weather** system is the software which runs a meteostation and publishes its measurements.

**Takeoffs & Landings** conforms to the **Weather** Context by using its model without any
preliminary checks or transformations.

### Security Checks Context

The **Security Checks** system is the software which registers the passengers who pass through
the airport security. The system is heavily audited and thus the cost of changing it is big.

**Takeoffs & Landings** sets up an anticorruption layer to transform and filter data received from
the **Security Checks** system.
