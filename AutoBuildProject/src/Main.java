import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        final var startInstant = Instant.now();

        final var buildNames = GradleFunction.getBuildList();
        boolean fastBuild = false;


        for (var buildName : buildNames) {
            GradleFunction.translateJavaFile(buildName);
            GradleFunction.translateResourcesDirName();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
//                CommandRunner.zipBuildRun();
                var iRobot = new IntelliJButtonPushRobot();

                if (!fastBuild) {
                    iRobot.runNowTaskRun();
                    TimeUnit.SECONDS.sleep(15); //初回ビルドに付き、長めに待機
                    fastBuild = true;
                }else {
                    iRobot.currentTaskRun();
                    TimeUnit.SECONDS.sleep(5); //IntelliJのビルド終了待ち
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("All zipBuild finish. is Not SR2 = " + GradleFunction.isNotSR2());

        final var endInstant = Instant.now();
        System.out.println(
                "All zipBuild finish time: "
                        + endInstant.minusSeconds(startInstant.getEpochSecond()).getEpochSecond()
                        + "second"

        );

    }

}

/**
 * やっぱり2768環境で他forgeに依存したmodに依存したリトルメイドは作れないっぽい
 * cmd経由で gradlew zipBuildするとcompile Javaが失敗する
 * 何故かIntelliJの実行ボタンからzipBuildしたときは、欲しいビルドをいい感じにやってくれるっぽい
 * <p>
 * IntelliJのzipBuildさえ動かせればよいので、RobotクラスでIntelliJを操作する
 */

class IntelliJButtonPushRobot {

    private final Robot robot;

    IntelliJButtonPushRobot() {
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public void mouseClick() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(10);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(100);
    }

    public void leftDisplayClick() {
        robot.mouseMove(100, 100);
        robot.delay(100);
    }

    private void pushDoubleKeyBoardButton(int key1, int key2) {
        robot.keyPress(key1);
        robot.keyPress(key2);
        robot.delay(10);

        robot.keyRelease(key1);
        robot.keyRelease(key2);
        robot.delay(100);
    }

    public void currentTaskRun() {
        pushDoubleKeyBoardButton(KeyEvent.VK_SHIFT, KeyEvent.VK_F10);
    }

    /**
     * IntelliJにセットされているタスクを実行する
     */
    public void runNowTaskRun() {
        leftDisplayClick();
        mouseClick();
        currentTaskRun();
    }
}

class CommandHolder {

    static final String commandPrompt = "C:\\Windows\\system32\\cmd.exe";

    static final String commandPromptExitOption = "/c";

    static final String[] setCommandPromptCharCodeUTF8 = {
            "CHCP", "65001"
    };

    static final String andArguments = "&";

    enum ProjectRootDirName {
        LMLIB(""),
        NX("NX"),
        X("X");

        final String NAME;

        ProjectRootDirName(String addName) {
            NAME = addName;
        }
    }

    static final ProjectRootDirName PROJECT_ROOT_DIR_NAME = ProjectRootDirName.LMLIB;

    public static final Path projectDirPath = Paths.get(
            "D:\\Minecraft_Modding\\LittleMaidMobMultiModelSizeFor" + PROJECT_ROOT_DIR_NAME.NAME
    );

    static final String[] tree = {
            "tree"
    };

    static final String[] gradlewZipBuild = {
            "gradlew", "zipBuild"
    };

}

class CommandRunner {

    static void zipBuildRun() {
        System.out.println("gradlew zipBuild start");
        GradleFunction.processStart(CommandHolder.gradlewZipBuild)
                .forEach(System.out::println);
        System.out.println("gradlew zipBuild end");
    }

}

class GradleFunction {

