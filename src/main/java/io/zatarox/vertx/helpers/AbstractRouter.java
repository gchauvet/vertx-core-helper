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
package io.zatarox.vertx.helpers;

import io.zatarox.vertx.helpers.annotations.Path;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import java.lang.reflect.Method;

public abstract class AbstractRouter extends AbstractVerticle {


    final Router router;

    protected AbstractRouter(Router router) {
        this.router = router;
    }

    @Override
    public void start() throws Exception {
        for (Method method : this.getClass().getDeclaredMethods()) {
            final Path res = method.getDeclaredAnnotation(Path.class);
            if (res != null) {
                try {
                    final Method register = router.getClass().getMethod(res.method().name().toLowerCase(), String.class);
                    ((Route) register.invoke(router, res.path())).handler(event -> {
                        try {
                            method.invoke(this, event);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

}
