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
package com.griddynamics.genesis.spring

import org.springframework.web.servlet.mvc.annotation.{AnnotationMethodHandlerAdapter=>AMHA}
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.beans.factory.config.BeanPostProcessor

/**
 * Temporary workaround to add LiftJsonConverter to AnnotationMethodHandlerAdapter
 * https://www.jira.springsource.org/browse/SPR-7504?focusedCommentId=58053&page=com.atlassian.jira.plugin.system.issuetabpanels%253Acomment-tabpanel
 */
class MessageConverterPostProcessor(liftJsonConverter : HttpMessageConverter[ScalaObject])
        extends BeanPostProcessor{

    def postProcessAfterInitialization(bean: AnyRef, beanName: String) = bean

    def postProcessBeforeInitialization(bean: AnyRef, beanName: String) = {
        if(bean.isInstanceOf[AMHA]){
            val adapter = bean.asInstanceOf[AMHA]
            adapter.setMessageConverters(Array(liftJsonConverter) ++ adapter.getMessageConverters)
        }
        bean
    }

}