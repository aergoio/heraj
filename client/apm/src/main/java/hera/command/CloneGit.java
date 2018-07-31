/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static org.eclipse.jgit.lib.Constants.HEAD;

import hera.FileContent;
import hera.FileSet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Stream;
import lombok.Getter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.treewalk.TreeWalk;

public class CloneGit extends AbstractCommand implements ProgressMonitor {

  @Getter
  protected FileSet fileSet = new FileSet();

  @Override
  public void execute() throws Exception {
    final String packageName = getArgument(0);
    final String branch = getOptionalArgument(1).orElse("master");
    final String remoteUri = "https://github.com/" + packageName + ".git";

    final InMemoryRepository repository = new InMemoryRepository(new DfsRepositoryDescription());
    final Git git = new Git(repository);
    git.remoteAdd().setName("origin").setUri(new URIish(remoteUri)).call();

    final FetchResult fetchResult = git.fetch()
        .setRemote("origin")
        .setRefSpecs("+refs/heads/" + branch + ":refs/remotes/origin/" + branch)
        .call();

    logger.debug("Refs: {}", fetchResult.getAdvertisedRefs());
    final Ref fetchHead = fetchResult.getAdvertisedRef(HEAD);

    final ObjectId commitId = fetchHead.getObjectId();
    logger.debug("Commit id: {}", commitId);

    final FileSet fileSet = new FileSet();
    try (final RevWalk revWalk = new RevWalk(repository)) {
      final RevCommit revCommit = revWalk.parseCommit(commitId);
      final RevTree revTree = revCommit.getTree();
      logger.debug("Having tree: " + revTree);

      // now try to find a specific file
      try (final TreeWalk treeWalk = new TreeWalk(repository)) {
        treeWalk.addTree(revTree);
        treeWalk.setRecursive(true);
        while (treeWalk.next()) {
          final String path = treeWalk.getPathString();
          logger.debug("Path: {}", path);
          if (treeWalk.isSubtree()) {
            treeWalk.enterSubtree();
          } else {
            final ObjectId objectId = treeWalk.getObjectId(0);
            final FileContent file = new FileContent(path, () -> {
              try {
                final ObjectLoader loader = repository.open(objectId);
                final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                loader.copyTo(byteOut);
                return new ByteArrayInputStream(byteOut.toByteArray());
              } catch (final IOException e) {
                throw new IllegalStateException(e);
              }
            });
            fileSet.add(file);
          }
        }
      }
      revWalk.dispose();
    }

    this.fileSet = fileSet;
    logger.debug("FileSet: {}", fileSet);
  }

  public Stream<FileContent> stream() {
    return fileSet.stream();
  }

  @Override
  public void start(final int totalTasks) {
    logger.debug("Start: {}", totalTasks);
  }

  @Override
  public void beginTask(final String title, final int totalWork) {
    logger.debug("Start {}: {}", title, totalWork);
  }

  @Override
  public void update(final int completed) {
    logger.trace("Update {}", completed);
  }

  @Override
  public void endTask() {
    logger.debug("End");
  }

  @Override
  public boolean isCancelled() {
    logger.trace("Check cancelled");
    return false;
  }
}
