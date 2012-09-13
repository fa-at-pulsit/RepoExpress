package com.strategicgains.repoexpress.eclipselink;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;

import com.strategicgains.repoexpress.eclipselink.annotation.UnpersistedProperty;
import com.strategicgains.repoexpress.eclipselink.relations.BackReferenceRelationship;
import com.strategicgains.repoexpress.eclipselink.relations.ManyToManyRelationship;
import com.strategicgains.repoexpress.eclipselink.relations.OneToManyRelationship;
import com.strategicgains.repoexpress.eclipselink.relations.OneToOneRelationship;
import com.strategicgains.repoexpress.eclipselink.relations.RelationshipDefinition;
import com.strategicgains.repoexpress.eclipselink.util.ClassUtils;
import com.sun.xml.internal.ws.util.StringUtils;

/**
 * <p>Enables use of convention-over-configuration for EclipseLink O/R mapping.  Using the
 * API described in this class, a developer can depend on naming conventions for
 * database columns and have this class automatically, at run-time generate a TopLink
 * descriptor for a it based on the described relationships and direct-to-
 * field attributes.</p>
 * 
 * <p>Subclasses override configureRelationships() where calls to hasOne(), ownsOne(),
 * belongsTo(), hasMany(), ownsMany(), hasManyToManyWith(), ownsManyToManyWith(),
 * etc. are made.</p>
 *
 * <p>ConventionBasedEclipseLinkDescriptor extends EclipseLinkRelationalDescriptor, so 
 * functionality is not lost by utilizing this class.</p>
 *
 * <h2>Classes Mapped to Tables</h2>
 * <p>When using ConventionBasedEclipseLinkDescriptor, class names are mapped to a table
 * of the same name. However, while java classes are named with camel case, the
 * database table names are assumed to be separated with underscores ('_').
 * Therefore, automatic mapping ensures underscores are inserted before capital
 * letters in the camel case strings and the final result is converted to upper
 * case.</p>
 *
 * <p>For single-table inheritance, the parentOf(Class) and childOf(Class) methods are
 * available. For classes with parents, the class is mapped to the table of the
 * parent. Additionally, there is assumed to be a type indicator column on that
 * table called, TYPE_INDICATOR, which is a VARCHAR2 (or equivalent) and contains the
 * fully-qualified class name of the child as the indicator. <strong>Both parentOf() and
 * childOf() must be called to implement single-table inheritance.</strong> The parentOf()
 * method is called on the parent class passing the child class (not an instance).
 * And childOf() is called for all the children passing the parent class (not an
 * instance) as a parameter.</p>
 *
 * <b>For example:</b>
 * A 'UserSettings' class would get mapped to the 'USER_SETTINGS' table.
 * The 'CourseRoster' class would get mapped to the 'COURSE_ROSTER' table.
 * 'AGoofyClassNameXYZ' class would get mapped to the 'A_GOOFY_CLASS_NAME_XYZ'
 * table.
 *
 * <h2>Attributes Mapped to Columns</h2>
 * <p>By default, all direct-to-field (in a EclipseLink sense) object attributes are
 * mapped to columns of the same name. Camel case attribute names are treated the
 * same as camel case class names. Therefore, automatic mapping ensures underscores
 * are inserted before capital letters in the camel case strings and the final
 * result is converted to upper case to create a column name.</p>
 *
 * <p>When a class contains attributes that you don't want mapped to the database, a
 * call to ignore() with the attribute name will suppress it being mapped. Also, if
 * an attribute has a complex mapping type not directly supported by
 * ConventionBasedEclipseLinkDescriptor, you can ignore() the attribute or use the
 * @UnmappedProperty annotation then create your own mapping from the 
 * convenience methods on EclipseLinkRelationalMapping.</p>
 *
 * <p>ConventionBasedEclipseLinkDescriptor also ignores object attributes that have the
 * 'transient' modifier. It may be more convenient to denote your attributes that
 * way instead of using a call to ignore(). However, it may obfuscate your
 * intentions if transient is used simply for mapping purposes. Having ignore()
 * calls in the mapping descriptor may be more intention-revealing.</p>
 *
 * <p>Additionally, ConventionBasedEclipseLinkDescriptor ignores any attributes prefixed
 * with the word "this" and does not map them.</p>
 *
 * <b>For example:</b>
 * a 'userName' object attribute is mapped to the USER_NAME column.
 * 'aVeryLongAttributeNameForSomething' maps to A_VERY_LONG_ATTRIBUTE_NAME_FOR_SOMETHING.
 *
 * <p>Note that column name lengths may be an issue if your attribute names are long.
 * Oracle 10g supports column names longer-than 31 characters. But it may be
 * important to note the length of columns supported by the underlying DB platform.
 * Also see the section 'Changing the Default Mapping Behavior' below to override
 * table and column names, if needed.</p>
 *
 * <h2>'Owns' vs. 'Has' Relationships</h2>
 * <p>ConventionBasedEclipselinkDescriptor makes a distinction between relationships of
 * ownership and those of referral. Ownership relationships (represented by the
 * owns...() methods) imply that the relationship is privately held and if the
 * owner gets deleted, so does the owned object(s). This is the same as an EclipseLink
 * privately held relationship. Referral relationships (represented by the has...()
 * methods) imply that the object only references the other object and the object
 * referred-to does not get deleted when the referring object is deleted.</p>
 *
 * <h2>Single-Table Inheritance</h2>
 * <p>ConventionBasedEclipselinkDescriptor supports single-table inheritance (all the
 * classes in a hierarchy stored in the same table--as opposed to multi-table
 * inheritance where each class is stored in its own table). This is accomplished
 * via the parentOf() and childOf() operations.</p>
 *
 * <p>To implement single-table inheritance, the parent class descriptor calls
 * parentOf() passing the child class as a parameter. Also, to complete the
 * implementation, the child class descriptor must also call childOf() passing the
 * parent class as a parameter. By convention, the table will be named after the
 * parent class. If there are multiple layers of inheritance, the table will be
 * named after the root class.</p>
 *
 * <p>For simplicity, the examples below do not involve single-table inheritance
 * hierarchies. Thus, it is important to note that if such hierarchies were
 * involved, the convention would assume table names (and primary keys) for the
 * root class instead of the related class.</p>

 * <b>For Example:</b>
 * <pre>
 * class ParentDescriptor
 * ...
 *     parentOf(Child.class);
 * ...
 *
 * class ChildDescriptor
 * ...
 *     childOf(Parent.class);
 *     parentOf(GrandChild.class);
 * ...
 *
 * class GrandChildDescriptor
 * ...
 *     childOf(Child.class);
 * ...
 * </pre>
 * <p>Assumes the Parent, Child and GrandChild classes will be stored in a PARENT
 * table, with PARENT.OID being the primary key.</p>
 *
 * <h2>Multi-Table Inheritance</h2>
 * <p>It turns out that ConventionBasedEclipseLinkDescriptor supports multi-table
 * inheritance where portions of an object may span multiple tables. If you end up
 * needing that functionality, use the addTableName() EclipseLink API in the descriptor
 * constructor.</p>
 *
 * <h2>One to One Relationships</h2>
 * <p>One to one relationships are mapped using the hasOne() and ownsOne() APIs,
 * passing the class and name of the referring attribute.</p>
 *
 * <p>By convention, the foreign key column is assumed to be the attribute name with
 * the camel case converted to underbar-delimited, upper-case, with '_OID' appended
 * to it. It will relate (in the mapping) by convention to the table corresponding
 * to the related class's primary key (OID).</p>
 *
 * <b>For Example:</b>
 * <pre>
 * class User
 * ...
 *     ValueHolderInterface userSettings; //UserSettings reference
 * ...
 *
 * class UserDescriptor
 * ...
 *     ownsOne(UserSettings.class, "userSettings");
 * ...
 * </pre>
 * <p>Assumes a USER table with a USER_SETTINGS_OID column that would be mapped to
 * reference the USER_SETTINGS.OID column.</p>
 *
 * <h2>Variable One to One Relationships</h2>
 * <p>A variable one-to-one relationship utilizes the same foreign key column in
 * the referring object to reference various object types.</p>
 * 
 * <p>By mapping the same attribute multiple times, presumably referring to a
 * different class each time, <tt>ConventionBasedEclipseLinkDescriptor</tt> creates
 * a VariableOneToOneMapping and assumes a column named TYPE_INDICATOR exists
 * in the underlying table.  This works for hasOne(), ownsOne() and belongsTo()
 * mappings. The TYPE_INDICATOR column will contain the simple class name
 * corresponding to the object type to which the foreign key refers.</p>
 * 
 * <b>For example:</b>
 * <pre>
 * hasOne(ClassOne.class, "reference");
 * hasOne(ClassTwo.class, "reference");
 * </pre>
 * 
 * <h2>One to Many Relationships</h2>
 * <p>One to many relationships are mapped using the hasMany() and ownsMany() methods.
 * However, it is necessary to also have a back-reference from the referenced class
 * back to the referring class, mapped with a belongsTo() in the referenced-class
 * descriptor.</p>
 *
 * <p>As you know, in this case, the foreign key column ends up in the referenced
 * table as opposed to the referencing table as they do in the class structure.
 * Hence the O/R impedance mismatch.</p>
 *
 * <b>For Example:</b>
 * <pre>
 * class ResourceBundle
 * ...
 *     ValueHolderInterface resources; //List of Resource.
 * ...
 *
 * class Resource
 * ...
 *     ValueHolderInterface owningBundle; //Back-reference 1:1 to ResourceBundle.
 * ...
 *
 * class ResourceBundleDescriptor
 * ...
 *     ownsMany(Resource.class, "resources");
 * ...
 *
 * class ResourceDescriptor
 * ...
 *     belongsTo(ResourceBundle.class, "owningBundle");
 * ...
 * </pre>
 * <p>By convention assumes ResourceBundle maps to table RESOURCE_BUNDLE, Resource
 * maps to table RESOURCE. And the RESOURCE table has a column named
 * OWNING_BUNDLE_OID which references the RESOURCE_BUNDLE.OID column.</p>
 *
 * <h2>Many to Many Relationships</h2>
 * <p>Many to many relationships are mapped using the hasManyToManyWith() and
 * ownsManyToManyWith() methods. Many to many relationships require a cross-reference
 * table in the underlying RDBMS.  By convention, this descriptor assumes the name to
 * be the names of the two classes concatenated with an underscore between them, suffixed
 * with "_X".  See the description below for more information and examples.</p>
 *
 * <b>For Example:</b>
 * <pre>
 * class Course
 * ...
 *     ValueHolderInterface students;  //List of Student.
 * ...
 *
 * class Student
 * ...
 *     List Courses;  //List of Course.
 * ...
 *
 * class CourseDescriptor
 * ...
 *     hasManyToManyWith(Student.class, "students");
 * ...
 *
 * class StudentDescriptor
 * ...
 *     hasManyToManyWith(Course.class, "courses");
 * ...
 * </pre>
 * <p>The above example results in the convention assuming three tables named, STUDENT, COURSE, and
 * COURSE_STUDENT_X. The table COURSE_STUDENT_X is assumed to have two columns,
 * COURSE_OID and STUDENT_OID. The Student attribute, courses, is mapped to reference
 * the COURSE_STUDENT_X.STUDENT_OID column while the Course students attribute is
 * mapped to reference the COURSE_STUDENT_X.COURSE_OID column.</p>
 *
 * <p>You may be asking yourself, "Why 'COURSE_STUDENT_X' instead of
 * 'STUDENT_COURSE_X'" Good question. And the answer is very simple: because 'C'
 * comes before 'S' in the western alphabet. The descriptor assumes that the cross table
 * names are created by concatenating the root class names involved, in alphabetic
 * order. So a many-to-many mapping involving 'Orange' and 'Oat' classes would
 * result in a 'OAT_ORANGE_X' table.</p>
 *
 * <p>It is valuable to note that for many-to-many relationships, only one of the two
 * mappings can be writable, if using ownsManyToManyWith(). Both descriptors cannot
 * be specified as privately-held relationships (e.g. one of the mappings must use
 * <b>has</b>ManyToManyWith()).</p>
 *
 * <h2>Changing the Default Mapping Behavior</h2>
 * <p>Every has...() and owns...() method returns an instance of ConventionBasedDescriptorParameters
 * which can be used to modify the default mapping behavior.  Additionally, each setting method
 * on ConventionBasedDescriptorParameters returns the same instance so method cascading or chaining
 * can be performed.</p>
 * 
 * <b>For example:</b>
 * <pre>
 * belongsTo(CourseRoster.class,
 *           CourseRoster.ROSTER_KEY)
 *           .setForeignKeyName("ROSTER_OID");
 *
 *ownsMany(Instructor.class,
 *         CourseRoster.INSTRUCTOR_KEY)
 *         .setCollectionClass(TreeSet.class);
 * </pre>
 *
 * <h2>Usage</h2>
 * <p>To simplify mapping tasks, the API for mapping classes to the database relates
 * only to the objects themselves, making assumptions about the database based on
 * class names, attribute names, and OIDs.</p>
 *
 * <b>For example:</b>
 * <pre>
 * public class UserDescriptor
 * extends ConventionBasedEclipselinkDescriptor
 * {
 *     public UserDescriptor()
 *     {
 *         super(User.class);
 *     }
 *
 *     protected void configureRelationships()
 *     {
 *         //Inheritence
 *         childOf(Person.class);             //User extends Person.
 *         parentOf(SingleSignOnUser.class);  //SingleSignOnUser extends User.
 *         //Relationships
 *         ownsOne(UserSettings.class, "settings");       // 1:1 private relationship.
 *         ownsMany(Address.class, "addresses");          // 1:M private relationship.
 *         hasManyToManyWith(Job.class, "jobs");          // M:M relationship.
 *         belongsTo(OrganizationUnit.class, "orgUnit");  // 1:1 back-reference.
 *     }
 * }
 *
 * public class WmsProject
 * extends ConventionBasedEclipseLinkProject
 * {
 *     protected void configureDynamicDescriptors()
 *     {
 *         // This class has no relationships so no descriptor subclass is required.
 *         addDynamicDescriptor(new ConventionBasedEclipselinkDescriptor(DirectToFieldOnly.class));
 *         //Instantiate the UserDescriptor.
 *         addDynamicDescriptor(new UserDescriptor());
 *     }
 * }
 * </pre>
 * 
 * <h2>Limitations</h2>
 * <p>Because ConventionBasedEclipseLinkDescriptor automatically computes the back-references from/to
 * a given class (based on belongsTo()), if there is more-than one back-reference to a 
 * class, ConventionBasedEclipseLinkDescriptor may choose to use the wrong one.  Thus, <strong>multiple 
 * back-references between objects are not supported.</strong>  Also note that if 
 * ConventionBasedEclipseLinkDescriptor does not find a belongsTo() back-reference it will
 * search ownsOne() and hasOne() relationships to find a back-reference, which may end up using
 * the wrong one.</p>
 * 
 * <b>For Example:</b>
 * <pre>
 * belongsTo(Another.class, "firstBackReference");
 * belongsTo(Another.class, "secondBackReference");
 * </pre>
 * 
 * <p>Please note that this is the converse of the Variable one to one mapping described above as
 * here, the class is the same but the attribute names differ.  Above the referred-to class
 * differs, but the attribute names are the same.</p>
 * 
 * <p>This happens rarely, but could easily happen in the case where you have multiple 1-M
 * mappings in one object to another, where each 1-M collection is filtered, say 
 * active employees, retired employees, etc.  In this case, the employee class might have
 * multiple 1-1 back-references to the holder.  This would have to be mapped by hand in
 * postMappingGeneration().</p>
 * 
 * @author Todd Fredrich
 * @since Apr 13, 2007
 */
