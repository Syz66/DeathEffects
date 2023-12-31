import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.Remapper
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

plugins {
    kotlin("jvm") version ("1.8.0")
    java
    id("com.github.weave-mc.weave-gradle") version ("fac948db7f")
}

val PACKAGE = "me.zircta.deatheffects"
val ZENITH_LOADER_VERSION = "0.1.6"
val ZENITH_CORE_VERSION = "1.4.1"
val CONCENTRA_VERSION = "0.1.2"

group = PACKAGE
version = "1.0"

minecraft.version("1.8.9")

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.polyfrost.cc/releases")
    maven("https://repo.spongepowered.org/maven/")
    maven("https://gitlab.com/api/v4/projects/45235852/packages/maven")
    maven("https://gitlab.com/api/v4/projects/50863327/packages/maven")
}

dependencies {
    implementation("com.gitlab.candicey.zenithloader:Zenith-Loader:$ZENITH_LOADER_VERSION")
    implementation("com.gitlab.candicey.zenithcore:Zenith-Core:$ZENITH_CORE_VERSION")
    implementation("cc.polyfrost:oneconfig-1.8.9-forge:0.2.0-alpha+")
    compileOnly("com.github.Weave-MC:Weave-Loader:v0.2.4")
    compileOnly("org.spongepowered:mixin:0.8.5")
}

tasks.test {
    useJUnitPlatform()
}

val relocate: TaskProvider<Task> = tasks.register("relocate") {
    val originalJar = project.tasks.getByName("jar").outputs.files.singleFile

    doLast {
        val jar = JarFile(originalJar)
        val entries = jar.entries()
        val newJar = JarOutputStream(FileOutputStream(File(originalJar.parentFile, "${originalJar.nameWithoutExtension}-relocated.jar")))

        val classNameReplaceList = mapOf(
            "com/gitlab/candicey/zenithcore" to "com/gitlab/candicey/zenithcore_v${ZENITH_CORE_VERSION.replace('.', '_')}",
            "com/gitlab/candicey/zenithloader" to "${PACKAGE.replace('.', '/')}/libs/zenithloader",
        )

        val stringReplaceList = mapOf(
            "@@CONCENTRA_VERSION@@" to CONCENTRA_VERSION,
            "@@ZENITH_CORE_VERSION@@" to ZENITH_CORE_VERSION,
        )

        val writeEntryToFile = { file: JarFile, outStream: JarOutputStream, entry: JarEntry, entryName: String ->
            outStream.putNextEntry(JarEntry(entryName))
            outStream.write(file.getInputStream(entry).readBytes())
            outStream.closeEntry()
        }

        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (!entry.isDirectory) {
                when {
                    entry.name.startsWith(PACKAGE.replace('.', '/')) || entry.name.startsWith("com/gitlab/candicey/zenithloader") -> {
                        val bytes = jar.getInputStream(entry).readBytes()

                        var cr = ClassReader(bytes)
                        var cw = ClassWriter(cr, 0)
                        cr.accept(ClassRemapper(cw, object : Remapper() {
                            override fun map(internalName: String): String =
                                classNameReplaceList.entries.fold(internalName) { acc, (target, replacement) -> acc.replaceFirst(target, replacement) }
                        }), 0)

                        cr = ClassReader(cw.toByteArray())
                        val cn = ClassNode()
                        cr.accept(cn, 0)

                        for (method in cn.methods) {
                            for (insn in method.instructions) {
                                if (insn is LdcInsnNode) {
                                    if (insn.cst is String) {
                                        val cst = insn.cst as? String ?: continue
                                        if (stringReplaceList.containsKey(cst)) {
                                            val newCst = stringReplaceList[cst]
                                            if (newCst != null) {
                                                insn.cst = newCst
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        cw = ClassWriter(cr, 0)
                        cn.accept(cw)

                        newJar.putNextEntry(JarEntry(classNameReplaceList.entries.fold(entry.name) { acc, (target, replacement) -> acc.replaceFirst(target, replacement) }))
                        newJar.write(cw.toByteArray())
                        newJar.closeEntry()
                    }

                    entry.name == "weave.mod.json" -> {
                        val bytes = jar.getInputStream(entry).readBytes()
                        var string = String(bytes)

                        for ((target, replacement) in classNameReplaceList) {
                            val dotTarget = target.replace('/', '.')
                            val dotReplacement = replacement.replace('/', '.')

                            string = string.replace(dotTarget, dotReplacement)
                        }

                        newJar.putNextEntry(JarEntry(entry.name))
                        newJar.write(string.toByteArray())
                        newJar.closeEntry()
                    }

                    entry.name == "zenithloader/dependencies/${PACKAGE.substringAfterLast('.')}.versions.json" -> {
                        val bytes = jar.getInputStream(entry).readBytes()
                        var string = String(bytes)

                        for ((target, replacement) in stringReplaceList) {
                            string = string.replace(target, replacement)
                        }

                        newJar.putNextEntry(JarEntry(entry.name))
                        newJar.write(string.toByteArray())
                        newJar.closeEntry()
                    }

                    else -> writeEntryToFile(jar, newJar, entry, entry.name)
                }
            }
        }

        newJar.close()
    }

    outputs.file(originalJar.parentFile.resolve("${originalJar.nameWithoutExtension}-relocated.jar"))
}

tasks.jar {
    finalizedBy(tasks.getByName("relocate"))

    val wantedJar = listOf("Zenith-Loader")
    configurations["compileClasspath"]
        .filter { wantedJar.find { wantedJarName -> it.name.contains(wantedJarName) } != null }
        .forEach { file: File ->
            from(zipTree(file.absoluteFile)) {
                this.duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
        }
}

val cleanBuild: Task = tasks.create("cleanBuild") {
    dependsOn(tasks.getByName("clean"))
    finalizedBy(tasks.getByName("build"))
}
