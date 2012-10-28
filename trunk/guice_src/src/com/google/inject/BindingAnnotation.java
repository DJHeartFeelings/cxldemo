/**
 * Copyright (C) 2006 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.inject;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * gucie可以根据annotatedWith注解指定实例，如:<br>
 * bind(FortuneService.class).annotatedWith(Mega.class) .to(MegaFortuneService.class); 
 * <br>其中Mega类的定义就需要使用到BindingAnnotation注解作标示。
 * <p>
 * Annotates annotations which are used for binding. Only one such annotation
 * may apply to a single injection point. You must also annotate binder
 * annotations with {@code @Retention(RUNTIME)}. For example:
 *
 * <pre>
 *   {@code @}Retention(RUNTIME)
 *   {@code @}Target({ FIELD, PARAMETER, METHOD })
 *   {@code @}BindingAnnotation
 *   public {@code @}interface Transactional {}
 * </pre>
 *
 * @author crazybob@google.com (Bob Lee)
 */
@Target(ANNOTATION_TYPE)
@Retention(RUNTIME)
public @interface BindingAnnotation {}
