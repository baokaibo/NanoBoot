package org.nanoboot.cli;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * NanoBoot CLI 工具
 */
public class NanoBootCLI {

    private static final String TEMPLATE_DIR = "templates";

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0];

        switch (command) {
            case "create":
                if (args.length < 2) {
                    System.out.println("错误: 请提供项目名称");
                    printUsage();
                    return;
                }
                String projectName = args[1];
                createProject(projectName);
                break;
            case "--version":
            case "-v":
                System.out.println("NanoBoot CLI v1.0.0");
                break;
            case "help":
            case "-h":
            default:
                printUsage();
                break;
        }
    }

    private static void printUsage() {
        System.out.println("Usage: nanoboot [command] [options]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  create <project-name>    Create a new NanoBoot project");
        System.out.println("  --version, -v           Show version information");
        System.out.println("  help, -h                Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  nanoboot create my-app");
    }

    private static void createProject(String projectName) {
        System.out.println("Creating NanoBoot project: " + projectName);

        try {
            Path projectPath = Paths.get(projectName);

            // 创建项目目录结构
            createProjectStructure(projectPath);

            // 生成配置文件
            generateConfigFiles(projectPath, projectName);

            // 生成源代码
            generateSourceCode(projectPath, projectName);

            // 生成示例代码
            generateExampleCode(projectPath, projectName);

            System.out.println("Project '" + projectName + "' created successfully!");
            System.out.println();
            System.out.println("To run your application:");
            System.out.println("  cd " + projectName);
            System.out.println("  mvn spring-boot:run");
            System.out.println();
            System.out.println("Or package and run:");
            System.out.println("  mvn package");
            System.out.println("  java -jar target/" + projectName + "-0.0.1-SNAPSHOT.jar");

        } catch (Exception e) {
            System.err.println("Error creating project: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createProjectStructure(Path projectPath) throws IOException {
        // 创建目录结构
        Files.createDirectories(projectPath.resolve("src/main/java"));
        Files.createDirectories(projectPath.resolve("src/main/resources"));
        Files.createDirectories(projectPath.resolve("src/test/java"));
        Files.createDirectories(projectPath.resolve("src/test/resources"));

        // 创建包结构
        String basePackage = projectPath.getFileName().toString().toLowerCase().replaceAll("-", ".");
        String[] packageParts = basePackage.split("\\.");

        Path packagePath = projectPath.resolve("src/main/java");
        for (String part : packageParts) {
            packagePath = packagePath.resolve(part);
        }
        Files.createDirectories(packagePath);

        // 创建测试包结构
        Path testPackagePath = projectPath.resolve("src/test/java");
        for (String part : packageParts) {
            testPackagePath = testPackagePath.resolve(part);
        }
        Files.createDirectories(testPackagePath);
    }

    private static void generateConfigFiles(Path projectPath, String projectName) throws IOException {
        // 生成pom.xml
        generatePomXml(projectPath, projectName);

        // 生成application.properties
        Path resourcesPath = projectPath.resolve("src/main/resources");
        Files.write(resourcesPath.resolve("application.properties"),
            ("# Application Properties\n" +
             "app.name=" + projectName + "\n" +
             "server.port=8080\n" +
             "# Database Configuration\n" +
             "# spring.datasource.url=jdbc:h2:mem:testdb\n" +
             "# spring.datasource.driver-class-name=org.h2.Driver\n" +
             "# spring.datasource.username=sa\n" +
             "# spring.datasource.password=\n" +
             "# JPA Configuration\n" +
             "# spring.jpa.hibernate.ddl-auto=create-drop\n" +
             "# spring.jpa.show-sql=true").getBytes());

        // 生成.gitignore
        Files.write(projectPath.resolve(".gitignore"),
            ("target/\n" +
             "*.jar\n" +
             "*.war\n" +
             "*.class\n" +
             ".idea/\n" +
             "*.iml\n" +
             ".settings/\n" +
             ".classpath\n" +
             ".project\n" +
             ".vscode/\n" +
             ".DS_Store\n" +
             "logs/\n" +
             "*.log\n").getBytes());
    }

    private static void generatePomXml(Path projectPath, String projectName) throws IOException {
        String pomContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "    <groupId>com.example</groupId>\n" +
                "    <artifactId>" + projectName + "</artifactId>\n" +
                "    <version>0.0.1-SNAPSHOT</version>\n" +
                "    <name>" + projectName + "</name>\n" +
                "    <description>Demo project for NanoBoot</description>\n" +
                "\n" +
                "    <properties>\n" +
                "        <java.version>1.8</java.version>\n" +
                "        <nanoboot.version>1.0.0-SNAPSHOT</nanoboot.version>\n" +
                "        <maven.compiler.source>1.8</maven.compiler.source>\n" +
                "        <maven.compiler.target>1.8</maven.compiler.target>\n" +
                "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
                "    </properties>\n" +
                "\n" +
                "    <dependencies>\n" +
                "        <dependency>\n" +
                "            <groupId>org.nanoboot</groupId>\n" +
                "            <artifactId>nano-boot-starter</artifactId>\n" +
                "            <version>${nanoboot.version}</version>\n" +
                "        </dependency>\n" +
                "        \n" +
                "        <!-- Optional dependencies -->\n" +
                "        <dependency>\n" +
                "            <groupId>org.nanoboot</groupId>\n" +
                "            <artifactId>nano-boot-data</artifactId>\n" +
                "            <version>${nanoboot.version}</version>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.nanoboot</groupId>\n" +
                "            <artifactId>nano-boot-websocket</artifactId>\n" +
                "            <version>${nanoboot.version}</version>\n" +
                "        </dependency>\n" +
                "        \n" +
                "        <!-- Test dependencies -->\n" +
                "        <dependency>\n" +
                "            <groupId>org.nanoboot</groupId>\n" +
                "            <artifactId>nano-boot-core</artifactId>\n" +
                "            <version>${nanoboot.version}</version>\n" +
                "            <scope>test</scope>\n" +
                "        </dependency>\n" +
                "    </dependencies>\n" +
                "\n" +
                "    <build>\n" +
                "        <plugins>\n" +
                "            <plugin>\n" +
                "                <groupId>org.apache.maven.plugins</groupId>\n" +
                "                <artifactId>maven-compiler-plugin</artifactId>\n" +
                "                <version>3.8.1</version>\n" +
                "                <configuration>\n" +
                "                    <source>1.8</source>\n" +
                "                    <target>1.8</target>\n" +
                "                </configuration>\n" +
                "            </plugin>\n" +
                "            <plugin>\n" +
                "                <groupId>org.apache.maven.plugins</groupId>\n" +
                "                <artifactId>maven-jar-plugin</artifactId>\n" +
                "                <version>3.2.0</version>\n" +
                "                <configuration>\n" +
                "                    <archive>\n" +
                "                        <manifest>\n" +
                "                            <addClasspath>true</addClasspath>\n" +
                "                            <classpathPrefix>lib/</classpathPrefix>\n" +
                "                            <mainClass>com.example." + getMainClassName(projectName) + "</mainClass>\n" +
                "                        </manifest>\n" +
                "                    </archive>\n" +
                "                </configuration>\n" +
                "            </plugin>\n" +
                "        </plugins>\n" +
                "    </build>\n" +
                "\n" +
                "</project>";

        Files.write(projectPath.resolve("pom.xml"), pomContent.getBytes());
    }

    private static void generateSourceCode(Path projectPath, String projectName) throws IOException {
        String basePackage = projectPath.getFileName().toString().toLowerCase().replaceAll("-", ".");
        String[] packageParts = basePackage.split("\\.");
        String packageName = String.join(".", packageParts);

        Path packageDir = projectPath.resolve("src/main/java");
        for (String part : packageParts) {
            packageDir = packageDir.resolve(part);
        }

        // 生成主启动类
        String mainClassName = getMainClassName(projectName);
        String mainClassContent = "package " + packageName + ";\n" +
                "\n" +
                "import org.nanoboot.annotation.Annotation.NanoBootApplication;\n" +
                "import org.nanoboot.starter.NanoBootApplicationRunner;\n" +
                "\n" +
                "@NanoBootApplication\n" +
                "public class " + mainClassName + " {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Starting NanoBoot application...\");\n" +
                "        NanoBootApplicationRunner.run(" + mainClassName + ".class, args);\n" +
                "    }\n" +
                "}\n";

        Files.write(packageDir.resolve(mainClassName + ".java"), mainClassContent.getBytes());
    }

    private static void generateExampleCode(Path projectPath, String projectName) throws IOException {
        String basePackage = projectPath.getFileName().toString().toLowerCase().replaceAll("-", ".");
        String[] packageParts = basePackage.split("\\.");
        String packageName = String.join(".", packageParts);

        Path packageDir = projectPath.resolve("src/main/java");
        for (String part : packageParts) {
            packageDir = packageDir.resolve(part);
        }

        // 创建子目录结构
        Path controllerDir = packageDir.resolve("controller");
        Path serviceDir = packageDir.resolve("service");
        Path dtoDir = packageDir.resolve("dto");

        Files.createDirectories(controllerDir);
        Files.createDirectories(serviceDir);
        Files.createDirectories(dtoDir);

        // 生成服务类
        String serviceContent = "package " + packageName + ".service;\n" +
                "\n" +
                "import org.nanoboot.annotation.Annotation.Service;\n" +
                "import org.nanoboot.annotation.Annotation.Value;\n" +
                "\n" +
                "@Service\n" +
                "public class ApplicationService {\n" +
                "\n" +
                "    @Value(\"${app.name:DefaultApp}\")\n" +
                "    private String appName;\n" +
                "\n" +
                "    public String getAppInfo() {\n" +
                "        return \"Application: \" + appName + \" running on NanoBoot\";\n" +
                "    }\n" +
                "    \n" +
                "    public String greet(String name) {\n" +
                "        return \"Hello \" + name + \"! Welcome to \" + appName + \".\";\n" +
                "    }\n" +
                "}\n";

        Files.write(serviceDir.resolve("ApplicationService.java"), serviceContent.getBytes());

        // 生成DTO类
        String dtoContent = "package " + packageName + ".dto;\n" +
                "\n" +
                "public class GreetingRequest {\n" +
                "    private String name;\n" +
                "    \n" +
                "    public GreetingRequest() {}\n" +
                "    \n" +
                "    public GreetingRequest(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "    \n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "    \n" +
                "    public void setName(String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "}\n";

        Files.write(dtoDir.resolve("GreetingRequest.java"), dtoContent.getBytes());

        // 生成控制器类
        String controllerContent = "package " + packageName + ".controller;\n" +
                "\n" +
                "import org.nanoboot.annotation.Annotation.Autowired;\n" +
                "import org.nanoboot.annotation.Annotation.Controller;\n" +
                "import org.nanoboot.annotation.Annotation.GetMapping;\n" +
                "import org.nanoboot.annotation.Annotation.PostMapping;\n" +
                "import org.nanoboot.annotation.Annotation.RequestParam;\n" +
                "import org.nanoboot.annotation.Annotation.RequestBody;\n" +
                "import org.nanoboot.annotation.Annotation.RequestMapping;\n" +
                "import " + packageName + ".service.ApplicationService;\n" +
                "import " + packageName + ".dto.GreetingRequest;\n" +
                "\n" +
                "@Controller\n" +
                "@RequestMapping(\"/api\")\n" +
                "public class ApplicationController {\n" +
                "\n" +
                "    @Autowired\n" +
                "    private ApplicationService applicationService;\n" +
                "    \n" +
                "    @GetMapping(\"/info\")\n" +
                "    public String getInfo() {\n" +
                "        return applicationService.getAppInfo();\n" +
                "    }\n" +
                "    \n" +
                "    @GetMapping(\"/greet\")\n" +
                "    public String greet(@RequestParam(value = \"name\", defaultValue = \"World\") String name) {\n" +
                "        return applicationService.greet(name);\n" +
                "    }\n" +
                "    \n" +
                "    @PostMapping(\"/greet\")\n" +
                "    public String greetUser(@RequestBody GreetingRequest request) {\n" +
                "        return applicationService.greet(request.getName());\n" +
                "    }\n" +
                "}\n";

        Files.write(controllerDir.resolve("ApplicationController.java"), controllerContent.getBytes());
    }

    private static String getMainClassName(String projectName) {
        // 将项目名转换为主类名（驼峰命名法）
        String[] parts = projectName.split("[-_]");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    sb.append(part.substring(1).toLowerCase());
                }
            }
        }
        String className = sb.toString();
        // 确保类名以Application结尾
        if (!className.endsWith("Application") && !className.endsWith("App")) {
            className += "Application";
        }
        return className;
    }
}