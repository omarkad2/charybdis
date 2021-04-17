# Charybdis
### Demo spring boot application using Charybdis: [charybdis-demo](https://github.com/omarkad2/charybdis-demo)
[![Build Status](https://travis-ci.org/omarkad2/charybdis.svg?branch=master)](https://travis-ci.org/omarkad2/charybdis)
[![codecov](https://codecov.io/gh/omarkad2/charybdis/branch/master/graph/badge.svg)](https://codecov.io/gh/omarkad2/charybdis)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ma.markware.charybdis/charybdis-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ma.markware.charybdis/charybdis-core)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/omarkad2/charybdis/issues)
[![License](https://img.shields.io/github/license/fridujo/spring-automocker.svg)](https://opensource.org/licenses/Apache-2.0)

*Charybdis* is an Object Mapping framework for Cassandra database.
 
It provides an abstraction over the Datastax driver, and adds a set of tools in order 
to offer a seamless Model-Transformation between *POJOs* and database entities 
while ensuring optimal performance.

Charybdis uses Java annotation processing (APT) to generate the needed metadata for mapping and querying 
the database.

In this regard, Charybdis, unlike ORM libraries working at runtime, offers the following advantages:
- **High performance**: Use plain java methods to serialize and deserialize database entities at 
runtime, since most work is done at compile-time. 
- **Model validation**: Get error reports at build-time when models are incomplete 
or incorrect.
- **Seamless model-transformation**: Complex java data structures can be transformed seamlessly to 
Cassandra data types.

## Installation
### Maven
Add the following dependency to your **pom.xml**

```xml
<dependency>
    <groupId>ma.markware.charybdis</groupId>
    <artifactId>charybdis-core</artifactId>
    <version>2.3.1</version>
</dependency>
```

<!--### Gradle-->
<!--Add the following dependency to your **build.gradle**-->
<!--```groovy-->
<!--repositories {-->
<!--    mavenCentral()-->
<!--}-->

<!--dependencies {-->
<!--    testCompile('com.github.charybdis:charybdis-core:1.0.0')-->
<!--}-->
<!--```-->

## Compatibility
Charybdis is compatible with Apache Cassandra 2.1 and higher.
 
It requires Java 8 or higher.

## Usage
Let's design a real-world example that has the following:
- Keyspace **keyspace_demo**
- Table **user**
- User-defined type (UDT) **address** (column in user).
- User-defined type (UDT) **country** (field in address).

### Modeling
#### Keyspace modeling
Keyspace **keyspace_demo**:
```java
@Keyspace(name = "keyspace_demo")
public class KeyspaceDemo {}
```
After compile, this generates class `KeyspaceDemo_Keyspace` with the needed metadata.

#### Udt modeling
Udt **address**:
```java
@Udt(keyspace = "keyspace_demo", name = "address")
public class Address {

    @UdtField
    private Integer number; // Avoid using primitive types in model class!
    
    @UdtField
    private String street;
    
    @UdtField
    private String city;
    
    @UdtField
    private @Frozen Country country;
    
    // Public no-arg constructor, getter and setters ...
}
```
After compile, this generates class `Address_Udt` with the needed metadata.

Udt **country**:
```java
@Udt(keyspace = "keyspace_demo", name = "country")
public class Country {

    @UdtField(name = "country_name")
    private String countryName;
    
    @UdtField(name= "country_code")
    private String countryCode;
    
    // Public no-arg constructor, getter and setters ...
}
```
After compile, this generates class `Country_Udt` with the needed metadata.

#### Table modeling
Table **user**:

```java
@Table(keyspace = "keyspace_demo", name = "user")
public class User extends AbstractUser {

    @Column
    @PartitionKey
    @GeneratedValue // Generates id automatically when using Crud API
    private UUID id;

    @Column(name = "joining_date")
    @ClusteringKey(index = 0, order = ClusteringOrder.DESC)
    private Instant joiningDate;

    @Column
    private List<@Frozen Address> addresses;

    @Column
    @Index(name = "access_role") // Generates a secondary index on this column
    private RoleEnum role;

    @Column(name = "access_logs")
    private Map<Instant, String> accessLogs;
  
    // Public no-arg constructor, getter and setters ...
}
```
After compile, this generates class `User_Table` with the needed metadata.

#### Materialized View modeling
Cassandra Materialized View is also supported, it allows to create a query only table from a base table.

Materialized view **user_by_role** using base table **user** modeled using class **User.java** (see above).

```java
@MaterializedView(keyspace = "keyspace_demo", baseTable = User.class, name = "user_by_role")
public class UserByRole extends AbstractUser {

    @Column
    @PartitionKey
    private RoleEnum role;
    
    @Column
    @ClusteringKey
    private UUID id;

    @Column(name = "joining_date")
    @ClusteringKey(index = 1, order = ClusteringOrder.DESC)
    private Instant joiningDate;

    @Column
    private List<@Frozen Address> addresses;
    
    // Public no-arg constructor, getter and setters ...
}
```
After compile, this generates class `UserByRole_View` with the needed metadata.

### Code Generation
After project is built, some classes are generated by Charybdis APT. 
These classes are implementations of Charybdis' metadata APIs.
Additionally, we generate DDL scripts, loaded in classpath, to help you create or reset your database.

The following Cql files are generated for the modelling above:
- ddl_create.cql
    ```cql
    CREATE KEYSPACE IF NOT EXISTS keyspace_demo WITH REPLICATION={'class' : 'SimpleStrategy', 'replication_factor' : 1};
    CREATE TYPE IF NOT EXISTS keyspace_demo.address(number int,street text,city text,country frozen<country>);
    CREATE TYPE IF NOT EXISTS keyspace_demo.country(country_name text,country_code text);
    CREATE TABLE IF NOT EXISTS keyspace_demo.user(id uuid,joining_date timestamp,addresses list<frozen<address>>,role text,access_logs map<timestamp,text>,creation_date timestamp,last_updated_date timestamp,PRIMARY KEY(id, joining_date))WITH CLUSTERING ORDER BY(joining_date DESC);
    CREATE INDEX IF NOT EXISTS user_role_idx ON keyspace_demo.user(role);
    ```
- ddl_drop.cql
    ```cql
    DROP KEYSPACE IF EXISTS keyspace_demo;
    DROP TYPE IF EXISTS keyspace_demo.country;
    DROP TYPE IF EXISTS keyspace_demo.address;
    DROP TABLE IF EXISTS keyspace_demo.user;
    DROP INDEX IF EXISTS keyspace_demo.user_role_idx;
    ```

### Querying
In order to interact with our database, we need to instantiate [CqlTemplate](https://github.com/omarkad2/charybdis/blob/master/core/src/main/java/ma/markware/charybdis/CqlTemplate.java).

It is the main class in Charybdis core package. It can be instantiated by providing an implementation of [SessionFactory](https://github.com/omarkad2/charybdis/blob/master/core/src/main/java/ma/markware/charybdis/session/SessionFactory.java),
if none provided we fallback on [DefaultSessionFactory](https://github.com/omarkad2/charybdis/blob/master/core/src/main/java/ma/markware/charybdis/session/DefaultSessionFactory.java).
```java
CqlTemplate cqlTemplate = new CqlTemplate();
// Or with a specific implementation of SessionFactory
CqlTemplate cqlTemplate = new CqlTemplate(customSessionFactory);
```
This class gives us access to different APIs **Dsl API** or **Crud API** in order to manage our Cql queries. 
#### Dsl API

- Insert:
    ```java
    List<Adress> addresses = List.of(...);
    boolean applied = cqlTemplate.dsl().insertInto(User_Table.user, User_Table.id, User_Table.joiningDate, User_Table.addresses)
                       .values(UUID.randomUUID(), Instant.now(), addresses)
                       .ifNotExists()
                       .execute();
    ```

- Update:
    ```java
    boolean applied = cqlTemplate.dsl().update(User_Table.user)
                       .set(User_Table.addresses.entry(0), new Address(...)) // Updates address at index 0.
                       .set(User_Table.role, RoleEnum.ADMIN)
                       .set(User_Table.accessLogs, User_Table.accessLogs.append(Map.of(Instant.now(), "Ubuntu"))) // Adds entry to column 'access_logs'
                       .execute();
    ```

- Select: 
    ```java
    User user = cqlTemplate.dsl().selectFrom(User_Table.user)
                       .where(User_Table.joiningDate.lt(Instant.parse("2020-01-01T00:00:00Z")))
                       .allowFiltering()
                       .fetchOne();
    ```
- Delete:
    ```java
    boolean applied = cqlTemplate.dsl().delete()
                       .from(User_Table.user)
                       .where(User_Table.id.eq(UUID.fromString("c9b593c0-f5cb-4e88-bd55-88dee10a4e97")))
                       .execute();
    ```
#### Crud API
- Insert:
    ```java
    User persistedUser = cqlTemplate.crud().create(User_Table.user, new User(...));
    ```

- Update:
    ```java
    persistedUser.setJoiningDate(Instant.now());
    persistedUser = cqlTemplate.crud().update(User_Table.user, persistedUser);
    ```

- Select: 
    ```java
    Optional<User> adminUser = cqlTemplate.crud().findOptional(User_Table.user, User_Table.id.eq(userId)
                                                                        .and(User_Table.joiningDate.lt(Instant.now()))
                                                                        .and(User_Table.role.eq(RoleEnum.ADMIN)))
    ```
- Delete:
    ```java
    boolean deleted = cqlTemplate.crud().delete(User_Table.user, persistedUser);
    ```

### Batch queries
Charybdis also supports Cql Batch queries. For convenience we chose to have the same syntax as before to build batch enclosed queries, using both **Crud** and **Dsl** APIs.

Let's suppose we have two tables : 
- **person** : table with partition key id.
- **person_by_ssn** : table with same data in table person but with different partition key "ssn" *(social security number)*.
#### Batch logged
```java
// Instantiate batch API
Batch batch = cqlTemplate.batch().logged();

// Add update query to our batch on table 'person' (Dsl API)
cqlTemplate.dsl(batch).update(Person_Table.person)
           .set(Person_Table.name, "John Doe")
           .where(Person_Table.id.eq(1))
           .execute();
// Add update query to our batch on table 'person_by_ssn' (Dsl API)
cqlTemplate.dsl(batch).update(PersonBySsn_Table.person_by_ssn)
           .set(PersonBySsn_Table.name, "John Doe")
           .where(PersonBySsn_Table.ssn.eq("999-00-1111"))
           .execute();

// Execute batch query
batch.execute();
```

#### Batch Unlogged
```java
// Instantiate batch API
Batch batch = cqlTemplate.batch().unlogged();

// Add insert query to our batch on table 'person' (Dsl API)
cqlTemplate.dsl(batch).insertInto(Person_Table.person, Person_Table.id, Person_Table.ssn, Person_Table.name)
           .values(1, "999-00-1111", "Franklin Roosevelt")
           .execute();
// Add insert query to our batch on table 'person' (Crud API)
new Person newPerson = new Person(1, "999-00-2222", "Theodore Roosevelt"); 
cqlTemplate.crud(batch).create(Person_Table.person, newPerson);

// Execute batch query
batch.execute();
```
#### Programmatic Batch Management
It is also possible to execute a bunch of write queries as a single batch with the following:
```java
cqlTemplate.executeAsLoggedBatch(() -> {
  // Query1: Persist person entity using Dsl API
  cqlTemplate.dsl().insertInto(Person_Table.person, Person_Table.id, Person_Table.ssn, Person_Table.name)
             .values(1, "999-00-1111", "Franklin Roosevelt")
             .execute();
  // Query2: Persist person_by_ssn using a custom service (which in turn uses Charybdis's Crud or Dsl API)
  personService.persistPersonBySsn(person);
});
```
Both queries *Query1* and *Query2* will be executed as a single logged batch query.

### Lightweight Transaction
Charybdis also handles lightweight transactions in order to prevent race conditions in
cases where strong consistency is not enough and client needs to read, then write data.
- LWT for Insert:

    Insert a row only if it doesn't exist
    ```java
    cqlTemplate.dsl().insertInto(User_Table.user, User_Table.id, User_Table.joiningDate, User_Table.addresses)
            .values(UUID.randomUUID(), Instant.now(), addresses)
            .ifNotExists()
            .execute();
    ```
- LWT for Update:

    Update a row only if it fulfills a condition (in the example below user must have *ADMIN* role)
    ```java
    cqlTemplate.dsl().update(User_Table.user)
               .set(User_Table.addresses, newAddresses)
               .where(User_Table.id.eq(UUID.fromString("c9b593c0-f5cb-4e88-bd55-88dee10a4e97")))
               .if_(User_Table.role.eq(RoleEnum.ADMIN))
               .execute();
    ```
- LWT for Delete:

    Delete a row only if it fulfills a condition (in the example below user must have *ADMIN* role)
    ```java
    cqlTemplate.dsl().delete()
            .from(User_Table.user)
            .where(User_Table.id.eq(UUID.fromString("c9b593c0-f5cb-4e88-bd55-88dee10a4e97")))
            .if_(User_Table.role.eq(RoleEnum.ADMIN))
            .execute();
    ```
 
### Tuneable Consistency
Consistency can be defined at table definition level, so as to be applied on all queries
involving said table, like the following:
```java
@Table(keyspace = "keyspace_demo", name = "table", writeConsistency = ConsistencyLevel.QUORUM, 
              readConsistency = ConsistencyLevel.QUORUM, serialConsistency = SerialConsistencyLevel.SERIAL)
public class Table {
    // attributes and methods ... 
}
```
   
We can also have a fine-grained control over consistency, by having a particular level for certain queries.
This can be done at runtime:
- In Crud API:
    ```java
    cqlTemplate.crud().withConsistency(ConsistencyLevel.EACH_QUORUM)
                 .withSerialConsistency(SerialConsistencyLevel.LOCAL_SERIAL)
                 .create(User_Table.user, new User(...));
    ```
- In Dsl API:
    ```java
    cqlTemplate.dsl().withConsistency(ConsistencyLevel.EACH_QUORUM)
            .withSerialConsistency(SerialConsistencyLevel.LOCAL_SERIAL)
            .insertInto(User_Table.user, User_Table.id, User_Table.joiningDate, User_Table.addresses)
            .values(UUID.randomUUID(), Instant.now(), addresses)
            .ifNotExists()
            .execute();
    ```

### Asynchronous Capabilities
All queries can be executed in an asynchronous manner, using datastax asynchronous API. Asynchronous methods return instances of *java.util.concurrent.CompletableFuture*. 

Here are some examples:
- In Dsl API:
    ```java
    CompletableFuture<Boolean> futureInstance = 
                cqlTemplate.dsl().insertInto(User_Table.user, User_Table.id, User_Table.joiningDate, User_Table.addresses)
                                               .values(UUID.randomUUID(), Instant.now(), addresses)
                                               .ifNotExists()
                                               .executeAsync();
    ```

- In Batch API:
    ```java
    Batch batch = cqlTemplate.batch().unlogged();

    cqlTemplate.dsl(batch).insertInto(Person_Table.person, Person_Table.id, Person_Table.ssn, Person_Table.name)
               .values(1, "999-00-1111", "Franklin Roosevelt")
               .execute();
    new Person newPerson = new Person(1, "999-00-2222", "Theodore Roosevelt"); 
    cqlTemplate.crud(batch).create(Person_Table.person, newPerson);
    
    // Execute batch query asynchronously
    CompletableFuture<Void> futureInstance = batch.executeAsync();
    ```
Check documentation for more details: [Datastax Asynchronous API Doc](https://docs.datastax.com/en/developer/java-driver/4.2/manual/core/async/)

## Licensing
Charybdis is licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this project except in compliance with the License. 
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
