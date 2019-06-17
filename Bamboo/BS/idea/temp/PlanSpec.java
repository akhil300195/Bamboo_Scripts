import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.AtlassianModule;
import com.atlassian.bamboo.specs.api.builders.BambooOid;
import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.deployment.ReleaseNaming;
import com.atlassian.bamboo.specs.api.builders.permission.DeploymentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.EnvironmentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.task.AnyTask;
import com.atlassian.bamboo.specs.builders.task.CleanWorkingDirectoryTask;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.builders.trigger.AfterSuccessfulDeploymentTrigger;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.MapBuilder;


@BambooSpec
public class PlanSpec {
    
    public Deployment rootObject() {
        final Deployment rootObject = new Deployment(new PlanIdentifier("ALMT", "UT"),
                //.oid(new BambooOid("1dyfzf23d0nl1")),
            "alm_ansible_poc")
            //.oid(new BambooOid("1dysgeja2ik22"))
            .releaseNaming(new ReleaseNaming("release-7")
                    .autoIncrement(true))
            .environments(new Environment("ALM-DEV")
                    .tasks(new AnyTask(new AtlassianModule("com.cdk.bamboo.ansible-deployment-v2:cdk.ansible.deploymentv2"))
                            .description("Ansible Deployments")
                            .configuration(new MapBuilder()
                                    .put("teamName", "Ansible Migration POC Preprod")
                                    .put("inventoryAccessCode", "cdfa83d867e0dde3a32c731af75502a4")
                                    .put("organizationName", "ALM Engineering Services")
                                    .put("towerInstanceName", "0")
                                    .put("scmInstanceId", "0")
                                    .put("playbookRepoProjectKey", "DSALM")
                                    .put("logLevelVerbose", "")
                                    .put("playbookRepoBranch", "master")
                                    .put("jobMachineCredentialName", "aes_ansp_preprod_machinekey")
                                    .put("playbookName", "praveen_poc.yml")
                                    .put("scmInstanceName", "0")
                                    .put("noDeploymentCleanup", "true")
                                    .put("playbookRepoSlug", "alm_ansible_poc")
                                    .put("scmCredentialName", "ALM Engineering Services_Bamboo_Tower_SCM_Key")
                                    .put("playbookRepoName", "alm_ansible_poc")
                                    .put("inventoryName", "ALM Engineering Services_Ansible Migration POC_Preprod")
                                    .put("vaultCredentialName", "Ansible Vault Credentials")
                                    .put("towerInstanceId", "0")
                                    .put("playbookRepoProjectName", "ALM")
                                    .build())),
                new Environment("Deployment log aggregator")
                    .description("log aggregator")
                    .tasks(new CleanWorkingDirectoryTask(),
                        new ScriptTask()
                            .description("Echo")
                            .inlineBody("echo \"hello world\""))
                    .triggers(new AfterSuccessfulDeploymentTrigger("ALM-DEV")
                            .name("After successful deployment")
                            .description("Trigger")));
        return rootObject;
    }
    
    public DeploymentPermissions deploymentPermission() {
        final DeploymentPermissions deploymentPermission = new DeploymentPermissions("alm_ansible_poc")
            .permissions(new Permissions()
                    .userPermissions("kananiv", PermissionType.VIEW)
                    .userPermissions("kamblea", PermissionType.EDIT, PermissionType.VIEW)
                    .loggedInUserPermissions(PermissionType.VIEW)
                    .anonymousUserPermissionView());
        return deploymentPermission;
    }
    
    public EnvironmentPermissions environmentPermission1() {
        final EnvironmentPermissions environmentPermission1 = new EnvironmentPermissions("alm_ansible_poc")
            .environmentName("ALM-DEV")
            .permissions(new Permissions()
                    .userPermissions("kamblea", PermissionType.EDIT, PermissionType.VIEW, PermissionType.BUILD)
                    .loggedInUserPermissions(PermissionType.VIEW)
                    .anonymousUserPermissionView());
        return environmentPermission1;
    }
    
    public EnvironmentPermissions environmentPermission2() {
        final EnvironmentPermissions environmentPermission2 = new EnvironmentPermissions("alm_ansible_poc")
            .environmentName("Deployment log aggregator")
            .permissions(new Permissions()
                    .userPermissions("kamblea", PermissionType.EDIT, PermissionType.VIEW, PermissionType.BUILD)
                    .loggedInUserPermissions(PermissionType.VIEW)
                    .anonymousUserPermissionView());
        return environmentPermission2;
    }
    
    public static void main(String... argv) {
        //By default credentials are read from the '.credentials' file.
        BambooServer bambooServer = new BambooServer("http://bamboo.cdk.com");
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
