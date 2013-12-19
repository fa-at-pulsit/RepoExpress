**Build Status** [![Build Status](https://buildhive.cloudbees.com/job/RestExpress/job/RepoExpress/badge/icon)](https://buildhive.cloudbees.com/job/RestExpress/job/RepoExpress/)

**Waffle.io** [![Stories in Ready](https://badge.waffle.io/RestExpress/RepoExpress.png?label=ready)](https://waffle.io/RestExpress/RepoExpress)

RepoExpress is a thin wrapper on MongoDB and Morphia to provide a simple and easy way to create
domain storage options using the Repository pattern.

Designed to be utilized in conjunction with RestExpress, RepoExpress is easy to use:
1) Create a new InMemoryRepository() or new MongodbRepository() extending them if necessary.
2) Annotate your domain model using the Morphia annotations.
3) Call the CRUD methods.
4) Extend the repository if custom queries or other unique functionality is required.

Dependencies:
* If MongodbRepository is used, MongoDB must be running.
* MongoDB drivers (jar).
* Morphia mapping library (jar).
* Voldemort drivers (jar).
* Junit, to run unit tests.

Change History:
===================================================================================================
Release 0.3.4 - SNAPSHOT (in branch 'master')
* Voldemort support is deprecated.

Release 0.3.3 - 17 Jul 2013
* Fixed issue with MongoDB repository query filter implementation where it converted the
  value to lower-case erroneously.
* Added MongodbRepository.find(QueryFilter) method.
* Changed message in InvalidObjectIdException thrown within StringToByteArrayAdapter.convert().
* Changed generic type on AbstractRepositoryObserver from T extends TimestampedIdentifiable to T extends Identifiable.
* Changed generic type on types parameter of MongodbEntityRepository constructor from Class<T> to Class<? extends T> to allow subclasses of T to be passed in.
* Fixed issued in configureQueryFilter(), removing call to toLowerCase() on the value portion of the contains query.
* Introduced MongodbUuidEntityRepository and related support classes, including UuidConverter
  to produce/parse URL-safe UUID instances.
* Changed MongodbRepository.configureQueryFilter() to leverage FilterOperator settings in FilterComponent
  and support Object values in FilterComponent.

Release 0.3.2
* Updated to latest RestExpress-Common, refactoring for QueryFilter addition of operator.

Release 0.3.1
* Introduced Maven build.
* Switched to com.github.jmkgreen.morphia version of Morphia (v 1.2.2)

Release 0.3.0
* Refactored to create separate jars for common functionality and DB-dependent functionality.
  Now creates 2 jars: RepoExpress...jar and RepoExpress...-mongodb.jar
* Introduced AbstractTimestampedIdentifiable which has default behavior for TimestampedIdentifiable.
* Introduced AbstractMongodbPersistable which extends AbstractTimestampedIdentifiable and includes
  id that is a MongoDB ObjectId.  get/set-Id() methods adapt the string to/from an ObjectId.
* DefaultTimestampedIdentifiableRepositoryObserver now also sets updatedAt on creation.
* Introduced Voldemort repository.
* Added Redis repository, using JOhm.

Release 0.2.2
* Exposed Mongo driver instance in case you need it for something tricky (that Morphia doesn't
  support) in sub-classes.
* Fixed issue in MongodbRepository.exists() where it wasn't calling convertId() and using new
  ObjectId() directly.
* Added try/catch in MongodbRepository.doDelete(String) to catch InvalidObjectIdException and
  translate it to NotFoundException.
* Introduced InvalidObjectIdException to enable implementations of IdentifierAdapter to throw
  it when it cannot convert the string to an internal identifier.  This enables upstream callers
  to handle it and identify the issue.

Release 0.2
* Update MongodbRepository to allow use of Replication Sets.

Release 0.1
* Initial import. Combined concepts from RestExpress and JigForJava full-stack framework.

Maven Usage
===========
Stable:
```xml
		<dependency>
			<groupId>com.strategicgains.repoexpress</groupId>
			<artifactId>repoexpress-mongodb</artifactId>
			<version>0.3.2</version>
		</dependency>
```
Development:
```xml
		<dependency>
			<groupId>com.strategicgains.repoexpress</groupId>
			<artifactId>repoexpress-mongodb</artifactId>
			<version>0.3.3-SNAPSHOT</version>
		</dependency>
```
Or download the jar directly from: 
http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22RestExpress%22

Note that to use the SNAPSHOT version, you must enable snapshots and a repository in your pom file as follows:
```xml
  <profiles>
    <profile>
       <id>allow-snapshots</id>
          <activation><activeByDefault>true</activeByDefault></activation>
       <repositories>
         <repository>
           <id>snapshots-repo</id>
           <url>https://oss.sonatype.org/content/repositories/snapshots</url>
           <releases><enabled>false</enabled></releases>
           <snapshots><enabled>true</enabled></snapshots>
         </repository>
       </repositories>
     </profile>
  </profiles>
```
