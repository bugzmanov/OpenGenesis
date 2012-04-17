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
package com.griddynamics.genesis.configuration

import org.springframework.context.annotation.{Configuration, Bean}
import com.griddynamics.genesis.service.{CredentialService, impl}
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import com.griddynamics.genesis.util.InputUtil

@Configuration
class SingleCredentialServiceContext extends CredentialServiceContext {
    @Value("${genesis.system.default.vm.identity:not-set}") var defaultVmIdentity : String = _
    @Value("${genesis.system.default.vm.credential:not-set}") var defaultVmCredentialResource : Resource = _
    lazy val defaultVmCredential = {
      try {
        InputUtil.resourceAsString(defaultVmCredentialResource)
      } catch {
        //assume it's plain text password
        case e => defaultVmCredentialResource.getFilename

      }
    }

      @Bean def credentialService = {
        if (defaultVmIdentity != "not-set")
          new impl.SingleCredentialsService(defaultVmIdentity, defaultVmCredential)
        else
          new impl.EmptyCredentialsService
      }
}