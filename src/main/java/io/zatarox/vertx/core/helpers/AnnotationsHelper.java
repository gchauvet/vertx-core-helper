/*
 * Copyright 2016 Guillaume Chauvet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zatarox.vertx.core.helpers;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.zatarox.vertx.async.AsyncFactorySingleton;
import io.zatarox.vertx.async.api.AsyncCollections;
import io.zatarox.vertx.async.impl.AsyncResultHandlerWrapper;
import io.zatarox.vertx.core.helpers.annotations.VerticleGroup;
import java.util.Arrays;
import java.util.List;
import org.reflections.Reflections;

public final class AnnotationsHelper {

    /**
     * Loads verticles annoted with @VerticleGroup
     *
     * @param instance Vertx instance requester
     * @param groups verticle groups to load
     * @param hndlr handler
     */
    public static void deployVerticles(final Vertx instance, String[] groups, final Handler<AsyncResult<Void>> hndlr) {
        final List<String> theGroups = Arrays.asList(groups);
        final Reflections reflections = new Reflections();
        final AsyncCollections async = AsyncFactorySingleton.getInstance().createCollections(instance.getOrCreateContext());
        async.each(reflections.getTypesAnnotatedWith(VerticleGroup.class), (item, eachHandler) -> {
            if (theGroups.contains(item.getAnnotation(VerticleGroup.class).group())) {
                instance.deployVerticle(item.getName(), new AsyncResultHandlerWrapper(eachHandler));
            }
        }, handler -> {
            hndlr.handle(handler);
        });
    }

}