    /**
     * java直下にあるファイルで、ファイル名にModelLittleMaidが含まれるファイルの最も最初にマッチした物を返す
     * 基本的にModelLittleMaidを含むファイル名は一つしかないと仮定している
     * <p>
     * Mint OSだと相対パスで記述できたけど、WindowsだとGradleがコケるのでやむを得ず絶対パス表記にした。
     * もし、このコードを使用される方が居たら、適切なパスに変更してください。
     */
    static String getSourceFileName() {

        var workDir = Paths.get(CommandHolder.projectDirPath.toString(), "\\src\\main\\java");

        String sourceFileCodeName = "Error";
        try {
            sourceFileCodeName = Files.list(workDir).
                    findFirst()
                    .orElseThrow(IllegalStateException::new)
                    .getFileName()
                    .toString();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        for (Path path: Files.list(workDir)){
//            String s = path.getFileName().toString();
//            if (s.contains("ModelLittleMaid")) {
//                sourceFileCodeName = s;
//                break;
//            }
//        }
        String sourceFileStrPath = CommandHolder.projectDirPath + "\\src\\main\\java" + sourceFileCodeName;
        Path sourceFilePath = Paths.get(sourceFileStrPath);
        return sourceFilePath.getFileName().toString();
    }

    static String getBuildName(String fullSourceFileName) {
        final int endIndex = fullSourceFileName.indexOf(".");
        final int startIndex = fullSourceFileName.indexOf("_") + 1;
        return fullSourceFileName.substring(startIndex, endIndex);
    }

    public static class BuildName {
        final String CLASS_NAME;
        final String CLASS_FILE_NAME;
        final String SIZE_RATE_NAME;

        BuildName(String baseName, String sizeRateName) {
            CLASS_NAME = createClassName(baseName);

            String suffix = "";
            if (!CLASS_NAME.contains(".java")) {
                suffix = ".java";
            }
            CLASS_FILE_NAME = CLASS_NAME + suffix;
            SIZE_RATE_NAME = "SizeRate." + sizeRateName;
        }

        /**
         * @param baseName example: "333DangerNotSR2"
         * @return example: ""ModelLittleMaid_ZeroDot333DangerNotSR2
         */
        private String createClassName(final String baseName) {
            return "ModelLittleMaid_ZeroDot" +
                    baseName;
        }

        @Override
        public String toString() {
            return "BuildName{" +
                    "CLASS_NAME='" + CLASS_NAME + '\'' +
                    ", CLASS_FILE_NAME='" + CLASS_FILE_NAME + '\'' +
                    ", SIZE_RATE_NAME='" + SIZE_RATE_NAME + '\'' +
                    '}';
        }
    }

    static boolean isNotSR2() {
        return false;
    }

    static List<String> getBaseClassNameList() {
        final List<String> baseNames = new ArrayList<>();
        baseNames.add("200Danger");
        baseNames.add("250Danger");
        baseNames.add("300Danger");
        baseNames.add("350Danger");
        baseNames.add("400Danger");
        baseNames.add("450");
        baseNames.add("500");
        baseNames.add("550");
        baseNames.add("600");
        baseNames.add("650");
        baseNames.add("700");
        baseNames.add("750");
        baseNames.add("800");
        baseNames.add("850");
        baseNames.add("900");
        baseNames.add("950");
        baseNames.add("370PassedHalfBlockDanger");
        baseNames.add("733PassedBlock");
        baseNames.add("333Danger");

        if (baseNames.size() != 19) {
            throw new AssertionError();
        }
        return baseNames;
    }

    static List<String> getBaseNameListOrAddNotSR2() {
        if (!isNotSR2()) {
            return getBaseClassNameList();
        }

        return changeNotSR2(getBaseClassNameList());
    }

    static List<String> changeNotSR2(final List<String> baseNameList) {
        List<String> baseNamesAddNotSR2 = new ArrayList<>();
        for (var baseName : baseNameList) {
            baseNamesAddNotSR2.add(baseName + "NotSR2");
        }
        return baseNamesAddNotSR2;
    }

    static Map<String, String> getSizeRateNameMap() {
        final NavigableMap<String, String> sizeRateNameMap = new TreeMap<>();
        sizeRateNameMap.put("200Danger", "ZERO_DOT200_DANGER");
        sizeRateNameMap.put("250Danger", "ZERO_DOT250_DANGER");
        sizeRateNameMap.put("300Danger", "ZERO_DOT300_DANGER");
        sizeRateNameMap.put("333Danger", "ZERO_DOT333_DANGER");
        sizeRateNameMap.put("350Danger", "ZERO_DOT350_DANGER");
        sizeRateNameMap.put("400Danger", "ZERO_DOT400_DANGER");
        sizeRateNameMap.put("450", "ZERO_DOT450");
        sizeRateNameMap.put("500", "ZERO_DOT500");
        sizeRateNameMap.put("550", "ZERO_DOT550");
        sizeRateNameMap.put("600", "ZERO_DOT600");
        sizeRateNameMap.put("650", "ZERO_DOT650");
        sizeRateNameMap.put("700", "ZERO_DOT700");
        sizeRateNameMap.put("750", "ZERO_DOT750");
        sizeRateNameMap.put("800", "ZERO_DOT800");
        sizeRateNameMap.put("850", "ZERO_DOT850");
        sizeRateNameMap.put("900", "ZERO_DOT900");
        sizeRateNameMap.put("950", "ZERO_DOT950");
        sizeRateNameMap.put("370PassedHalfBlockDanger", "PASSED_HALF_BLOCK_DANGER");
        sizeRateNameMap.put("733PassedBlock", "PASSED_BLOCK");

        if (sizeRateNameMap.size() != 19) {
            throw new AssertionError();
        }

        final var addNotSR2List = changeNotSR2(getBaseClassNameList());

        for (var i : addNotSR2List) {
            final var rowKey = sizeRateNameMap.floorKey(i);
            final var sizeRateName = sizeRateNameMap.get(rowKey);
            sizeRateNameMap.put(i, sizeRateName);
        }

        return sizeRateNameMap;
    }

    static List<BuildName> getBuildList() {
        final List<BuildName> buildList = new ArrayList<>();
        final var sizeRateMap = getSizeRateNameMap();
        final var classBaseNameList = getBaseNameListOrAddNotSR2();
        for (var baseName : classBaseNameList) {
            buildList.add((new BuildName(baseName, sizeRateMap.get(baseName))));
        }

        return buildList;
    }

    static String getProjectDirName() {
        return CommandHolder.projectDirPath.toString();
    }

    static Path getProjectJavaDirPath() {
        return Paths.get(getProjectDirName(), "src\\main\\java");
    }

    static Path getNowJavaFilePath() {
        Path path = getProjectJavaDirPath();
        try {
            final var pathStream = Files.list(path);
            return pathStream.findFirst().orElseThrow(IllegalStateException::new);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new AssertionError();
    }


    static void translateResourcesDirName() {
        Path resourcePath = null;
//        for (Path path : Files.list(Paths.get("D:\\Minecraft_Modding\\LittleMaidMobMultiModelSizeFor\\src\\main\\resources\\assets\\minecraft\\textures\\entity\\littleMaid"))) {
//            resourcePath = path;
//            break;
//        }
        try {
            resourcePath = Files.list(Paths.get(CommandHolder.projectDirPath.toString(), "\\src\\main\\resources\\assets\\minecraft\\textures\\entity\\littleMaid"))
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String baseName = "SizeFor_" + getBuildName(getSourceFileName());
        Objects.requireNonNull(resourcePath);
        final Path newResourcePath = resourcePath.resolveSibling(Paths.get(baseName));
        try {
            Files.move(resourcePath, newResourcePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static List<String> getJavaCodeAllLines() {
        try {
            return Files.readAllLines(getNowJavaFilePath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    static List<String> changeJavaCode(String classBaseName, String sizeRateName) {
        var javaCode = getJavaCodeAllLines();

        List<String> overWrittenJavaCode = new ArrayList<>();
        for (var line : javaCode) {
            if (line.contains("private static final SizeRate sizeRate = ")) {
                overWrittenJavaCode.add("private static final SizeRate sizeRate = " + sizeRateName + ";");
                continue;
            }

            if (line.contains("public ModelLittleMaid_ZeroDot")) {
                String text = "public " + classBaseName + line.substring(line.indexOf("("));
                overWrittenJavaCode.add(text);
                continue;
            }

            if (line.contains("public class ModelLittleMaid_ZeroDot")) {
                overWrittenJavaCode.add("public class " + classBaseName + " extends ModelLittleMaidBase {");
                continue;
            }
            overWrittenJavaCode.add(line);
        }
        return overWrittenJavaCode;

    }

    @SuppressWarnings("unused")
    static List<String> getNotEqualsLineList(List<String> baseList, List<String> changedList) {
        if (baseList.size() != changedList.size()) {
            throw new IllegalArgumentException();
        }

        final List<String> resultList = new ArrayList<>();
        for (int i = 0; i < baseList.size(); i++) {
            if (!baseList.get(i).equals(changedList.get(i))) {
                resultList.add(changedList.get(i));
            }
        }

        return resultList;
    }


    @SuppressWarnings("unused")
    static void listPrintln(List<Object> list) {
        for (var i : list) {
            System.out.println(i);
        }
    }

//    task example() {
//        doLast {
//            // doLastに入れないと同期のたびに実行されて面倒なことになる
//        }
//    }

//    task testChangeJavaCode() {
//        doLast {
//            final var rawText = getJavaCodeAllLines()
//            List<String> list = new ArrayList<>()
//            for (i in getBuildList()) {
//                final List<String> changedText = changeJavaCode(i.CLASS_NAME, i.SIZE_RATE_NAME)
//                list.addAll(getNotEqualsLineList(rawText, changedText))
//            }
//
//            listPrintln(list)
//
//
//        }
//    }

    static void translateJavaFile(final BuildName buildName) {
        var textList = changeJavaCode(buildName.CLASS_NAME, buildName.SIZE_RATE_NAME);
        final Path oldJavaFilePath = getNowJavaFilePath();

        var path = getProjectJavaDirPath().resolve(buildName.CLASS_FILE_NAME);
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.write(path, textList, StandardCharsets.UTF_8, StandardOpenOption.WRITE);

            Files.delete(oldJavaFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    static void testTranslateJavaFile() {
//        translateJavaFile(getBuildList().get(0));
//    }

//    static void zipBuildRun() {
//        final String[] changeDirectoryProjectRoot = {
//                "cd", getProjectDirName()
//        };
//
//        final String[] gradlewZipBuild = {
//                "gradlew", "zipBuild"
//        };
//
////        var cmd = Runtime.getRuntime();
////        cmd.exec(changeDirectoryProjectRoot)
//
//        ProcessBuilder processBuilder = new ProcessBuilder(changeDirectoryProjectRoot);
//        try {
//            var process = processBuilder.start();
//            try (BufferedReader bufferedReader = new BufferedReader())
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    static List<String> processStart(final String[] command) {

        InputStream inputStream;
        final List<String> resultList = new ArrayList<>();
        final List<String> commandList = new ArrayList<>();

        commandList.add(CommandHolder.commandPrompt);
        commandList.add(CommandHolder.commandPromptExitOption);

        commandList.addAll(Arrays.asList(CommandHolder.setCommandPromptCharCodeUTF8));
        commandList.add(CommandHolder.andArguments);
        commandList.addAll(Arrays.asList(command));


        try {
            final Process process = new ProcessBuilder((commandList.toArray(new String[0])))
                    .directory(CommandHolder.projectDirPath.toFile())
                    .start();

            inputStream = process.getInputStream();

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((inputStream)))) {
                String systemOutLine;
                for (; ; ) {
                    systemOutLine = bufferedReader.readLine();
                    if (systemOutLine == null) {
                        inputStream.close();
                        break;
                    }
                    resultList.add(systemOutLine);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultList;

    }

}