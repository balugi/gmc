/**
 * UserAuthDao.java
 *
 * Copyright (c) 2011 Gluster, Inc. <http://www.gluster.com>
 * This file is part of Gluster Management Console.
 *
 * Gluster Management Console is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gluster Management Console is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.gluster.storage.management.gateway.security;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;

import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

import com.gluster.storage.management.core.exceptions.GlusterRuntimeException;

/**
 * 
 */
public class UserAuthDao extends JdbcDaoImpl implements GlusterUserDetailsService {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gluster.storage.management.gateway.security.GlusterUserDetailsService#changePassword(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void changePassword(String username, String password) {
		try {
			getJdbcTemplate().update("UPDATE USERS SET PASSWORD = ? WHERE USERNAME = ?", password, username);
			Connection connection = getDataSource().getConnection(); 
			connection.commit();
			connection.close();
		} catch(Exception e) {
			String errMsg = "Exception while changing password of user [" + username + "]. Error: " + e.getMessage();
			try {
				getDataSource().getConnection().rollback();
			} catch (SQLException e1) {
				throw new GlusterRuntimeException(errMsg + ", " + e1.getMessage());
			}
			throw new GlusterRuntimeException(errMsg);
		}
	}
}
