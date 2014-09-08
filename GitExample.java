import java.io.File;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.nyu.cs.javagit.api.*;
import edu.nyu.cs.javagit.api.commands.GitAddResponse;
import edu.nyu.cs.javagit.api.commands.GitLogResponse.Commit;

import java.io.*;
import java.util.*;
import java.lang.Object.*;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.CannotDeleteCurrentBranchException;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NotMergedException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.lib.*;

public class GitExample 
{
	String m_path="master";
	Git git;
    
	ArrayList<String> GetAllBranches(String RemoteBranchpath) throws IOException, GitAPIException
    {
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(RemoteBranchpath+"/.git"))//
          .readEnvironment() // scan environment GIT_* variables
          .findGitDir() // scan up the file system tree
          .build();
       git = new Git(repository);
    	List<Ref> branches = git.branchList().setListMode(ListMode.REMOTE).call();
    	ArrayList<String> branchList=new ArrayList<String>();
        for (Ref branch : branches) 
        {
            String branchName1 = branch.getName();
            branchName1=branchName1.substring(branchName1.lastIndexOf("/")+1);
            branchList.add(branchName1);
        }
        return branchList;
    }
	void ChangeBranch(String brachName) throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException
	{
		  git.checkout().setName(brachName).call();
	}
	void CreateNewRepository(String path) throws IOException
	{
		File targetDir = new File(path+"/.git");
		Repository repo = new FileRepository(targetDir);
		repo.create(true);
	}
	void CloneRepository(String RepositoryName,String localPath ) throws IOException, InvalidRemoteException, TransportException, GitAPIException
	{
		
		Git.cloneRepository().setURI(RepositoryName).
        setDirectory(new File(localPath)).
        setBranch(m_path).setBare(false).setRemote("origin").
        setNoCheckout(false).call();
		
		
        
	}
	void CreateBranch(String branchName,String userName,String Password) throws IOException, InvalidRemoteException, TransportException, GitAPIException
	{
		/*FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(path+"/.git"))
          .readEnvironment() // scan environment GIT_* variables
          .findGitDir() // scan up the file system tree
          .build();
        Git git = new Git(repository);*/

        git.checkout().setCreateBranch(true).setForce(true).setName(branchName).call();
    	UsernamePasswordCredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(userName, Password);
    	git.push().setCredentialsProvider(credentialsProvider).setRemote("origin").call();
    	System.out.println("Done");
	}
	void push(String branch,String userName,String Password,String Message) throws InvalidRemoteException, TransportException, GitAPIException, IOException
	{
		/*FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(path+"/.git"))
          .readEnvironment() // scan environment GIT_* variables
          .findGitDir() // scan up the file system tree
          .build();
        
        Git git = new Git(repository);*/
		
		git.checkout().setForce(true).setName(branch).call();
		git.add().addFilepattern(".").call();
		git.commit().setMessage(Message).call();
		CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(userName, Password);
		git.push().setCredentialsProvider(credentialsProvider).setRemote("origin").call();
	}
	void deleteBranch(String branchName) throws NotMergedException, CannotDeleteCurrentBranchException, GitAPIException
	{
		git.checkout().setName("master").call();
		git.branchDelete().setForce(true).setBranchNames("refs/heads/"+branchName).call();
		RefSpec refSpec = new RefSpec().setSource(null).setDestination("refs/remotes/origin/"+branchName);
		git.push().setRefSpecs(refSpec).setRemote("origin").call();
		//git.branchDelete().setBranchNames(branchName).call();
	}
}
