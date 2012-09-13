package com.strategicgains.repoexpress.eclipselink;



/**
 * ConventionBasedDescriptor defines the interface for objects that support O/R mapping
 * in a convention-over-configuration fashion.  This interface describes abbreviated
 * ways of describing inter-class relationships so implementors can derive mapping
 * details.
 * 
 * Note that the relationship descriptor methods return a reference to {@link ConventionBasedDescriptorParameters}
 * which facilitates the chaining of calls to the returned instance so the relationship
 * can be fully described in a single statement, if desired.
 * 
 * For example:
 * 
 * 		hasMany(Employee.class, "employeeMap")
 * 			.setColumnName("EMP")
 * 			.setMapClass(HashMap.class)
 * 			.setAccessorMethodName("getEmployeeId");
 * 
 * @author Todd Fredrich
 * @since Apr 13, 2007
 */
public interface ConventionBasedDescriptor
{
	//DIRECT TO FIELD
	
	/**
	 * Mark a particular object attribute as non-persistent.  Calling ignore for an attribute
	 * name means that attribute will not be mapped to a column in the database.
	 * 
	 * @param attributeName the name of an object attribute that will not be stored in the database.
	 */
	public void ignore(String attributeName);


	//RELATIONSHIPS

	/**
	 * Indicate that there is a back-reference relationship to another class.
	 * 
	 * @param relatedClass the related class to which this object belongs.
	 * @param attributeName the name of the object attribute that refers to the owner.
	 * @return {@link ConventionBasedDescriptorParameters} to enable configuration of back-reference relationship settings.
	 */
	public ConventionBasedDescriptorParameters belongsTo(Class<?> relatedClass, String attributeName);
	
	/**
	 * Indicate a one-to-one relationship that is not privately held.
	 * 
	 * @param relatedClass the related class.
	 * @param attributeName the attribute referring to the related class.
	 * @return {@link ConventionBasedDescriptorParameters} to enable configuration of 1:1 relationship settings.
	 */
	public ConventionBasedDescriptorParameters hasOne(Class<?> relatedClass, String attributeName);

	/**
	 * Indicate a one-to-one relationship where the related object is
	 * owned: meaning that when the owner is deleted, so is the owned object.
	 *  
	 * @param relatedClass the related class.
	 * @param attributeName the attribute referring to the related class.
	 * @return {@link ConventionBasedDescriptorParameters} to enable configuration of 1:1 owned relationship settings.
	 */
	public ConventionBasedDescriptorParameters ownsOne(Class<?> relatedClass, String attributeName);

	/**
	 * Indicate a one-to-many relationship held as a collection in the 
	 * containing object.
	 * 
	 * @param relatedClass the related class.
	 * @param attributeName the attribute referring to the related class.
	 * @return {@link ConventionBasedDescriptorParameters} to enable configuration of 1:M relationship settings.
	 */
	public ConventionBasedDescriptorParameters hasMany(Class<?> relatedClass, String attributeName);
	
	/**
	 * Indicate a one-to-many relationship where the related objects are
	 * owned: when the owner is deleted, so are the owned objects.
	 * 
	 * @param relatedClass the related class.
	 * @param attributeName the attribute referring to the related class.
	 * @return {@link ConventionBasedDescriptorParameters} to enable configuration of 1:M owned relationship settings.
	 */
	public ConventionBasedDescriptorParameters ownsMany(Class<?> relatedClass, String attributeName);

	/**
	 * Indicate a many-to-many relationship.
	 * 
	 * @param relatedClass the related class.
	 * @param attributeName the attribute referring to the related class.
	 * @return {@link ConventionBasedDescriptorParameters} to enable configuration of M:M relationship settings.
	 */
	public ConventionBasedDescriptorParameters hasManyToManyWith(Class<?> relatedClass, String attributeName);

	/**
	 * Indicate a many-to-many relationship where the related objects are
	 * owned: when the owner is deleted, so are the owned objects.
	 * 
	 * @param relatedClass the related class.
	 * @param attributeName the attribute referring to the related class.
	 * @return {@link ConventionBasedDescriptorParameters} to enable configuration of M:M owned relationship settings.
	 */
	public ConventionBasedDescriptorParameters ownsManyToManyWith(Class<?> relatedClass, String attributeName);

	
	//INHERITANCE

	/**
	 * Indicate inheritance, providing a sub-class.
	 * 
	 * @param childClass the sub-class.
	 */
	public void parentOf(Class<?> childClass);

	/**
	 * Indicate inheritance, providing the super-class.
	 * 
	 * @param parentClass the super-class.
	 */
	public void childOf(Class<?> parentClass);
}
