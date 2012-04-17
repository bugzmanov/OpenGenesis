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
package com.griddynamics.genesis.template.dsl.groovy

import collection.mutable.ListBuffer
import groovy.lang.Closure
import scala.Some
import com.griddynamics.genesis.service.ValidationError
import java.lang.IllegalStateException

class EnvWorkflow(val name : String, val variables : List[VariableDetails], val stepsGenerator : Option[Closure[Unit]])

class EnvironmentTemplate(val name : String,
                          val version : String,
                          val projectId : Option[String],
                          val createWorkflow : String,
                          val destroyWorkflow : String,
                          val workflows : List[EnvWorkflow]) {
}

class VariableDetails(val name : String, val clazz : Class[_ <: AnyRef], val description : String,
                      val validators : Seq[Closure[Boolean]], val isOptional: Boolean = false, val defaultValue: Option[Any])

class VariableBuilder(val name : String) {
    var description : String = _
    var validators = new ListBuffer[Closure[Boolean]]
    var clazz : Class[_ <: AnyRef] = classOf[String]
    var defaultValue: Any = _
    var isOptional: Boolean = false

    def as(value : Class[_ <: AnyRef]) = {
        this.clazz = value
        this
    }

    def description(description : String) = {
        description_=(description)
        this
    }

    def validator(validator : Closure[Boolean]) = {
        validators += validator
        this
    }
  
    def optional(v: Any) = {
      isOptional = true
      defaultValue = v
      this
    }

    def newVariable = new VariableDetails(name, clazz, description, validators, isOptional, Option(defaultValue))
}

class VariableDeclaration {
    val builders = new ListBuffer[VariableBuilder]

    def variable(name : String) = {
        val builder = new VariableBuilder(name)
        builders += builder
        builder
    }
}

class WorkflowDeclaration {
    var variablesBlock : Option[Closure[Unit]] = None
    var stepsBlock : Option[Closure[Unit]] = None

    def variables(variables : Closure[Unit]) {
        variablesBlock = Some(variables)
    }

    def steps(steps : Closure[Unit]) {
        stepsBlock = Some(steps)
    }
}

class EnvTemplateBuilder {
    var name : String = _
    var version : String = _

    var createWorkflow : String = _
    var destroyWorkflow : String = _

    val workflows = ListBuffer[EnvWorkflow]()

    def workflow(name: String, details : Closure[Unit]) = {
        if (workflows.find(_.name == name).isDefined)
            throw new IllegalStateException("workflow with name '%s' is already defined".format(name))
        val delegate = new WorkflowDeclaration
        details.setDelegate(delegate)
        details.call()

        val variableBuilders = delegate.variablesBlock match {
            case Some(block) => {
                val variablesDelegate = new VariableDeclaration
                block.setDelegate(variablesDelegate)
                block.call()
                variablesDelegate.builders
            } case None => Seq[VariableBuilder]()
        }

        val variables = for(builder <- variableBuilders) yield builder.newVariable

        workflows += new EnvWorkflow(name, variables.toList, delegate.stepsBlock)
        this
    }

    def name(value : String) : EnvTemplateBuilder = {
        if (name != null)
            throw new IllegalStateException("name is already set")
        this.name = value
        this
    }

    def version(value : String) : EnvTemplateBuilder = {
        if (version != null) throw new IllegalStateException("version is already set")
        this.version = value
        this
    }

    def createWorkflow(name : String) : EnvTemplateBuilder = {
        if (createWorkflow != null) throw new IllegalStateException("create workflow name is already set")
        this.createWorkflow = name
        this
    }

    def destroyWorkflow(name : String) : EnvTemplateBuilder = {
        if (destroyWorkflow != null) throw new IllegalStateException("destroy workflow name is already set")
        this.destroyWorkflow = name
        this
    }

    def newTemplate = {
        if (name == null) throw new IllegalStateException("name is not set")
        if (version == null) throw new IllegalStateException("version is not set")
        if (createWorkflow == null) throw new IllegalStateException("create workflow name is not set")
        if (destroyWorkflow == null) throw new IllegalStateException("destroy workflow name is not set")
        if (workflows.find(_.name == createWorkflow).isEmpty) throw new IllegalStateException("create workflow is not defined")
        if (workflows.find(_.name == destroyWorkflow).isEmpty) throw new IllegalStateException("destroy workflow is not defined")
        new EnvironmentTemplate(name, version, None, createWorkflow, destroyWorkflow, workflows.toList)
    }
  
    def newTemplate(extName: Option[String], extVersion: Option[String], extProject: Option[String]) = {
      val templateName = extName match {
        case None => name
        case Some(s) => s
      }
      val templateVersion = extVersion match {
        case None => version
        case Some(s) => s
      }
      if (templateName == null) throw new IllegalStateException("name is not set")
      if (templateVersion == null) throw new IllegalStateException("version is not set")
      if (createWorkflow == null) throw new IllegalStateException("create workflow name is not set")
      if (destroyWorkflow == null) throw new IllegalStateException("destroy workflow name is not set")
      if (workflows.find(_.name == createWorkflow).isEmpty) throw new IllegalStateException("create workflow is not defined")
      if (workflows.find(_.name == destroyWorkflow).isEmpty) throw new IllegalStateException("destroy workflow is not defined")
      new EnvironmentTemplate(templateName, templateVersion, extProject, createWorkflow, destroyWorkflow, workflows.toList)
    }
}

class BlockDeclaration {
    val bodies = ListBuffer[Closure[Unit]]()

    def declare(body : Closure[Unit]) {
        if (body == null) return
        bodies += body
    }
}