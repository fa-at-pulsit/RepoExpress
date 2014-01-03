package com.strategicgains.repoexpress.mongodb;

import java.net.UnknownHostException;
import java.util.Properties;

import org.restexpress.common.exception.ConfigurationException;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoConfig
{
	private static final String URI_PROPERTY = "mongodb.uri";

	private String dbName;
	private MongoClient client;

    public MongoConfig(Properties p)
    {
		String uri = p.getProperty(URI_PROPERTY);

		if (uri == null)
		{
			throw new ConfigurationException("Please define a MongoDB URI for property: " + URI_PROPERTY);
		}

		MongoClientURI mongoUri = new MongoClientURI(uri);
		dbName = mongoUri.getDatabase();
		try
        {
	        client = new MongoClient(mongoUri);
        }
        catch (UnknownHostException e)
        {
        	throw new ConfigurationException(e);
        }
		
		initialize(p);
    }

    /**
     * Sub-classes can override to initialize other properties.
     * 
     * @param p Propreties
     */
	protected void initialize(Properties p)
    {
		// default is to do nothing.
    }

	public String getDbName()
	{
		return dbName;
	}

	public MongoClient getClient()
	{
		return client;
	}
}
