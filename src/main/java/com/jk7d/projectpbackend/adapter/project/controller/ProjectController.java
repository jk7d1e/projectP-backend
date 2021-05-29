package com.jk7d.projectpbackend.adapter.project.controller;

import com.jk7d.projectpbackend.adapter.project.model.ProjectCreateDto;
import com.jk7d.projectpbackend.adapter.project.model.ProjectUpdateDto;
import com.jk7d.projectpbackend.service.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.UUID;

@RestController
@RequestMapping(value = "/projects")
public class ProjectController {

    private final ProjectService projectService;


    @Autowired
    public ProjectController(final ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createProject(@Valid @RequestBody final ProjectCreateDto projectCreateDto) throws URISyntaxException {
        return this.projectService.createProject(projectCreateDto);
    }

    @GetMapping(value = "/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@projectSecurity.isProjectMember(authentication, #projectId)")
    public ResponseEntity<?> readProjectById(@PathVariable final UUID projectId) {
        return this.projectService.readProjectById(projectId);
    }

    @PutMapping(value = "/{projectId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@projectSecurity.isProjectOwner(authentication, #projectId)")
    public ResponseEntity<?> updateProjectById(@PathVariable final UUID projectId, @Valid @RequestBody final ProjectUpdateDto projectUpdateDto) {
        return this.projectService.updateProjectById(projectId, projectUpdateDto);
    }

    @DeleteMapping(value = "/{projectId}")
    @PreAuthorize("@projectSecurity.isProjectOwner(authentication, #projectId)")
    public ResponseEntity<String> deleteProjectById(@PathVariable final UUID projectId) {
        return this.projectService.deleteProjectById(projectId);
    }
}
