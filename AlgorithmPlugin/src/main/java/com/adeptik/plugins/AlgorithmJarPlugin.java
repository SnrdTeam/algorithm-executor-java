package com.adeptik.plugins;

import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.bundling.Zip;

/**
 * Плагин Gradle для сборки Опредления алгоритма
 */
@SuppressWarnings("WeakerAccess")
public class AlgorithmJarPlugin implements Plugin<Project> {

    public static final String BUILD_GROUP = "build";
    public static final String DISTRIBUTION_GROUP = "distribution";
    public static final String COMPILE_ALGORITHM_TASK_NAME = "compileAlgorithm";
    public static final String ALG_TASK_NAME = "alg";
    public static final String RUNTIME_FORMAT = "jre%d-jar";
    public static final String ALG_EXTENSION = "alg";

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(Project project) {

        project.getPluginManager().apply(JavaPlugin.class);

        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        configureCompileAlgorithm(javaConvention);
        configureAlg(project);
    }

    /**
     * Инициализация задачи "alg"
     *
     * @param project Проект
     */
    private void configureAlg(Project project) {

        Zip alg = project.getTasks().create(ALG_TASK_NAME, Zip.class);
        alg.setGroup(DISTRIBUTION_GROUP);
        Task compileAlgorithm = project.getTasks().getByName(COMPILE_ALGORITHM_TASK_NAME);
        alg.dependsOn(compileAlgorithm);
        alg.from(compileAlgorithm);
        alg.setExtension(ALG_EXTENSION);
        alg.setMetadataCharset("UTF-8");
    }

    /**
     * Инициализация задачи "compileAlgorithm"
     *
     * @param javaConvention Расширение плагина Java
     */
    private void configureCompileAlgorithm(JavaPluginConvention javaConvention) {

        ProjectInternal project = javaConvention.getProject();
        CompileAlgorithmTask alg = project.getTasks().create(COMPILE_ALGORITHM_TASK_NAME, CompileAlgorithmTask.class);
        alg.setDescription("Assembles a alg archive containing algorithm definition.");
        alg.setGroup(BUILD_GROUP);
        Task jar = project.getTasks().getByName(JavaPlugin.JAR_TASK_NAME);
        alg.dependsOn(jar);
        alg.setRuntimeName(getRuntimeName(javaConvention));
        alg.getLibs().from(jar);
        Configuration compileConfiguration = project.getConfigurations().getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME);
        alg.getLibs().from(compileConfiguration);
        project.afterEvaluate(x -> alg.setRuntimeName(getRuntimeName(javaConvention)));

        Task build = project.getTasks().getByName(JavaBasePlugin.BUILD_TASK_NAME);
        build.dependsOn(alg);
    }

    private String getRuntimeName(JavaPluginConvention javaConvention) {
        return String.format(RUNTIME_FORMAT,
                min(javaConvention.getTargetCompatibility(), JavaVersion.current()).ordinal() + 1);
    }

    private static JavaVersion min(JavaVersion version1, JavaVersion version2) {

        if (version1 == null && version2 == null)
            return null;
        if (version1 == null)
            return version2;
        if (version2 == null)
            return version1;

        if (version1.ordinal() <= version2.ordinal())
            return version1;
        return version2;
    }
}
