import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.BambooOid;
import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.deployment.ReleaseNaming;
import com.atlassian.bamboo.specs.api.builders.permission.DeploymentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.EnvironmentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.builders.task.ArtifactDownloaderTask;
import com.atlassian.bamboo.specs.builders.task.CleanWorkingDirectoryTask;
import com.atlassian.bamboo.specs.builders.task.DownloadItem;
import com.atlassian.bamboo.specs.builders.trigger.AfterSuccessfulBuildPlanTrigger;
import com.atlassian.bamboo.specs.util.BambooServer;

@BambooSpec
public class PlanSpec {
    
    public Deployment rootObject() {
        final Deployment rootObject = new Deployment(new PlanIdentifier("PB", "ALPBP"),
                //.oid(new BambooOid("14usexbmyt79g")),
            "Deploy_pb_Artifacts")
            //.oid(new BambooOid("14v4vwstoaubu"))
            .description("deploys the artifact in various environments")
            .releaseNaming(new ReleaseNaming("release-22")
                    .autoIncrement(true))
            .environments(new Environment("QA")
                    .description("deploys here for testing")
                    .tasks(new CleanWorkingDirectoryTask()
                            .enabled(false),
                        new ArtifactDownloaderTask()
                            .description("Download release contents")
                            .artifacts(new DownloadItem()
                                    .artifact("Deploy_pb_Artifact")
                                    .path("TestRepo/")))
                    .triggers(new AfterSuccessfulBuildPlanTrigger()
                            .description("Deploying the Artifact")),
                new Environment("dev")
                    .tasks(new CleanWorkingDirectoryTask(),
                        new ArtifactDownloaderTask()
                            .description("Download release contents")
                            .artifacts(new DownloadItem()
                                    .allArtifacts(true))));
        return rootObject;
    }
    
    public DeploymentPermissions deploymentPermission() {
        final DeploymentPermissions deploymentPermission = new DeploymentPermissions("Deploy_pb_Artifacts")
            .permissions(new Permissions()
                    .userPermissions("varanasp", PermissionType.VIEW, PermissionType.EDIT)
                    .loggedInUserPermissions(PermissionType.VIEW)
                    .anonymousUserPermissionView());
        return deploymentPermission;
    }
    
    public EnvironmentPermissions environmentPermission1() {
        final EnvironmentPermissions environmentPermission1 = new EnvironmentPermissions("Deploy_pb_Artifacts")
            .environmentName("QA")
            .permissions(new Permissions()
                    .userPermissions("tester1", PermissionType.VIEW, PermissionType.EDIT)
                    .userPermissions("varanasp", PermissionType.BUILD, PermissionType.VIEW, PermissionType.EDIT)
                    .loggedInUserPermissions(PermissionType.VIEW));
        return environmentPermission1;
    }
    
    public EnvironmentPermissions environmentPermission2() {
        final EnvironmentPermissions environmentPermission2 = new EnvironmentPermissions("Deploy_pb_Artifacts")
            .environmentName("dev")
            .permissions(new Permissions()
                    .userPermissions("varanasp", PermissionType.BUILD, PermissionType.VIEW, PermissionType.EDIT)
                    .loggedInUserPermissions(PermissionType.VIEW)
                    .anonymousUserPermissionView());
        return environmentPermission2;
    }
    
    public static void main(String... argv) {
        //By default credentials are read from the '.credentials' file.
        BambooServer bambooServer = new BambooServer("http://bamboo-lab.cdk.com");
        final PlanSpec planSpec = new PlanSpec();
        
        final Deployment rootObject = planSpec.rootObject();
        bambooServer.publish(rootObject);
        
        final DeploymentPermissions deploymentPermission = planSpec.deploymentPermission();
        bambooServer.publish(deploymentPermission);
        
        final EnvironmentPermissions environmentPermission1 = planSpec.environmentPermission1();
        bambooServer.publish(environmentPermission1);
        
        final EnvironmentPermissions environmentPermission2 = planSpec.environmentPermission2();
        bambooServer.publish(environmentPermission2);
    }
}