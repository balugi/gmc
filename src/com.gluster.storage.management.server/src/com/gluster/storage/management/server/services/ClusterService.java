/*******************************************************************************
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
 *******************************************************************************/
package com.gluster.storage.management.server.services;

import static com.gluster.storage.management.core.constants.RESTConstants.FORM_PARAM_CLUSTER_NAME;
import static com.gluster.storage.management.core.constants.RESTConstants.FORM_PARAM_SERVER_NAME;
import static com.gluster.storage.management.core.constants.RESTConstants.PATH_PARAM_CLUSTER_NAME;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gluster.storage.management.core.constants.CoreConstants;
import com.gluster.storage.management.core.exceptions.GlusterRuntimeException;
import com.gluster.storage.management.core.model.GlusterServer;
import com.gluster.storage.management.core.model.Status;
import com.gluster.storage.management.server.data.ClusterInfo;
import com.gluster.storage.management.server.data.PersistenceDao;
import com.gluster.storage.management.server.data.ServerInfo;
import com.gluster.storage.management.server.utils.GlusterUtil;
import com.gluster.storage.management.server.utils.SshUtil;
import com.sun.jersey.api.core.InjectParam;

/**
 * Service class for functionality related to clusters
 */
@Component
public class ClusterService {
	@Autowired
	private PersistenceDao<ClusterInfo> clusterDao;
	
	@Autowired
	private PersistenceDao<ServerInfo> serverDao;

	@Autowired
	private GlusterUtil glusterUtil;

	@Autowired
	private SshUtil sshUtil;
	
	public List<ClusterInfo> getAllClusters() {
		return clusterDao.findAll();
	}
	
	public ClusterInfo getCluster(String clusterName) {
		List<ClusterInfo> clusters = clusterDao.findBy("name = ?1", clusterName);
		if(clusters.size() == 0) {
			return null;
		}

		return clusters.get(0);
	}
	
	public ClusterInfo getClusterForServer(String serverName) {
		List<ServerInfo> servers = serverDao.findBy("name = ?1", serverName);
		if(servers.size() == 0) {
			return null;
		}

		return servers.get(0).getCluster();
	}
	
	public void createCluster(@FormParam(FORM_PARAM_CLUSTER_NAME) String clusterName) {
		EntityTransaction txn = clusterDao.startTransaction();
		ClusterInfo cluster = new ClusterInfo();
		cluster.setName(clusterName);

		try {
			clusterDao.save(cluster);
			txn.commit();
		} catch (RuntimeException e) {
			txn.rollback();
			throw e;
		}
	}
	
	public void registerCluster(@FormParam(FORM_PARAM_CLUSTER_NAME) String clusterName,
			@FormParam(FORM_PARAM_SERVER_NAME) String knownServer) {
		EntityTransaction txn = clusterDao.startTransaction();
		ClusterInfo cluster = new ClusterInfo();
		cluster.setName(clusterName);
		
		GlusterServer server = new GlusterServer(knownServer);
		try {
			List<GlusterServer> glusterServers = glusterUtil.getGlusterServers(server);
			List<ServerInfo> servers = new ArrayList<ServerInfo>();
			for(GlusterServer glusterServer : glusterServers) {
				String serverName = glusterServer.getName();
				
				checkAndSetupPublicKey(serverName);

				ServerInfo serverInfo = new ServerInfo(serverName);
				serverInfo.setCluster(cluster);
				clusterDao.save(serverInfo);
				servers.add(serverInfo);
			}
			cluster.setServers(servers);
			clusterDao.save(cluster);
			txn.commit();
		} catch(RuntimeException e) {
			txn.rollback();
			throw e;
		}
	}
	
	private void checkAndSetupPublicKey(String serverName) {
		if(sshUtil.isPublicKeyInstalled(serverName)) {
			return;
		}
		
		if(!sshUtil.hasDefaultPassword(serverName)) {
			// public key not installed, default password doesn't work. can't install public key
			throw new GlusterRuntimeException(
					"Gluster Management Gateway uses the default password to set up keys on the server."
							+ CoreConstants.NEWLINE + "However it seems that the password on server [" + serverName
							+ "] has been changed manually." + CoreConstants.NEWLINE
							+ "Please reset it back to the standard default password and try again.");
		}
		
		// install public key (this will also disable password based ssh login)
		sshUtil.installPublicKey(serverName);
	}
	
	public void unregisterCluster(@PathParam(PATH_PARAM_CLUSTER_NAME) String clusterName) {
		ClusterInfo cluster = getCluster(clusterName);
		
		if (cluster == null) {
			throw new GlusterRuntimeException("Cluster [" + clusterName + "] doesn't exist!");
		}

		unregisterCluster(cluster);
	}

	public void unregisterCluster(ClusterInfo cluster) {
		EntityTransaction txn = clusterDao.startTransaction();
		try {
			clusterDao.delete(cluster);
			txn.commit();
		} catch (RuntimeException e) {
			txn.rollback();
			throw e;
		}
	}
	
	public void mapServerToCluster(String clusterName, String serverName) {
		EntityTransaction txn = clusterDao.startTransaction();
		ClusterInfo cluster = getCluster(clusterName);
		ServerInfo server = new ServerInfo(serverName);
		server.setCluster(cluster);
		try {
			clusterDao.save(server);
			cluster.addServer(server);
			clusterDao.update(cluster);
			txn.commit();
		} catch (Exception e) {
			txn.rollback();
			throw new GlusterRuntimeException("Couldn't create cluster-server mapping [" + clusterName + "]["
					+ serverName + "]! Error: " + e.getMessage(), e);
		}
	}
	
	public void unmapServerFromCluster(String clusterName, String serverName) {
		EntityTransaction txn = clusterDao.startTransaction();
		ClusterInfo cluster = getCluster(clusterName);
		List<ServerInfo> servers = cluster.getServers();
		for(ServerInfo server : servers) {
			if(server.getName().equals(serverName)) {
				servers.remove(server);
				clusterDao.delete(server);
				break;
			}
		}
		try {
			clusterDao.update(cluster);
			txn.commit();
		} catch(Exception e) {
			txn.rollback();
			throw new GlusterRuntimeException("Couldn't unmap server [" + serverName + "] from cluster [" + clusterName
					+ "]! Error: " + e.getMessage(), e);
		}
	}
}
