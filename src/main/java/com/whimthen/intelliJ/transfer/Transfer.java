package com.whimthen.intelliJ.transfer;

import com.intellij.database.psi.DbDataSource;
import com.intellij.database.util.DbUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.util.containers.JBIterable;
import com.whimthen.intelliJ.transfer.cache.DataSourceCache;
import com.whimthen.intelliJ.transfer.ui.DataTransferDialogWrapper;
import com.whimthen.intelliJ.transfer.utils.GlobalUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author whimthen
 * @version 1.0.0
 */
public class Transfer extends AnAction {

	static {
		ProjectManager manager = ProjectManager.getInstance();
		manager.addProjectManagerListener(new ProjectManagerListener() {
			@Override
			public void projectOpened(@NotNull Project project) {
				putDataSourceFromProject(project);
			}
		});
		Project[] openProjects = manager.getOpenProjects();
		if (openProjects.length > 0) {
			Stream.of(openProjects).forEach(Transfer::putDataSourceFromProject);
		}
	}

	@Override
	public void actionPerformed(AnActionEvent e) {
		Project project = e.getProject();
		if (Objects.isNull(project)) {
			return;
		}
		putDataSourceFromProject(project);
		DataTransferDialogWrapper dialog = DataTransferDialogWrapper.getInstance(project);
		dialog.pack();
		dialog.setVisible(true);
	}

	private static void putDataSourceFromProject(Project project) {
		GlobalUtil.nonNullConsumer(project, p -> {
			JBIterable<DbDataSource> dataSources = DbUtil.getDataSources(p);
			if (dataSources.isNotEmpty()) {
				dataSources.forEach(DataSourceCache::add);
			}
		});
	}

}
