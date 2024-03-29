/*
 * $Id: StrutsActionProxyFactory.java 651946 2008-04-27 13:41:38Z apetrelli $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// Copyright 2006 Google Inc. All Rights Reserved.

package org.apache.struts2.impl;

import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.DefaultActionProxyFactory;

public class StrutsActionProxyFactory extends DefaultActionProxyFactory {

	/**
	 * 实例化@StrutsActionProxy , 并执行其prepare方法(对其一些属性赋值、初始化，如invocation属性)
	 */
    public ActionProxy createActionProxy(ActionInvocation inv, String namespace, String actionName, String methodName, boolean executeResult, boolean cleanupContext) {
        
        StrutsActionProxy proxy = new StrutsActionProxy(inv, namespace, actionName, methodName, executeResult, cleanupContext);
        container.inject(proxy);
        proxy.prepare();
        return proxy;
    }
}
