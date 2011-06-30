/**
 * TaskInfo.java
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
package com.gluster.storage.management.core.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.gluster.storage.management.core.utils.StringUtil;

@XmlRootElement
public class TaskInfo extends Entity {
	public enum TASK_TYPE {
		DISK_FORMAT, BRICK_MIGRATE, VOLUME_REBALANCE
	}

	private TASK_TYPE type;
	private String reference;
	private String description;
	private Boolean pauseSupported;
	private Boolean stopSupported;
	private Boolean commitSupported;
	private TaskStatus status;
	
	public TaskInfo() {
	}

	@XmlElement(name="id")
	public String getName() {
		return super.getName();
	}

	public TASK_TYPE getType() {
		return type;
	}

	public void setType(TASK_TYPE type) {
		this.type = type;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public Boolean canPause() {
		return pauseSupported;
	}

	public void setCanPause(Boolean canPause) {
		this.pauseSupported = canPause;
	}

	public Boolean canStop() {
		return stopSupported;
	}

	public void setCanStop(Boolean canStop) {
		this.stopSupported = canStop;
	}
	
	public Boolean canCommit() {
		return this.commitSupported;
	}
	
	public void setCanCommit(Boolean canCommit) {
		this.commitSupported = canCommit;
	}

	/* (non-Javadoc)
	 * @see com.gluster.storage.management.core.model.Entity#filter(java.lang.String, boolean)
	 */
	@Override
	public boolean filter(String filterString, boolean caseSensitive) {
		return StringUtil.filterString(getDescription() + getStatus().getMessage(), filterString, caseSensitive);
	}
}