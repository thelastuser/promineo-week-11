package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {
	
	private ProjectDao projectDao = new ProjectDao();

	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}

	public List<Project> fetchAllProjects() {
		
		ProjectDao db = new ProjectDao();
		
		return db.fetchAllProjects();
	}

	public Project fetchProjectById(Integer projectId) {
		
		return projectDao.fetchProjectById(projectId).orElseThrow( () -> new NoSuchElementException("Project with project ID=" + projectId + " does not exist."));

	}

	public void modifyProjectDetails(Project p) {
		
		if(!projectDao.modifyProjectDetails(p)) {
			throw new DbException("Project with ID=" + p.getProjectId() + " does not exist.");
		}

	}

	public void deleteProject(Integer projectId) {
		if (!projectDao.deleteProject(projectId)) {
			throw new DbException("No such project exists.");
		}
	}

}
