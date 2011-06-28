package com.gluster.storage.management.gui.views;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.gluster.storage.management.client.GlusterDataModelManager;
import com.gluster.storage.management.core.model.TaskInfo;
import com.gluster.storage.management.gui.views.pages.TasksPage;

public class TasksView extends ViewPart {
	
	public static final String ID = TasksView.class.getName();
	private TasksPage page;
	

	public TasksView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		page = new TasksPage(getSite(), parent, SWT.NONE, getAllTasks());
		page.layout(); // IMP: lays out the form properly
	}

	
	private List<TaskInfo> getAllTasks() {
		return GlusterDataModelManager.getInstance().getModel().getCluster().getTaskInfoList();
	}

	@Override
	public void setFocus() {
		page.setFocus();
	}

}
