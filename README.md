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
