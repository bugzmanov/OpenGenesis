/*
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
 *   Project:     Genesis
 *   Description:  Continuous Delivery Platform
 */
package com.griddynamics.genesis.service.impl

import org.scalatest.mock.MockitoSugar
import org.scalatest.junit.AssertionsForJUnit
import com.griddynamics.genesis.util.IoUtil
import org.springframework.core.convert.support.DefaultConversionService
import org.mockito.{Matchers, Mockito}
import org.junit.{Ignore, Test}
import com.griddynamics.genesis.cache.NullCacheManager
import org.mockito.Mockito._
import com.griddynamics.genesis.template.VersionedTemplate
import com.griddynamics.genesis.api

class GroovyIncludesTest extends AssertionsForJUnit with MockitoSugar with DSLTestUniverse  {

    when(configService.get(Matchers.any(), Matchers.any())).thenReturn(Some(new api.Configuration(Some(0), "", 0, None, Map())))

    val templateService = new GroovyTemplateService(templateRepoService,
        List(new DoNothingStepBuilderFactory), new DefaultConversionService,
        Seq(), null, configService, NullCacheManager)

    val bodyMain = IoUtil.streamAsString(classOf[GroovyTemplateServiceTest].getResourceAsStream("/groovy/Includes.genesis"))
    val bodyInc =
      """
          f = { x -> println("Test function call."  + x) };
          s = "Test String value";
          i = 0
     """

    Mockito.when(templateRepository.listSources()).thenReturn(Map(
        VersionedTemplate("1") -> bodyMain,
        VersionedTemplate("/groovy/test1.groovy") -> bodyInc)
    )

    @Test
    @Ignore("includes are not supported for a while")
    def testInclude() {
        assert(templateService.findTemplate(0, "Includes", "0.1", 1).isDefined)
    }

}
