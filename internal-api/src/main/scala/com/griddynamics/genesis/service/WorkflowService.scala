/**
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 *   http://www.griddynamics.com
 *
 *   This library is free software; you can redistribute it and/or modify it under the terms of
 *   the GNU Lesser General Public License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or any later version.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *   AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *   IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *   DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 *   FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *   DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *   SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *   CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *   OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *   OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   @Project:     Genesis
 *   @Description: Execution Workflow Engine
 */
package com.griddynamics.genesis.service

package workflow

import java.lang.RuntimeException
import com.griddynamics.genesis.plugin.GenesisStep

case class FailureDetails(description: String, executionLog: String)

case class WorkflowStatus(status: WorkflowStatus, stepsCompleted: Int, stepsTotal: Int, failedSteps: Seq[FailureDetails])

class WorkflowException(reason: String) extends RuntimeException(reason)

trait WorkflowFuture {
    def getStatus: String

    def resume()

    def suspend()

    def cancel()
}

trait Environment {
    def destroy(steps: Seq[GenesisStep]): WorkflowFuture

    def executeWorkflow(steps: Seq[GenesisStep]): WorkflowFuture

    def listWorkflows: Seq[WorkflowFuture]

    def currentWorkflow: WorkflowFuture
}

trait WorkflowService {
    def createEnvironment(projectId:Int, envName: String, envCreator: String, steps: Seq[GenesisStep]): Environment

    def getEnvironment(envName: String): Environment

    def listEnvironments: Seq[Environment]
}