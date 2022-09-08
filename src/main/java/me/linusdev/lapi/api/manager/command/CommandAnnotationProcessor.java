/*
 * Copyright (c) 2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi.api.manager.command;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes("me.linusdev.lapi.api.manager.command.Command")
public class CommandAnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        roundEnv.processingOver();

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "hi");
        System.out.println("process");

        for(TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);

            for( Element e : elements){
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, e.getSimpleName());
            }

        }

        try {
            processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "main", "a");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "hi");

        return true;
    }

    public static void main(String[] args) {
        System.out.println("main1");
        System.out.println("mainsdad22");
    }
}
