package com.jk7d.projectpbackend.service.project;

import com.jk7d.projectpbackend.adapter.project.model.ProjectCreateDto;
import com.jk7d.projectpbackend.adapter.project.model.ProjectDto;
import com.jk7d.projectpbackend.adapter.project.model.ProjectUpdateDto;
import com.jk7d.projectpbackend.service.security.IUserDetails;
import com.jk7d.projectpbackend.store.project.Project;
import com.jk7d.projectpbackend.store.project.ProjectRepository;
import com.jk7d.projectpbackend.store.user.User;
import com.jk7d.projectpbackend.store.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectService(final ProjectRepository projectRepository, final UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    /**
     * @param projectCreateDto
     * @return
     * @throws URISyntaxException
     */
    public ResponseEntity<String> createProject(final ProjectCreateDto projectCreateDto) throws URISyntaxException {
        final IUserDetails userDetails = (IUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Optional<User> owner = this.userRepository.findByEmail(userDetails.getEmail());
        if (owner.isPresent()) {
            Project project = new Project(projectCreateDto.name(), null, owner.get());
            project = this.projectRepository.save(project);
            return ResponseEntity.created(new URI("/projects/" + project.getId())).body("Created project");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public ResponseEntity<?> readProjectById(final UUID id) {
        final Optional<Project> project = this.projectRepository.findById(id);
        if (project.isPresent()) {
            final Project projectObj = project.get();
            final List<String> members = this.getProjectMembers(projectObj);
            final ProjectDto projectDto = new ProjectDto(projectObj.getId(), projectObj.getName(),
                    projectObj.getDescription(), projectObj.getOwner().getUsername(), members);
            return ResponseEntity.ok().body(projectDto);
        } else {
            return ResponseEntity.badRequest().body("Error: Project " + id + " not found.");

        }
    }

    /**
     * @param id
     * @param projectUpdateDto
     * @return
     */
    public ResponseEntity<?> updateProjectById(final UUID id, final ProjectUpdateDto projectUpdateDto) {
        final Optional<Project> project = this.projectRepository.findById(id);
        if (project.isPresent()) {
            final Project projectObj = this.validateProjectFieldsForUpdate(projectUpdateDto, project.get());

            final ProjectDto projectDto = new ProjectDto(projectObj.getId(), projectObj.getName(),
                    projectObj.getDescription(), projectObj.getOwner().getUsername(), this.getProjectMembers(projectObj));
            return ResponseEntity.ok().body(projectDto);
        } else {
            return ResponseEntity.badRequest().body("Error: Project " + id + " not found.");
        }
    }

    /**
     * @param id
     * @return
     */
    public ResponseEntity<String> deleteProjectById(final UUID id) {
        final Optional<Project> project = this.projectRepository.findById(id);
        if (project.isPresent()) {
            this.projectRepository.delete(project.get());
            return ResponseEntity.ok().body("Deleted.");
        } else {
            return ResponseEntity.badRequest().body("Error: Project " + id + " not found.");
        }
    }

    /**
     * @param project
     * @return
     */
    private List<String> getProjectMembers(final Project project) {
        final List<String> list = new ArrayList<>(Collections.emptyList());
        for (final User u : project.getMembers()) {
            list.add(u.getUsername());
        }
        return list;
    }

    /**
     * @param projectUpdateDto
     * @param project
     * @return
     */
    private Project validateProjectFieldsForUpdate(final ProjectUpdateDto projectUpdateDto, final Project project) {
        if (!projectUpdateDto.name().equals(project.getName())) {
            project.setName(projectUpdateDto.name());
        }

        if (!projectUpdateDto.description().equals(project.getDescription())) {
            project.setDescription(projectUpdateDto.description());
        }

        return this.projectRepository.save(project);
    }

}
