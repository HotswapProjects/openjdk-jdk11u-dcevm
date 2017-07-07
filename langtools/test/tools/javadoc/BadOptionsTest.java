/*
 * Copyright (c) 2002, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug 8169676
 * @summary boolean result of Option.process is often ignored
 * @modules jdk.compiler/com.sun.tools.javac.api
 * @modules jdk.compiler/com.sun.tools.javac.main
 * @modules jdk.javadoc/jdk.javadoc.internal.api
 * @modules jdk.javadoc/jdk.javadoc.internal.tool
 * @library /tools/lib /tools/javadoc/lib
 * @build toolbox.JavacTask toolbox.JavadocTask toolbox.TestRunner toolbox.ToolBox ToyDoclet
 * @run main BadOptionsTest
 */

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import javax.lang.model.SourceVersion;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import toolbox.JavadocTask;
import toolbox.ModuleBuilder;
import toolbox.Task;
import toolbox.TestRunner;
import toolbox.ToolBox;

/*
 * This is primarily a test of the error reporting mechanisms
 * for bad options provided by javac and utilized by javadoc.
 * It is not an exhaustive test of all bad option forms detected
 * by javac/javadoc.
 */
public class BadOptionsTest extends TestRunner {

    public static void main(String... args) throws Exception {
        BadOptionsTest t = new BadOptionsTest();
        t.runTests();
    }

    private final ToolBox tb = new ToolBox();
    private final Path src = Paths.get("src");
    private final Class<?> docletClass;

    BadOptionsTest() throws Exception {
        super(System.err);
        docletClass = getClass().getClassLoader().loadClass("ToyDoclet");
        init();
    }

    void init() throws IOException {
        tb.writeJavaFiles(src,
                "public class C { }");

    }

    @Test
    public void testAddModulesEmptyArg() {
        Task.Result result = new JavadocTask(tb, Task.Mode.API)
                .docletClass(docletClass)
                .options("--add-modules", "")
                .files(src.resolve("C.java"))
                .run(Task.Expect.FAIL)
                .writeAll();
        checkFound(result.getOutput(Task.OutputKind.DIRECT),
                "javadoc: error - no value for --add-modules option");
        checkNotFound(result, "Exception", "at jdk.javadoc/");
    }

    @Test
    public void testAddModulesBadName() {
        Task.Result result = new JavadocTask(tb, Task.Mode.API)
                .docletClass(docletClass)
                .options("--add-modules", "123")
                .files(src.resolve("C.java"))
                .run(Task.Expect.FAIL)
                .writeAll();
        checkFound(result.getOutput(Task.OutputKind.DIRECT),
                "error: bad name in value for --add-modules option: '123'");
        checkNotFound(result, "Exception", "at jdk.javadoc/");
    }

    @Test
    public void testAddExportsEmptyArg() {
        Task.Result result = new JavadocTask(tb, Task.Mode.API)
                .docletClass(docletClass)
                .options("--add-exports", "")
                .files(src.resolve("C.java"))
                .run(Task.Expect.FAIL)
                .writeAll();
        checkFound(result.getOutput(Task.OutputKind.DIRECT),
                "javadoc: error - no value for --add-exports option");
        checkNotFound(result, "Exception", "at jdk.javadoc/");
    }

    @Test
    public void testAddExportsBadArg() {
        Task.Result result = new JavadocTask(tb, Task.Mode.API)
                .docletClass(docletClass)
                .options("--add-exports", "m/p")
                .files(src.resolve("C.java"))
                .run(Task.Expect.FAIL)
                .writeAll();
        checkFound(result.getOutput(Task.OutputKind.DIRECT),
                "javadoc: error - bad value for --add-exports option");
        checkNotFound(result, "Exception", "at jdk.javadoc/");
    }

    @Test
    public void testAddExportsBadName() {
        Task.Result result = new JavadocTask(tb, Task.Mode.API)
                .docletClass(docletClass)
                .options("--add-exports", "m!/p1=m2")
                .files(src.resolve("C.java"))
                .run()
                .writeAll();
        checkFound(result.getOutput(Task.OutputKind.DIRECT),
                "warning: bad name in value for --add-exports option: 'm!'");
        checkNotFound(result, "Exception", "at jdk.javadoc/");
    }

    private void checkFound(String log, String... expect) {
        for (String e : expect) {
            if (!log.contains(e)) {
                error("Expected string not found: '" + e + "'");
            }
        }
    }

    private void checkNotFound(Task.Result result, String... unexpected) {
        for (Task.OutputKind k : Task.OutputKind.values()) {
            String r = result.getOutput(k);
            for (String u : unexpected) {
                if (r.contains(u)) {
                    error("Unexpected string found: '" + u + "'");
                }
            }
        }
    }
}