@SuppressWarnings("serial")
public class ConventionBasedEclipseLinkDescriptor
extends AbstractDescriptor
implements ConventionBasedDescriptor
{
	//CONSTANTS
    
	public static final String TABLE_NAME_PREFIX_KEY = "ConventionBasedTopLinkDescriptor.TABLE_PREFIX_KEY";

//    private static final Logger log = Logger.getLogger(ConventionBasedTopLinkDescriptor.class);

    protected static final String TABLE_COLUMN_DELIMITER = ".";
	private static final char DELIMITER = '_';
	private static final String OID = "OID";
	private static final String PK_SUFFIX = TABLE_COLUMN_DELIMITER + OID;
	private static final String FK_SUFFIX = DELIMITER + OID;
	private static final String RELATION_TABLE_SUFFIX = DELIMITER + "X";
    public static final String SEQUENCE_SUFFIX = DELIMITER + "SEQ";
	public static final String TYPE_INDICATOR = "TYPE_INDICATOR";
	private static final String OID_ATTRIBUTE = "oid";
    
    private static String tableNamePrefix = "";


	//INSTANCE VARIABLES

	/*
	 * Names of attributes that should not be mapped.  See ignore().
	 */
	private List<String> ignoredAttributes = new ArrayList<String>();

	/*
	 * Names of attributes that are 1-1 back references.  See belongsTo().
	 */
	private List<String> ownerAttributes = new ArrayList<String>();
	
	/*
	 * Describes inheritance.  A list of classes that extend the class described
	 * herein.  See parentOf().
	 */
	private List<Class<?>> children = new ArrayList<Class<?>>();
	
	/*
	 * Describes inheritance.  The class that the class described herein extends.  There
	 * can only be one.  See childOf().
	 */
	private Class<?> parent = null;

	/*
	 * A list of attribute names that represent 1-Many relationships.  Names are kept 
	 * separately from the oneToManyRelationships list of relation descriptors to 
	 * facilitate contains('attributeName') checking.  See isOneToManyField().
	 */
	private List<String> oneToManyAttributes = new ArrayList<String>();
	
	/*
	 * A list of attribute names that represent 1-1 relationships.  Names are kept 
	 * separately from the oneToOneRelationships list of relation descriptors to 
	 * facilitate contains('attributeName') checking.  See isOneToOneField().
	 */	
	private List<String> oneToOneAttributes = new ArrayList<String>();
	
	/*
	 * A list of attribute names that represent Many-Many relationships.  Names 
	 * are kept separately from the manyToManyRelationships list of relation
	 * descriptors to facilitate contains('attributeName') checking.  See
	 * isManyToManyField().
	 */	
	private List<String> manyToManyAttributes = new ArrayList<String>();

	/**
	 * A list of relationship descriptors that describe directional relationships
	 * from this class to others.
	 */
	private List<RelationshipDefinition> relationships = new ArrayList<RelationshipDefinition>();
	
	/**
	 * True if the caller has set the table name directly, overriding
	 * the convention-based table name generation.
	 */
	private boolean hasUserDefinedTableName = false;

    /**
     * A Map of custom column names set specifically in a subclass descriptor
     * to override a dynamically generated column name. Intended for use when
     * the dynamically generated column name is too long due to database limitations.
     */
    private Map<String, String> customColumnNames = new HashMap<String, String>();
    
	//CONSTRUCTING

	/**
	 * Create a new ConventionBasedTopLinkDescriptor for the given class.  For
	 * classes that do not have relationships (inheritance or referential), simply
	 * calling this constructor is sufficient to map all of the fields as direct-
	 * to-field mappings.  Although, it still needs to be added to the Toplink
	 * Project (see ConventionBasedTopLinkProject).
	 * 
	 * @param describedClass the class that this descriptor is for.
	 * @see ConventionBasedProject
	 */
	public ConventionBasedEclipseLinkDescriptor(Class<?> describedClass)
	{
		super();
		setJavaClass(describedClass);
		configureRelationships();
	}


	//ACCESSING
	
	/**
	 * Return the parent (per single-table inheritance) of this class.  The parent
	 * gets set via childOf().
	 * 
	 * @return the single-table inheritance parent or null if single-table inheritance
	 *         is not in play for this object.
	 */
	public Class<?> getParent()
	{
		return parent;
	}

	/**
	 * Returns the user-set (via setPrimaryKeyName()) primary key name for this descriptor, overriding
	 * the convention-based behavior of this descriptor.  If using the convention-based behavior of this
	 * descriptor, returns null.
	 * 
	 * @return the user-set primary key name, otherwise null if using convention-based primary key.
	 */
	public String getPrimaryKeyName()
	{
		return (getPrimaryKeyFieldNames().isEmpty() ? null : (String) getPrimaryKeyFieldNames().get(0));
	}
	
	@Override
	public void setTableName(String name)
	{
		hasUserDefinedTableName = true;
		super.setTableName(name);
	}
    
    /**
     * Maps the attribute to the value of the columnName argument
     * instead of dynamically generating the column name. This
     * is intened to be used instead of having to ignore an attribute
     * and use addDirectMapping to customize a column name. 
     * 
     * @param attributeName the name of the object aspect to map according to this method
     * @param columnName the name of the column to which this attribute should be mapped
     */
    public void setColumnName(String attributeName, String columnName)
    {
        customColumnNames.put(attributeName, columnName);
    }


	//RELATIONSHIPS

	/**
	 * Describes a back-reference, one-to-one relationship which is required in the referred-to object
	 * in a one-to-many relationship.  There must be a corresponding hasMany() or ownsMany() call
	 * in the descriptor for the referring class.
	 * 
	 * @param relatedClass the related class to which this object belongs.
	 * @param attributeName the name of the object attribute that refers to the owner--holds the back-reference.
	 * @return {@link ConventionBasedDescriptorParameters} to enable configuration of back-reference relationship settings.
	 */
	public ConventionBasedDescriptorParameters belongsTo(Class<?> relatedClass, String attributeName)
	{
		ownerAttributes.add(attributeName);
		BackReferenceRelationship relationship = new BackReferenceRelationship(getJavaClass(), attributeName, relatedClass);
		relationships.add(relationship);
		return relationship.getParameters();
	}

	/**
	 * Describes a 1-Many relationship from the class described herein to another class.  There
	 * must be a corresponding call to belongsTo() in the descriptor for the related class.
	 * 
	 * @param relatedClass the referred-to class (the 'contained' class).
	 * @param attributeName the name of the object attribute holding the collection.
	 */
	public ConventionBasedDescriptorParameters hasMany(Class<?> relatedClass, String attributeName)
	{
		return addOneToManyRelationship(relatedClass, attributeName);
	}

	/**
	 * Describes a privately-held, 1-Many relationship from the class described herein to another
	 * class.  This means that when the 'owner' is removed, so are the elements in the privatly-held
	 * collection.  There must be a corresponding call to belongsTo() in the descriptor for the
	 * related class.
	 * 
	 * @param relatedClass the referred-to class (the 'contained' class).
	 * @param attributeName the name of the object attribute holding the collection.
	 */
	public ConventionBasedDescriptorParameters ownsMany(Class<?> relatedClass, String attributeName)
	{
		return addOneToManyRelationship(relatedClass, attributeName)
			.setOwned();
	}

	private ConventionBasedDescriptorParameters addOneToManyRelationship(Class<?> relatedClass, String attributeName)
	{
		oneToManyAttributes.add(attributeName);
		OneToManyRelationship relationship = new OneToManyRelationship(getJavaClass(), attributeName, relatedClass);
		relationships.add(relationship);
		return relationship.getParameters();
	}

	/**
	 * Describes a Many-Many relationship from the class described herein to another class. This
	 * implies a cross-reference table, conventionally assumed to be the names of the involved
	 * classes concatenated in alphabetical order and suffixed with '_X'.
	 * 
	 * @param relatedClass the referred-to class (the 'contained' class).
	 * @param attributeName the name of the object attribute holding the collection.
	 */
	public ConventionBasedDescriptorParameters hasManyToManyWith(Class<?> relatedClass, String attributeName)
	{
		return addManyToManyRelationship(relatedClass, attributeName);
	}

	/**
	 * Describes a privately-held, Many-Many relationship from the class described herein to
	 * another class. This implies a cross-reference table, conventionally assumed to be the
	 * names of the involved classes contatenated in alphabetical order and suffixed with '_X'.
	 * 
	 * @param relatedClass the referred-to class (the 'contained' class).
	 * @param attributeName the name of the object attribute holding the collection.
	 */
	public ConventionBasedDescriptorParameters ownsManyToManyWith(Class<?> relatedClass, String attributeName)
	{
		return addManyToManyRelationship(relatedClass, attributeName)
			.setOwned();
	}

	private ConventionBasedDescriptorParameters addManyToManyRelationship(Class<?> relatedClass, String attributeName)
	{
		manyToManyAttributes.add(attributeName);
		ManyToManyRelationship relationship = new ManyToManyRelationship(getJavaClass(), attributeName, relatedClass);
		relationships.add(relationship);
		return relationship.getParameters();
	}

	/**
	 * Describe a one-to-one relationship from the class described herein to another via
	 * the given attribute name.  The relationship is a reference-only one.  In other words,
	 * it is not privately-held and will not be "cascade deleted."
	 * 
	 * @param relatedClass the referred-to class.
	 * @param attributeName the name of the attribute holding the relationship.
	 * @return {@link ConventionBasedDescriptorParameters} to enable configuration of 1:1 relationship settings.
	 */
	public ConventionBasedDescriptorParameters hasOne(Class<?> relatedClass, String attributeName)
	{
		return addOneToOneRelationship(relatedClass, attributeName);
	}

	/**
	 * Describe a privately-held, one-to-one relationship from the class described herein to another via
	 * the given attribute name.  Privately held implies that when the 'owner' is removed, so is the 'owned'
	 * object.
	 * 
	 * @param relatedClass the referred-to class.
	 * @param attributeName the name of the attribute holding the relationship.
	 */
	public ConventionBasedDescriptorParameters ownsOne(Class<?> relatedClass, String attributeName)
	{
		return addOneToOneRelationship(relatedClass, attributeName)
			.setOwned();
	}

	private ConventionBasedDescriptorParameters addOneToOneRelationship(Class<?> relatedClass, String attributeName)
	{
		oneToOneAttributes.add(attributeName);
		OneToOneRelationship relationship = new OneToOneRelationship(getJavaClass(), attributeName, relatedClass);
		relationships.add(relationship);
		return relationship.getParameters();
	}

	/**
	 * Mark a particular object attribute as non-persistent.  Calling ignore for an attribute
	 * name means that attribute will not be mapped to a column in the database.
	 * 
	 * @param attributeName the name of an object attribute that will not be stored in the database.
	 */
	public void ignore(String attributeName)
	{
		ignoredAttributes.add(attributeName);
	}
	
	
	//INHERITANCE
	
	/**
	 * Used in a single-table inheritance scenario, childOf() indicates the object
	 * that the one described herein extends.  In other words, it indicates what the
	 * parent object is.  Since Java can only extend one class, childOf() may be
	 * only called once for a given descriptor.
	 * 
	 * @param parentClass the parent class reflecting inheritance. 
	 */
	public void childOf(Class<?> parentClass)
	{
		Assert.isNull(parent, 
			getJavaClass().getName()
			+ ": childOf() can be called only once--only one parent allowed.");
		parent = parentClass;
	}

	/**
	 * Used in a single-table inheritance scenario, parentOf() indicates an object
	 * that extends the one described herein.  In other words, it indicates that the
	 * passed in childClass is an extender.  This method may be called multiple times
	 * within a given descriptor.
	 * 
	 * @param childClass a class that extends the one described herein. 
	 */
	public void parentOf(Class<?> childClass)
	{
		children.add(childClass);
	}

	
	//MAPPING
    
	/**
	 * This is the method that extenders override to implement calls to 
	 * belongsTo(), owns...(), has...(), etc.
	 */
	protected void configureRelationships()
	{
	    configureIgnore();
	}
	
    /**
     * Set all the domain aspects that should be ignored by the convention-based mapping here
     */
    protected void configureIgnore()
    {
    }
    
    /**
     * Subclasses can override this method to have an opportunity to amend themselves
     * in some way, knowing that this method will be called after all of the mappings
     * have been fully installed on each and every registered descriptor.
     */
    
    protected void postMappingGeneration()
    {
    }
	
	/**
	 * Navigates the meta data from the descriptor (and project) and generates actual Toplink
	 * mappings.  The project is needed to navigate relationships and determine column names,
	 * primary and foreign key names, etc.
	 */
	public void configureDynamicMappings(ConventionBasedProject project)
	{
		configureTable(project);
        configureSequencing(project);
		configureInheritance(project);
		configureDirectToFieldMappings();
		configureRelationshipMappings(project);
        postMappingGeneration();
	}
    
	/**
	 * Generates the table name and primary key field for the toplink descriptor.
	 * If this descriptor is a single-table inheritance child (it has a parent),
	 * the method simply returns, as the TopLink descriptor handles table name
	 * and primary key via the parent descriptor.
	 * 
	 * @param project a convention-based toplink project.  Used to navigate
	 *        relationships for determination of table and PK names.
	 */
	protected void configureTable(ConventionBasedProject project)
	{
		if (hasParent()) return;

		if (!hasUserDefinedTableName())
		{
			super.setTableName(getInheritanceRootTableName(project));
		}
		
		addPrimaryKeyFieldName(
			(hasPrimaryKeyName() 
				? getPrimaryKeyName() 
				: (hasUserDefinedTableName() 
                    ? (getTableName() + PK_SUFFIX) 
                    : asTablePrimaryKey(getInheritanceRootTableName(project)))));
        
	}

    /**
     * Sets up the Oracle db sequencing for TopLink sequence numbers 
     * @param project
     */
    protected void configureSequencing(ConventionBasedProject project)
    {
        String tableName;

        if (!hasUserDefinedTableName() || hasParent())
        {
            tableName = getInheritanceRootTableName(project);
        }
        else
        {
            tableName = getTableName();
        }
        
        String oidColumn = tableName + PK_SUFFIX;
//        debug("Setting sequencing for " + getJavaClass().getSimpleName() + " to " + oidColumn);
        setSequenceNumberFieldName(oidColumn);
        setSequenceNumberName(tableName + SEQUENCE_SUFFIX);
    }

	/**
	 * Generate inheritance policy, if nessesary, for the toplink descriptor.
	 * Depends on calls to childOf() and parentOf().
	 */
	protected void configureInheritance(ConventionBasedProject project)
	{
		configureParentInheritance();
		configureChildInheritance(project);
	}

	/**
	 * Sets the parent class on the toplink inheritance policy, if childOf() was called.
	 */
	private void configureParentInheritance()
	{
		if (hasParent())
		{
//			debug(parent.getSimpleName() + " configured as parent of " + getJavaClass().getSimpleName());
			getDescriptorInheritancePolicy().setParentClass(parent);
		}
	}

	/**
	 * Sets the class indicator field name and class indicator for each child class, if
	 * parentOf() was called.  The class indicator field name is the TYPE_INDICATOR constant
	 * declared above.  The class indicator is the child class simple name as returned by
	 * a call to class.getSimpleName().
	 */
	private void configureChildInheritance(ConventionBasedProject project)
	{
		if (hasChildren() && !hasParent())
		{
	        getDescriptorInheritancePolicy().setClassIndicatorFieldName(TYPE_INDICATOR);
			addDirectQueryKey(TYPE_INDICATOR, TYPE_INDICATOR);
            
            if (!ClassUtils.isAbstract(getJavaClass()))
            {
                getDescriptorInheritancePolicy().addClassIndicator(getJavaClass(), getJavaClass().getSimpleName());
            }
	
	        for(Class<?> childClass : getConcreteDescendants(project))
			{
//	        	debug(childClass.getSimpleName() + " configured as child of " + getJavaClass().getSimpleName());
				getDescriptorInheritancePolicy().addClassIndicator(childClass, childClass.getSimpleName());
			}
		}
	}

	/**
	 * Generate DirectToFieldMappings for the toplink descriptor.  All declared fields are
	 * assumed to be mappable unless a call to ignore() is made for a particular one.  The
	 * inheritance hierarchy of the class is navigated to retrieve all inherited fields.
	 * However, if this is a single-table inheritance scenario (call to childOf() occurred),
	 * then only the declared fields within the described class are mapped.
	 */
	protected void configureDirectToFieldMappings()
	{
		List<Field> fields = getFields(getJavaClass());
		
		for (Field field : fields)
		{
			if (isDirectToFieldMapping(field))
			{
				String fieldName = field.getName();
//				debug("Direct to Field Mapping: " 
//					+ getJavaClass().getSimpleName() 
//					+ "." 
//					+ fieldName 
//					+ " to " 
//                    + getColumnName(fieldName));
				addDirectMapping(fieldName, getColumnName(fieldName));
			}
		}
	}
    
    /*
     * Either pull the column name from the custom column name
     * map or generate it dynamically.
     */
    private String getColumnName(String fieldName)
    {
        if (customColumnNames.get(fieldName) != null)
        {
            return customColumnNames.get(fieldName);
        }
        return asColumnName(fieldName);
    }

	protected void configureRelationshipMappings(ConventionBasedProject project)
	{
		for (RelationshipDefinition relationship : relationships)
		{
			if (relationship.isBackReferenceRelationship())
				configureBelongsToMapping((BackReferenceRelationship) relationship, project);
			else if (relationship.isOneToOneRelationship())
				configureOneToOneMapping((OneToOneRelationship) relationship, project);
			else if (relationship.isOneToManyRelationship())
				configureOneToManyMapping((OneToManyRelationship) relationship, project);
			else if (relationship.isManyToManyRelationship())
				configureManyToManyMapping((ManyToManyRelationship) relationship, project);
			else
				Assert.fail("Relationship is not of supported type");
		}
	}

	/**
	 * Generate OneToOneMappings for the toplink descriptor as indicated by calls to
	 * hasOne() or ownsOne().  The project is used to navigate relationships when 
	 * determining table, column, and foreign key names.
	 * 
	 * @param project a convention-based toplink project.
	 */
	protected void configureOneToOneMapping(OneToOneRelationship relation, ConventionBasedProject project)
	{
		if (CollectionUtils.containsMultiple(oneToOneAttributes, relation.getAttributeName()))
		{
			addVariableOneToOneMapping(relation, project);
		}
		else
		{
			addOneToOneMapping(relation, project);
		}
	}
	
	/**
	 * Generate OneToOneMappings for the back-references needed for the leaf nodes in
	 * each one-to-many relationship.  These are generated in response to calls on
	 * belongsTo().  The project is used to navigate relationships when 
	 * determining table, column, and foreign key names.
	 * 
	 * @param project a convention-based toplink project.
	 */
	protected void configureBelongsToMapping(BackReferenceRelationship relation, ConventionBasedProject project)
	{
		if (CollectionUtils.containsMultiple(ownerAttributes, relation.getAttributeName()))
		{
			addVariableOneToOneMapping(relation, project);
		}
		else
		{
			addOneToOneMapping(relation, project);
		}
	}

	/**
	 * Generate OneToManyMappings for the toplink descriptor as indicated by calls to
	 * hasMany() or ownsMany().  The project is used to navigate relationships when 
	 * determining table, column, and foreign key names.
	 * 
	 * @param project a convention-based toplink project.
	 */
	protected void configureOneToManyMapping(OneToManyRelationship relation, ConventionBasedProject project)
	{
        OneToManyMapping map = relation.asTopLinkMapping(project);
        map.addTargetForeignKeyFieldName(
			(relation.hasForeignKeyName()
				? relation.getForeignKeyName()
				: getBackReferenceForeignKeyName(relation, project)),
			(relation.hasTargetPrimaryKeyName()
				? relation.getTargetPrimaryKeyName()
				: asTablePrimaryKey(getInheritanceRootTableName(project))));

		addMapping(map);
//		debug(map.getAttributeName() + " is a 1-Many mapping to " + map.getTargetForeignKeyFieldNames().get(0) + "/" + map.getSourceKeyFieldNames().get(0));
	}

	/**
	 * Generate ManyToManyMappings for the toplink descriptor as indicated by calls to
	 * hasManyToManyWith() or ownsManyToManyWith().  The project is used to navigate 
	 * relationships when determining table, column, and foreign key names.
	 * 
	 * @param project a convention-based toplink project.
	 */
	protected void configureManyToManyMapping(ManyToManyRelationship relation, ConventionBasedProject project)
	{
		ManyToManyMapping map = relation.asTopLinkMapping(project);
		map.setRelationTableName(
			(relation.hasRelationTableName()
				? relation.getRelationTableName()
				: asRelationTableName(
					getInheritanceRootTableNameFor(relation.getRelatedClass(), project),
					getInheritanceRootTableName(project))));
		map.setSourceRelationKeyFieldName(
			(relation.hasForeignKeyName() 
				? relation.getForeignKeyName() 
				: asSingularizedOidForeignKey(
					getInheritanceUnprefixedRootTableNameFor(getJavaClass(), project))));
		map.setTargetRelationKeyFieldName(
			(relation.hasRelationForeignKeyName()
				? relation.getRelationForeignKeyName()
				: asSingularizedOidForeignKey(
					getInheritanceUnprefixedRootTableNameFor(relation.getRelatedClass(), project))));
		
		addMapping(map);
//		debug(map.getAttributeName() + " is a Many-Many mapping where: " + map.getSourceRelationKeyFieldNames().get(0) + " relates to " + map.getTargetRelationKeyFieldNames().get(0) + " on relation table " + map.getRelationTableName());
	}
	
	
	//TESTING

	/**
	 * Answer whether this descriptor has single-table inheritance relationship(s) to child classes.
	 * 
	 * @return true if parentOf() was called.
	 */
	public boolean hasChildren()
	{
		return (!children.isEmpty());
	}

	/**
	 * Answer whether this descriptor has single-table inheritance relationship(s) to a parent class.
	 * 
	 * @return true if childOf() was called.
	 */
	public boolean hasParent()
	{
		return (getParent() != null);
	}

	/**
	 * Answer whether there is a user-set primary key name.
	 * 
	 * @return true if the primary key name was set--not using convention-based naming.
	 */
	public boolean hasPrimaryKeyName()
	{
		return (getPrimaryKeyName() != null);
	}
	
	/**
	 * Answer whether there is a user-set table name.
	 * 
	 * @return true if the table name was set--not using convention-based naming.
	 */
	public boolean hasUserDefinedTableName()
	{
		return hasUserDefinedTableName;
	}
    
    public boolean hasMultipleTableNames()
    {
        return (getTableNames().size() > 1);
    }

	/**
	 * Answer whether the given field qualifies for a DirectToFieldMapping.  A field
	 * is disqualified for direct to field mapping when it is:
	 *     ignored via ignore(),
	 *     does not have the @ConventionMappingAbsent annotation
	 *     is a relationship (indicated via belongsTo(), has/ownsOne(), has/ownsMany(), has/ownsManyToManyWith(),
	 *     a constant (static final),
	 *     a transient field,
	 *     beginning with the name 'this'.
	 * 
	 * @param field the field to test.
	 * @return true if the field should be mapped as a DirectToFieldMapping.
	 */
	private boolean isDirectToFieldMapping(Field field)
	{
		return (!isIgnored(field)
			&& !isConventionMappingAbsent(field)
			&& !isBelongsToField(field)
			&& !isOneToOneField(field)
			&& !isOneToManyField(field)
			&& !isManyToManyField(field)
            && !ClassUtils.isFieldConstant(field)
            && !ClassUtils.isFieldTransient(field)
            && !isThis(field)
            && !field.isSynthetic()
		);
	}

	/**
	 * Answer whether the field is ignored.
	 * 
	 * @param field the field to test.
	 * @return true if the field is in the ignored list.
	 */
	private boolean isIgnored(Field field)
	{
		return ignoredAttributes.contains(field.getName());
	}

	private boolean isConventionMappingAbsent(Field field)
	{
		return field.isAnnotationPresent(UnpersistedProperty.class);
	}

	/**
	 * Answer whether the field is in the belongs-to list.
	 * 
	 * @param field the field to test.
	 * @return true if the field is in the belongs-to list.
	 */
	private boolean isBelongsToField(Field field)
	{
		return ownerAttributes.contains(field.getName());
	}

	/**
	 * Answer whether the field holds a 1-1 relationship.
	 * 
	 * @param field the field to test.
	 * @return true if the field holds a 1-1 relationship.
	 */
	private boolean isOneToOneField(Field field)
	{
		return oneToOneAttributes.contains(field.getName());
	}

	/**
	 * Answer whether the field holds a 1-Many relationship.
	 * 
	 * @param field the field to test.
	 * @return true if the field holds a 1-Many relationship.
	 */
	private boolean isOneToManyField(Field field)
	{
		return oneToManyAttributes.contains(field.getName());
	}

	/**
	 * Answer whether the field holds a Many-Many relationship.
	 * 
	 * @param field the field to test.
	 * @return true if the field holds a Many-Many relationship.
	 */
	private boolean isManyToManyField(Field field)
	{
		return manyToManyAttributes.contains(field.getName());
	}
    
	/**
	 * Answer whether the field name begins with 'this'.
	 * 
	 * @param field the field to test.
	 * @return true if the field name begins with 'this'.
	 */
    private boolean isThis(Field field)
    {
    	return (field.getName().startsWith("this"));
    }

	
	//UTILITY

    /**
     * Creates a database table name, converting camel-case to upper-case, inserting underbars ('_')
     * where each camel-case previously occurred.
     * 
     * For example: aCamelCaseName becomes A_CAMEL_CASE_NAME
     * 
     * @param name a name to use when creating a table name.
     * @return a name suitable for a database table.
     */
	private String asTableName(String name)
	{
		return tableNamePrefix + StringUtils.toSeparatedString(name, DELIMITER).toUpperCase();
	}

	/**
	 * Appends the primary key ('.OID') to the given table name.
     * 
     * For example: A_TABLE_NAME becomes A_TABLE_NAME.OID
	 * 
	 * @param tableName a name to use when creating a table primary key name.
	 * @return a name suitable for a database primary key name.
	 */
	private String asTablePrimaryKey(String tableName)
	{
		return tableName + PK_SUFFIX;
	}

	/**
	 * Creates a column name, converting camel-case in name to upper-case, inserting underbars ('_')
	 * where each camel-case previously occurred.
     * 
     * For example: aCamelCaseName becomes A_CAMEL_CASE_NAME
	 * 
	 * @param name a name to use when creating a column name.
	 * @return a name suitable for a database column name.
	 */
	private String asColumnName(String name)
	{
		return StringUtils.toSeparatedString(name, DELIMITER).toUpperCase();
	}

	/**
	 * Creates a foreign key column name, converting camel-case in name to upper-case, 
	 * inserting underbars ('_') where each camel-case previously occurred and appending
	 * a foreign key suffix ('_OID') to it.  It also singularizes the name.
     * 
     * For example: aCamelCaseName becomes A_CAMEL_CASE_NAME_OID, somePeople becomes SOME_PERSON_OID
	 * 
	 * @param name a name to use when creating a foreign key column name.
	 * @return a name suitable for a database foreign key column name.
	 */
	private String asSingularizedOidForeignKey(String name)
	{
		return asOidForeignKey(StringUtils.singularize(name));
	}

	/**
	 * Creates a foreign key column name, converting camel-case in name to upper-case, 
	 * inserting underbars ('_') where each camel-case previously occurred and appending
	 * a foreign key suffix ('_OID') to it.  This method does not singularize the name.
     * 
     * For example: aCamelCaseName becomes A_CAMEL_CASE_NAME_OID, somePeople becomes SOME_PEOPLE_OID
	 * 
	 * @param name a name to use when creating a foreign key column name.
	 * @return a name suitable for a database foreign key column name.
	 */
	private String asOidForeignKey(String name)
	{
		return asColumnName(name) + FK_SUFFIX;
	}

	/**
	 * Creates a relation-table name (for many-to-many mappings), concatenating name1 with name2 separated
	 * by an underbar ('_') and appending a suffix ('_X').  The names are concatenated in alphabetic order.
	 * 
	 * For example: using the names 'student' and 'course' becomes 'COURSE_STUDENT_X'. Order doesn't matter
	 * as the names are alway sorted to produce 'COURSE_STUDENT_X'.  The two names could not produce 
	 * 'STUDENT_COURSE_X'.
	 * 
	 * @param name1 a name to use when creating the name of a relation table.
	 * @param name2 a name to use when creating the name of a relation table.
	 * @return name1 and name2 concatentated in alpha order, separated by underbars, suffixed and upper-cased.
	 */
	private String asRelationTableName(String name1, String name2)
	{
		String result = null;
        name1 = (name1.startsWith(tableNamePrefix) ? name1.substring(tableNamePrefix.length()) : name1);
        name2 = (name2.startsWith(tableNamePrefix) ? name2.substring(tableNamePrefix.length()) : name2);

		if (name1.compareTo(name2) < 0)
		{
			result = name1 + DELIMITER + name2;
		}
		else
		{
			result = name2 + DELIMITER + name1;
		}
		
		return (tableNamePrefix + result + RELATION_TABLE_SUFFIX).toUpperCase();
	}

	/**
	 * Sets all instance variables to null so they can be GC'd.  Called from
	 * ConventionBasedTopLinkProject.resetDescriptors() to reduce memory
	 * requirements at runtime--since this meta data is only needed once.  After
	 * the toplink descriptors are generated, the convention-based details are
	 * no longer used.
	 */
	public void reset()
	{
		children = null;
		ignoredAttributes = null;
		manyToManyAttributes = null;
		oneToManyAttributes = null;
		oneToOneAttributes = null;
		relationships = null;
		parent = null;
	}

	private void addOneToOneMapping(OneToOneRelationship relation, ConventionBasedProject project)
	{
		OneToOneMapping map = relation.asTopLinkMapping(project);
		map.addForeignKeyFieldName(
			(relation.hasForeignKeyName() 
				? relation.getForeignKeyName() 
				: asOidForeignKey(relation.getAttributeName())),
			(relation.hasTargetPrimaryKeyName()
				? relation.getTargetPrimaryKeyName()
				: asTablePrimaryKey(
					getInheritanceRootTableNameFor(relation.getRelatedClass(), project))));

		addMapping(map);
//		debug(map.getAttributeName() + " is a 1-1 mapping to " + map.getForeignKeyFieldNames().get(0));
	}
	
	private void addVariableOneToOneMapping(RelationshipDefinition relation, ConventionBasedProject project)
	{
		VariableOneToOneMapping map = (VariableOneToOneMapping) getMappingForAttributeName(relation.getAttributeName());
		
		if (map == null)
		{
			map = new VariableOneToOneMapping();
			relation.configureMappingDefaults(map);
	        map.setTypeFieldName(TYPE_INDICATOR);
	        map.setForeignQueryKeyName(
				(relation.hasForeignKeyName() 
					? relation.getForeignKeyName() 
					: asOidForeignKey(relation.getAttributeName())),
				(relation.hasTargetPrimaryKeyName()
					? relation.getTargetPrimaryKeyName()
					: OID_ATTRIBUTE));

			addMapping(map);
		}
		
		map.addClassIndicator(relation.getRelatedClass(), relation.getRelatedClass().getSimpleName());
//		debug(map.getAttributeName() + " is a Variable 1-1 mapping to " + map.getForeignKeyFieldNames().get(0));
	}
    
	/**
	 * For the class described herein, navigate the relationships in the convention-based toplink project
	 * to retrieve the name of the table at the root of an inheritance hierarchy.  If there is
	 * no inheritance hierarchy, the table name for the class described herein is returned.
	 * 
	 * @param project a convention-based toplink project.
	 * @return the inheritance root table, or the table name for the class herein described.
	 */
    private String getInheritanceRootTableName(ConventionBasedProject project)
    {
        return getInheritanceRootTableNameFor(getJavaClass(), project);
    }
    
	/**
	 * For the given class, navigate the relationships in the convention-based toplink project
	 * to retrieve the name of the class at the root of an inheritance hierarchy.  If there is
	 * no inheritance hierarchy, the name of the table for the given class is returned.
	 * 
	 * @param aClass a class for which to find the inheritance root table name.
	 * @param project a convention-based toplink project.
	 * @return the inheritance-root table, or the table name for the class herein described.
	 */
    private String getInheritanceRootTableNameFor(Class<?> aClass, ConventionBasedProject project)
    {
        Class<?> root = project.getInheritanceRootFor(aClass);
        ConventionBasedEclipseLinkDescriptor rootDescriptor = project.getDynamicDescriptorFor(root);
        String tableName = null;

        if (rootDescriptor != null && rootDescriptor.hasUserDefinedTableName())
        {
            tableName = rootDescriptor.getTableName();
        }
        else
        {
            tableName = asTableName(root.getSimpleName());
        }
        
        return tableName;
    }

	/**
	 * For the given class, navigate the relationships in the convention-based toplink project
	 * to retrieve the name of the class at the root of an inheritance hierarchy, which is used
	 * to create a table or column name.  If, however, there is a table name set on the root
	 * descriptor, that name is returned.  The returned string is not manipulated: no case
	 * changes or conversion from camel-case to separated string, table name prefixing, etc.
	 * 
	 * @param aClass a class for which to find the inheritance root table name.
	 * @param project a convention-based toplink project.
	 * @return the inheritance-root table, or the table name for the class herein described.
	 */
    private String getInheritanceUnprefixedRootTableNameFor(Class<?> aClass, ConventionBasedProject project)
    {
        String tableName = getInheritanceRootTableNameFor(aClass, project);
        
        if (tableName.startsWith(getTableNamePrefix()))
        {
        	tableName = tableName.substring(tableName.indexOf(getTableNamePrefix()) + getTableNamePrefix().length());
        }

        return tableName;
    }

	/**
	 * In the case of a 1-Many mapping, due to the Object/Relational impedance mismatch the 'owned' object
	 * in the database contains the foreign key reference, while in the object parlance, the 'owning'
	 * object maintains the relationship.  This method then, navigates the relationship(s) in the
	 * project to determine what the name of the back-reference foreign key would be in the 'owned' object,
	 * including the table name (e.g. OWNING_LANGUAGE.OID).
	 *  
	 * @param relation a convention-based relation descriptor.
	 * @param project a convention-based toplink project to use in navigating the relationships.
	 * @return a string suitable for use as a table foreign key name. 
	 */
	private String getBackReferenceForeignKeyName(RelationshipDefinition relation, ConventionBasedProject project)
	{
		String relatedColumnName = "";
		RelationshipDefinition descriptor = getBackReferenceRelationshipDefinition(relation, project);
		
		if (descriptor != null)
		{
			if (descriptor.hasForeignKeyName())
				relatedColumnName = descriptor.getForeignKeyName();
			else
				relatedColumnName = asOidForeignKey(descriptor.getAttributeName());
		}
		else
			Assert.fail(relation.getRelatedClassName() 
				+ " must have a belongsTo("
				+ getJavaClass().getSimpleName()
				+ ".class, 'attributeName') call in its descriptor");

		String relatedTableName = getInheritanceRootTableNameFor(relation.getRelatedClass(), project);
		return (relatedTableName + "." + relatedColumnName);
	}
	
	/**
	 * Retrieves from the given project the convention-based relationship descriptor that represents a
	 * back-reference from the related class to the mapped class.
	 * 
	 * @param relatedClass the class in which to find a back-reference.
	 * @param project a convention-based toplink project to use in navigating the relationships.
	 * @return a convention-based relation descriptor.
	 */
	protected RelationshipDefinition getBackReferenceRelationshipDefinition(RelationshipDefinition relationship, ConventionBasedProject project)
	{
		RelationshipDefinition result = null;
		ConventionBasedEclipseLinkDescriptor currentDescribedClassDescriptor = project.getDynamicDescriptorFor(relationship.getDescribedClass());
		
		while (result == null && currentDescribedClassDescriptor != null)
		{
			ConventionBasedEclipseLinkDescriptor currentRelatedClassDescriptor = project.getDynamicDescriptorFor(relationship.getRelatedClass());
			Assert.isNotNull(currentRelatedClassDescriptor, 
				relationship.getDescribedClass().getSimpleName() 
				+ " references class, " + relationship.getRelatedClassName() 
				+ ", which does not have a descriptor in project, " 
				+ project.getClass().getSimpleName());

			while (result == null && currentRelatedClassDescriptor != null)
			{
				result = currentRelatedClassDescriptor.getBackReferenceRelationshipDefinition(relationship.getDescribedClass());
				
				if (result == null)
				{
					currentRelatedClassDescriptor = project.getDynamicDescriptorFor(currentRelatedClassDescriptor.getParent());
				}
			}
			
			if (result == null)
			{
				currentDescribedClassDescriptor = project.getDynamicDescriptorFor(currentDescribedClassDescriptor.getParent());
			}
		}

		return result;
	}

	/**
	 * For this descriptor, find an 'owner' that matches the given mapped class.
	 * 
	 * @param mappedClass a class for which to find a relation descriptor.
	 * @return a convention-based relation descriptor.
	 */
	private RelationshipDefinition getBackReferenceRelationshipDefinition(Class<?> mappedClass)
	{
		RelationshipDefinition result = findBackReferenceRelationshipDefinition(mappedClass);
		
		if (result == null)
		{
			result = findBackPointingOneToOneRelationshipDefinition(mappedClass);
		}
		
		return result;
	}
	
	private RelationshipDefinition findBackReferenceRelationshipDefinition(Class<?> mappedClass)
	{
		for (RelationshipDefinition relationship : relationships)
		{
			if (relationship.getRelatedClass().equals(mappedClass)
				&& relationship instanceof BackReferenceRelationship)
			{
				return relationship;
			}
		}
		
		return null;
	}
		
	private RelationshipDefinition findBackPointingOneToOneRelationshipDefinition(Class<?> mappedClass)
	{
		for (RelationshipDefinition relationship : relationships)
		{
			if (relationship.getRelatedClass().equals(mappedClass)
				&& relationship instanceof OneToOneRelationship)
			{
				return relationship;
			}
		}
		
		return null;
	}

	/**
	 * Reflectively navigate the given class's inheritance hierarchy to gather a list of
	 * all declared fields.  However, if the descriptor is for a child within a single-table
	 * inheritance hierarchy, only retrieve the declared fields for the given class.
	 * 
	 * @param aClass the class for which to retrieve declared fields.
	 * @return a list of fields in the class, including inherited ones.
	 */
	private List<Field> getFields(Class<?> aClass)
	{
		if (hasParent())
		{
			ArrayList<Field> results = new ArrayList<Field>();
			results.addAll(Arrays.asList(aClass.getDeclaredFields()));
			return results;
		}
		
		return ClassUtils.getAllDeclaredFields(aClass);
	}
    
    public static String getTableNamePrefix()
    {
        return tableNamePrefix;
    }
    
    public static void setTableNamePrefix(String prefix)
    {
        tableNamePrefix = prefix;
    }
    
    private List<Class<?>> getConcreteDescendants(ConventionBasedProject project)
    {
        List<Class<?>> results = new ArrayList<Class<?>>();
        
        for(Class<?> child : children)
        {
            if (!ClassUtils.isAbstract(child))
                results.add(child);

            ConventionBasedEclipseLinkDescriptor childDescriptor = project.getDynamicDescriptorFor(child);
            results.addAll(childDescriptor.getConcreteDescendants(project));
        }
        
        return results;
    }
}
