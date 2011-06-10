/**
 * Task.java
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

public abstract class Task {
	public enum TASK_TYPE {
		DISK_FORMAT, BRICK_MIGRATE, VOLUME_REBALANCE
	}
	
	public String[] TASK_TYPE_STR = { "Format Disk", "Migrate Brick", "Volume Rebalance" };
	
	private TaskInfo info;
	
	public Task(TASK_TYPE type, String reference, String description) {
		info = new TaskInfo();
		info.setId(getTaskType(type) + "-" + reference); // construct id
		info.setType(type);
		info.setReference(reference);
		info.setDescription(description);
	}
	public Task(TaskInfo info) {
		setInfo(info);
	}

	public String getTaskType(TASK_TYPE type) {
		return TASK_TYPE_STR[type.ordinal()];
	}
	
	public abstract String getId();

	public abstract TaskInfo resume();

	public abstract TaskInfo stop();

	public abstract TaskInfo pause();

	public abstract TASK_TYPE getType();
	
	public abstract TaskInfo getTaskInfo();
	
	public TaskInfo getInfo() {
		return info;
	}
	
	public void setInfo(TaskInfo info) {
		this.info = info; // TODO: review assigning reference and copy object 
	}
}
