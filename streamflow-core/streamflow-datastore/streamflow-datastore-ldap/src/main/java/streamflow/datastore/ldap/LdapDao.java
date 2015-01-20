/**
 * Copyright 2014 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package streamflow.datastore.ldap;

import com.google.inject.Inject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import streamflow.common.exception.DaoMethodNotAllowedException;
import streamflow.datastore.core.GenericDao;
import streamflow.model.User;
import streamflow.model.util.Entity;

import org.apache.isis.security.shiro.IsisLdapRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

public abstract class LdapDao<T extends Entity<ID>, ID extends Serializable> 
	implements GenericDao<T, ID> {

    protected Class<T> persistentClass;
    
    protected Subject currentUser;
    
    protected IsisLdapRealm realm;
    
    protected abstract T toObject(Attributes entity);
    
    protected abstract List<Attributes> toEntity(T entity);
    
    @Inject
    public LdapDao(IsisLdapRealm ctx, Class<T> persistentClass) {
       this.realm = ctx;
       this.persistentClass = persistentClass;
    }

    @Override
    public boolean exists(ID id) {
    	throw new DaoMethodNotAllowedException("Exist has not been implemented for this LDAP DAO");
        //return datastore.get(persistentClass, id) != null;
    }

    @Override
    public List<T> findAll() {
    	throw new DaoMethodNotAllowedException("FindAll has not been implemented for this LDAP DAO");
        //Query<T> q = datastore.createQuery(persistentClass);
        //return q.asList();
    }

	@Override
	public T findById(ID id) {
		throw new DaoMethodNotAllowedException("FindById has not been implemented for this LDAP DAO");
	}

	@Override
	public T save(T entity) {
		throw new DaoMethodNotAllowedException("Save has not been implemented for this LDAP DAO");
	}

	@Override
	public T update(T entity) {
		throw new DaoMethodNotAllowedException("Update has not been implemented for this LDAP DAO");
	}

	@Override
	public void delete(T entity) {
		throw new DaoMethodNotAllowedException("Delete has not been implemented for this LDAP DAO");
	}

	@Override
	public void deleteById(ID id) {
		throw new DaoMethodNotAllowedException("Delete has not been implemented for this LDAP DAO");
	}
	
    protected T AssertSingleResult(List<T> entities) throws Exception 
    {
		if (entities.size()>1)
		{
			throw new Exception("Result set contains ("+ entities.size() + ") instead of a single result!");
		}
		
		return entities.get(0);
    }
    
    public List<T> query(String base, String filter) throws Exception 
    {
    	List<T> entities = new ArrayList<T>();
    	
    	try 
    	{
    	   	LdapContextFactory ctx = realm.getContextFactory();
    	   	LdapContext lctx = ctx.getSystemLdapContext();
    	   	
    	   	if (lctx == null)
    	   	{
    	   		throw new Exception("Failed to get System LDAP Context!");
    	   	}
    	   	
			@SuppressWarnings("unchecked")
			Hashtable<String, String> env = (Hashtable<String, String>) lctx.getEnvironment();
			
			DirContext dc = new InitialDirContext(env);  
				
			// NOTE: options are in set { OBJECT_SCOPE, ONELEVEL_SCOPE, SUBTREE_SCOPE }
			SearchControls sc = new SearchControls();  
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);  
			
			NamingEnumeration<SearchResult> searchResults = dc.search(base, filter, sc);
			 
			while (searchResults.hasMore()) 
			{  
			    SearchResult result = searchResults.next();
			    Attributes atts = result.getAttributes();
			    NamingEnumeration<String> ids = atts.getIDs();
			    T entity = toObject(atts);
			    entities.add(entity);
			}  

			dc.close();          
		} 
    	catch (NamingException ex) 
    	{
			throw new Exception(ex.getMessage());
		}
    	
    	return entities;
    }



    

}