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
package com.gluster.storage.management.gui.views.pages;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchSite;

import com.gluster.storage.management.core.model.Brick;
import com.gluster.storage.management.core.model.ClusterListener;
import com.gluster.storage.management.core.model.DefaultClusterListener;
import com.gluster.storage.management.core.model.Event;
import com.gluster.storage.management.core.model.Event.EVENT_TYPE;
import com.gluster.storage.management.core.model.Volume;
import com.gluster.storage.management.gui.BrickTableLabelProvider;

public class BricksPage extends AbstractTableViewerPage<Brick> {
	private List<Brick> bricks;
	
	public enum BRICK_TABLE_COLUMN_INDICES {
		SERVER, BRICK, FREE_SPACE, TOTAL_SPACE, STATUS
	};

	private static final String[] DISK_TABLE_COLUMN_NAMES = new String[] { "Server", "Brick Directory", "Free Space (GB)",
			"Total Space (GB)", "Status" };

	public BricksPage(Composite parent, int style, IWorkbenchSite site, final List<Brick> bricks) {
		super(site, parent, style, bricks);
		this.bricks = bricks;
	}

	@Override
	protected ClusterListener createClusterListener() {
		return new DefaultClusterListener() {
			@Override
			public void volumeChanged(Volume volume, Event event) {
				if (event.getEventType() == EVENT_TYPE.BRICKS_ADDED || event.getEventType() == EVENT_TYPE.BRICKS_REMOVED) {
					tableViewer.refresh();
					parent.update();
				}
				
			}
		};
	}

	@Override
	protected String[] getColumnNames() {
		return DISK_TABLE_COLUMN_NAMES;
	}
	
	@Override
	protected void setColumnProperties(Table table) {
		guiHelper.setColumnProperties(table, BRICK_TABLE_COLUMN_INDICES.SERVER.ordinal(), SWT.CENTER, 100);
		guiHelper.setColumnProperties(table, BRICK_TABLE_COLUMN_INDICES.BRICK.ordinal(), SWT.CENTER, 100);
		guiHelper.setColumnProperties(table, BRICK_TABLE_COLUMN_INDICES.FREE_SPACE.ordinal(), SWT.CENTER, 90);
		guiHelper.setColumnProperties(table, BRICK_TABLE_COLUMN_INDICES.TOTAL_SPACE.ordinal(), SWT.CENTER, 90);
	}
	
	@Override
	protected ITableLabelProvider getLabelProvider() {
		return new BrickTableLabelProvider();
	}
	
	@Override
	protected IContentProvider getContentProvider() {
		return new ArrayContentProvider();
	}
	
	@Override
	protected List<Brick> getAllEntities() {
		return bricks;
	}
}