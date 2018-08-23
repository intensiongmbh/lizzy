/*******************************************************************************
 * Copyright 2018 Intension GmbH (https://www.intension.de)
 * and other contributors as indicated by the @author tags.
 * 
 * Licensed under the Eclipse Public License - v 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.eclipse.org/legal/epl-2.0/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.intension.lizzy.converter.gherkin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;

import de.intension.lizzy.converter.CaseFormat;
import gherkin.ast.Feature;

/**
 * Converts a gherkin feature string to a java class file.
 * See more about the file generation at: {@link GherkinGenerator}.
 * 
 * @author <a href="mailto:ikuba@intension.de">Ingo Kuba</a>
 */
public class GherkinConverter
{

    private String     location     = "src/test/java";
    private String     packageName;
    private CaseFormat methodFormat = CaseFormat.SNAKE_CASE;

    /**
     * Converts a gherkin string to a java test class.
     * Uses {@link GherkinParser#parseFeature(String)}
     * And {@link GherkinGenerator#generate(Feature, String, CaseFormat)}
     * to generate the file content.
     *
     * @param string Gherkin feature string to convert.
     * @throws IOException When the file could not be written to.
     */
    public void convert(String string)
        throws IOException
    {
        Feature feature = GherkinParser.parseFeature(string);
        JavaFile javaFile = GherkinGenerator.generate(feature, packageName, methodFormat);
        File dir = new File(location);
        File file = new File(location + File.separator + javaFile.packageName.replace(".", File.separator) + File.separator + javaFile.typeSpec.name + ".java");
        if (file.exists()) {
            appendMethods(javaFile, file);
            return;
        }
        javaFile.writeTo(dir);
    }

    /**
     * Appends methods to an existing test class.
     * Method doesn't get added if method already exists in the file.
     */
    private void appendMethods(JavaFile javaFile, File file)
        throws IOException
    {
        CompilationUnit unit = JavaParser.parse(file);
        Optional<ClassOrInterfaceDeclaration> optional = unit.getClassByName(javaFile.typeSpec.name);
        if (!optional.isPresent()) {
            return;
        }
        ClassOrInterfaceDeclaration javaClass = optional.get();
        javaFile.typeSpec.methodSpecs.forEach(method -> addMethod(javaClass, method));
        String polishedFile = unit.toString().replace("@Test()", "@Test");
        Files.write(file.toPath(), polishedFile.getBytes());
    }

    /**
     * Adds a method to the class declaration with modifier {@link Modifier#PUBLIC}
     * and annotation @{@link Test}.
     */
    private MethodDeclaration addMethod(ClassOrInterfaceDeclaration javaClass, MethodSpec methodSpec)
    {
        String methodName = methodSpec.name;
        if (!javaClass.getMethodsByName(methodName).isEmpty()) {
            return null;
        }
        javaClass.addMethod(methodName, Modifier.PUBLIC);
        MethodDeclaration method = javaClass.getMethodsByName(methodName).get(0);
        method.setJavadocComment(methodSpec.javadoc.toString());
        method.addAnnotation(Test.class);
        return method;
    }

    /**
     * Determines where the java file is located.
     * Default is src/test/java/&#60;packageName>
     *
     * @see #setPackageName(String)
     */
    public GherkinConverter setLocation(String location)
    {
        this.location = location;
        return this;
    }

    /**
     * Determines the package name of the class to generate and where the java file is located.
     *
     * @see #setLocation(String)
     */
    public GherkinConverter setPackageName(String packageName)
    {
        this.packageName = packageName;
        return this;
    }

    /**
     * @see #setMethodFormat(CaseFormat)
     */
    public GherkinConverter setMethodFormat(String methodFormat)
    {
        this.methodFormat = CaseFormat.valueOf(methodFormat);
        return this;
    }

    /**
     * Use this format to determine whether the method names
     * should be generated in snake or camel case.
     *
     * @see CaseFormat
     */
    public GherkinConverter setMethodFormat(CaseFormat methodFormat)
    {
        this.methodFormat = methodFormat;
        return this;
    }
}
