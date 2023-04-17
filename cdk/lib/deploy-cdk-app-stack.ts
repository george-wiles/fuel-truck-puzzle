import { Stack, StackProps, Construct, SecretValue } from '@aws-cdk/core';
import { Vpc } from '@aws-cdk/aws-ec2';
import * as ecr from '@aws-cdk/aws-ecr';
import * as ecs from '@aws-cdk/aws-ecs';
import * as ecspatterns from '@aws-cdk/aws-ecs-patterns';
import * as codebuild from '@aws-cdk/aws-codebuild';
import { ManagedPolicy } from '@aws-cdk/aws-iam';
import { Artifact, Pipeline } from '@aws-cdk/aws-codepipeline';
import { GitHubSourceAction, CodeBuildAction, EcsDeployAction} from '@aws-cdk/aws-codepipeline-actions';
import { PipelineProject, LocalCacheMode } from '@aws-cdk/aws-codebuild';


const repoName = "puzzle-webapp";

/**
 * Creates and deploys a vpc, load balancer Fargate serverless instance with a dockerised springboot
 * app using ECR/EC2 via code build pipeline using aws-ecs-patterns.
 * 
 * Will cost some $ as docker java 11 template and ECR is not free tier.
 */
export class ContDeployDockerStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    var oauthToken = SecretValue.secretsManager('github/portal/oauth/token/');

    let sourceOutput: Artifact;
    let buildOutput: Artifact;

    //Place resource definitions here.
    var vpc = new Vpc(this, 'puzzle.vpc', {
      cidr: '10.0.0.0/16',
      maxAzs: 2
    });

  
    const ecrRepository = ecr.Repository.fromRepositoryName(this, repoName, repoName);

    var pipelineProject = this.createPipelineProject(ecrRepository);
    pipelineProject.role?.addManagedPolicy(ManagedPolicy.fromAwsManagedPolicyName('AmazonEC2ContainerRegistryPowerUser'));

    sourceOutput = new Artifact();
    buildOutput = new Artifact();

    var githubSourceAction = this.createWilesPortalGithubSourceAction(sourceOutput, oauthToken);
    console.log("git action: " + githubSourceAction);
    var buildAction = this.createWilesPortalBuildAction(pipelineProject, sourceOutput, buildOutput);
    console.log("build action: " + buildAction);
    var ecsDeployAction = this.createEcsDeployAction(vpc, ecrRepository, buildOutput, pipelineProject);
    console.log("build action: " + ecsDeployAction);

    var pipeline = new Pipeline(this, 'puzzle_pipeline', {
      stages: [
        {
          stageName: 'Source',
          actions: [githubSourceAction]
        },
        {
          stageName: 'Build',
          actions: [buildAction]
        },
        {
          stageName: 'Deploy',
          actions: [ecsDeployAction]
        },
      ],
      pipelineName: "puzzle_pipeline"
    });

  }

  // ----------------------- some helper methods -----------------------
  /**
   * create the Pipeline Project wuth Buildspec and stuff
   */
  private createPipelineProject(ecrRepo: ecr.IRepository): codebuild.PipelineProject {
    var pipelineProject = new codebuild.PipelineProject(this, 'puzzle-codepipeline', {
      projectName: 'my-puzzle',
      environment: {
        buildImage: codebuild.LinuxBuildImage.STANDARD_5_0,
        privileged: true
      },
      environmentVariables: {
        "ECR_REPO": {
          value: ecrRepo.repositoryUriForTag()
        }
      },
      buildSpec: codebuild.BuildSpec.fromObject({
        version: '0.2',
        phases: {
          install: {
            commands: [
              "#apt-get update -y",
            ],
            finally: [
              "echo Done installing deps"
            ],
          },
          pre_build: {
            commands: [
              'echo Logging in to Amazon ECR...',
              'echo docker info',
              'docker info',
              'echo $ECR_REPO',
              'aws --version',
              'echo $(aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin $ECR_REPO)',
              'aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin $ECR_REPO',
              'COMMIT_HASH=$(echo $CODEBUILD_RESOLVED_SOURCE_VERSION | cut -c 1-7)',
              'IMAGE_TAG=${COMMIT_HASH:=latest}'
            ],
          },
          build: {
            commands: [
              'echo Build started on `date`',
              'cd app',
              'javac --version',
              './mvnw bootJar',
              'echo Building Docker Image $ECR_REPO:latest',
              'docker build -f Dockerfile -t $ECR_REPO:latest .',
              'echo Tagging Docker Image $ECR_REPO:latest with $ECR_REPO:$IMAGE_TAG',
              'docker tag $ECR_REPO:latest $ECR_REPO:$IMAGE_TAG',
              'echo Pushing Docker Image to $ECR_REPO:latest and $ECR_REPO:$IMAGE_TAG',
              'docker push $ECR_REPO:latest',
              'docker push $ECR_REPO:$IMAGE_TAG',
              'pwd'
            ],
            finally: [
              "echo Done building code"
            ],
          },
          post_build: {
            commands: [
              "echo creating imagedefinitions.json dynamically",
              "printf '[{\"name\":\"" + repoName + "\",\"imageUri\": \"" + ecrRepo.repositoryUriForTag() + ":latest\"}]' > imagedefinitions.json",
              'XX="$(ls -l /root/.mvn/)"; printf "%s\n" "$XX"',
              'ls -la',
              'pwd',
              'cat ./imagedefinitions.json',
              'mv ./imagedefinitions.json ../',
              "echo Build completed on `date`"
            ]
          }
        },
        artifacts: {
          files: [
            "./imagedefinitions.json"
          ]
        },
        cache: {
          paths: [
            './app/.mvn/**/*',
          ]
       }
      }),
      cache: codebuild.Cache.local(LocalCacheMode.DOCKER_LAYER, LocalCacheMode.CUSTOM)
    });
    return pipelineProject;
  }

  /**
   * creates Github Source
   * @param sourceOutput where to put the clones Repository
   */
  public createWilesPortalGithubSourceAction(sourceOutput: Artifact, oauthToken: SecretValue): GitHubSourceAction {
    return new GitHubSourceAction({
      actionName: 'wiles_puzzle_github_source',
      owner: 'george-wiles',
      repo: 'puzzle-webapp',
      oauthToken: oauthToken,
      output: sourceOutput,
      branch: 'main',
    });
  }

  /**
   * Creates the BuildAction for Codepipeline build step
   * @param pipelineProject pipelineProject to use 
   * @param sourceActionOutput input to build
   * @param buildOutput where to put the ouput
   */
  public createWilesPortalBuildAction(pipelineProject: codebuild.PipelineProject, sourceActionOutput: Artifact,
    buildOutput: Artifact): CodeBuildAction {
    var buildAction = new CodeBuildAction({
      actionName: 'PuzzlePortalWebAppBuild',
      project: pipelineProject,
      input: sourceActionOutput,
      outputs: [buildOutput],

    });
    return buildAction;
  }

  public createEcsDeployAction(vpc: Vpc, ecrRepo: ecr.IRepository, buildOutput: Artifact, pipelineProject: PipelineProject): EcsDeployAction {
    return new EcsDeployAction({
      actionName: 'EcsDeployAction',
      service: this.createLoadBalancedFargateService(this, vpc, ecrRepo, pipelineProject).service,
      input: buildOutput,
    })
  };

  createLoadBalancedFargateService(scope: Construct, vpc: Vpc, ecrRepository: ecr.IRepository, pipelineProject: PipelineProject) {
    var fargateService = new ecspatterns.ApplicationLoadBalancedFargateService(scope, 'puzzlePortalLbFargateService', {
      vpc: vpc,
      memoryLimitMiB: 512,
      cpu: 256,
      assignPublicIp: true,
      taskImageOptions: {
        containerName: repoName,
        image: ecs.ContainerImage.fromRegistry(repoName),
        containerPort: 8080,
      },
    });
    fargateService.taskDefinition.executionRole?.addManagedPolicy((ManagedPolicy.fromAwsManagedPolicyName('AmazonEC2ContainerRegistryPowerUser')));
    return fargateService;
  }
}