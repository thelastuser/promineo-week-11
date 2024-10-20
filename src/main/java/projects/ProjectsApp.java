package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	
	private ProjectService projectService = new ProjectService();
	
	private Project curProject = new Project();

	public static void main(String[] args) {
		
		new ProjectsApp().processUserSelections();
		
	}
	
	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project",
			"4) Update project details",
			"5) Delete a project"
			);
	// @formatter: on
	
	private Scanner scanner = new Scanner(System.in);

	private void processUserSelections() {
		boolean done = false;
		
		while(!done) {
			try {
				int selection = getUserSelection();
				switch (selection) {
				case -1:
					done = exitMenu();
					break;
				case 1:
					createProject();
					break;
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
				case 4:
					updateProjectDetails();
					break;
				case 5:
					deleteProject();
					break;
				default:
					System.out.print("\n" + selection + " is not a valid selection. Try again.");
				}
			}
			catch(Exception e) { 
				System.out.println("Something bad happened: " + e + " Try again." );
			}
		}
		
	}

	private void deleteProject() {
		listProjects();
		Integer projectId = getIntInput("Enter the project ID of the project you want to delete");
		projectService.deleteProject(projectId);
		System.out.println("\nProject " + projectId + " was deleted successfully.");
		if (curProject.getProjectId() == projectId) {
			curProject = null;
		}
	}

	private void updateProjectDetails() {
		if (curProject.getProjectId() == null) {
			System.out.println("\nPlease select a project.");
			return;
		}

		Project p = new Project();
		
		String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
		if (Objects.nonNull(projectName)) { p.setProjectName(projectName); } else { p.setProjectName(curProject.getProjectName()); }
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
		if (Objects.nonNull(estimatedHours)) { p.setEstimatedHours(estimatedHours); } else { p.setEstimatedHours(curProject.getEstimatedHours()); }
		BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + curProject.getActualHours() + "]");
		if (Objects.nonNull(actualHours)) { p.setActualHours(actualHours); } else { p.setActualHours(curProject.getActualHours()); }
		Integer difficulty = getIntInput("Enter the difficulty [" + curProject.getDifficulty() + "]");
		if (Objects.nonNull(difficulty)) { p.setDifficulty(difficulty); } else { p.setDifficulty(curProject.getDifficulty()); }
		String notes = getStringInput("Enter notes [" + curProject.getNotes() + "]");
		if (Objects.nonNull(notes)) { p.setNotes(notes); } else { p.setNotes(curProject.getNotes()); }
		
		p.setProjectId(curProject.getProjectId());
		projectService.modifyProjectDetails(p);
		
		curProject = projectService.fetchProjectById(curProject.getProjectId());
		
	}

	private void selectProject() {
		listProjects();
		
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		curProject = null;
		
		curProject = projectService.fetchProjectById(projectId);
		
		if (Objects.isNull(curProject)) {
			System.out.println("Invalid project ID selected");
		}
		
	}

	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		System.out.print("\nProjects:\n");
		projects.forEach(project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));
	}

	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes.");
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
		
	}



	private boolean exitMenu() {
		// flag for exit
		System.out.println("\nExiting the menu. Goodbye.");
		return true;
	}

	private int getUserSelection() {
		printOperations();
		
		Integer input = getIntInput("\nEnter menu selection");
		
		return Objects.isNull(input) ? -1 : input;
	}

	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		if (Objects.isNull(input)) { return null; }
		try { return new BigDecimal(input).setScale(2);}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	}
	
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		if (Objects.isNull(input)) { return null; }
		try { return Integer.valueOf(input);}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		return input.isBlank() ? null : input.trim();
	}

	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit.");
		operations.forEach(line -> System.out.println(" " + line));
		if (curProject.getProjectId()==null) {
			System.out.println("\nYou are not working with a project.");
		}
		else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}
	

}
