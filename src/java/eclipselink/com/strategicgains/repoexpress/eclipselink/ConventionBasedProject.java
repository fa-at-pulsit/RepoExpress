package com.strategicgains.repoexpress.eclipselink;

import java.util.HashMap;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Login;

/**
 * @author Todd Fredrich
 * @since April 20, 2007
 */
@SuppressWarnings("serial")
public abstract class ConventionBasedProject
extends AbstractProject
{
	//INSTANCE VARIABLES

	private HashMap<Class<?>, ConventionBasedEclipseLinkDescriptor> descriptorMap;

	
	//CONSTRUCTING

	/**
	 * 
	 */
	public ConventionBasedProject()
	{
		super();
	}

	/**
	 * @param login
	 */
	public ConventionBasedProject(DatabaseLogin login)
	{
		super(login);
	}

	/**
	 * @param login
	 */
	public ConventionBasedProject(Login login)
	{
		super(login);
	}

	
	//INITIALIZING

    protected void initialize()
    {
    	descriptorMap = new HashMap<Class<?>, ConventionBasedEclipseLinkDescriptor>();
    }

    //CONFIGURING
    
    /* @see com.pstechnology.eaf.persistence.toplink.TopLinkNonJTAProject#configureDescriptors() */
    @Override
    protected void configureDescriptors() 
    {
    	configureDynamicDescriptors();
    	generateDynamicMappings();
    }
    
    protected abstract void configureDynamicDescriptors();

    
    //ACCESSING

    /**
     * Add a convention-based descriptor to this project.
     * 
     * @param descriptor a convention-based descriptor.
     */
	public void addDynamicDescriptor(ConventionBasedEclipseLinkDescriptor descriptor)
    {
    	descriptorMap.put(descriptor.getJavaClass(), descriptor);
    }
	
	/**
	 * Retrieve the convention-based descriptor for the given class.
	 * 
	 * @param aClass a class for which to retrieve the descriptor.
	 * @return a ConventionBasedTopLinkDescriptor, or null if not found.
	 */
	protected ConventionBasedEclipseLinkDescriptor getDynamicDescriptorFor(Class<?> aClass)
	{
		return descriptorMap.get(aClass);
	}

	/**
	 * For the given class, navigate the relationships in the project to retrieve the class
	 * at the root of a single-table inheritance hierarchy.  If there is no single-table
	 * inheritance hierarchy, the given class is returned.
	 *
	 * @param aClass a class for which to find the single-table inheritance root.
	 * @return the single-table inheritance root, or the class herein described.
	 */
	protected Class<?> getInheritanceRootFor(Class<?> aClass)
    {
    	Class<?> result = aClass;
    	
    	ConventionBasedEclipseLinkDescriptor descriptor = getDynamicDescriptorFor(aClass);

    	if (descriptor != null && descriptor.hasParent())
    	{
    		result = getInheritanceRootFor(descriptor.getParent());
    	}
    	
    	return result;
    }

    
    //UTILITY
    
	/**
	 * Utilize the convention-based meta data to generate actual toplink mappings.
	 * Then cleanup the meta data to reduce runtime memory requirements.
	 */
    private void generateDynamicMappings()
    {
    	for (ConventionBasedEclipseLinkDescriptor descriptor : descriptorMap.values())
    	{
    		descriptor.configureDynamicMappings(this);
    		addDescriptor((AbstractDescriptor) descriptor);
    	}
    	
    	resetDescriptors();
	}

	/**
	 * Sets all instance variables to null so they can be GC'd.  Called from
	 * ConventionBasedTopLinkProject.generateDynamicDescriptorMappings() to 
	 * reduce memory requirements at runtime--since this meta data is only
	 * needed once.  After the toplink descriptors are generated, the
	 * convention-based details are no longer used.
	 */
    private void resetDescriptors()
	{
    	for (ConventionBasedEclipseLinkDescriptor descriptor : descriptorMap.values())
    	{
    		descriptor.reset();
    	}
    	
    	descriptorMap = null;
	}
}
