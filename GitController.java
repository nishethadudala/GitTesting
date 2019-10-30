package com.matilda.git.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

import com.matilda.git.VO.GitVO;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import static java.io.File.separator;

import static org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode.TRACK;
import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;

@Controller
public class GitController {

    private static final String GIT_FOLDER = ".git";
    private File targetFolder;
    private final AtomicLong counter = new AtomicLong();

    @PostMapping("/cloneNoAuth")
    @ResponseBody
    public String cloneWithNoAuth(@RequestBody String Url) {

        String cloneDirectoryPath = "C:\\Users\\CNET102\\GitTesting\\cloneNOAuth";
        try {
            System.out.println("Cloning "+ Url+" into "+ cloneDirectoryPath);
            Git.cloneRepository()
                    .setURI(Url)
                    .setDirectory(Paths.get(cloneDirectoryPath).toFile())
                    .call();
            System.out.println("Completed Cloning");
        } catch (GitAPIException e) {
            System.out.println("Exception occurred while cloning repo");
            e.printStackTrace();
        }
        return "cloned";
    }
    
    @PostMapping ("/cloneWithAuth")
    @ResponseBody
    public String cloneWithAuth(@RequestBody GitVO gitVO) {

        String cloneDirectoryPath = "C:\\Users\\CNET102\\GitTesting\\cloneWithAuth";
        try {
            System.out.println("Cloning "+ gitVO.getRepoUrl() +" into "+ cloneDirectoryPath);
            Git.cloneRepository()
                    .setURI(gitVO.getRepoUrl())
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitVO.getUserName(), gitVO.getPassword()))
                    .setDirectory(Paths.get(cloneDirectoryPath).toFile())
                    .call();
            System.out.println("Completed Cloning");

        } catch (GitAPIException e) {
            System.out.println("Exception occurred while cloning repo");
            e.printStackTrace();
        }
        return "cloned";
    }

    @PostMapping ("/pull")
    public void pull(@RequestBody GitVO gitVO) throws GitAPIException, IOException {
        try (
                Repository db = createRepository(gitVO);
                Git git = Git.wrap(db);
        ) {
            try {
                PullResult result = git.pull()
                        .setCredentialsProvider(createCredentialsProvider(gitVO))
                        .setRemote("origin")
                        .setRemoteBranchName(gitVO.getBranchName())
                        .call();
                if(result.isSuccessful()) {
                    System.out.println("git pull successful");
                }
            } catch (RefNotFoundException e) {
                System.out.println(e.toString());
            }
        }
    }

    private UsernamePasswordCredentialsProvider createCredentialsProvider(GitVO gitVo) {
        return new UsernamePasswordCredentialsProvider(gitVo.getUserName(), gitVo.getPassword());
    }

    private Repository createRepository(GitVO gitVO) throws IOException, GitAPIException {
        this.targetFolder = createGitWorkDirectory("GitTesting");
        final Repository repository = Git.cloneRepository()
                .setURI(gitVO.getRepoUrl())
                .setDirectory(targetFolder)
                .setCloneAllBranches(true)
                .setBranch("master")
                .call()
                .getRepository();
        return repository;
    }

    private File createGitWorkDirectory(String repositoryName) throws IOException {
        return Files.createTempDirectory("git-cloned-repo-" + repositoryName + "-").toFile();
    }

}