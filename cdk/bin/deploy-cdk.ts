#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import { ContDeployDockerStack } from '../lib/deploy-cdk-app-stack';


const app = new cdk.App();
new ContDeployDockerStack(app, 'ContDeployDockerStack', {
  env: { account: process.env.CDK_DEFAULT_ACCOUNT, region: process.env.CDK_DEFAULT_REGION },

});