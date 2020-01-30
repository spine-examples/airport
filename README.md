# Airport

This repository contains an example of a multi-context Airport Management system.

The repository accompanies the article on third-party integration in Spine posted in our blog.

## Structure

The system consists of several modules. All of the modules represent a way of integrating with
the **Takeoffs & Landings** Bounded Context.

### Airplane Supplies Context

The **Airplane Supplies Context** Bounded Context and the **Takeoffs & Landings** Bounded Context
constitute a Customer/Supplier models pair.

### Weather Context

**Takeoffs & Landings** conforms to the **Weather** Context by using its model without any
preliminary checks or transformations.

### Security Checks Context

**Takeoffs & Landings** sets up an anticorruption layer to transform and filter data received from
the **Security Checks** system.
